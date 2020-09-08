package pw.cdmi.box.disk.files.web;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.util.HtmlUtils;

import pw.cdmi.box.disk.client.api.AsyncTaskClient;
import pw.cdmi.box.disk.client.api.NodeClient;
import pw.cdmi.box.disk.client.api.TrashClient;
import pw.cdmi.box.disk.client.domain.node.INode;
import pw.cdmi.box.disk.client.domain.node.RestFileInfo;
import pw.cdmi.box.disk.client.domain.node.RestFolderInfo;
import pw.cdmi.box.disk.client.domain.node.RestFolderLists;
import pw.cdmi.box.disk.client.domain.node.basic.BasicNodeListRequest;
import pw.cdmi.box.disk.client.domain.task.RequestAddAsyncTask;
import pw.cdmi.box.disk.user.domain.User;
import pw.cdmi.box.disk.user.service.UserService;
import pw.cdmi.box.domain.Order;
import pw.cdmi.box.domain.Page;
import pw.cdmi.box.domain.PageImpl;
import pw.cdmi.box.domain.PageRequest;
import pw.cdmi.core.exception.BadRquestException;
import pw.cdmi.core.exception.ErrorCode;
import pw.cdmi.core.exception.RestException;
import pw.cdmi.core.restrpc.RestClient;

@Controller
@RequestMapping(value = "/trash")
public class TrashController extends CommonController
{
    public static final String PATH_SEPARATER = "/";
    
    private static final String ERROR_FORBIDDEN = "Forbidden";
    
    private static final Logger LOGGER = LoggerFactory.getLogger(FolderController.class);
    
    private NodeClient nodeClient;
    
    private TrashClient trashClient;
    
    @Resource
    private RestClient ufmClientService;
    
    @Autowired
    private UserService userService;
    
    @RequestMapping(value = "changeParent/{ownerId}", method = RequestMethod.GET)
    public String changeParent(@PathVariable("ownerId") long ownerId, Model model)
    {
        model.addAttribute("ownerId", ownerId);
        return "files/changeParentFolder";
    }
    
