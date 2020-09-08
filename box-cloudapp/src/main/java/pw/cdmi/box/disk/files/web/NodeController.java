package pw.cdmi.box.disk.files.web;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.HtmlUtils;
import pw.cdmi.box.disk.client.api.AsyncTaskClient;
import pw.cdmi.box.disk.client.api.NodeClient;
import pw.cdmi.box.disk.client.domain.common.RestBaseObject;
import pw.cdmi.box.disk.client.domain.node.*;
import pw.cdmi.box.disk.client.domain.node.basic.BasicNodeListRequest;
import pw.cdmi.box.disk.client.domain.task.LinkUser;
import pw.cdmi.box.disk.client.domain.task.RequestAddAsyncTask;
import pw.cdmi.box.disk.client.domain.task.RequestNode;
import pw.cdmi.box.disk.share.domain.INodeLink;
import pw.cdmi.box.disk.user.domain.User;
import pw.cdmi.box.disk.user.service.UserService;
import pw.cdmi.box.disk.utils.CommonTools;
import pw.cdmi.box.disk.utils.FilesCommonUtils;
import pw.cdmi.box.domain.Order;
import pw.cdmi.box.domain.Page;
import pw.cdmi.box.domain.PageImpl;
import pw.cdmi.box.domain.PageRequest;
import pw.cdmi.core.exception.*;
import pw.cdmi.core.restrpc.RestClient;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

@Controller
@RequestMapping(value = "/nodes")
public class NodeController extends CommonController
{
    private static Logger logger = LoggerFactory.getLogger(NodeController.class);
    
    private static final String PAGE_RENAME_EXCEPTION = "files/renameNodeException";
    
    private static final String PATH_SEPARATER = "/";
    
    private static final String TYPE_COPY = "copy";
    
    private static final String TYPE_MOVE = "move";
    
    private NodeClient nodeClient;
    
    @Resource
    private RestClient ufmClientService;
    
	@Autowired
	private UserService userService;
    
