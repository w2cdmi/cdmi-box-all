package com.huawei.sharedrive.app.openapi.restv2.folder;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import io.swagger.annotations.Api;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.huawei.sharedrive.app.account.domain.Account;
import com.huawei.sharedrive.app.account.service.AccountService;
import com.huawei.sharedrive.app.core.domain.Thumbnail;
import com.huawei.sharedrive.app.event.domain.EventType;
import com.huawei.sharedrive.app.exception.BaseRunException;
import com.huawei.sharedrive.app.exception.FilesNameConflictException;
import com.huawei.sharedrive.app.exception.ForbiddenException;
import com.huawei.sharedrive.app.exception.NoSuchFolderException;
import com.huawei.sharedrive.app.exception.NoSuchItemsException;
import com.huawei.sharedrive.app.exception.NoSuchParentException;
import com.huawei.sharedrive.app.exception.NoSuchSourceException;
import com.huawei.sharedrive.app.files.dao.ShortcutDao;
import com.huawei.sharedrive.app.files.domain.FileINodesList;
import com.huawei.sharedrive.app.files.domain.INode;
import com.huawei.sharedrive.app.files.domain.Shortcut;
import com.huawei.sharedrive.app.files.service.FileBaseService;
import com.huawei.sharedrive.app.files.service.FolderService;
import com.huawei.sharedrive.app.files.service.FolderServiceV2;
import com.huawei.sharedrive.app.files.service.NodeService;
import com.huawei.sharedrive.app.files.service.lock.Locks;
import com.huawei.sharedrive.app.log.domain.UserLogType;
import com.huawei.sharedrive.app.node.manager.NodeManager;
import com.huawei.sharedrive.app.oauth2.domain.UserToken;
import com.huawei.sharedrive.app.oauth2.service.UserTokenHelper;
import com.huawei.sharedrive.app.openapi.domain.node.CreateFolderRequest;
import com.huawei.sharedrive.app.openapi.domain.node.GetNodeByNameRequest;
import com.huawei.sharedrive.app.openapi.domain.node.ListFolderRequest;
import com.huawei.sharedrive.app.openapi.domain.node.NodeCopyRequest;
import com.huawei.sharedrive.app.openapi.domain.node.NodeMoveRequest;
import com.huawei.sharedrive.app.openapi.domain.node.RenameAndSetSyncRequest;
import com.huawei.sharedrive.app.openapi.domain.node.RestBaseObject;
import com.huawei.sharedrive.app.openapi.domain.node.RestFileInfo;
import com.huawei.sharedrive.app.openapi.domain.node.RestFolderInfo;
import com.huawei.sharedrive.app.openapi.domain.node.RestFolderLists;
import com.huawei.sharedrive.app.openapi.domain.node.RestNodeList;
import com.huawei.sharedrive.app.openapi.rest.FilesCommonApi;
import com.huawei.sharedrive.app.security.service.SecurityMatrixService;
import com.huawei.sharedrive.app.security.service.SecurityMethod;
import com.huawei.sharedrive.app.share.domain.INodeLink;
import com.huawei.sharedrive.app.share.service.LinkServiceV2;
import com.huawei.sharedrive.app.user.domain.User;
import com.huawei.sharedrive.app.user.service.UserService;
import com.huawei.sharedrive.app.utils.FilesCommonUtils;
import com.huawei.sharedrive.app.utils.PropertiesUtils;
import com.huawei.sharedrive.app.utils.hwcustom.HeaderPacker;

import pw.cdmi.box.domain.Order;
import pw.cdmi.box.ufm.tools.DoctypeManager;
import pw.cdmi.common.util.signature.SignatureUtils;
import pw.cdmi.core.utils.DateUtils;

/**
 * 文件夹API Rest接口, 提供文件夹列举, 复制, 移动, 删除等操作
 * 
 * @author t90006461
 * @version CloudStor CSE Service Platform Subproject, 2014-4-30
 * @see
 * @since
 */
@Controller
@RequestMapping(value = "/api/v2/folders/{ownerId}")
@Api(description = "文件夹API Rest接口, 提供文件夹列举, 复制, 移动, 删除等操作")
public class FolderApi extends FilesCommonApi
{
    private static final Logger LOGGER = LoggerFactory.getLogger(FolderApi.class);
    
    private static final String WECHAT_NAME =  "wxfolder";
    private static final String IMAGE_NAME =  "image";
    private static final String DOCUMENT_NAME =  "document";
    private static final String VIDEO_NAME =  "video";
    
    @Autowired
    private FileBaseService fileBaseService;
    
    @Autowired
    private FolderService folderService;
    
    @Autowired
    private FolderServiceV2 folderServiceV2;
    
    @Autowired
    private NodeService nodeService;
    
    @Autowired
    private UserTokenHelper userTokenHelper;
    
    @Autowired
    private SecurityMatrixService securityMatrixService;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private NodeManager nodeManager;
    
    @Autowired
    private MessageSource messageSource;
    
    @Autowired
    private LinkServiceV2 linkServiceV2;
    
    @Autowired
    private AccountService accountService;
    
    @Autowired
    private ShortcutDao shortcutDao;
    
    @RequestMapping(value = "/{folderId}/copy", method = RequestMethod.PUT)
    @ResponseBody
    public ResponseEntity<RestFolderInfo> copyFolder(HttpServletRequest request, @PathVariable Long ownerId,
        @PathVariable Long folderId, @RequestBody NodeCopyRequest copyRequest,
        @RequestHeader("Authorization") String token) throws BaseRunException
    {
        UserToken userToken = null;
        INode srcNode = new INode();
        try
        {
            FilesCommonUtils.checkNonNegativeIntegers(ownerId, folderId);
            copyRequest.checkParameter();
            // Token 验证
            Map<String, String> headerCustomMap = HeaderPacker.getCustomHeaderMap(request);
            userToken = userTokenHelper.checkTokenAndGetUserForV2(token, headerCustomMap);
            if (token.startsWith(UserTokenHelper.LINK_PREFIX)) {
                String date = request.getHeader("Date");
                userToken = userTokenHelper.getLinkToken(token, date);
                userTokenHelper.assembleUserToken(ownerId, userToken);
            }else {
                userToken = userTokenHelper.checkTokenAndGetUserForV2(token, headerCustomMap);
            }
            // 用户状态校验
            userTokenHelper.checkUserStatus(userToken.getAppId(), ownerId);
            userTokenHelper.checkUserStatus(userToken.getAppId(), copyRequest.getDestOwnerId());
            try
            {
                securityMatrixService.checkSecurityMatrix(userToken,
                    ownerId,
                    folderId,
                    copyRequest.getDestOwnerId(),
                    SecurityMethod.NODE_COPY,
                    headerCustomMap);
            }
            catch (NoSuchItemsException e)
            {
                throw new NoSuchSourceException(e);
            }
            // 空间及文件数校验
            fileBaseService.checkSpaceAndFileCount(copyRequest.getDestOwnerId(), userToken.getAccountId());
            fillLinkUser(request, copyRequest, userToken);
            
            boolean isAutoRename = copyRequest.isAutoRename();
            srcNode = new INode(ownerId, folderId);
            INode destParent = new INode(copyRequest.getDestOwnerId(), copyRequest.getDestParent());
            
            srcNode = fileBaseService.getINodeInfo(ownerId, folderId);
            
            if (srcNode == null){
                String message = "inode not exist, ownerId:" + ownerId + ", inodeid:" + folderId;
                throw new NoSuchSourceException(message);
            }
            INode resultNode ;
            if(copyRequest.getLink()!=null){
            	INodeLink link=new INodeLink();
				link.setId(copyRequest.getLink().getLinkCode());
				link.setPlainAccessCode(copyRequest.getLink().getPlainAccessCode());
				String srcName = srcNode.getName();
                resultNode = doCopy(isAutoRename, userToken, srcNode, destParent, srcName,link);
            }else{
            	  String srcName = srcNode.getName();
                  resultNode = doCopy(isAutoRename, userToken, srcNode, destParent, srcName);
            }
          
            
            FilesCommonUtils.setNodeVersionsForV2(resultNode);
            RestFolderInfo folderInfo = new RestFolderInfo(resultNode);
            return new ResponseEntity<RestFolderInfo>(folderInfo, HttpStatus.OK);
        }
        catch (BaseRunException t)
        {
            
            String[] logParams = new String[]{String.valueOf(ownerId != null ? ownerId : ""),
                String.valueOf(folderId != null ? folderId : ""),
                String.valueOf(copyRequest.getDestOwnerId() != null ? copyRequest.getDestOwnerId() : ""),
                String.valueOf(copyRequest.getDestParent() != null ? copyRequest.getDestParent() : "")};
            if (null != srcNode)
            {
                fileBaseService.sendINodeEvent(userToken,
                    EventType.OTHERS,
                    null,
                    null,
                    UserLogType.COPY_FOLDER_ERR,
                    logParams,
                    StringUtils.trimToEmpty(srcNode.getName()));
            }
            else
            {
                fileBaseService.sendINodeEvent(userToken,
                    EventType.OTHERS,
                    null,
                    null,
                    UserLogType.COPY_FOLDER_ERR,
                    logParams,
                    null);
            }
            
            throw t;
        }
    }
    
    
    
    
    
    
    
    
    
    
    