    @RequestMapping(value = "clean/{ownerId}", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<String> cleanAll(@PathVariable Long ownerId, HttpServletRequest request)
    {
        try
        {
            super.checkToken(request);
            RequestAddAsyncTask requestAsynTask = new RequestAddAsyncTask();
            requestAsynTask.setType(RequestAddAsyncTask.TYPE_CLEAN);
            requestAsynTask.setSrcOwnerId(ownerId);
            return new ResponseEntity<String>(new AsyncTaskClient(ufmClientService).addAsyncTask(getToken(),
                requestAsynTask).getId(), HttpStatus.OK);
        }
        catch (RestException e)
        {
            LOGGER.error(e.getMessage(), e);
            return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
        }
    }
    
    @RequestMapping(value = "delete", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<String> deleteTrashItem(Long ownerId, String ids, HttpServletRequest request)
    {
        if (StringUtils.isBlank(ids))
        {
            return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
        }
        
        try
        {
            super.checkToken(request);
            String[] idArray = ids.split(",");
            
            String token = getToken();
            
            for (String id : idArray)
            {
                getTrashClient().deleteTrashItem(token, ownerId, Long.parseLong(id));
            }
            return new ResponseEntity<String>(HttpStatus.OK);
        }
        catch (RestException e)
        {
            LOGGER.error(e.getMessage(), e);
            return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
        }
    }
    
    @RequestMapping(method = RequestMethod.GET)
    public String enter(Model model)
    {
        model.addAttribute("ownerId", getCurrentUser().getId());
        return "files/trashIndex";
    }
    
    @RequestMapping(value = "list", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<Page<INode>> list(Long ownerId,
        @RequestParam(value = "pageNumber", defaultValue = "1") int pageNumber,
        @RequestParam(value = "pageSize", defaultValue = "40", required = false) int pageSize,
        @RequestParam(value = "orderField", defaultValue = "modifiedAt") String orderField,
        @RequestParam(value = "desc", defaultValue = "true") boolean desc, HttpServletRequest request)
    {
        PageRequest pageRequest = new PageRequest(pageNumber, pageSize);
        pageRequest.setOrder(new Order(orderField, desc));
        pageRequest.getOrder().generateFileField();
        try
        {
            super.checkToken(request);
            
            long offset = 0L;
            if (pageNumber > 0)
            {
                offset = (long) (pageNumber - 1) * pageSize;
            }
            
            List<Order> orders = new ArrayList<Order>(3);
            orders.add(new Order("TYPE", "ASC"));
            orders.add(new Order(orderField, desc ? "DESC" : "ASC"));
            
            RestFolderLists list = getTrashClient().listTrashItems(getToken(),
                ownerId,
                new BasicNodeListRequest(pageSize, offset, orders, getDefultThumbnails()));
            
            Page<INode> page = new PageImpl<INode>(transToNodeList(list, getToken()), pageRequest,
                list.getTotalCount());
            return new ResponseEntity<Page<INode>>(page, HttpStatus.OK);
        }
        catch (RestException e)
        {
            LOGGER.error(e.getMessage(), e);
            if (ERROR_FORBIDDEN.equalsIgnoreCase(e.getCode()))
            {
                return new ResponseEntity<Page<INode>>(HttpStatus.FORBIDDEN);
            }
            return new ResponseEntity<Page<INode>>(HttpStatus.BAD_REQUEST);
        }
        catch (BadRquestException e)
        {
            LOGGER.error(e.getMessage(), e);
            return new ResponseEntity<Page<INode>>(HttpStatus.BAD_REQUEST);
        }
    }
    
    @RequestMapping(value = "restoreAll/{ownerId}", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<?> restoreAll(@PathVariable("ownerId") long ownerId, HttpServletRequest request)
    {
        try
        {
            super.checkToken(request);
            RequestAddAsyncTask requestAsynTask = new RequestAddAsyncTask();
            requestAsynTask.setType(RequestAddAsyncTask.TYPE_RESTORE);
            requestAsynTask.setSrcOwnerId(ownerId);
            return new ResponseEntity<String>(new AsyncTaskClient(ufmClientService).addAsyncTask(getToken(),
                requestAsynTask).getId(), HttpStatus.OK);
        }
        catch (RestException e)
        {
            LOGGER.error(e.getMessage(), e);
            return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
        }
    }
    
    @RequestMapping(value = "restore", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<String> restoreTrashItem(Long ownerId, String ids, HttpServletRequest request)
    {
        try
        {
            super.checkToken(request);
            String[] idArray = ids.split(",");
            
            String token = getToken();
            
            StringBuilder noParentIds = new StringBuilder();
            
            doRestore(ownerId, idArray, token, noParentIds);
            
            if (StringUtils.isNotBlank(noParentIds.toString()))
            {
                return new ResponseEntity<String>(HtmlUtils.htmlEscape(noParentIds.toString()),
                    HttpStatus.CONFLICT);
            }
            return new ResponseEntity<String>(HttpStatus.OK);
        }
        catch (NumberFormatException e)
        {
            LOGGER.error(e.getMessage(), e);
            return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
        }
    }
    
    private void doRestore(Long ownerId, String[] idArray, String token, StringBuilder noParentIds)
    {
        for (String id : idArray)
        {
            try
            {
                getTrashClient().restoreTrashItem(token, ownerId, Long.parseLong(id), null, true);
            }
            catch (RestException e)
            {
                if ("NoSuchParent".equals(e.getCode()))
                {
                    noParentIds.append(id).append(',');
                }
                else
                {
                    throw e;
                }
            }
        }
    }
    
    @RequestMapping(value = "changeParent", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<String> restoreWithNewParentFolder(Long ownerId, String ids, Long parentId,
        HttpServletRequest request)
    {
        super.checkToken(request);
        if (StringUtils.isBlank(ids))
        {
            return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
        }
        
        try
        {
            String[] idArray = ids.split(",");
            
            String token = getToken();
            
            for (String id : idArray)
            {
                getTrashClient().restoreTrashItem(token, ownerId, Long.parseLong(id), parentId, true);
            }
            return new ResponseEntity<String>(HttpStatus.OK);
        }
        catch (RestException e)
        {
            LOGGER.error(e.getMessage(), e);
            if ("NoSuchParent".equals(e.getCode()))
            {
                return new ResponseEntity<String>("NoSuchParentException", HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<String>(HtmlUtils.htmlEscape(e.getCode()),
                HttpStatus.INTERNAL_SERVER_ERROR);
        }
        catch (NumberFormatException e)
        {
            LOGGER.error(e.getMessage(), e);
            return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
        }
    }
    
    @PostConstruct
    void init()
    {
        trashClient = new TrashClient(ufmClientService);
        nodeClient = new NodeClient(ufmClientService);
    }
    
    private String generalNodePath(List<RestFolderInfo> parentList)
    {
        if (CollectionUtils.isEmpty(parentList))
        {
            return PATH_SEPARATER;
        }
        Collections.reverse(parentList);
        StringBuffer buffer = new StringBuffer();
        for (RestFolderInfo folderInfo : parentList)
        {
            buffer.append(PATH_SEPARATER).append(folderInfo.getName());
        }
        buffer.append(PATH_SEPARATER);
        return HtmlUtils.htmlEscape(buffer.toString());
    }
    
    private TrashClient getTrashClient()
    {
        return this.trashClient;
    }
    
    private List<INode> transToNodeList(RestFolderLists list, String token) throws RestException
    {
        List<INode> content = new ArrayList<INode>(list.getTotalCount());
        INode iNode = null;
        for (RestFolderInfo folderInfo : list.getFolders())
        {
            iNode = new INode(folderInfo);
            content.add(iNode);
        }
        for (RestFileInfo fileInfo : list.getFiles())
        {
            iNode = new INode(fileInfo);
            content.add(iNode);
        }
        
        List<RestFolderInfo> parentList;
        for (INode node : content)
        {
            try
            {
                node.setName(HtmlUtils.htmlEscape(node.getName()));
                User user = userService.getUserByCloudUserId(node.getModifiedBy());
                node.setModifiedByName(user != null ? user.getName() : "");
                parentList = nodeClient.getNodePath(token, node.getOwnedBy(), node.getId());
            }
            catch (RestException e)
            {
                if (ErrorCode.NO_SUCH_PARENT.getCode().equals(e.getCode())
                    || ErrorCode.NO_SUCH_ITEM.getCode().equals(e.getCode()))
                {
                    node.setPath("file.info.parentNotExist");
                    continue;
                }
                throw e;
            }
            node.setPath(generalNodePath(parentList));
            node.setDescription(HtmlUtils.htmlEscape(node.getDescription()));
            node.setLinkCode(HtmlUtils.htmlEscape(node.getLinkCode()));
            node.setModifiedByName(HtmlUtils.htmlEscape(node.getModifiedByName()));
        }
        return content;
    }
    
}