    /**
     * To cancel the synchronization settings
     * 
     * @param ownerId
     * @param nodeId
     * @return
     */
    @RequestMapping(value = "cancelSync/{ownerId}/{nodeId}", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<String> cancelSyncFolder(@PathVariable("ownerId") long ownerId,
        @PathVariable("nodeId") long nodeId, HttpServletRequest httpServletRequest)
    {
        super.checkToken(httpServletRequest);
        RenameAndSetSyncRequest request = new RenameAndSetSyncRequest();
        request.setSyncStatus(false);
        nodeClient.renameAndSetSync(getToken(), ownerId, nodeId, request);
        return new ResponseEntity<String>(HttpStatus.OK);
    }
    
    /**
     * 
     * @param ownerId
     * @param parentId
     * @param name
     * @return
     */
    @RequestMapping(value = "checkSameName", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<Boolean> checkSameNameNodeExsit(long ownerId, long parentId, String name)
    {
        GetNodeByNameRequest request = new GetNodeByNameRequest(name);
        boolean isExsit = nodeClient.isNodeExsit(getToken(), ownerId, parentId, request);
        return new ResponseEntity<Boolean>(isExsit, HttpStatus.OK);
    }
    
    /**
     * 
     * @param ownerId
     * @param parentId
     * @param name
     * @return
     */
    @RequestMapping(value = "checkSameNameToRename", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<Boolean> checkSameNameNodeExsitToRename(long ownerId, long parentId, long nodeId,
        String name)
    {
        GetNodeByNameRequest request = new GetNodeByNameRequest(name);
        boolean isExsit = nodeClient.isNodeExsitNoSelf(getToken(), ownerId, parentId, nodeId, request);
        return new ResponseEntity<Boolean>(isExsit, HttpStatus.OK);
    }
    
    @SuppressWarnings("PMD.ExcessiveParameterList")
    @RequestMapping(value = "copy/{srcOwnerId}", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<String> copy(@PathVariable("srcOwnerId") long srcOwnerId, String linkCode,
        long destOwnerId, long parentId, String ids, 
        @RequestParam(defaultValue = "", required = false, value = "startPoint") String startPoint,
        @RequestParam(defaultValue = "", required = false, value = "endPoint") String endPoint,
        HttpServletRequest request)
    {
        super.checkToken(request);
        INodeLink iNodeLink = null;
        if (StringUtils.isNotEmpty(linkCode))
        {
            iNodeLink = new INodeLink();
            String accessCode = CommonTools.getAccessCode(linkCode);
            iNodeLink.setId(linkCode);
            iNodeLink.setPlainAccessCode(accessCode);
        }
        
        String taskId = asyncCopyOrMove(getToken(),
            TYPE_COPY,
            srcOwnerId,
            destOwnerId,
            parentId,
            ids,
            false,
            iNodeLink, startPoint, endPoint);
        return new ResponseEntity<String>(taskId, HttpStatus.OK);
    }
    
    @RequestMapping(value = "delete", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<String> delete(long ownerId, String ids, HttpServletRequest request)
    {
        super.checkToken(request);
        if (StringUtils.isBlank(ids))
        {
            return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
        }
        String taskId = asyncDelete(getToken(), ownerId, ids);
        return new ResponseEntity<String>(taskId, HttpStatus.OK);
    }
    
    @RequestMapping(value = "copyAndMove/{destOwnerId}", method = RequestMethod.GET)
    public String enterCopyAndMove(@PathVariable("destOwnerId") long destOwnerId, 
    		  @RequestParam(value = "startPoint", required = false, defaultValue = "") String startPoint, 
    	      @RequestParam(value = "endPoint", required = false, defaultValue = "") String endPoint,
    	      @RequestParam(value = "nodeName", required = false, defaultValue = "") String nodeName,
    	      @RequestParam(value = "nodeType", required = false, defaultValue = "") String nodeType,
    	      @RequestParam(value = "nodeId", required = false, defaultValue = "0") long nodeId, 
    		Model model){
    	 model.addAttribute("destOwnerId", destOwnerId);
         model.addAttribute("startPoint", startPoint);
         model.addAttribute("endPoint", endPoint);
         model.addAttribute("nodeId", nodeId);
         model.addAttribute("nodeName", nodeName);
         model.addAttribute("nodeType", nodeType);

        return "files/copyAndMove";
    }
    
    @RequestMapping(value = "rename/{ownerId}/{parentId}/{nodeId}", method = RequestMethod.GET)
    public String gotoRename(@PathVariable("ownerId") long ownerId, @PathVariable("parentId") long parentId,
        @PathVariable("nodeId") long nodeId, Model model)
    {
        model.addAttribute("ownerId", ownerId);
        model.addAttribute("parentId", parentId);
        model.addAttribute("nodeId", nodeId);
        try
        {
            RestBaseObject restNode = nodeClient.getNodeInfo(getToken(), ownerId, nodeId);
            if (null == restNode)
            {
                return PAGE_RENAME_EXCEPTION;
            }
            String name = restNode.getName();
            name = FilesCommonUtils.transferString(name);
            model.addAttribute("name", name);
        }
        catch (NoSuchItemsException e)
        {
            logger.debug("", e);
            return PAGE_RENAME_EXCEPTION;
        }
        catch (RestException e)
        {
            logger.error("Fail in get node info.", e);
        }
        return "files/renameNode";
    }
    
    @PostConstruct
    public void init()
    {
        nodeClient = new NodeClient(ufmClientService);
    }
    
    @RequestMapping(value = "move/{srcOwnerId}", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<String> move(@PathVariable("srcOwnerId") long srcOwnerId, long destOwnerId,
        long parentId, String ids,
        @RequestParam(defaultValue = "", required = false, value = "startPoint") String startPoint,
        @RequestParam(defaultValue = "", required = false, value = "endPoint") String endPoint,
        HttpServletRequest request)
    {
        super.checkToken(request);
        if (StringUtils.isBlank(ids))
        {
            return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
        }
        
        String taskId = asyncCopyOrMove(getToken(),
            TYPE_MOVE,
            srcOwnerId,
            destOwnerId,
            parentId,
            ids,
            false,
            null, startPoint, endPoint);
        return new ResponseEntity<String>(taskId, HttpStatus.OK);
    }
    
    @SuppressWarnings("PMD.ExcessiveParameterList")
    @RequestMapping(value = "renameCopy/{srcOwnerId}", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<String> renameCopy(@PathVariable("srcOwnerId") long srcOwnerId, String linkCode,
        long destOwnerId, long parentId, String ids, 
        @RequestParam(defaultValue = "", required = false, value = "startPoint") String startPoint,
        @RequestParam(defaultValue = "", required = false, value = "endPoint") String endPoint,
        HttpServletRequest request)
    {
        try
        {
            super.checkToken(request);
            INodeLink iNodeLink = null;
            if (StringUtils.isNotEmpty(linkCode))
            {
                iNodeLink = new INodeLink();
                String accessCode = CommonTools.getAccessCode(linkCode);
                iNodeLink.setId(linkCode);
                iNodeLink.setPlainAccessCode(accessCode);
            }
            
            String taskId = asyncCopyOrMove(getToken(),
                TYPE_COPY,
                srcOwnerId,
                destOwnerId,
                parentId,
                ids,
                true,
                iNodeLink, startPoint, endPoint);
            return new ResponseEntity<String>(taskId, HttpStatus.OK);
        }
        catch (Exception e)
        {
            logger.info(e.getMessage(), e);
            return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
        }
    }
    
    @RequestMapping(value = "renameMove/{srcOwnerId}", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<String> renameMove(@PathVariable("srcOwnerId") long srcOwnerId, long destOwnerId,
        long parentId, String ids, 
        @RequestParam(defaultValue = "", required = false, value = "startPoint") String startPoint,
        @RequestParam(defaultValue = "", required = false, value = "endPoint") String endPoint,
        HttpServletRequest request)
    {
        try
        {
            super.checkToken(request);
            String taskId = asyncCopyOrMove(getToken(),
                TYPE_MOVE,
                srcOwnerId,
                destOwnerId,
                parentId,
                ids,
                true,
                null, startPoint, endPoint);
            return new ResponseEntity<String>(taskId, HttpStatus.OK);
        }
        catch (Exception e)
        {
            logger.info(e.getMessage(), e);
            return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
        }
    }
    
    @RequestMapping(value = "rename", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<String> renameNode(long ownerId, long parentId, long nodeId, String name,
        HttpServletRequest httpServletRequest) throws RestException
    {
        super.checkToken(httpServletRequest);
        FilesCommonUtils.checkNodeNameVaild(name);
        RenameAndSetSyncRequest request = new RenameAndSetSyncRequest();
        request.setName(name);
        nodeClient.renameAndSetSync(getToken(), ownerId, nodeId, request);
        return new ResponseEntity<String>(HttpStatus.OK);
    }
    
    @SuppressWarnings("PMD.ExcessiveParameterList")
    @RequestMapping(value = "search", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<Page<INode>> search(@RequestParam(value = "ownerId") long ownerId,
        @RequestParam(value = "name") String name,
        @RequestParam(value = "pageNumber", defaultValue = "1") int pageNumber,
        @RequestParam(value = "pageSize", defaultValue = "40", required = false) int pageSize,
        @RequestParam(value = "orderField") String orderField, @RequestParam(value = "desc") boolean desc,
        @RequestParam(value = "labelIds", required = false) String labelIds,
        @RequestParam(value = "docType", required = false) String docType,
        @RequestParam(value = "searchType", required = false, defaultValue = "0") int searchType,
        HttpServletRequest request)
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
            
            BasicNodeListRequest searchRequest = generalRequest(name, orderField, desc, offset, pageSize);
            searchRequest.setDocType(docType);
            searchRequest.setLabelIds(labelIds);
            RestFolderLists list = nodeClient.search(getToken(), ownerId, searchRequest);

            List<INode> nodeList = transToNodeList(list, getToken());
            for (INode inode : nodeList) {
                if (inode.getCreatedBy() != 0) {
                    try {
                        User createUser = userService.getUserByCloudUserId(inode.getCreatedBy());
                        if (createUser != null) {
                            inode.setCreatedByName(createUser.getName());
                        }
                    } catch (Exception e) {
                    }
                }
            }
            Page<INode> page = new PageImpl<INode>(nodeList, pageRequest, list.getTotalCount());
            return new ResponseEntity<Page<INode>>(page, HttpStatus.OK);
        }
        catch (RestException e)
        {
            if (ErrorCode.FORBIDDEN_OPER.getCode().equalsIgnoreCase(e.getCode()))
            {
                return new ResponseEntity<Page<INode>>(HttpStatus.FORBIDDEN);
            }
            logger.error(e.getMessage(), e);
            return new ResponseEntity<Page<INode>>(HttpStatus.BAD_REQUEST);
        }
        catch (BadRquestException e)
        {
            logger.error(e.getMessage(), e);
            return new ResponseEntity<Page<INode>>(HttpStatus.BAD_REQUEST);
        }
    }
    
    @RequestMapping(value = "setSync/{ownerId}/{nodeId}", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<String> setSyncFolder(@PathVariable("ownerId") long ownerId,
        @PathVariable("nodeId") long nodeId, HttpServletRequest httpServletRequest)
    {
        super.checkToken(httpServletRequest);
        RenameAndSetSyncRequest request = new RenameAndSetSyncRequest();
        request.setSyncStatus(true);
        nodeClient.renameAndSetSync(getToken(), ownerId, nodeId, request);
        return new ResponseEntity<String>(HttpStatus.OK);
    }
    
    @SuppressWarnings("PMD.ExcessiveParameterList")
    private String asyncCopyOrMove(String ufmToken, final String type, long srcOwnerId,
        final long destOwnerId, final long parentId, final String ids, boolean autoRename, INodeLink link,
        String startPoint, String endPoint)
    {
        RequestAddAsyncTask requestAsynTask = new RequestAddAsyncTask();
        requestAsynTask.setType(type);
        requestAsynTask.setSrcOwnerId(srcOwnerId);
        List<RequestNode> srcNodeList = new LinkedList<RequestNode>();
        String[] idArray = ids.split(",");
        for (String srcIdStr : idArray)
        {
            addSrcNode(srcNodeList, srcIdStr);
        }
        if (null != link)
        {
            LinkUser linkUser = new LinkUser();
            linkUser.setLinkCode(link.getId());
            linkUser.setPlainAccessCode(link.getPlainAccessCode());
            requestAsynTask.setLink(linkUser);
        }
        requestAsynTask.setSrcNodeList(srcNodeList);
        requestAsynTask.setAutoRename(autoRename);
        requestAsynTask.setDestOwnerId(destOwnerId);
        requestAsynTask.setDestFolderId(parentId);
        requestAsynTask.setStartPoint(startPoint);
        requestAsynTask.setEndPoint(endPoint);
        AsyncTaskClient asyncTaskClient = new AsyncTaskClient(ufmClientService);
        return asyncTaskClient.addAsyncTask(ufmToken, requestAsynTask).getId();
    }

   
    
    private String asyncDelete(final String ufmToken, final long ownerId, final String ids)
    {
        RequestAddAsyncTask requestAsynTask = new RequestAddAsyncTask();
        requestAsynTask.setType(RequestAddAsyncTask.TYPE_DELETE);
        requestAsynTask.setSrcOwnerId(ownerId);
        List<RequestNode> srcNodeList = new LinkedList<RequestNode>();
          
        String[] idArray = ids.split(",");
        for (String srcIdStr : idArray)
        {
            addSrcNode(srcNodeList, srcIdStr);
        }
        requestAsynTask.setSrcNodeList(srcNodeList);
        return new AsyncTaskClient(ufmClientService).addAsyncTask(ufmToken, requestAsynTask).getId();
    }

    @SuppressWarnings("PMD.PreserveStackTrace")
    private void addSrcNode(List<RequestNode> srcNodeList, String srcIdStr)
    {
        RequestNode srcNode = new RequestNode();
        try
        {
            srcNode.setSrcNodeId(Long.parseLong(srcIdStr));
        }
        catch (NumberFormatException e)
        {
            logger.error("srcIdStr parseLong failed", e);
            throw new InvalidParamException(e.getMessage());
        }
        srcNodeList.add(srcNode);
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
    
    private BasicNodeListRequest generalRequest(String name, String orderField, boolean desc, long offset, int limit)
    {
        BasicNodeListRequest searchRequest = new BasicNodeListRequest(limit, offset);
        searchRequest.setName(name);
        Thumbnail smallThumb = new Thumbnail(Thumbnail.DEFAULT_SMALL_WIDTH, Thumbnail.DEFAULT_SMALL_HEIGHT);
        Thumbnail bigThumb = new Thumbnail(Thumbnail.DEFAULT_BIG_WIDTH, Thumbnail.DEFAULT_BIG_HEIGHT);
        searchRequest.addThumbnail(smallThumb);
        searchRequest.addThumbnail(bigThumb);
        Order orderByType = new Order("TYPE", "ASC");
        Order orderByField = new Order(orderField, desc ? "DESC" : "ASC");
        searchRequest.addOrder(orderByType);
        searchRequest.addOrder(orderByField);
        return searchRequest;
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
        // get the creator name for teamspace
        for (INode node : content)
        {
            node.setName(HtmlUtils.htmlEscape(node.getName()));
            
            parentList = nodeClient.getNodePath(token, node.getOwnedBy(), node.getId());
            node.setPath(generalNodePath(parentList));
            
            node.setDescription(HtmlUtils.htmlEscape(node.getDescription()));
            node.setLinkCode(HtmlUtils.htmlEscape(node.getLinkCode()));
            node.setModifiedByName(HtmlUtils.htmlEscape(node.getModifiedByName()));
            node.setVersion(HtmlUtils.htmlEscape(node.getVersion()));     
        }
        return content;
    }
    
    /**
     *    
     * 文件智能分类查询
     *
     * <参数类型> @param ownerId 拥有都
     * <参数类型> @param name 模糊文件名
     * <参数类型> @param docType   分类类型【可在ufm工程.DocType类中查看】
     * <参数类型> @param pageNumber
     * <参数类型> @param pageSize
     * <参数类型> @param orderField
     * <参数类型> @param desc
     * <参数类型> @param request
     * <参数类型> @return 
     *
     * @return ResponseEntity<Page<INode>>
     */
    @RequestMapping(value = "searchByDocType", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<Page<INode>> searchByDocType(@RequestParam(value = "ownerId") long ownerId,
        @RequestParam(value = "name") String name,
        @RequestParam(value = "docType") Integer docType,
        @RequestParam(value = "pageNumber", defaultValue = "1") int pageNumber,
        @RequestParam(value = "pageSize", defaultValue = "40", required = false) int pageSize,
        @RequestParam(value = "orderField") String orderField, @RequestParam(value = "desc") boolean desc,
        @RequestParam(value = "labelIds", defaultValue = "", required = false) String labelIds,
        @RequestParam(value = "searchType", defaultValue = "0", required = false) int searchType,
        HttpServletRequest request)
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
            
            BasicNodeListRequest   condition = generalRequest(name, orderField, desc, offset, pageSize);
            condition.setLabelIds(labelIds);
            RestFolderLists list =  nodeClient.listSearchByDocType(getToken() , condition , ownerId , docType);

            
            List<INode> nodeList = transToNodeList(list, getToken());
            
            Page<INode> page = new PageImpl<INode>(nodeList, pageRequest, list.getTotalCount());
            return new ResponseEntity<Page<INode>>(page, HttpStatus.OK);
        }
        catch (RestException e)
        {
            if (ErrorCode.FORBIDDEN_OPER.getCode().equalsIgnoreCase(e.getCode()))
            {
                return new ResponseEntity<Page<INode>>(HttpStatus.FORBIDDEN);
            }
            logger.error(e.getMessage(), e);
            return new ResponseEntity<Page<INode>>(HttpStatus.BAD_REQUEST);
        }
        catch (Exception e)
        {
            logger.error(e.getMessage(), e);
            return new ResponseEntity<Page<INode>>(HttpStatus.BAD_REQUEST);
        }
    }
}
