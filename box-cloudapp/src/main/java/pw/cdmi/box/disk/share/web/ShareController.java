package pw.cdmi.box.disk.share.web;

import blade.kit.json.JSONArray;
import blade.kit.json.JSONObject;
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
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.util.HtmlUtils;
import pw.cdmi.box.disk.accountrole.domain.PageNodeRoleInfo;
import pw.cdmi.box.disk.accountrole.service.AccountRoleService;
import pw.cdmi.box.disk.client.api.MailMsgClient;
import pw.cdmi.box.disk.client.api.UserClient;
import pw.cdmi.box.disk.client.domain.mailmsg.MailMsg;
import pw.cdmi.box.disk.client.domain.node.INode;
import pw.cdmi.box.disk.files.service.FileService;
import pw.cdmi.box.disk.files.service.FolderService;
import pw.cdmi.box.disk.files.web.CommonController;
import pw.cdmi.box.disk.group.domain.GroupConstants;
import pw.cdmi.box.disk.group.domain.RestGroup;
import pw.cdmi.box.disk.group.domain.RestGroupMemberOrderRequest;
import pw.cdmi.box.disk.group.service.GroupMemberService;
import pw.cdmi.box.disk.httpclient.rest.common.Constants;
import pw.cdmi.box.disk.httpclient.rest.response.CollectUserResponse;
import pw.cdmi.box.disk.httpclient.rest.response.MultiUserSearchResponse;
import pw.cdmi.box.disk.oauth2.domain.UserToken;
import pw.cdmi.box.disk.share.domain.INodeShare;
import pw.cdmi.box.disk.share.domain.INodeShareV2;
import pw.cdmi.box.disk.share.domain.RestMultiUser;
import pw.cdmi.box.disk.share.service.ShareService;
import pw.cdmi.box.disk.user.domain.User;
import pw.cdmi.box.disk.user.service.UserTokenManager;
import pw.cdmi.box.disk.utils.*;
import pw.cdmi.box.domain.Page;
import pw.cdmi.box.domain.PageRequest;
import pw.cdmi.core.exception.*;
import pw.cdmi.core.restrpc.RestClient;
import pw.cdmi.core.restrpc.domain.TextResponse;
import pw.cdmi.core.utils.JsonUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;

@Controller
@RequestMapping(value = "/share")
public class ShareController extends CommonController
{
    public static final int MAX_USERNAME_SEARCH_SIZE = 2000;
    
    public final static String PAGE_INVITE_SHARE = "share/inviteShare";
    
    public final static String PAGE_INVITE_SHARE_HW = "share/inviteShareHwldap";
    
    private static Logger logger = LoggerFactory.getLogger(ShareController.class);
    
    private static final int PAGE_SIZE_USER_LIST = Integer.MAX_VALUE;
    
    private static final String SRC_NODE_NULL = "inviteShare404";
    
    private static final Integer INITIALSIZE = 10;
    
    @Autowired
    private FileService fileService;
    
    @Autowired
    private FolderService folderService;
    
    private final static String SHARED_USER_TYPE_OF_GROUP = "1";
    
    private final static String SHARED_USER_TYPE_OF_USER = "0";
    
    private final static String SHARED_USER_TYPE_OF_DEPARTMENT = "2";
    
    @Autowired
    private ShareService shareService;
    
    @Resource
    private RestClient uamClientService;
    
    @Resource
    private RestClient ufmClientService;
    
    @Autowired
    private UserTokenManager userTokenManager;
    
    @Autowired
    private AccountRoleService accountRoleService;
    
    @Autowired
    private GroupMemberService groupMemberService;
    
    private UserClient userClient;
    