    @RequestMapping(value = "/batch/copy", method = RequestMethod.PUT)
    @ResponseBody
    public ResponseEntity<?> batchCopyFolder(HttpServletRequest request, @PathVariable Long ownerId, @RequestBody NodeCopyRequest copyRequest,
        @RequestHeader("Authorization") String token) throws BaseRunException
	{
		UserToken userToken = null;
		INode srcNode = null;
		try {
			copyRequest.checkParameter();
			// Token 验证
			Map<String, String> headerCustomMap = HeaderPacker.getCustomHeaderMap(request);
			if (token.startsWith(UserTokenHelper.LINK_PREFIX)) {
				String date = request.getHeader("Date");
				userToken = userTokenHelper.getLinkToken(token, date);
				userTokenHelper.assembleUserToken(ownerId, userToken);
			} else {
				userToken = userTokenHelper.checkTokenAndGetUserForV2(token, headerCustomMap);
			}
			// 用户状态校验
			userTokenHelper.checkUserStatus(userToken.getAppId(), ownerId);
			userTokenHelper.checkUserStatus(userToken.getAppId(), copyRequest.getDestOwnerId());
			
			// 空间及文件数校验
			User user = userService.get(ownerId);
			fileBaseService.checkSpaceAndFileCount(copyRequest.getDestOwnerId(), user.getAccountId());
			fillLinkUser(request, copyRequest, userToken);
			for (INode inode : copyRequest.getSrcNodes()) {
				long folderId = inode.getId();
				long owner = inode.getOwnedBy();
				try {
					securityMatrixService.checkSecurityMatrix(userToken, inode.getOwnedBy(), inode.getId(), copyRequest.getDestOwnerId(), SecurityMethod.NODE_COPY, headerCustomMap);
				} catch (NoSuchItemsException e) {
					throw new NoSuchSourceException(e);
				}
				boolean isAutoRename = copyRequest.isAutoRename();
				srcNode = new INode(owner, folderId);
				INode destParent = new INode(copyRequest.getDestOwnerId(), copyRequest.getDestParent());
				srcNode = fileBaseService.getINodeInfo(owner, folderId);

				if (srcNode == null) {
					String message = "inode not exist, ownerId:" + owner + ", inodeid:" + folderId;
					throw new NoSuchSourceException(message);
				}
				String srcName = srcNode.getName();
				INodeLink link=new INodeLink();
				link.setId(copyRequest.getLink().getLinkCode());
				link.setPlainAccessCode(copyRequest.getLink().getPlainAccessCode());
				if(srcNode.getType()<1){
					doCopy(isAutoRename, userToken, srcNode, destParent, srcName,link);
				}else{
					doCopyFile(isAutoRename, userToken, srcNode, destParent, srcName,link);
				}
				
			}
			return new ResponseEntity<String>(HttpStatus.OK);
		} catch (BaseRunException t) {
			throw t;
		}
	}
    
    
    
    
    
    
    /**
     * 创建文件夹
     * 
     * @param createRequest 创建文件夹请求对象
     * @param ownerId 文件夹所有者ID
     * @param token 认证Token
     * @return
     * @throws BaseRunException
     */
    @RequestMapping(value = "", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<RestFolderInfo> createFolder(@RequestBody CreateFolderRequest createRequest,
        @PathVariable Long ownerId, @RequestHeader("Authorization") String token,
        HttpServletRequest request) throws BaseRunException
    {
        UserToken userToken = null;
        try
        {
            FilesCommonUtils.checkNonNegativeIntegers(ownerId);
            createRequest.checkParameter();
            userToken = userTokenHelper.checkTokenAndGetUserForV2(token, null);
            
            // 用户状态校验
            userTokenHelper.checkUserStatus(userToken.getAppId(), ownerId);
            Map<String, String> headerCustomMap = HeaderPacker.getCustomHeaderMap(request);
            securityMatrixService.checkSecurityMatrix(userToken,
                ownerId,
                createRequest.getParent(),
                SecurityMethod.FOLDER_CREATE,
                headerCustomMap);
            INode newFolder = createRequest.transToINode();
            boolean autoMerge = createRequest.getMergeValue();
            newFolder.setOwnedBy(ownerId);
            INode node = folderService.createFolder(userToken, newFolder, autoMerge);
            RestFolderInfo folderInfo = new RestFolderInfo(node);
            return new ResponseEntity<RestFolderInfo>(folderInfo, HttpStatus.CREATED);
        }
        catch (NoSuchItemsException e)
        {
            throw new NoSuchParentException(e);
        }
        catch (BaseRunException t)
        {
            String[] logParams = new String[]{String.valueOf(ownerId),
                String.valueOf(createRequest.getParent() != null ? createRequest.getParent() : "")};
            String keyword = StringUtils.trimToEmpty(createRequest.getName());
            fileBaseService.sendINodeEvent(userToken,
                EventType.OTHERS,
                null,
                null,
                UserLogType.CREATE_FOLDER_ERR,
                logParams,
                keyword);
            throw t;
        }
        
    }
    
    
    /**
     * 获取微信文件夹  没有就创建一个
     * 
     */
    @RequestMapping(value = "/getWxFolder", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<RestFolderInfo> checkIsHaveWxFolder(@PathVariable Long ownerId,String fileType, @RequestHeader("Authorization") String token,
        HttpServletRequest request,String wxName,String language) throws BaseRunException
    {
        UserToken userToken = null;
        try
        {
            FilesCommonUtils.checkNonNegativeIntegers(ownerId);
            userToken = userTokenHelper.checkTokenAndGetUserForV2(token, null);
            
            // 用户状态校验
            userTokenHelper.checkUserStatus(userToken.getAppId(), ownerId);
            Map<String, String> headerCustomMap = HeaderPacker.getCustomHeaderMap(request);
            securityMatrixService.checkSecurityMatrix(userToken,
                ownerId,
                INode.FILES_ROOT,
                SecurityMethod.FOLDER_CREATE,
                headerCustomMap);
            ListFolderRequest listFolderRequest=new ListFolderRequest();
            List<Order> orderList = listFolderRequest.getOrder();
            List<Thumbnail> thumbnailList = listFolderRequest.getThumbnail();
            int limit = listFolderRequest.getLimit();
            long offset = listFolderRequest.getOffset();
            INode wxnode=new INode(ownerId, INode.FILES_ROOT);
            wxnode.setStatus(INode.STATUS_NORMAL);
            FileINodesList reList = folderServiceV2.listNodesByFilter(userToken,wxnode, offset,limit,orderList,thumbnailList,listFolderRequest.getWithExtraType(), headerCustomMap);
            Locale locale;
            if ("zh".equals(language)) {  
            	locale = new Locale("zh", "CN");  
            } else if ("en".equals(language)) {  
            	locale = new Locale("en", "US");  
            }else{
            	locale = new Locale("zh", "CN");  
            }
            String wxfolderName = messageSource.getMessage(WECHAT_NAME,new Object[]{wxName} ,locale);
            boolean haseWxFolder=false;
            for(INode wxfolder :reList.getFolders()){
            	if(wxfolder.getType()==INode.TYPE_WECHAT&&wxfolder.getName().equals(wxfolderName)){
            		haseWxFolder=true;
            		wxnode=wxfolder;
            		break;
            	}
            }
            if(haseWxFolder==false){
                  wxnode.setParentId(INode.FILES_ROOT);
                  wxnode.setName(wxfolderName);
            	  wxnode = createFolder(ownerId, userToken, wxnode, INode.TYPE_WECHAT);
            }  
            boolean subWxFolder=false;
            
            FileINodesList subWxList=folderServiceV2.listNodesByFilter(userToken,wxnode, offset, limit, orderList, thumbnailList,listFolderRequest.getWithExtraType(),  headerCustomMap);
            for(INode subfolder :subWxList.getFolders()){
    			if(fileType.equals("image")&&subfolder.getType()==INode.TYPE_IMAGE){
    				subWxFolder=true;
    				wxnode=subfolder;
    				break;
    			}
				if(fileType.equals("video")&&subfolder.getType()==INode.TYPE_VIDEO){
					subWxFolder=true;
					wxnode=subfolder;   
					break;
				}
				if(fileType.equals("file")&&subfolder.getType()==INode.TYPE_DOCUMENT){
					subWxFolder=true;
					wxnode=subfolder;
					break;
				}
    		}
            if(subWxFolder==false){
            	byte subFolderType=0;
        		String folderName = "";
            	if(fileType.equals("image")){
            		subFolderType=INode.TYPE_IMAGE;
            		folderName=messageSource.getMessage(IMAGE_NAME,new Object[]{} ,locale);
    			}
            	if(fileType.equals("video")){
            		subFolderType=INode.TYPE_VIDEO;
            		folderName=messageSource.getMessage(VIDEO_NAME,new Object[]{} ,locale);
    			}
            	if(fileType.equals("file")){
            		subFolderType=INode.TYPE_DOCUMENT;
            		folderName=messageSource.getMessage(DOCUMENT_NAME,new Object[]{} ,locale);
    			}
            	wxnode.setName(folderName);
        	    wxnode = createFolder(ownerId, userToken, wxnode, subFolderType);
            }
            RestFolderInfo folderInfo = new RestFolderInfo(wxnode);
            return new ResponseEntity<RestFolderInfo>(folderInfo, HttpStatus.CREATED);
            
        }
        catch (NoSuchItemsException e)
        {
            throw new NoSuchParentException(e);
        }
        catch (BaseRunException t)
        {
            String[] logParams = new String[]{String.valueOf(ownerId),INode.FILES_ROOT+""};
            fileBaseService.sendINodeEvent(userToken,EventType.OTHERS, null,null,
                UserLogType.CREATE_FOLDER_ERR,
                logParams,
                null);
            throw t;
        }
        
    }
    
    
    
    /**
     * 获取微信文件夹  没有就创建一个
     * 
     */
    @RequestMapping(value = "/getInboxFolder", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<RestFolderInfo> getInboxFolder(@PathVariable Long ownerId, @RequestHeader("Authorization") String token,
        HttpServletRequest request) throws BaseRunException
    {
        UserToken userToken = null;
        try
        {
            FilesCommonUtils.checkNonNegativeIntegers(ownerId);
            userToken = userTokenHelper.checkTokenAndGetUserForV2(token, null);
            
            // 用户状态校验
            userTokenHelper.checkUserStatus(userToken.getAppId(), ownerId);
            Map<String, String> headerCustomMap = HeaderPacker.getCustomHeaderMap(request);
            securityMatrixService.checkSecurityMatrix(userToken,
                ownerId,
                INode.FILES_ROOT,
                SecurityMethod.FOLDER_CREATE,
                headerCustomMap);
            ListFolderRequest listFolderRequest=new ListFolderRequest();
            INode filter=new INode(ownerId, INode.FILES_ROOT);
            filter.setStatus(INode.STATUS_NORMAL);
            filter.setType(INode.TYPE_INBOX);
            FileINodesList reList = folderServiceV2.listNodesByFilter(userToken,filter,listFolderRequest,headerCustomMap);
            INode inboxNode;
            if(reList.getFolders().size()==0){
            	  filter.setName("folder.inbox");
            	  inboxNode = createFolder(ownerId, userToken, filter, INode.TYPE_INBOX);
            }else{
            	 inboxNode = reList.getFolders().get(0);
            }
            RestFolderInfo folderInfo = new RestFolderInfo(inboxNode);
            return new ResponseEntity<RestFolderInfo>(folderInfo, HttpStatus.CREATED);
            
        }
        catch (NoSuchItemsException e)
        {
            throw new NoSuchParentException(e);
        }
        catch (BaseRunException t)
        {
            String[] logParams = new String[]{String.valueOf(ownerId),INode.FILES_ROOT+""};
            fileBaseService.sendINodeEvent(userToken,EventType.OTHERS, null,null,
                UserLogType.CREATE_FOLDER_ERR,
                logParams,
                null);
            throw t;
        }
        
    }
    

	private INode createFolder(Long ownerId, UserToken userToken, INode inode, byte subFolderType) {
		CreateFolderRequest createRequest=new CreateFolderRequest();
		createRequest.setParent(inode.getId());
		INode newFolder = createRequest.transToINode();
		boolean autoMerge = createRequest.getMergeValue();
		newFolder.setOwnedBy(ownerId);
		newFolder.setCreatedBy(userToken.getId());
		newFolder.setType(subFolderType);
		newFolder.setName(inode.getName());
		newFolder.setCreatedAt(new Date());
		newFolder.setModifiedAt(new Date());
		inode = folderService.createFolder(userToken, newFolder, autoMerge);
		return inode;
	}
    
    
    /**
     * 专为外链上传文件夹创建文件夹
     * 
     * @param createRequest 创建文件夹请求对象
     * @param ownerId 文件夹所有者ID
     * @param token 认证Token
     * @return
     * @throws BaseRunException
     */
    @RequestMapping(value = "/link", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<RestFolderInfo> createFolderForLink(@RequestBody CreateFolderRequest createRequest,
        @PathVariable Long ownerId, @RequestHeader("Authorization") String token,@RequestHeader("linkCode") String linkCode,@RequestHeader("linkType") String linkType,
        HttpServletRequest request) throws BaseRunException
    {
        UserToken userToken = null;
        try
        {
            FilesCommonUtils.checkNonNegativeIntegers(ownerId);
            createRequest.checkParameter();
            //visitors
            if(StringUtils.isNotBlank(linkType)&& "anonymous".equals(linkType)){
            	String appId = PropertiesUtils.getProperty("defaultAppId");
            	userToken = new UserToken();
            	userToken.setAppId(appId);
            	userToken.setId(ownerId);
            }else{
            	//For logined visitors
            	userToken = new UserToken();
            	userToken.setId(ownerId);
            }
            //为外链上传文件夹判断.added by jeffrey
           if(StringUtils.isNotBlank(linkCode)){
            	userToken.setLinkCode(linkCode);
            }
            // 用户状态校验
            userTokenHelper.checkUserStatus(userToken.getAppId(), ownerId);
            Map<String, String> headerCustomMap = HeaderPacker.getCustomHeaderMap(request);
            securityMatrixService.checkSecurityMatrix(userToken,
                ownerId,
                createRequest.getParent(),
                SecurityMethod.FOLDER_CREATE,
                headerCustomMap);
            INode newFolder = createRequest.transToINode();
            boolean autoMerge = createRequest.getMergeValue();
            newFolder.setOwnedBy(ownerId);
            INode node = folderService.createFolder(userToken, newFolder, autoMerge);
            RestFolderInfo folderInfo = new RestFolderInfo(node);
            return new ResponseEntity<RestFolderInfo>(folderInfo, HttpStatus.CREATED);
        }
        catch (NoSuchItemsException e)
        {
            throw new NoSuchParentException(e);
        }
        catch (BaseRunException t)
        {
            String[] logParams = new String[]{String.valueOf(ownerId),
                String.valueOf(createRequest.getParent() != null ? createRequest.getParent() : "")};
            String keyword = StringUtils.trimToEmpty(createRequest.getName());
            fileBaseService.sendINodeEvent(userToken,
                EventType.OTHERS,
                null,
                null,
                UserLogType.CREATE_FOLDER_ERR,
                logParams,
                keyword);
            throw t;
        }
        
    }
    
    
    
    /**
     * 删除文件夹
     * 
     * @param ownerId
     * @param folderId
     * @param token
     * @return
     * @throws BaseRunException
     */
    @RequestMapping(value = "/{folderId}", method = RequestMethod.DELETE)
    @ResponseBody
    public ResponseEntity<String> deleteFolder(@PathVariable Long ownerId, @PathVariable Long folderId,
        @RequestHeader("Authorization") String token, HttpServletRequest request) throws BaseRunException
    {
        UserToken userToken = null;
        INode node = new INode();
        byte nodeType = 0;
        try
        {
            FilesCommonUtils.checkNonNegativeIntegers(ownerId, folderId);
            
            // Token 验证
            Map<String, String> headerCustomMap = HeaderPacker.getCustomHeaderMap(request);
            userToken = userTokenHelper.checkTokenAndGetUserForV2(token, headerCustomMap);
            
            // 用户状态校验
            userTokenHelper.checkUserStatus(userToken.getAppId(), ownerId);
            
            securityMatrixService.checkSecurityMatrix(userToken,
                ownerId,
                folderId,
                SecurityMethod.NODE_DELETE,
                headerCustomMap);
            node = fileBaseService.getAndCheckNode(ownerId, folderId, INode.TYPE_FOLDER_ALL);
            nodeType = node.getType();
            try
            {
                Locks.SYNCMETADATA_LOCK.tryLock();
                nodeService.deleteNode(userToken, node);
            }
            finally
            {
                Locks.SYNCMETADATA_LOCK.unlock();
            }
            ResponseEntity<String> rsp = new ResponseEntity<String>(HttpStatus.OK);
            
            return rsp;
        }
        catch (BaseRunException t)
        {
            String keyword = StringUtils.trimToEmpty(node.getName());
            String parentId = String.valueOf(node.getParentId());
            String[] logParams = new String[]{String.valueOf(ownerId), parentId};
            UserLogType userLogType = UserLogType.DELETE_FOLDER_ERR;
            if (nodeType == INode.TYPE_FILE)
            {
                userLogType = UserLogType.DELETE_FILE_ERR;
            }
            else if (nodeType == INode.TYPE_VERSION)
            {
                userLogType = UserLogType.DELETE_VERSION_FILE_ERR;
            }
            fileBaseService.sendINodeEvent(userToken,
                EventType.INODE_DELETE,
                null,
                null,
                userLogType,
                logParams,
                keyword);
            throw t;
        }
    }
    
    /**
     * 获取文件夹详情
     * 
     * @param ownerId
     * @param folderId
     * @param token
     * @return
     * @throws BaseRunException
     */
    @RequestMapping(value = "/{folderId}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<?> getFolderInfo(@PathVariable Long ownerId, @PathVariable Long folderId,
        @RequestHeader("Authorization") String token, HttpServletRequest request) throws BaseRunException
    {
        INode inode = null;
        UserToken userToken = null;
        try
        {
            FilesCommonUtils.checkNonNegativeIntegers(ownerId, folderId);
            Map<String, String> headerCustomMap = HeaderPacker.getCustomHeaderMap(request);
            // Token 验证
            if (token.startsWith(UserTokenHelper.LINK_PREFIX))
            {
                String date = request.getHeader("Date");
                userToken = userTokenHelper.getLinkToken(token, date);
                userTokenHelper.assembleUserToken(ownerId, userToken);
            }
            else
            {
                userToken = userTokenHelper.checkTokenAndGetUserForV2(token, headerCustomMap);
            }
            
            // 用户状态校验
            userTokenHelper.checkUserStatus(userToken.getAppId(), ownerId);
            try
            {
                securityMatrixService.checkSecurityMatrix(userToken,
                    ownerId,
                    folderId,
                    SecurityMethod.NODE_INFO,
                    headerCustomMap);
            }
            catch (NoSuchItemsException e)
            {
                throw new NoSuchFolderException(e);
            }
            // 调用service获取目录信息
            inode = folderServiceV2.getFolderInfo(userToken, ownerId, folderId);
            RestFolderInfo folderInfo = new RestFolderInfo(inode);
            return new ResponseEntity<RestFolderInfo>(folderInfo, HttpStatus.OK);
        }
        catch (BaseRunException t)
        {
            if (inode != null)
            {
                String keyword = StringUtils.trimToEmpty(inode.getName());
                
                String[] logParams = new String[]{String.valueOf(ownerId),
                    String.valueOf(inode.getParentId())};
                fileBaseService.sendINodeEvent(userToken,
                    EventType.OTHERS,
                    null,
                    null,
                    UserLogType.GET_FOLDER_ERR,
                    logParams,
                    keyword);
            }
            
            throw t;
        }
    }
    
    /**
     * 根据名称获取子文件夹或文件信息
     * 
     * @param ownerId
     * @param folderId
     * @param request
     * @param token
     * @return
     * @throws BaseRunException
     */
    @RequestMapping(value = "/{folderId}/children", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<RestNodeList> getNodeByName(@PathVariable Long ownerId,
        @PathVariable Long folderId, @RequestBody GetNodeByNameRequest request,
        @RequestHeader("Authorization") String token, HttpServletRequest requestServlet) throws BaseRunException
    {
        UserToken userToken = null;
        try
        {
            FilesCommonUtils.checkNonNegativeIntegers(ownerId, folderId);
            
            // 参数校验
            request.checkParameter();
            
            // Token 验证
            Map<String, String> headerCustomMap = HeaderPacker.getCustomHeaderMap(requestServlet);
            userToken = userTokenHelper.checkTokenAndGetUserForV2(token, headerCustomMap);
            
            // 用户状态校验
            userTokenHelper.checkUserStatus(userToken.getAppId(), ownerId);
            
            securityMatrixService.checkSecurityMatrix(userToken,
                ownerId,
                folderId,
                SecurityMethod.FOLDER_LIST,
                headerCustomMap);
            
            // 父目录状态校验
            INode temp = new INode();
            temp.setOwnedBy(ownerId);
            temp.setParentId(folderId);
            fileBaseService.getAndCheckParentNode(temp);
            
            List<INode> nodeList = nodeService.getNodeByName(userToken, ownerId, folderId, request.getName());
            
            RestNodeList restNodeList = new RestNodeList(nodeList, userToken.getDeviceType());
            return new ResponseEntity<RestNodeList>(restNodeList, HttpStatus.OK);
        }
        catch (BaseRunException t)
        {
            String[] params = new String[]{String.valueOf(ownerId), String.valueOf(folderId)};
            String keyword = StringUtils.trimToEmpty(request.getName());
            fileBaseService.sendINodeEvent(userToken,
                null,
                null,
                null,
                UserLogType.GET_FOLDER_BY_NAME_ERR,
                params,
                keyword);
            throw t;
        }
    }
    
    /**
     * 列举文件夹
     * 
     * @param ownerId 文件夹所有者ID
     * @param folderId 文件夹ID
     * @param listFolderRequest 列举文件夹请求对象
     * @param token 认证token
     * @return
     * @throws BaseRunException
     */
    @RequestMapping(value = "/{folderId}/items", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<RestFolderLists> listFolder(@PathVariable Long ownerId,
        @PathVariable Long folderId, @RequestBody(required = false) ListFolderRequest listFolderRequest,
        @RequestHeader("Authorization") String token, HttpServletRequest request, Locale locale) throws BaseRunException
    {
        UserToken userToken = null;
        INode parentNode = null;
        try
        {
            FilesCommonUtils.checkNonNegativeIntegers(ownerId, folderId);
            if (listFolderRequest != null) {
                listFolderRequest.checkParameter();
            } else {
                listFolderRequest = new ListFolderRequest();
            }
            Map<String, String> headerCustomMap = HeaderPacker.getCustomHeaderMap(request);
            if (token.startsWith(UserTokenHelper.LINK_PREFIX)) {
                String date = request.getHeader("Date");
                userToken = userTokenHelper.getLinkToken(token, date);
                userTokenHelper.assembleUserToken(ownerId, userToken);
            }else {
                userToken = userTokenHelper.checkTokenAndGetUserForV2(token, headerCustomMap);
            }
            // 用户状态校验
            userTokenHelper.checkUserStatus(userToken.getAppId(), ownerId);
            
            securityMatrixService.checkSecurityMatrix(userToken, ownerId, folderId, SecurityMethod.FOLDER_LIST, headerCustomMap);
           
            if(folderId != INode.FILES_ROOT){
            	  try{
            		  parentNode =  folderService.getNodeInfo(userToken, ownerId, folderId);
                  }catch (NoSuchItemsException e)
                  {
                      LOGGER.error("Folder not exists [ " + ownerId + "; " + folderId + " ]");
                      throw new NoSuchFolderException(e);
                  }
            }
            INode filter = listFolderRequest.tran2Filter();
            filter.setStatus(INode.STATUS_NORMAL);
            filter.setOwnedBy(ownerId);
            if(folderId>=0){
            	  filter.setId(folderId);
            }
            FileINodesList reList = folderServiceV2.listNodesByFilter(userToken,filter,listFolderRequest,headerCustomMap);
            
            RestFolderLists folderList = new RestFolderLists(reList, userToken.getDeviceType(),messageSource,locale);
            fillListUserInfo(folderList);
            fillListShortCutInfo(folderList,userToken.getId());
            return new ResponseEntity<RestFolderLists>(folderList, HttpStatus.OK);
        }
        catch (BaseRunException t)
        {
            if (parentNode != null)
            {
                String keyword = StringUtils.trimToEmpty(parentNode.getName());
                String[] logParams = new String[]{String.valueOf(ownerId),
                    String.valueOf(folderId != INode.FILES_ROOT ? parentNode.getParentId() : INode.FILES_ROOT)};
                fileBaseService.sendINodeEvent(userToken,
                    EventType.OTHERS,
                    null,
                    null,
                    UserLogType.LIST_FOLDER_ERR,
                    logParams,
                    keyword);
            }
            throw t;
        }
    }
    
    
    
    
  
    
    
    private void fillListShortCutInfo(RestFolderLists folderList, long createdBy) {
		// TODO Auto-generated method stub
    	List<RestFolderInfo> folderInfoList =folderList.getFolders();
    	for(int i=0;i<folderInfoList.size();i++){
    		try {
    			RestFolderInfo inode = folderInfoList.get(i);
        	    Shortcut shortcut = new Shortcut();
        	    shortcut.setNodeId(inode.getId());
        	    shortcut.setOwnerId(inode.getOwnedBy());
        	    shortcut.setCreateBy(createdBy);;
        	    Shortcut dbShortcut = shortcutDao.getByOwnerIdAndNodeId(shortcut);
        	    if(dbShortcut!=null){
        	    	folderInfoList.get(i).setIsShortcut(true);
        	    }else{
        	    	folderInfoList.get(i).setIsShortcut(false);
        	    }
    		} catch (Exception e) {
    			// TODO: handle exception
    		}
    	}
    	
		
	}












	/**
     * 为外链列举文件夹
     * 
     * @param ownerId 文件夹所有者ID
     * @param folderId 文件夹ID
     * @param listFolderRequest 列举文件夹请求对象
     * @param token 认证token
     * @return
     * @throws BaseRunException
     */
    @RequestMapping(value = "/{folderId}/link/{linkCode}", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<RestFolderLists> listFolderForlink(@PathVariable Long ownerId,@PathVariable String linkCode,
        @PathVariable Long folderId, @RequestBody(required = false) ListFolderRequest listFolderRequest,
        @RequestHeader("Authorization") String token, HttpServletRequest request) throws BaseRunException
    {
        // Token 验证
        Map<String, String> headerCustomMap = HeaderPacker.getCustomHeaderMap(request);
        UserToken userToken = userTokenHelper.checkTokenAndGetUserForV2(token, headerCustomMap);
        try
        {
            // 用户状态校验
            userTokenHelper.assembleUserToken(ownerId, userToken);
            userTokenHelper.checkUserStatus(userToken.getAppId(), ownerId);
            FilesCommonUtils.checkNonNegativeIntegers(ownerId, folderId);
            if (listFolderRequest != null){
                listFolderRequest.checkParameter();
            }else{
                listFolderRequest = new ListFolderRequest();
            }
            String dateStr = DateUtils.dataToString(DateUtils.RFC822_DATE_FORMAT, new Date(), null);
            UserToken userlinkToken = new UserToken();
            userlinkToken.setLinkCode(linkCode);
    		String authStr = "link," + linkCode + ',' + SignatureUtils.getSignature(null, dateStr);
    		userlinkToken.setToken(authStr);
    	
            INode filter=new INode();
            filter.setOwnedBy(ownerId);
            filter.setParentId(folderId);
            filter.setStatus(INode.STATUS_NORMAL);
            
            FileINodesList reList = folderServiceV2.listNodesByFilter(userToken,filter,listFolderRequest,headerCustomMap);
            RestFolderLists folderList = new RestFolderLists(reList, userToken.getDeviceType(),messageSource,request.getLocale());
            fillListUserInfo(folderList);
            return new ResponseEntity<RestFolderLists>(folderList, HttpStatus.OK);
        }
        catch (BaseRunException t)
        {
            throw t;
        }
    }  
    
    
  
    
    /**
     * 列举最近浏览的文件
     * @param ownerId 文件夹所有者ID
     * @param folderId 文件夹ID
     */
    @RequestMapping(value = "/recent", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<RestFolderLists> listFolderForRecent(@PathVariable Long ownerId,@RequestBody ListFolderRequest re,
        @RequestHeader("Authorization") String token, HttpServletRequest request) throws BaseRunException
    {
        UserToken userToken = null;
        try
        {
            
            Map<String, String> headerCustomMap = HeaderPacker.getCustomHeaderMap(request);
            userToken = userTokenHelper.checkTokenAndGetUserForV2(token, headerCustomMap);
            
            // 用户状态校验
            userTokenHelper.checkUserStatus(userToken.getAppId(), ownerId);
            int offset = re.getOffset().intValue();
            int limit = re.getLimit();
            List<Thumbnail> thumbnailList= re.getThumbnail();
            List<INode> fileList = folderServiceV2.listFolderForRecent(userToken,offset,limit,thumbnailList,headerCustomMap);
            FileINodesList fileINodesList=new FileINodesList();
            fileINodesList.setFiles(fileList);
            RestFolderLists folderList = new RestFolderLists(fileINodesList , userToken.getDeviceType(),messageSource,request.getLocale());
            fillListUserInfo(folderList);
            return new ResponseEntity<RestFolderLists>(folderList,HttpStatus.OK);
        }
        catch (BaseRunException t)
        {
            
            throw t;
        }
    }  
    /**
     * 删除最近浏览的文件
     * 
     */
    @RequestMapping(value = "/recent/deleteByNode/{nodeId}", method = RequestMethod.DELETE)
    @ResponseBody
    public ResponseEntity<?> deleteRecentByNode(@PathVariable Long ownerId,@PathVariable Long nodeId,
        @RequestHeader("Authorization") String token, HttpServletRequest request) throws BaseRunException
    {
        UserToken userToken = null;
        try
        {
            
            Map<String, String> headerCustomMap = HeaderPacker.getCustomHeaderMap(request);
            userToken = userTokenHelper.checkTokenAndGetUserForV2(token, headerCustomMap);
            userTokenHelper.checkUserStatus(userToken.getAppId(), ownerId);
            folderServiceV2.deleteRecentByNode(ownerId,nodeId);
            folderServiceV2.deleteShortByNodeId(ownerId,nodeId);
            return new ResponseEntity<>(HttpStatus.OK);
        }
        catch (BaseRunException t)
        {
            
            throw t;
        }
    }  
    
    /**
     * 删除最近浏览的文件
     * 
     */
    @RequestMapping(value = "/recent/delete/{nodeId}", method = RequestMethod.DELETE)
    @ResponseBody
    public ResponseEntity<?> deleteRecent(@PathVariable Long ownerId,@PathVariable Long nodeId,
        @RequestHeader("Authorization") String token, HttpServletRequest request) throws BaseRunException
    {
        UserToken userToken = null;
        try
        {
            
            Map<String, String> headerCustomMap = HeaderPacker.getCustomHeaderMap(request);
            userToken = userTokenHelper.checkTokenAndGetUserForV2(token, headerCustomMap);
            userTokenHelper.checkUserStatus(userToken.getAppId(), ownerId);
            folderServiceV2.deleteRecent(userToken.getId(),ownerId,nodeId);
            folderServiceV2.deleteShortByNodeId(ownerId,nodeId);
            return new ResponseEntity<>(HttpStatus.OK);
        }
        catch (BaseRunException t)
        {
            
            throw t;
        }
    }  
    
    
    /**
     * 创建最近浏览记录
     * 
     * @throws BaseRunException
     */
    @RequestMapping(value = "/recent/create/{nodeId}", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<?> createRecent(@PathVariable Long ownerId,@PathVariable Long nodeId,
        @RequestHeader("Authorization") String token, HttpServletRequest request) throws BaseRunException
    {
        UserToken userToken = null;
        try
        {
            
            Map<String, String> headerCustomMap = HeaderPacker.getCustomHeaderMap(request);
            userToken = userTokenHelper.checkTokenAndGetUserForV2(token, headerCustomMap);
            userTokenHelper.checkUserStatus(userToken.getAppId(), ownerId);
            folderServiceV2.createRecent(userToken.getId() ,ownerId, nodeId);
            return new ResponseEntity<>(HttpStatus.OK);
        }
        catch (BaseRunException t)
        {
            
            throw t;
        }
    }  
    
    
    
    
    @RequestMapping(value = "/{folderId}/move", method = RequestMethod.PUT)
    @ResponseBody
    public ResponseEntity<RestFolderInfo> moveFolder(@PathVariable Long ownerId, @PathVariable Long folderId,
        @RequestBody NodeMoveRequest request, @RequestHeader("Authorization") String token,
        HttpServletRequest requestServlet)
        throws BaseRunException
    {
        UserToken userToken = null;
        INode srcNode = null;
        try
        {
            FilesCommonUtils.checkNonNegativeIntegers(ownerId, folderId);
            request.checkParameter();
            
            // Token 验证
            Map<String, String> headerCustomMap = HeaderPacker.getCustomHeaderMap(requestServlet);
            userToken = userTokenHelper.checkTokenAndGetUserForV2(token, headerCustomMap);
            
            // 用户状态校验
            userTokenHelper.checkUserStatus(userToken.getAppId(), ownerId);
            userTokenHelper.checkUserStatus(userToken.getAppId(), request.getDestOwnerId());
            boolean isAutoRename = request.isAutoRename();
            srcNode = new INode(ownerId, folderId);
            INode destNode = new INode(request.getDestOwnerId(), request.getDestParent());
            INode resultNode = null;
            if (isAutoRename)
            {
                srcNode = fileBaseService.getINodeInfo(ownerId, folderId);
                if (srcNode == null)
                {
                    throw new NoSuchSourceException();
                }
                String newName = srcNode.getName();
                int renameNumber = 1;
                while (true)
                {
                    try
                    {
                        resultNode = folderService.moveNodeToFolderCheckType(userToken,
                            srcNode,
                            destNode,
                            newName,
                            INode.TYPE_FOLDER_ALL);
                        break;
                    }
                    catch (FilesNameConflictException e)
                    {
                        newName = FilesCommonUtils.getNewName(INode.TYPE_FOLDER_ALL, newName, renameNumber);
                        renameNumber++;
                        continue;
                    }
                }
            }
            else
            {
                resultNode = folderService.moveNodeToFolderCheckType(userToken,
                    srcNode,
                    destNode,
                    null,
                    INode.TYPE_FOLDER_ALL);
            }
            
            FilesCommonUtils.setNodeVersionsForV2(resultNode);
            RestFolderInfo folderInfo = new RestFolderInfo(resultNode);
            return new ResponseEntity<RestFolderInfo>(folderInfo, HttpStatus.OK);
        }
        catch (BaseRunException t)
        {
            String keyword = null;
            if (srcNode != null)
            {
                keyword = StringUtils.trimToEmpty(srcNode.getName());
            }
            String[] logParams = new String[]{String.valueOf(ownerId != null ? ownerId : ""),
                String.valueOf(folderId != null ? folderId : ""),
                String.valueOf(request != null ? request.getDestOwnerId() : ""),
                String.valueOf(request != null ? request.getDestParent() : "")};
            fileBaseService.sendINodeEvent(userToken,
                EventType.OTHERS,
                null,
                null,
                UserLogType.MOVE_FOLDER_ERR,
                logParams,
                keyword);
            throw t;
        }
        
    }
    
    /**
     * 重命名并设置同步状态
     * 
     * @param ownerId
     * @param folderId
     * @param request
     * @param token
     * @return
     * @throws BaseRunException
     */
    @RequestMapping(value = "/{folderId}", method = RequestMethod.PUT)
    @ResponseBody
    public ResponseEntity<RestFolderInfo> renameAndSetSyncStatus(@PathVariable Long ownerId,
        @PathVariable Long folderId, @RequestBody RenameAndSetSyncRequest request,
        @RequestHeader("Authorization") String token, HttpServletRequest requestServlet) throws BaseRunException
    {
        UserToken userToken = new UserToken();
        INode node = new INode();
        try
        {
            FilesCommonUtils.checkNonNegativeIntegers(ownerId, folderId);
            request.checkParameter();
            
            // Token 验证
            Map<String, String> headerCustomMap = HeaderPacker.getCustomHeaderMap(requestServlet);
            userToken = userTokenHelper.checkTokenAndGetUserForV2(token, headerCustomMap);
            
            // 用户状态校验
            userTokenHelper.checkUserStatus(userToken.getAppId(), ownerId);
            
            securityMatrixService.checkSecurityMatrix(userToken,
                ownerId,
                folderId,
                SecurityMethod.NODE_RENAME,
                headerCustomMap);
            
            node = fileBaseService.getAndCheckNode(ownerId, folderId, INode.TYPE_FOLDER_ALL);
            if (StringUtils.isNotEmpty(request.getExtraType())
                && request.getExtraType().equals(INode.TYPE_BACKUP_EMAIL_STR))
            {
                node.setType(INode.TYPE_BACKUP_EMAIL);
                node.setSyncStatus(INode.SYNC_STATUS_EMAIL);
            }
            node = nodeService.renameAndSetSyncStatus(userToken,
                ownerId,
                node,
                request.getName(),
                request.getSyncStatus(),
                node.getType());
            RestFolderInfo folderInfo = new RestFolderInfo(node);
            return new ResponseEntity<RestFolderInfo>(folderInfo, HttpStatus.OK);
        }
        catch (BaseRunException t)
        {
            String[] logParams = new String[]{String.valueOf(ownerId),
                (node != null ? String.valueOf(node.getParentId()) : null),
                (node != null ? String.valueOf(node.getSyncStatus()) : null)};
            String keyword = StringUtils.trimToEmpty(request.getName());
            fileBaseService.sendINodeEvent(userToken,
                EventType.OTHERS,
                null,
                null,
                UserLogType.UPDATE_FOLDER_NAME_SYNC_ERR,
                logParams,
                keyword);
            throw t;
        }
    }
    
    
    
    private INode doCopy(boolean isAutoRename, UserToken userToken, INode srcNode, INode destParent,
        String srcName)
    {
        INode resultNode = null;
        int renameNumber = 1;
        String newName = srcName;
        do
        {
            try
            {
                resultNode = folderService.copyNodeToFolderCheckType(userToken,
                    srcNode,
                    destParent,
                    newName,
                    INode.TYPE_FOLDER_ALL,
                    false);
                return resultNode;
            }
            catch (FilesNameConflictException e)
            {
                if (isAutoRename)
                {
                    newName = FilesCommonUtils.getNewName(srcNode.getType(), srcName, renameNumber);
                    renameNumber++;
                    continue;
                }
                throw e;
            }
        } while (true);
    }
    
    
    private INode doCopy(boolean isAutoRename, UserToken userToken, INode srcNode, INode destParent,
        String srcName,INodeLink link)
    {
        INode resultNode = null;
        int renameNumber = 1;
        String newName = srcName;
        do
        {
            try
            {
                resultNode = folderService.copyNodeToFolderCheckType(userToken,
                    srcNode,
                    destParent,
                    newName,
                    INode.TYPE_FOLDER_ALL,
                    false,link);
                return resultNode;
            }
            catch (FilesNameConflictException e)
            {
                if (isAutoRename)
                {
                    newName = FilesCommonUtils.getNewName(srcNode.getType(), srcName, renameNumber);
                    renameNumber++;
                    continue;
                }
                throw e;
            }
        } while (true);
    }
    
    
    /**
     * 填充外链用户的身份信息
     * 
     * @param restReq
     * @param userToken
     * @throws BaseRunException
     */
    private void fillLinkUser(HttpServletRequest request, NodeCopyRequest restReq, UserToken userToken)
        throws BaseRunException
    {
        if (null != restReq.getLink())
        {
            userToken.setLinkCode(restReq.getLink().getLinkCode());
            if (StringUtils.isNotEmpty(restReq.getLink().getLinkCode()))
            {
                INodeLink nodeLink = null;
                String authorization = UserTokenHelper.LINK_PREFIX + restReq.getLink().getLinkCode() + ','
                    + restReq.getLink().getPlainAccessCode();
                try
                {
                    String dateStr = request.getHeader("Date");
                    nodeLink = userTokenHelper.checkLinkToken(authorization, dateStr);
                    userToken.setPlainAccessCode(nodeLink.getPlainAccessCode());
                    userToken.setDate(request.getHeader("Date"));
                }
                catch (Exception e)
                {
                    throw new ForbiddenException(e);
                }
            }
            
        }
    }
    
    
    /**
     * 
     * web端访问，返回文件分类查询接口
     * 
     * <参数类型> @param request <参数类型> @param ownerId <参数类型> @param doctype <参数类型> @param
     * token <参数类型> @param listFolderRequest <参数类型> @return <参数类型> @throws
     * BaseRunException
     * 
     * @return ResponseEntity<RestFolderLists>
     */
    @RequestMapping(value = "/{doctype}/doctype/web/search" , method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity <RestFolderLists> searchINodesByDoctype(HttpServletRequest request ,
              @PathVariable Long ownerId , @PathVariable Integer doctype ,
              @RequestHeader("Authorization") String token ,
              @RequestBody(required = false) ListFolderRequest searchRequest) throws BaseRunException
    {
         UserToken userToken = null;
         INode node = null;
         Long folderId = INode.FILES_ROOT;

         try
         {
              FilesCommonUtils.checkNonNegativeIntegers(ownerId , folderId);
              if (searchRequest != null)
              {
                   searchRequest.checkParameter();
              }
              else
              {
                   searchRequest = new ListFolderRequest();
              }

              ResponseEntity <RestFolderLists> re = getINodeByDoctypeCommon(request , ownerId , doctype , token ,
                        userToken , node , folderId , searchRequest);

              return re;

         }
         catch (Exception t)
         {
              LOGGER.error("ufm FolderApi.getINodesByDoctype web error: " , t);
              throw t;
         }

    }

     /**
      * 
      * web端访问，返回文件分类接口
      * 
      * <参数类型> @param request <参数类型> @param ownerId <参数类型> @param doctype <参数类型> @param
      * token <参数类型> @param listFolderRequest <参数类型> @return <参数类型> @throws
      * BaseRunException
      * 
      * @return ResponseEntity<RestFolderLists>
      */
     @RequestMapping(value = "/{doctype}/doctype/web/list" , method = RequestMethod.POST)
     @ResponseBody
     public ResponseEntity <RestFolderLists> getINodesByDoctype(HttpServletRequest request ,
               @PathVariable Long ownerId , @PathVariable Integer doctype ,
               @RequestHeader("Authorization") String token ,
               @RequestBody(required = false) ListFolderRequest listFolderRequest) throws BaseRunException
     {
          UserToken userToken = null;
          INode node = null;
          Long folderId = INode.FILES_ROOT;

          try
          {
               FilesCommonUtils.checkNonNegativeIntegers(ownerId , folderId);
               if (listFolderRequest != null)
               {
                    listFolderRequest.checkParameter();
               }
               else
               {
                    listFolderRequest = new ListFolderRequest();
               }

               ResponseEntity <RestFolderLists> re = getINodeByDoctypeCommon(request , ownerId , doctype , token ,
                         userToken , node , folderId , listFolderRequest);

               return re;

          }
          catch (Exception t)
          {
               LOGGER.error("ufm FolderApi.getINodesByDoctype web error: " , t);
               throw t;
          }

     }
     
     private void fillPathInfo(RestFolderLists nodeList)
     {
         List<RestFolderInfo> folderList = nodeList.getFolders();
         List<RestFileInfo> fileList = nodeList.getFiles();
         List<RestBaseObject> pathList = null;
         for(RestFolderInfo tempFolder: folderList)
         {
             pathList = this.nodeManager.getNodePath(tempFolder.getOwnedBy(), tempFolder.getId());
             tempFolder.setPath(pathList);
         }
         for(RestFileInfo tempFile: fileList)
         {
             pathList = this.nodeManager.getNodePath(tempFile.getOwnedBy(), tempFile.getId());
             tempFile.setPath(pathList);
         }
     }


     /**
      * 
      * pc端访问，返回文件分类接口
      * 
      * <参数类型> @param request <参数类型> @param ownerId <参数类型> @param doctype <参数类型> @param
      * jsonStr <参数类型> @param token <参数类型> @return <参数类型> @throws
      * BaseRunException
      * 
      * @return ResponseEntity<RestFolderLists>
      */
     @SuppressWarnings({ "unchecked" })
     @RequestMapping(value = "/{doctype}/doctype/list" , method = RequestMethod.POST)
     @ResponseBody
     public ResponseEntity <RestFolderLists> getINodesByDoctype(HttpServletRequest request ,
               @PathVariable Long ownerId , @PathVariable Integer doctype ,
               @RequestBody(required = false) String jsonStr , @RequestHeader("Authorization") String token)
               throws Exception
     {
          UserToken userToken = null;
          INode node = null;
          Long folderId = INode.FILES_ROOT;
          Map <String , Object> map;
          try
          {
               map = (Map <String , Object>) new ObjectMapper().readValue(jsonStr , Map.class);
          }
          catch (JsonParseException e)
          {
               LOGGER.error("ufm FolderApi.getINodesByDoctype error: " , e);
               throw e;
          }
          catch (JsonMappingException e)
          {
               LOGGER.error("ufm FolderApi.getINodesByDoctype error: " , e);
               throw e;
          }
          catch (IOException e)
          {
               LOGGER.error("ufm FolderApi.getINodesByDoctype error: " , e);
               throw e;
          }

          Object pageNumber = map.get("pageNumber");
          if (null == pageNumber)
          {
               pageNumber = 1;
          }
          Object pageSize = map.get("pageSize");
          if (null == pageSize)
          {
               pageSize = 40;
          }
          Object orderField = map.get("orderField");
          if (null == orderField)
          {
               orderField = "modifiedAt";
          }
          Object desc = map.get("desc");
          if (null == desc)
          {
               desc = true;
          }
          try
          {
               FilesCommonUtils.checkNonNegativeIntegers(ownerId , folderId);

               long offset = 0L;
               if ((Integer) pageNumber > 0)
               {
                    offset = (long) ((Integer) pageNumber - 1) * (Integer) pageSize;
               }
               ListFolderRequest listFolderRequest = generalRequest(orderField.toString() , (boolean) desc , offset ,
                         (Integer) pageSize);

               ResponseEntity <RestFolderLists> re = getINodeByDoctypeCommon(request , ownerId , doctype , token ,
                         userToken , node , folderId , listFolderRequest);
               return re;
          }
          catch (Exception t)
          {
               LOGGER.error("ufm FolderApi.getINodesByDoctype pc error: " , t);
               throw t;
          }
     }


     /**
      * 
      * 以json格式返回doctype.json文件内容
      * 
      * <参数类型> @return <参数类型> @throws Exception
      * 
      * @return String
      */
     @RequestMapping(value = "/doctype/items" , method = RequestMethod.POST)
     @ResponseBody
     public ResponseEntity <String> getJsonDoctype(@PathVariable Long ownerId ,
               @RequestHeader("Authorization") String token , HttpServletRequest request) throws Exception
     {
          UserToken userToken = null;
          try
          {
               // uam访问不需要验证
               if(ownerId != -1) {
                    if (token.startsWith(UserTokenHelper.LINK_PREFIX))
                    {
                         String date = request.getHeader("Date");
                         userToken = userTokenHelper.getLinkToken(token , date);
                         assembleUserToken(ownerId , userToken);
                    }
                    else
                    {
                    	Map<String, String> headerCustomMap = HeaderPacker.getCustomHeaderMap(request);
                         userToken = userTokenHelper.checkTokenAndGetUserForV2(token, headerCustomMap);
                    } 
                    // 用户状态校验
                    userTokenHelper.checkUserStatus(userToken.getAppId() , ownerId);
               }
               
                return new ResponseEntity <String>(folderServiceV2.getJsonDoctype() , HttpStatus.OK);
          }
          catch (Exception t)
          {
               LOGGER.error("ufm[openapi].FolderApi.getJsonDoctype error: " , t);
               throw t;
          }
     }


     @Autowired
     DoctypeManager doctypeManager;
//     /**
//      * 文件智能分类
//      * @param ownerId
//      * @param token
//      * @param requestMap
//      * @param request
//      * @return
//      * @throws Exception
//      */
//     @Deprecated
//     @RequestMapping(value = "/doctype/fileSplit" , method = RequestMethod.POST)
//     @ResponseBody
//     public ResponseEntity <String> fileSplit(@PathVariable Long ownerId ,
//               @RequestHeader("Authorization") String token , @RequestBody  String json ,
//               HttpServletRequest request) throws Exception
//     {
//          UserToken userToken = null;
//          try
//          {
//               // uam访问不需要验证
//               if (ownerId != -1)
//               {
//                    if (token.startsWith(UserTokenHelper.LINK_PREFIX))
//                    {
//                         String date = request.getHeader("Date");
//                         userToken = userTokenHelper.getLinkToken(token , date);
//                         assembleUserToken(ownerId , userToken);
//                    }
//                    else
//                    {
//                    	Map<String, String> headerCustomMap = HeaderPacker.getCustomHeaderMap(request);
//                         userToken = userTokenHelper.checkTokenAndGetUserForV2(token,headerCustomMap);
//                    }
//                    // 用户状态校验
//                    userTokenHelper.checkUserStatus(userToken.getAppId() , ownerId);
//               }
//              
//              Map <String , Map <String , Object>> requestMap = doctypeManager.getdoctypeByJsonNode(new ObjectMapper().readTree(json));
//              folderServiceV2.updateINodeDoctype(requestMap);
//               
//
//               // 数据库更新成功再更新json文件
//               // Map <String , Map <String , Object>> alldoctypeMap = doctypeManager.getdoctypeAll();
//               //  for (String key : requestMap.keySet())
//               // {
//               //      alldoctypeMap.get(key).put(doctypes.extensions.name() , requestMap.get(key));
//               // }
//               //new ObjectMapper().writeValue(new PropertiesConfiguration(doctypeManager.jsonFile).getFile() ,alldoctypeMap);
//               // 改用数据库
//              doctypeManager.saveOrUpdate(requestMap);
//                
//               return new ResponseEntity <String>("success" , HttpStatus.OK);
//          }
//          catch (Exception e)
//          {
//               LOGGER.error("ufm[openapi].FolderApi.fileSplit error: " , e);
//               throw e;
//          }
//     }
     
     
     private ListFolderRequest generalRequest(String orderField , boolean desc , long offset , int limit)
     {
          ListFolderRequest listFolderRequest = new ListFolderRequest(limit , offset);
          Thumbnail smallThumb = new Thumbnail(Thumbnail.DEFAULT_SMALL_WIDTH , Thumbnail.DEFAULT_SMALL_HEIGHT);
          Thumbnail bigThumb = new Thumbnail(Thumbnail.DEFAULT_BIG_WIDTH , Thumbnail.DEFAULT_BIG_HEIGHT);
          listFolderRequest.addThumbnail(smallThumb);
          listFolderRequest.addThumbnail(bigThumb);
          Order orderByType = new Order("TYPE" , "ASC");
          Order orderByField = new Order(orderField , desc ? "DESC" : "ASC");
          listFolderRequest.addOrder(orderByType);
          listFolderRequest.addOrder(orderByField);
          return listFolderRequest;
     }


     
     
     /*
      * 公用部分
      * SearchRequest 方式
      */
      private ResponseEntity <RestFolderLists> getINodeByDoctypeCommon(HttpServletRequest request , Long ownerId ,
               Integer doctype , String token , UserToken userToken , INode node , Long folderId ,
               ListFolderRequest requestObj) throws BaseRunException
     {
          try
          {
        	  Map<String, String> headerCustomMap = HeaderPacker.getCustomHeaderMap(request);

               if (token.startsWith(UserTokenHelper.LINK_PREFIX))
               {
                    String date = request.getHeader("Date");
                    userToken = userTokenHelper.getLinkToken(token , date);
                    assembleUserToken(ownerId , userToken);
               }
               else
               {
                    userToken = userTokenHelper.checkTokenAndGetUserForV2(token,headerCustomMap);
               }

               // 用户状态校验
               userTokenHelper.checkUserStatus(userToken.getAppId() , ownerId);

               securityMatrixService.checkSecurityMatrix(userToken , ownerId , folderId , SecurityMethod.FOLDER_LIST,headerCustomMap);

               node = new INode();
               node.setOwnedBy(ownerId);
               node.setStatus(INode.STATUS_NORMAL);
               node.setDoctype(doctype);
               node.setName(requestObj.getName());
               node.setFilelabelIds(requestObj.getLabelIds());
               
//               FileINodesList reList = folderServiceV2.listNodesByFilter(userToken , node , offset , limit , orderList ,
//                         thumbnailList,requestObj.getWithExtraType(),headerCustomMap);
//               
               FileINodesList reList = folderServiceV2.listNodesByFilter(userToken,node,requestObj,headerCustomMap);
               RestFolderLists folderList = new RestFolderLists(reList , userToken.getDeviceType(),messageSource,request.getLocale());
               fillListUserInfo(folderList);
               return new ResponseEntity <RestFolderLists>(folderList , HttpStatus.OK);
          }
          catch (Exception t)
          {
               String keyword = null;
               String parentId = null;
               if (node != null)
               {
                    keyword = StringUtils.trimToEmpty(node.getName());
                    parentId = String.valueOf(folderId != INode.FILES_ROOT ? node.getParentId() : INode.FILES_ROOT);
               }
               String[] logParams = new String[]
               { String.valueOf(ownerId) , parentId };
               fileBaseService.sendINodeEvent(userToken , EventType.OTHERS , null , null , UserLogType.LIST_FOLDER_ERR ,
                         logParams , keyword);
               throw t;
          }
     }
      
      /**
       * 根据doctype名称搜索文件/文件夹(模糊匹配)
       * 
       * @param ownerId
       * @param request
       * @param token
       * @return
       * @throws BaseRunException
       */
      @RequestMapping(value = "/searchDoctype", method = RequestMethod.POST)
      @ResponseBody
      public ResponseEntity<?> searchDoctype(HttpServletRequest httprequest ,@PathVariable Long ownerId, @RequestBody ListFolderRequest request,
          @RequestHeader("Authorization") String token) throws BaseRunException
      {
          UserToken userToken = null;
          try
          {
        	  Map<String, String> headerCustomMap = HeaderPacker.getCustomHeaderMap(httprequest);
              FilesCommonUtils.checkNonNegativeIntegers(ownerId);
              request.checkParameter();
              
              // Token 验证
              userToken = userTokenHelper.checkTokenAndGetUserForV2(token,headerCustomMap);
              
              // 用户状态校验
              userTokenHelper.checkUserStatus(userToken.getAppId(), ownerId);
              
              securityMatrixService.checkSecurityMatrix(userToken, ownerId, null, SecurityMethod.FOLDER_LIST,headerCustomMap);
              List<Order> orderList = request.getOrder();
              List<Thumbnail> thumbnailList = request.getThumbnail();
              String name = request.getName();
              
              int limit = request.getLimit();
              long offset = request.getOffset();
              
              FileINodesList nodeList = nodeService.search(userToken,
                  ownerId,
                  name,
                  offset,
                  limit,
                  orderList,
                  thumbnailList,request.getWithExtraType());
              RestFolderLists folderList = new RestFolderLists(nodeList, userToken.getDeviceType(),messageSource,httprequest.getLocale());
              if (folderList.getFiles() != null && !folderList.getFiles().isEmpty())
              {
                  List<RestFileInfo> restFileInfos = folderList.getFiles();
                  for (RestFileInfo restFileInfo : restFileInfos)
                  {
                      transThumbnailUrlList(restFileInfo);
                  }
              }
              
              fillListUserInfo(folderList);
              if(request.getWithPath())
              {
                  fillPathInfo(folderList);
              }
              return new ResponseEntity<RestFolderLists>(folderList, HttpStatus.OK);
          }
          catch (Exception t)
          {
              // TODO parentID
              String[] logParams = new String[]{String.valueOf(ownerId),};
              String keyword = StringUtils.trimToEmpty(request.getName());
              fileBaseService.sendINodeEvent(userToken,
                  EventType.OTHERS,
                  null,
                  null,
                  UserLogType.SEARCH_NODE_LISTS_ERR,
                  logParams,
                  keyword);
              throw t;
          }
      }
      
      private void transThumbnailUrlList(RestFileInfo restFileInfo)
      {
          if (restFileInfo == null || restFileInfo.getThumbnailUrlList() == null)
          {
              return;
          }
          if (restFileInfo.getThumbnailUrlList().isEmpty())
          {
              restFileInfo.setThumbnailUrlList(null);
          }
      }
      
      /**
       * 
       * 外链鉴权，导致用户业务日志查询不完全
       * 
       * @param ownerId
       * @param userToken
       */
      private void assembleUserToken(Long ownerId, UserToken userToken)
      {
          if (userToken == null)
          {
              return;
          }
          try
          {
              User user = userService.get(null, ownerId);
              if (user != null)
              {
                  userToken.setAppId(user.getAppId());
              }
          }
          catch (Exception e)
          {
              LOGGER.error("", e);
          }
      }
      
      
      
      
      /**
       * 列举快捷目录
       * @return
       * @throws BaseRunException
       */
      @RequestMapping(value = "/shortcut/list", method = RequestMethod.POST)
      @ResponseBody
      public ResponseEntity<List<Shortcut>> listFolderForShortcut(@PathVariable Long ownerId,@RequestHeader("Authorization") String token,
    		  HttpServletRequest request) throws BaseRunException
      {
          UserToken userToken = null;
          try
          {
              
              Map<String, String> headerCustomMap = HeaderPacker.getCustomHeaderMap(request);
              userToken = userTokenHelper.checkTokenAndGetUserForV2(token, headerCustomMap);
              userTokenHelper.checkUserStatus(userToken.getAppId(), ownerId);
              List<Shortcut> shortcutList = folderServiceV2.listFolderForShortcut(userToken);
              return new ResponseEntity<List<Shortcut>>(shortcutList,HttpStatus.OK);
          }
          catch (BaseRunException t)
          {
              
              throw t;
          }
      }  
      
      /**
       * 删除快捷目录
       * @return
       * @throws BaseRunException
       */
      @RequestMapping(value = "/shortcut/{id}", method = RequestMethod.DELETE)
      @ResponseBody
      public ResponseEntity<?> deleteShortcut(@PathVariable Long ownerId,@PathVariable long id,
          @RequestHeader("Authorization") String token, HttpServletRequest request) throws BaseRunException
      {
          UserToken userToken = null;
          try
          {
              Map<String, String> headerCustomMap = HeaderPacker.getCustomHeaderMap(request);
              userToken = userTokenHelper.checkTokenAndGetUserForV2(token, headerCustomMap);
//              userTokenHelper.checkUserStatus(userToken.getAppId(), ownerId);
              folderServiceV2.deleteShortcut(id);
              return new ResponseEntity<>(HttpStatus.OK);
          }
          catch (BaseRunException t)
          {
              throw t;
          }
      }  
      
      /**
       * 删除快捷目录
       * @return
       * @throws BaseRunException
       */
      @RequestMapping(value = "/shortcut/delete", method = RequestMethod.DELETE)
      @ResponseBody
      public ResponseEntity<?> deleteShortcutBy(@PathVariable Long ownerId, @RequestBody Shortcut shortcut,
          @RequestHeader("Authorization") String token, HttpServletRequest request) throws BaseRunException
      {
          UserToken userToken = null;
          try
          {
              Map<String, String> headerCustomMap = HeaderPacker.getCustomHeaderMap(request);
              userToken = userTokenHelper.checkTokenAndGetUserForV2(token, headerCustomMap);
              shortcutDao.getByOwnerIdAndNodeId(shortcut);
              return new ResponseEntity<>(HttpStatus.OK);
          }
          catch (BaseRunException t)
          {
              throw t;
          }
      }  
      
    
      /**
       * 创建快捷目录
       * @return
       * @throws BaseRunException
       */
      @RequestMapping(value = "/shortcut/create", method = RequestMethod.POST)
      @ResponseBody
      public ResponseEntity<String> createShortcut(@PathVariable Long ownerId,@RequestBody Shortcut shortcut,
          @RequestHeader("Authorization") String token, HttpServletRequest request) throws BaseRunException
      {
          UserToken userToken = null;
          try
          {
              Map<String, String> headerCustomMap = HeaderPacker.getCustomHeaderMap(request);
              userToken = userTokenHelper.checkTokenAndGetUserForV2(token, headerCustomMap);
              userTokenHelper.checkUserStatus(userToken.getAppId(), ownerId);
              folderServiceV2.createShortcut(shortcut);
              return new ResponseEntity<>(HttpStatus.OK);
          }
          catch (BaseRunException t)
          {
        	throw t;
          }  
      }
      
 
      private INode doCopyFile(boolean isAutoRename, UserToken userToken, INode srcNode, INode destParent,
  	        String srcName,INodeLink link) throws FilesNameConflictException
  	    {
  	        INode resultNode = null;
  	        String newName = srcName;
  	        int renameNumber = 1;
  	        do
  	        {
  	            try
  	            {
  	                resultNode = folderService.copyNodeToFolderCheckType(userToken,
  	                    srcNode,
  	                    destParent,
  	                    newName,
  	                    INode.TYPE_FILE,
  	                    false,link);
  	                return resultNode;
  	            }
  	            catch (FilesNameConflictException e)
  	            {
  	                if (isAutoRename)
  	                {
  	                    newName = FilesCommonUtils.getNewName(INode.TYPE_FILE, srcName, renameNumber);
  	                    renameNumber++;
  	                    continue;
  	                }
  	                throw e;
  	            }
  	        } while (true);
  	    }
}
