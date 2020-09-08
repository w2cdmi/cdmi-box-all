package pw.cdmi.box.disk.share.web;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.util.HtmlUtils;

import pw.cdmi.box.disk.client.api.FileClient;
import pw.cdmi.box.disk.client.domain.node.INode;
import pw.cdmi.box.disk.client.domain.node.RestFileInfo;
import pw.cdmi.box.disk.client.domain.node.RestFolderInfo;
import pw.cdmi.box.disk.client.domain.node.RestFolderLists;
import pw.cdmi.box.disk.client.domain.node.RestVersionLists;
import pw.cdmi.box.disk.client.domain.node.Thumbnail;
import pw.cdmi.box.disk.client.domain.node.basic.BasicNodeListRequest;
import pw.cdmi.box.disk.files.service.FileService;
import pw.cdmi.box.disk.files.service.FolderService;
import pw.cdmi.box.disk.files.web.CommonController;
import pw.cdmi.box.disk.httpclient.rest.common.Constants;
import pw.cdmi.box.disk.oauth2.domain.UserToken;
import pw.cdmi.box.disk.share.domain.INodeShare;
import pw.cdmi.box.disk.share.domain.UserType;
import pw.cdmi.box.disk.share.service.ShareToMeService;
import pw.cdmi.box.disk.user.service.UserService;
import pw.cdmi.box.disk.utils.BusinessConstants;
import pw.cdmi.box.disk.utils.CustomUtils;
import pw.cdmi.box.disk.utils.FilesCommonUtils;
import pw.cdmi.box.disk.utils.ShareLinkExceptionUtil;
import pw.cdmi.box.domain.Order;
import pw.cdmi.box.domain.Page;
import pw.cdmi.box.domain.PageImpl;
import pw.cdmi.box.domain.PageRequest;
import pw.cdmi.core.exception.BaseRunException;
import pw.cdmi.core.exception.NoSuchItemsException;
import pw.cdmi.core.exception.RestException;
import pw.cdmi.core.restrpc.RestClient;

@Controller
@RequestMapping(value = "/shared")
public class ShareToMeController extends CommonController
{
    
    static final String PAGE_FOLDER_NULL = "invalidFolderError";
    
    static final String PAGE_SHARE_NULL = "share/inviteShare404.jsp";
    
    private static final String ATT_ERROR_INFO = "ErrorInfo";
    
    private static final String FOLDER_NULL = "folder_null";
    private static final int VERSION_PAGE_SIZE = 5;
    
    private static Logger logger = LoggerFactory.getLogger(ShareToMeController.class);
    
    @Autowired
    private FolderService folderService;
    
    @Autowired
    private ShareToMeService shareToMeService;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private FileService fileService;

	private FileClient fileClient;
	@Resource
    private RestClient ufmClientService;
    
    @PostConstruct
    void init()
    {
        this.fileClient = new FileClient(ufmClientService);
    }
    
    /**
     * 
     * @param ownerId
     * @param iNodeId
     * @return
     */
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public ResponseEntity<String> cancelShare(long ownerId, long iNodeId, HttpServletRequest request)
    {
        super.checkToken(request);
        try
        {
            UserToken user = getCurrentUser();
            shareToMeService.cancelShare(user, ownerId, iNodeId, UserType.TYPE_USER);
            return new ResponseEntity<String>(HttpStatus.OK);
        }
        catch (NoSuchItemsException noSuchError)
        {
            logger.error("", noSuchError);
            return new ResponseEntity<String>(ShareLinkExceptionUtil.getClassName(noSuchError),
                HttpStatus.NOT_FOUND);
        }
        catch (RestException e)
        {
            logger.error("", e);
            return new ResponseEntity<String>(ShareLinkExceptionUtil.getClassName(e),
                HttpStatus.INTERNAL_SERVER_ERROR);
        }
        
    }
    
    @RequestMapping(value = "/list/{ownerId}/{folderId}", method = RequestMethod.GET)
    public String gotoSharedSubFolder(@PathVariable long ownerId, @PathVariable long folderId, ModelMap model)
    {
        try
        {
            UserToken user = getCurrentUser();
            INode node = folderService.getNodeInfoCheckType(user, ownerId, folderId, INode.TYPE_FOLDER_ALL);
            if (null == node)
            {
                model.addAttribute(ATT_ERROR_INFO, FOLDER_NULL);
                return "share/" + PAGE_FOLDER_NULL;
            }
            model.addAttribute("ownerId", ownerId);
            try
            {
                String ownerName = userService.getUserByCloudUserId(ownerId).getName();
                model.addAttribute("ownerName", HtmlUtils.htmlEscape(ownerName));
            }
            catch (RestException e)
            {
                logger.warn("Can not get the ownerName");
            }
            model.addAttribute("shareRootId", folderId);
            model.addAttribute("linkHidden", StringUtils.isEmpty(CustomUtils.getValue("link.hidden")) ? false
                : CustomUtils.getValue("link.hidden"));
            return "share/shareFolderIndex";
        }
        catch (RestException e)
        {
            logger.error("", e);
            model.addAttribute(ATT_ERROR_INFO, ShareLinkExceptionUtil.getClassName(e));
            return "share/" + PAGE_FOLDER_NULL;
        }
        
    }
    