    @RequestMapping(value = "addShare", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<?> addSharedUsers(long iNodeId, String shareToStr, String message, String authType,
        HttpServletRequest request)
    {
        super.checkToken(request);
        UserToken user = getCurrentUser();
        List<INodeShareV2> sharedList = new ArrayList<INodeShareV2>(BusinessConstants.INITIAL_CAPACITIES);
        Map<Long,INodeShareV2> shareMap = new HashMap<>();
        INodeShareV2 share = null;
        ArrayList<String> temp = (ArrayList<String>) CommonTools.decodeTrunkDate(shareToStr);
        if (temp == null)
        {
            return new ResponseEntity<String>(HttpStatus.OK);
        }
        if (StringUtils.isNotBlank(message) && message.length() > 2000)
        {
            return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
        }
        HashSet<String> set = new HashSet<String>(BusinessConstants.INITIAL_CAPACITIES);
        set.addAll(temp);
        String data = null;
        String element = null;
        List<INodeShareV2> failedList = new ArrayList<INodeShareV2>(1);
        String userType;
        for (Iterator<String> iter = set.iterator(); iter.hasNext();)
        {
            element = iter.next();
            if (StringUtils.isEmpty(element))
            {
                continue;
            }
            data = HtmlUtils.htmlUnescape(element);
            share = new INodeShareV2();
            userType = getUserType(data);
            if (StringUtils.equals(SHARED_USER_TYPE_OF_USER, userType))
            {
                share.setSharedUserType((byte)0);
            }
            else if (StringUtils.equals(SHARED_USER_TYPE_OF_GROUP, userType))
            {
                share.setSharedUserType((byte)1);
            }

            if(StringUtils.equals(SHARED_USER_TYPE_OF_DEPARTMENT,userType))
            {
            	share.setSharedUserType((byte)2);
            	share.setiNodeId(iNodeId);
            	share.setOwnerId(user.getCloudUserId());
            	share.setOwnerName(user.getName());
            	share.setSharedUserId(getSharedUserId(data));
            	sharedList.add(share);
            	
            }else{
            	
            	share.setOwnerId(user.getCloudUserId());
            	share.setOwnerName(user.getName());
            	share.setSharedUserEmail(getEmail(data));
            	try
            	{
            		share.setSharedUserId(getSharedUserId(data));
            		if (StringUtils.equals(SHARED_USER_TYPE_OF_GROUP, userType))
                    {
            			sharedList.add(share);
                    }else{
                    	shareMap.put(getSharedUserId(data), share);
                    }
            	}
            	catch (RestException e)
            	{
            		failedList.add(share);
            	}
            	catch (UnsupportedOperationException e)
            	{
            		failedList.add(share);
            	}
            }
        }
        for (Long id : shareMap.keySet()) {
        	sharedList.add(shareMap.get(id));
		}
        if(sharedList.size()!=0){
        	 shareService.addShareList(user, sharedList, iNodeId, message, authType, failedList);
        }else{
        	 return new ResponseEntity<String>(HttpStatus.OK);
        }
        
        return new ResponseEntity<String>("P_OK", HttpStatus.OK);
    }
    
    @RequestMapping(value = "updateShare", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<?> updateShare(long iNodeId, long userId, String userType, String authType,
        HttpServletRequest request)
    {
        super.checkToken(request);
        UserToken user = getCurrentUser();
        shareService.updateShare(user, iNodeId, userId, Byte.parseByte(userType), authType);
        return new ResponseEntity<String>(HttpStatus.OK);
    }
    
    @RequestMapping(value = "cancelShare", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<String> cancelAllShare(long iNodeId, HttpServletRequest request)
    {
        super.checkToken(request);
        UserToken user = getCurrentUser();
        shareService.cancelAllShare(user, user.getId(), iNodeId);
        return new ResponseEntity<String>(HttpStatus.OK);
    }
    
    @RequestMapping(value = "refreshShare", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<String> refreshShareRequest()
    {
        return new ResponseEntity<String>(HttpStatus.OK);
    }
    
    @RequestMapping(value = "/deleteSharedUser", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<String> deleteShareUser(long iNodeId, Long sharedUserId, String sharedUserType,
        HttpServletRequest request)
    {
        super.checkToken(request);
        UserToken user = getCurrentUser();
        if (!sharedUserType.equals(SHARED_USER_TYPE_OF_USER)
            && !sharedUserType.equals(SHARED_USER_TYPE_OF_GROUP)
                &&!sharedUserType.equals("2"))
        {
            throw new InvalidParamException();
        }
        if (sharedUserId == null)
        {
            return new ResponseEntity<String>("invalid request", HttpStatus.BAD_REQUEST);
        }
        shareService.deleteShare(user, user.getId(), sharedUserId, getUserTypeStr(sharedUserType), iNodeId);
        
        return new ResponseEntity<String>(HttpStatus.OK);
    }
    
    
    
    @RequestMapping(value = "/deleteSharedUsers", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<String> deleteShareUsers(long iNodeId, String sharedUsers,
        HttpServletRequest request)
    {
        super.checkToken(request);
        UserToken user = getCurrentUser();
        JSONArray users=JSONArray.readFrom(sharedUsers);
        for(int i=0;i<users.size();i++){
        	JSONObject JSONObject=users.getJSONObject(i);
        	 shareService.deleteShare(user, user.getId(),Long.parseLong(JSONObject.getString("id")), getUserTypeStr(JSONObject.getString("type")), iNodeId);
        }
       
        
        return new ResponseEntity<String>(HttpStatus.OK);
    }
    
    @RequestMapping(value = "/folder/{ownerId}/{folderId}", method = RequestMethod.GET)
    public String gotoShareMyFolder(@PathVariable("folderId") long folderId, Model model)
    {
        try
        {
            UserToken user = getCurrentUser();
            INode folderNode = folderService.getNodeInfo(user, user.getCloudUserId(), folderId);
            if (null == folderNode)
            {
                return "share/" + SRC_NODE_NULL;
            }
            if (folderNode.getType() == INode.TYPE_FILE && FilesCommonUtils.isImage(folderNode.getName()))
            {
                String downloadUrl;
                try
                {
                    downloadUrl = fileService.getFileThumbUrls(user,
                        folderNode.getOwnedBy(),
                        folderNode.getId());
                    downloadUrl = htmlEscapeThumbnail(downloadUrl);
                }
                catch (RestException e)
                {
                    downloadUrl = "";
                }
                model.addAttribute("thumbnailUrl", downloadUrl);
            }
            userClient = new UserClient(uamClientService);
            boolean checkOrgEnabled = userClient.checkOrgEnabled(user.getToken());
    		model.addAttribute("isDepartment", checkOrgEnabled);
            model.addAttribute("loginUserName", HtmlUtils.htmlEscape(user.getLoginName()));
            model.addAttribute("folderId", folderId);
            model.addAttribute("ownerId", user.getCloudUserId());
            model.addAttribute("name", folderNode.getName());
            model.addAttribute("type", folderNode.getType());
            model.addAttribute("size", folderNode.getSize());
            model.addAttribute("modifiedAt", folderNode.getModifiedAt());
            model.addAttribute("shareStatus", folderNode.getShareStatus());
            
            MailMsg msg = new MailMsgClient(ufmClientService).getMailMsg(userTokenManager.getToken(),
                MailMsg.SOURCE_SHARE,
                user.getCloudUserId(),
                folderId);
            model.addAttribute("mailmsg", msg == null ? "" : msg.getMessage());
            
            List<PageNodeRoleInfo> systemRoles = accountRoleService.listAccountRoles(user.getAccountId());
            
            model.addAttribute("systemRoles", systemRoles);
            
            return "share/shareIndex";
        }
        catch (RestException e)
        {
            logger.error("", e);
            model.addAttribute("httpStatus", getHttpStatus(e));
            model.addAttribute("exceptionInfo", e.getCode());
            return "share/" + SRC_NODE_NULL;
        }
        catch (BaseRunException e)
        {
            logger.error("", e);
            model.addAttribute("httpStatus", getHttpStatus(e));
            model.addAttribute("exceptionInfo", ErrorCode.BAD_REQUEST.getCode());
            return "share/" + SRC_NODE_NULL;
        }
    }
    
    
    @RequestMapping(value = "/addShares", method = RequestMethod.GET)
    public String addShares(long iNodeId, Model model)
    {
        try
        {
            UserToken user = getCurrentUser();
            model.addAttribute("ownerId", user.getId());
            model.addAttribute("iNodeId", iNodeId);
            return "share/addshare";
        }
        catch (RestException e)
        {
            logger.error("", e);
            model.addAttribute("httpStatus", getHttpStatus(e));
            model.addAttribute("exceptionInfo", e.getCode());
            return "share/" + SRC_NODE_NULL;
        }
    }
    
    
    @RequestMapping(value = "/shareLinks", method = RequestMethod.GET)
    public String gotoShareLinks( Model model)
    {
        try
        {
            UserToken user = getCurrentUser();
            model.addAttribute("ownerId", user.getId());
            return "share/shareRecive";
        }
        catch (RestException e)
        {
            logger.error("", e);
            model.addAttribute("httpStatus", getHttpStatus(e));
            model.addAttribute("exceptionInfo", e.getCode());
            return "share/" + SRC_NODE_NULL;
        }
    }
    
    @RequestMapping(value = "/inboxFileList/{ownerId}/{parentId}", method = RequestMethod.GET)
    public String gotoInboxFileList(@PathVariable("ownerId") String ownerId,@PathVariable("parentId") Long parentId,Model model)
    {
			model.addAttribute("ownerId", ownerId);
			model.addAttribute("parentId", parentId);
			return "share/inboxFileList";
    }
    
    @RequestMapping(value = "listSharedUser", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<?> listSharedUser(Long ownerId, long iNodeId, int pageNumber, Model model, HttpServletRequest request)
    {
        super.checkToken(request);
        PageRequest pageRequest = new PageRequest(pageNumber, PAGE_SIZE_USER_LIST);
        try
        {
            UserToken user = getCurrentUser();
            if(ownerId == null) {
                ownerId = user.getId();
            }
            Page<INodeShare> page = shareService.getShareUserList(user, ownerId, iNodeId, pageRequest);
            int size = page.getContent().size();
            INodeShare nodeShare;
            for (int i = 0; i < size; i++)
            {
                nodeShare = page.getContent().get(i);
                nodeShare.setId(i + 1);
                nodeShare.setSharedUserName(HtmlUtils.htmlEscape(nodeShare.getSharedUserName()));
                nodeShare.setSharedDepartment(HtmlUtils.htmlEscape(nodeShare.getSharedDepartment()));
            }
            return new ResponseEntity<Page<INodeShare>>(page, HttpStatus.OK);
        }
        catch (BaseRunException e)
        {
            logger.error("", e);
            return new ResponseEntity<String>(ShareLinkExceptionUtil.getClassName(e), HttpStatus.BAD_REQUEST);
        }
    }
    
    @RequestMapping(value = "listUser", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<?> listUser(Model model, String userName, HttpServletRequest request)
    {
        super.checkToken(request);
        try
        {
            
            if (StringUtils.isBlank(userName))
            {
                logger.error("userName is blank!");
                return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
            }
            
            if (userName.length() > MAX_USERNAME_SEARCH_SIZE)
            {
                logger.error("search userName exceed max size(100):size=" + userName.length());
                return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
            }
            List<UserToken> listUser = shareService.getListUser(userName);
            Collections.sort(listUser, new AdUserSortor(getCurrentUser().getDepartment()));
            for (User user : listUser)
            {
                user.setDepartment(HtmlUtils.htmlEscape(user.getDepartment()));
                user.setLoginName(HtmlUtils.htmlEscape(user.getLoginName()));
                user.setName(HtmlUtils.htmlEscape(user.getName()));
                user.setLabel(user.getName());
            }
            return new ResponseEntity<List<UserToken>>(listUser, HttpStatus.OK);
        }
        catch (RestException e)
        {
            logger.error(e.getMessage(), e);
            return new ResponseEntity<String>(ShareLinkExceptionUtil.getClassName(e), HttpStatus.BAD_REQUEST);
        }
    }
    
    protected String htmlEscapeThumbnail(String thumbnailUrl)
    {
        if (thumbnailUrl == null)
        {
            return "";
        }
        return HtmlUtils.htmlEscape(thumbnailUrl.substring(0, thumbnailUrl.lastIndexOf("&")))
            + thumbnailUrl.substring(thumbnailUrl.lastIndexOf("&"));
    }
    
    @RequestMapping(value = "listNickUser", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<?> listNickUser(Model model, String userNames, HttpServletRequest request)
    {
        super.checkToken(request);
        if (!checkUserNamesValid(userNames))
        {
            return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
        }
        
        CollectUserResponse controlUserResponse = new CollectUserResponse();
        controlUserResponse.setMap(new HashMap<String, List<User>>(INITIALSIZE));
        HashSet<String> nikeNames = (HashSet<String>) StringTokenizerUtils.wsString(userNames);
        List<UserToken> listUser;
        List<User> controlUsers;
        try
        {
            for (String nName : nikeNames)
            {
                listUser = shareService.getListUser(nName);
                controlUsers = new ArrayList<User>(INITIALSIZE);
                for (User user : listUser)
                {
                    
                    user.setDepartment(HtmlUtils.htmlEscape(user.getDepartment()));
                    user.setLoginName(HtmlUtils.htmlEscape(user.getLoginName()));
                    user.setName(HtmlUtils.htmlEscape(user.getName()));
                    user.setLabel(user.getName());
                    
                    controlUsers.add(user);
                    
                }
                controlUserResponse.getMap().put(nName, controlUsers);
                if (controlUsers.size() == 1)
                {
                    controlUserResponse.setResult(true);
                }
                else
                {
                    controlUserResponse.setResult(false);
                    break;
                }
                
            }
            
            return new ResponseEntity<CollectUserResponse>(controlUserResponse, HttpStatus.OK);
        }
        catch (Exception e)
        {
            logger.error(e.getMessage(), e);
            return new ResponseEntity<String>(ShareLinkExceptionUtil.getClassName(e), HttpStatus.BAD_REQUEST);
        }
    }
    
    @RequestMapping(value = "listMultiUser", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<?> listMultiUser(Model model, String userNames)
    {
        MultiUserSearchResponse controlUserResponse = new MultiUserSearchResponse();
        try
        {
            if (!checkUserNamesValid(userNames))
            {
                return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
            }
            
            HashSet<String> nikeNames = (HashSet<String>) StringTokenizerUtils.wsString(userNames);
            
            controlUserResponse.setSingle(nikeNames.size() == 1);
            List<RestMultiUser> restMultiUsers = new ArrayList<RestMultiUser>(
                BusinessConstants.INITIAL_CAPACITIES);
            if (nikeNames.size() == 1)
            {
                setListForOneNikeName(controlUserResponse, nikeNames, restMultiUsers);
            }
            else
            {
                setListForMultiNikeNames(controlUserResponse, nikeNames, restMultiUsers);
            }
            
            removeUserNotInGroup(controlUserResponse.getSuccessList());
            
            return new ResponseEntity<MultiUserSearchResponse>(controlUserResponse, HttpStatus.OK);
        }
        catch (Exception e)
        {
            logger.error(e.getMessage(), e);
            return new ResponseEntity<String>(ShareLinkExceptionUtil.getClassName(e), HttpStatus.BAD_REQUEST);
        }
    }
    
    private void setListForMultiNikeNames(MultiUserSearchResponse controlUserResponse, Set<String> nikeNames,
        List<RestMultiUser> restMultiUsers)
    {
        List<RestMultiUser> multiUsers = new ArrayList<RestMultiUser>(BusinessConstants.INITIAL_CAPACITIES);
        List<UserToken> res;
        List<RestGroup> groups;
        for (String u : nikeNames)
        {
            res = shareService.getListUser(u);
            groups = shareService.getListGroupByNickName(u);
            if (CollectionUtils.isNotEmpty(res) && res.size() == 1
                || (CollectionUtils.isNotEmpty(groups) && groups.size() == 1))
            {
                if (groups.size() == 1 && res.isEmpty())
                {
                    RestGroup restGroup = groups.get(0);
                    RestMultiUser multiUser = RestMultiUser.buildObject(null,
                        restGroup,
                        GroupConstants.GROUP_USERTYPE_GROUP);
                    restMultiUsers.add(multiUser);
                    continue;
                }
                
                if (res.size() == 1 && groups.isEmpty())
                {
                    UserToken user = res.get(0);
                    RestMultiUser multiUser = RestMultiUser.buildObject(user,
                        null,
                        GroupConstants.GROUP_USERTYPE_USER);
                    multiUsers.add(multiUser);
                    continue;
                }
            }
            controlUserResponse.getFailList().add(u);
        }
        for (RestMultiUser multiUser : multiUsers)
        {
            restMultiUsers.add(multiUser);
        }
        controlUserResponse.setSuccessList(restMultiUsers);
    }
    
    private void setListForOneNikeName(MultiUserSearchResponse controlUserResponse, Set<String> nikeNames,
        List<RestMultiUser> restMultiUsers)
    {
        List<RestGroup> groups = shareService.getListGroupByNickName(nikeNames.iterator().next());
        RestMultiUser multiUser;
        for (RestGroup group : groups)
        {
            multiUser = RestMultiUser.buildObject(null, group, GroupConstants.GROUP_USERTYPE_GROUP);
            restMultiUsers.add(multiUser);
        }
        
        List<UserToken> res = shareService.getListUser(nikeNames.iterator().next());
        
        for (User user : res)
        {
            multiUser = RestMultiUser.buildObject(user, null, GroupConstants.GROUP_USERTYPE_USER);
            restMultiUsers.add(multiUser);
        }
        
        controlUserResponse.setSuccessList(restMultiUsers);
    }
    
    private boolean checkUserNamesValid(String userNames)
    {
        if (StringUtils.isBlank(userNames))
        {
            logger.error("userName is blank!");
            return false;
        }
        
        if (userNames.length() > MAX_USERNAME_SEARCH_SIZE)
        {
            logger.error("search userName exceed max size(1000):size=" + userNames.length());
            return false;
        }
        return true;
    }
    
    @RequestMapping(value = "listMultiGroup", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<?> listMultiGroup(Model model, String userNames, HttpServletRequest request)
    {
        super.checkToken(request);
        MultiUserSearchResponse controlUserResponse = new MultiUserSearchResponse();
        try
        {
            if (!checkUserNamesValid(userNames))
            {
                return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
            }
            
            HashSet<String> nikeNames = (HashSet<String>) StringTokenizerUtils.wsString(userNames);
            
            controlUserResponse.setSingle(nikeNames.size() == 1);
            List<RestMultiUser> restMultiUsers = new ArrayList<RestMultiUser>(
                BusinessConstants.INITIAL_CAPACITIES);
            if (nikeNames.size() == 1)
            {
                List<UserToken> res = shareService.getListUser(nikeNames.iterator().next());
                RestMultiUser multiUser;
                for (User user : res)
                {
                    multiUser = RestMultiUser.buildObject(user, null, GroupConstants.GROUP_USERTYPE_USER);
                    restMultiUsers.add(multiUser);
                }
                
                controlUserResponse.setSuccessList(restMultiUsers);
            }
            else
            {
                fillMultiUsers(controlUserResponse, nikeNames, restMultiUsers);
                controlUserResponse.setSuccessList(restMultiUsers);
            }
            
            return new ResponseEntity<MultiUserSearchResponse>(controlUserResponse, HttpStatus.OK);
        }
        catch (Exception e)
        {
            logger.error(e.getMessage(), e);
            return new ResponseEntity<String>(ShareLinkExceptionUtil.getClassName(e), HttpStatus.BAD_REQUEST);
        }
    }
    
    private void fillMultiUsers(MultiUserSearchResponse controlUserResponse, Set<String> nikeNames,
        List<RestMultiUser> restMultiUsers)
    {
        List<RestMultiUser> multiUsers = new ArrayList<RestMultiUser>(BusinessConstants.INITIAL_CAPACITIES);
        List<UserToken> res;
        for (String u : nikeNames)
        {
            res = shareService.getListUser(u);
            if (CollectionUtils.isNotEmpty(res) && res.size() == 1)
            {
                UserToken user = res.get(0);
                RestMultiUser multiUser = RestMultiUser.buildObject(user,
                    null,
                    GroupConstants.GROUP_USERTYPE_USER);
                multiUsers.add(multiUser);
                continue;
            }
            controlUserResponse.getFailList().add(u);
        }
        for (RestMultiUser multiUser : multiUsers)
        {
            restMultiUsers.add(multiUser);
        }
    }
    
    private String getUserType(String item)
    {
        int end = item.indexOf("[");
        return item.substring(0, end);
    }
    
    private String getEmail(String userName)
    {
        int begin = userName.lastIndexOf("[");
        int end = userName.lastIndexOf("]");
        return userName.substring(begin + 1, end);
    }
    
    private long getSharedUserId(String userStr) throws RestException
    {
        int beginIndex = userStr.indexOf("[");
        int endIndex = userStr.indexOf("]");
        try
        {
            String cloudStr = userStr.substring(beginIndex + 1, endIndex); 
            int indexOf = cloudStr.indexOf("user");
            if(indexOf>0){
            	return Long.parseLong(cloudStr.substring(0,indexOf));
            }
            return Long.parseLong(cloudStr);
        }
        catch (Exception e)
        {
            int secondBegin = userStr.lastIndexOf("[");
            String userName = userStr.substring(endIndex + 1, secondBegin);
            UserClient userClient = new UserClient(uamClientService);
            UserToken user = userClient.createUserFromLdap(getToken(), userName);
            return user.getCloudUserId();
        }
    }
    
    private String getUserTypeStr(String userType)
    {
        if (userType.equals(SHARED_USER_TYPE_OF_USER))
        {
            return Constants.SPACE_TYPE_USER;
        }
        
        if (userType.equals(SHARED_USER_TYPE_OF_GROUP))
        {
            return Constants.SPACE_TYPE_GROUP;
        }
        if (userType.equals(SHARED_USER_TYPE_OF_DEPARTMENT))
        {
            return Constants.SPACE_TYPE_ORGANIZATION;
        }
        
        
        throw new InvalidParamException();
    }
    
    private HttpStatus getHttpStatus(RestException exception)
    {
        ErrorCode errorCode = getErrorCode(exception.getCode());
        return ErrorCodeConvertor.convertToHttpStatus(errorCode);
    }
    
    private HttpStatus getHttpStatus(BaseRunException exception)
    {
        ErrorCode errorCode = getErrorCode(exception.getCode());
        return ErrorCodeConvertor.convertToHttpStatus(errorCode);
    }
    
    private ErrorCode getErrorCode(String inCode)
    {
        for (ErrorCode errorCode : ErrorCode.values())
        {
            if (inCode.equalsIgnoreCase(errorCode.getCode()))
            {
                return errorCode;
            }
        }
        return ErrorCode.INTERNAL_SERVER_ERROR;
    }
    
    /*
     *  param 1 : List<RestMultiUser> successList
     *  
     *  關于 方法第一行 注釋// if (true) { return; }
     *  個人認為個不是一個bug.所以在開發過程中多了第一行，當要恢復非群組用戶也可查詢到其它群組的能力。
     *  例：
     *  	1、用户user1创建群组【group1】，成员包括user1,user3; 
	 *		2、使用user2登录系统，共享文件或文件夹，查詢群组【group1】； 
	 *		第一行為true時，   user2不能查詢到群组【group1】，不能共享文件或文件夹到群组【group1】
	 *		第一行為false時，user2    能查詢到群组【group1】，    能共享文件或文件夹到群组【group1】
     */
	private void removeUserNotInGroup(List<RestMultiUser> successList) {
		// if (true) { return; }
		List<RestMultiUser> removeList = new ArrayList<>();
		Set<RestMultiUser> removeGroupIdSet = new HashSet<>();
		for (RestMultiUser rmu : successList) {
			if (GroupConstants.GROUP_USERTYPE_GROUP == rmu.getUserType()) {
				removeGroupIdSet.add(rmu);
			}
		}
		Iterator<RestMultiUser> iterator = removeGroupIdSet.iterator();
		//loop: 
		while (iterator.hasNext()) {
			RestMultiUser rmu = iterator.next();
			try { // ufm后臺業務邏輯在，在查詢到當前用用戶不在此群組時會報錯rest錯
				  // Return msg is: {"code":"Forbidden","message":"The operation is prohibited.","requestId":"-http-apr-8085-exec-9-1481543988601000001","type":"error"}
				groupMemberService.getUserList(rmu.getId(), new RestGroupMemberOrderRequest());
			} catch(RestException re) {
				removeList.add(rmu);
			}
			/*
			GroupMembershipsList gmsList = groupMemberService.getUserList(rmu.getId(), new RestGroupMemberOrderRequest());
			for (GroupMembershipsInfo gmsi : gmsList.getMemberships()) {
				if (this.getCurrentUser().getLoginName().equals(gmsi.getMember().getLoginName())) {
					break loop;
				}
			}
			removeList.add(rmu);
			*/
		}
		successList.removeAll(removeList);
	}

	public void covertDepId2ShareUser(Map<Long,INodeShareV2> shareMap,String data){

    	
    	int beginIndex = data.indexOf("[");
        int endIndex = data.indexOf("]");
        String depId = data.substring(beginIndex + 1, endIndex); 
    	
    	Map<String, String> headerMap = new HashMap<String, String>(1);
    	headerMap.put("Authorization", getCurrentUser().getToken());
    	
    	TextResponse response = uamClientService.performJsonPostTextResponse("/api/v2/users/getUsersByDepId",
    			headerMap,
    			depId);
    	if (response.getStatusCode() == HttpStatus.OK.value())
    	{	
    		String responseBody = response.getResponseBody();
    		List<UserToken> users = (List<UserToken>) JsonUtils.stringToList(responseBody,List.class,UserToken.class);
    		for (UserToken userToken : users) {
    			UserToken cunrent=getCurrentUser();
    			if(cunrent.getCloudUserId().longValue()==userToken.getCloudUserId().longValue()){
    				continue;
    			}
    			INodeShareV2 shareDep = new INodeShareV2();
    			shareDep.setSharedUserType((byte)0);
    			shareDep.setOwnerId(userToken.getCloudUserId());
    			shareDep.setOwnerName(userToken.getName());
    			shareDep.setSharedUserEmail(userToken.getEmail());
    			shareDep.setSharedUserId(userToken.getCloudUserId());
    			shareMap.put(userToken.getCloudUserId(), shareDep);
			}
    		
    	}
    	
    
    
	}
	
}