    @RequestMapping(method = RequestMethod.GET)
    public String gotoSharedToMeIndex(ModelMap model)
    {
        model.addAttribute("sharePage", "sharePage");
        model.addAttribute("linkHidden", StringUtils.isEmpty(CustomUtils.getValue("link.hidden")) ? false
            : CustomUtils.getValue("link.hidden"));
        return "share/shareToMeIndex";
    }
    
    @RequestMapping(value = "copyAndMove/{destOwnerId}/{folderId}", method = RequestMethod.GET)
    public String enterCopyAndMove(@PathVariable("destOwnerId") long destOwnerId,
        @PathVariable("folderId") long folderId, ModelMap model)
    {
        try
        {
            
            model.addAttribute("nodeName", HtmlUtils.htmlEscape(folderService.getNodeInfo(getCurrentUser(),
                destOwnerId,
                folderId).getName()));
        }
        catch (RestException e)
        {
            logger.warn("Can not get the nodeName");
        }
        model.addAttribute("destOwnerId", destOwnerId);
        model.addAttribute("folderId", folderId);
        return "share/copyAndMove";
    }
    
    /**
     * 
     * @param sharedUserId
     * @param pageNumber
     * @param orderField
     * @param desc
     * @return
     */
    @SuppressWarnings("PMD.ExcessiveParameterList")
    @RequestMapping(value = "/list", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<Page<INodeShare>> list(String name,
        @RequestParam(value = "pageNumber", defaultValue = "1") int pageNumber,
        @RequestParam(value = "orderField", defaultValue = "ownerName") final String orderField,
        @RequestParam(value = "desc", defaultValue = "false") boolean desc,
        @RequestParam(value = "pageSize", defaultValue = "40") int pageSize, HttpServletRequest request)
    {
        try
        {
            super.checkToken(request);
            UserToken user = getCurrentUser();
            PageRequest pageRequest = new PageRequest(pageNumber, pageSize, new Order(orderField, desc));
            Page<INodeShare> page = shareToMeService.listShareToMe(user, name, user.getId(), pageRequest);
            if (page == null)
            {
                return new ResponseEntity<Page<INodeShare>>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
            int id = 0;
            Long offset=0L;
            for (INodeShare nodeShare : page.getContent())
            {
                id++;
                nodeShare.setId(id);
                nodeShare.setOwnerName(HtmlUtils.htmlEscape(nodeShare.getOwnerName()));
                nodeShare.setName(HtmlUtils.htmlEscape(nodeShare.getName()));
                nodeShare.setSharedUserName(HtmlUtils.htmlEscape(nodeShare.getSharedUserName()));
                if(nodeShare.getType()==INode.TYPE_FILE){
                	 RestVersionLists versionList = fileClient.listVersion(getToken(),
                     		nodeShare.getOwnerId(),
                     		nodeShare.getiNodeId(),
                     		offset,
                             VERSION_PAGE_SIZE);
                     nodeShare.setVersion(versionList.getTotalCount()+"");
                }
               
            }
            return new ResponseEntity<Page<INodeShare>>(page, HttpStatus.OK);
        }
        
        catch (RestException e)
        {
            logger.error("list file error:", e);
            return new ResponseEntity<Page<INodeShare>>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        
    }
    
    /**
     * 
     * @param parentId
     * @param pageNumber
     * @param orderField
     * @param desc
     * @return
     */
    @SuppressWarnings("PMD.ExcessiveParameterList")
    @RequestMapping(value = "listsub", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<Page<INode>> listSub(long ownerId, long parentId, int pageNumber,
        String orderField, boolean desc, int pageSize, HttpServletRequest request)
    {
        super.checkToken(request);
        PageRequest pageRequest = new PageRequest(pageNumber, pageSize);
        
        try
        {
            long offset = 0L;
            if (pageNumber > 0)
            {
                offset = (long) (pageNumber - 1) * pageSize;
            }
            BasicNodeListRequest listFolderRequest = generalRequest(orderField, desc, offset, pageSize);
            RestFolderLists list = folderService.listNodesbyFilter(listFolderRequest,
                ownerId,
                getCurrentUser(),
                parentId);
            
            if (list == null)
            {
                return new ResponseEntity<Page<INode>>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
            
            List<INode> content = new ArrayList<INode>(list.getTotalCount());
            content.addAll(getNodesListWithoutStatusForFolders(list.getFolders()));
            content.addAll(getNodesListWithoutStatusForFiles(list.getFiles()));
            for (INode tempNode : content)
            {
                tempNode.setName(HtmlUtils.htmlEscape(tempNode.getName()));
            }
            Page<INode> page = new PageImpl<INode>(content, pageRequest, list.getTotalCount());
            return new ResponseEntity<Page<INode>>(page, HttpStatus.OK);
        }
        catch (RestException e)
        {
            logger.error("RestException" + e.getMessage(), e);
            return new ResponseEntity<Page<INode>>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        catch (BaseRunException e)
        {
            logger.error("BaseRunException" + e.getMessage(), e);
            return new ResponseEntity<Page<INode>>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    /**
     * 
     * @param ownerId
     * @param iNodeId
     * @return
     */
    @RequestMapping(value = "/mulDelete", method = RequestMethod.POST)
    public ResponseEntity<String> mulCancelShare(String ownerId, String iNodeId, HttpServletRequest request)
    {
        super.checkToken(request);
        if (StringUtils.isEmpty(ownerId))
        {
            logger.error("ownerId is empty");
            return new ResponseEntity<String>("BadRequestException", HttpStatus.BAD_REQUEST);
        }
        if (StringUtils.isEmpty(iNodeId))
        {
            logger.error("iNodeId is empty");
            return new ResponseEntity<String>("BadRequestException", HttpStatus.BAD_REQUEST);
        }
        String[] owners = ownerId.split(",");
        String[] iNodes = iNodeId.split(",");
        if (owners.length != iNodes.length)
        {
            logger.error("owners length is not same as inodeids lenth");
            return new ResponseEntity<String>("BadRequestException", HttpStatus.BAD_REQUEST);
        }
        try
        {
            UserToken user = getCurrentUser();
            long tempOwnerId = 0L;
            long tempINodeId = 0L;
            for (int i = 0; i < owners.length; i++)
            {
                tempOwnerId = Long.parseLong(owners[i].trim());
                tempINodeId = Long.parseLong(iNodes[i].trim());
                shareToMeService.cancelShare(user, tempOwnerId, tempINodeId, UserType.TYPE_USER);
            }
            return new ResponseEntity<String>(HttpStatus.OK);
        }
        catch (NumberFormatException e)
        {
            logger.error("NumberFormatException ", e);
            return new ResponseEntity<String>(ShareLinkExceptionUtil.getClassName(e),
                HttpStatus.INTERNAL_SERVER_ERROR);
        }
        catch (RestException e)
        {
            logger.error("RestException ", e);
            return new ResponseEntity<String>(ShareLinkExceptionUtil.getClassName(e),
                HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    /**
     * 
     * @param list
     * @return
     */
    private List<INode> getNodesListWithoutStatusForFolders(List<RestFolderInfo> list)
    {
        List<INode> tmpList = new ArrayList<INode>(BusinessConstants.INITIAL_CAPACITIES);
        INode in;
        for (RestFolderInfo iNode : list)
        {
            in = new INode();
            in.setId(iNode.getId());
            in.setCreatedAt(iNode.getCreatedAt());
            in.setCreatedBy(iNode.getCreatedBy());
            in.setDescription(iNode.getDescription());
            in.setModifiedAt(iNode.getModifiedAt());
            in.setModifiedBy(iNode.getModifiedBy());
            in.setName(iNode.getName());
            in.setOwnedBy(iNode.getOwnedBy());
            in.setParentId(iNode.getParent());
            in.setType(iNode.getType());
            if (iNode.getIsSharelink())
            {
                
                in.setLinkStatus(Constants.HAS_SET_LINK_FLAG);
            }
            in.setShareStatus(INode.SHARE_STATUS_UNSHARED);
            in.setShareStatus(INode.SYNC_STATUS_UNSET);
            in.setLinkCode(null);
            tmpList.add(in);
        }
        return tmpList;
    }
    
    /**
     * 
     * @param list
     * @return
     */
    private List<INode> getNodesListWithoutStatusForFiles(List<RestFileInfo> list)
    {
        List<INode> tmpList = new ArrayList<INode>(BusinessConstants.INITIAL_CAPACITIES);
        INode in;
        for (RestFileInfo iNode : list)
        {
            in = new INode();
            in.setId(iNode.getId());
            in.setCreatedAt(iNode.getCreatedAt());
            in.setCreatedBy(iNode.getCreatedBy());
            in.setDescription(iNode.getDescription());
            in.setModifiedAt(iNode.getModifiedAt());
            in.setModifiedBy(iNode.getModifiedBy());
            in.setName(iNode.getName());
            in.setOwnedBy(iNode.getOwnedBy());
            in.setParentId(iNode.getParent());
            in.setType(iNode.getType());
            if (iNode.getIsSharelink())
            {
                in.setLinkStatus(Constants.HAS_SET_LINK_FLAG);
            }
            in.setShareStatus(INode.SHARE_STATUS_UNSHARED);
            in.setShareStatus(INode.SYNC_STATUS_UNSET);
            in.setLinkCode(null);
            in.setThumbnailUrlList(iNode.getThumbnailUrlList());
            in.setSize(iNode.getSize());
            in.setPreviewable(iNode.isPreviewable());
            tmpList.add(in);
        }
        return tmpList;
    }
    
    private BasicNodeListRequest generalRequest(String orderField, boolean desc, long offset, int pageSize)
    {
        BasicNodeListRequest listFolderRequest = new BasicNodeListRequest(pageSize, offset);
        Thumbnail smallThumb = new Thumbnail(Thumbnail.DEFAULT_SMALL_WIDTH, Thumbnail.DEFAULT_SMALL_HEIGHT);
        Thumbnail bigThumb = new Thumbnail(Thumbnail.DEFAULT_BIG_WIDTH, Thumbnail.DEFAULT_BIG_HEIGHT);
        listFolderRequest.addThumbnail(smallThumb);
        listFolderRequest.addThumbnail(bigThumb);
        Order orderByType = new Order("TYPE", "ASC");
        Order orderByField = new Order(orderField, desc ? "DESC" : "ASC");
        listFolderRequest.addOrder(orderByType);
        listFolderRequest.addOrder(orderByField);
        return listFolderRequest;
    }
    
    /**
     * 
     * @param ownerId
     * @param iNodeId
     * @return
     */
    @RequestMapping(value = "/detail/{type}/{ownerId}/{iNodeId}", method = RequestMethod.GET)
    public String sharedDetail(@PathVariable String type, @PathVariable String ownerId,
        @PathVariable String iNodeId, ModelMap model)
    {
        if (StringUtils.isEmpty(type))
        {
            logger.error("type is empty");
            return PAGE_SHARE_NULL;
        }
        if (StringUtils.isEmpty(ownerId))
        {
            logger.error("ownerId is empty");
            return PAGE_SHARE_NULL;
        }
        if (StringUtils.isEmpty(iNodeId))
        {
            logger.error("iNodeId is empty");
            return PAGE_SHARE_NULL;
        }
        try
        {
            UserToken user = getCurrentUser();
            
            if (null == user.getCloudUserId() || user.getCloudUserId() <= 0)
            {
                model.addAttribute("isLoginUser", "false");
            }
            else
            {
                model.addAttribute("isLoginUser", "true");
            }
            
            INode iNode = new INode();
            if (INode.TYPE_FILE == Byte.valueOf(type))
            {
                RestFileInfo fileInfo = fileService.getFileInfo(getCurrentUser(),
                    Long.parseLong(ownerId),
                    Long.parseLong(iNodeId));
                iNode.setOwnedBy(fileInfo.getOwnedBy());
                iNode.setId(fileInfo.getId());
                iNode.setName(fileInfo.getName());
                iNode.setType(fileInfo.getType());
                iNode.setCreatedAt(fileInfo.getCreatedAt());
                iNode.setSize(fileInfo.getSize());
                iNode.setModifiedAt(fileInfo.getModifiedAt());
                iNode.setPreviewable(fileInfo.isPreviewable());
            }
            else if (FilesCommonUtils.isFolderType(Byte.valueOf(type)))
            {
                iNode = folderService.getNodeInfo(user, Long.parseLong(ownerId), Long.parseLong(iNodeId));
            }
            
            model.addAttribute("shareUserName", userService.getUserByCloudUserId(Long.parseLong(ownerId))
                .getLoginName());
            model.addAttribute("iNodeName", HtmlUtils.htmlEscape(iNode.getName()));
            model.addAttribute("iNodeSize", iNode.getSize());
            model.addAttribute("folderId", iNode.getId());
            model.addAttribute("linkCreateTime", iNode.getCreatedAt().getTime());
            model.addAttribute("linkUpdatedTime", iNode.getModifiedAt().getTime());
            
            model.addAttribute("iNodeData", iNode);
            
            if (FilesCommonUtils.isFolderType(iNode.getType()))
            {
                return "share/shareFolderDetail";
            }
            if (iNode.getType() == INode.TYPE_FILE && FilesCommonUtils.isImage(iNode.getName()))
            {
                String downloadUrl = fileService.getFileThumbUrls(user,
                    iNode,
                    null,
                    Constants.MEDIUM_THUMB_SIZE_URL);
                downloadUrl = htmlEscapeThumbnail(downloadUrl);
                model.addAttribute("thumbnailUrl", downloadUrl);
            }
            return "share/shareFileDetail";
        }
        catch (RestException e)
        {
            logger.error("", e);
            return "share/linkViewError.jsp";
        }
    }
}
