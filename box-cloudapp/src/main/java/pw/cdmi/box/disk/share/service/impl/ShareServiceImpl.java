package pw.cdmi.box.disk.share.service.impl;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import pw.cdmi.box.disk.client.api.MailMsgClient;
import pw.cdmi.box.disk.client.api.ShareClient;
import pw.cdmi.box.disk.client.domain.mailmsg.MailMsg;
import pw.cdmi.box.disk.client.domain.node.INode;
import pw.cdmi.box.disk.files.service.FolderService;
import pw.cdmi.box.disk.group.domain.GroupMembershipsInfo;
import pw.cdmi.box.disk.group.domain.GroupMembershipsList;
import pw.cdmi.box.disk.group.domain.RestGroup;
import pw.cdmi.box.disk.group.domain.RestGroupMemberOrderRequest;
import pw.cdmi.box.disk.group.service.GroupMemberService;
import pw.cdmi.box.disk.group.service.GroupService;
import pw.cdmi.box.disk.httpclient.rest.common.Constants;
import pw.cdmi.box.disk.httpclient.rest.request.RestMailMsgSetRequest;
import pw.cdmi.box.disk.oauth2.domain.UserToken;
import pw.cdmi.box.disk.share.domain.*;
import pw.cdmi.box.disk.share.domain.mail.RequestAttribute;
import pw.cdmi.box.disk.share.domain.mail.RequestMail;
import pw.cdmi.box.disk.share.domain.mail.RestMailSendRequest;
import pw.cdmi.box.disk.share.service.ShareService;
import pw.cdmi.box.disk.user.domain.User;
import pw.cdmi.box.disk.user.service.UserService;
import pw.cdmi.box.disk.user.service.UserTokenManager;
import pw.cdmi.box.disk.utils.BusinessConstants;
import pw.cdmi.box.disk.utils.PatternRegUtil;
import pw.cdmi.box.domain.Order;
import pw.cdmi.box.domain.Page;
import pw.cdmi.box.domain.PageImpl;
import pw.cdmi.box.domain.PageRequest;
import pw.cdmi.core.exception.BaseRunException;
import pw.cdmi.core.exception.BusinessException;
import pw.cdmi.core.exception.ForbiddenException;
import pw.cdmi.core.exception.RestException;
import pw.cdmi.core.restrpc.RestClient;
import pw.cdmi.core.restrpc.domain.TextResponse;
import pw.cdmi.core.utils.JsonUtils;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.*;

@Component
public class ShareServiceImpl implements ShareService
{
    private static Logger logger = LoggerFactory.getLogger(ShareServiceImpl.class);
    
    private static final int DEFAULT_LIST_ADUSER_NUMBER = 20;
    
    private static final int DEFAULT_LIST_GROUP_NUM = 30;
    
    private static final int DEFAULT_LIMIT_GROUP = 1000;
    
    private static final String DEFAULT_LIST_GROUP_TYPE = "private";
    
    private static final String DEFAULT_LIST_GROUP_USER_TYPE = "all";
    
    private static final String LIST_SHARE_DIRECT = "DESC";
    
    private String apiPath = "/api/v2/";
    
    @Autowired
    private FolderService folderService;
    
    private ShareClient shareClient;
    
    @Autowired
    private UserService userService;
    
    @Resource
    private RestClient uamClientService;
    
    @Resource
    private RestClient ufmClientService;
    
    @Autowired
    private UserTokenManager userTokenManager;
    
    @Autowired
    private GroupService groupService;
    
    @Autowired
    private GroupMemberService groupMemberService;
    
    @Override
    public String addShareList(UserToken user, List<INodeShareV2> shareList, long nodeId, String message, String authType, List<INodeShareV2> failedList) throws BaseRunException, RestException
    {
        if (CollectionUtils.isEmpty(shareList))
        {
            throw new ForbiddenException();
        }
        
        StringBuilder sb = new StringBuilder();
        sb.append(apiPath).append("shareships/").append(user.getCloudUserId()).append('/').append(nodeId);
        Map<String, String> headers = assembleToken();

        RestPutShareRequestV2 rv = new RestPutShareRequestV2();
        rv.setRoleName(authType);

        List<SharedUserV2> userList = new ArrayList<>();
        rv.setSharedUserList(userList);

        String retCode = null;
        for (INodeShareV2 is : shareList) {
            SharedUserV2 su = new SharedUserV2();
            su.setId(is.getSharedUserId());
            su.setType(is.getSharedUserType());
            userList.add(su);
        }

        TextResponse response = ufmClientService.performJsonPutTextResponse(sb.toString(), headers, rv);
        if (response.getStatusCode() == HttpStatus.OK.value()) {
            retCode = "OK";
        } else {
            RestException exception = JsonUtils.stringToObject(response.getResponseBody(), RestException.class);
            retCode = exception.getCode();
        }

        //由UFM负责发送邮件通知。
/*
        INode node = folderService.getNodeInfo(user, user.getCloudUserId(), nodeId);
        List<RequestMail> mailTo = new ArrayList<RequestMail>(BusinessConstants.INITIAL_CAPACITIES);
        if (response.getStatusCode() == HttpStatus.OK.value()) {
            retCode = "OK";
            sendSuccessMail(user, message, node, mailTo, is);
        } else {
            failedList.add(is);
            RestException exception = JsonUtils.stringToObject(response.getResponseBody(),
                    RestException.class);
            retCode = exception.getCode();
        }
        sendMails(user, nodeId, message, node, mailTo);
*/
        return retCode;
    }

    private void sendSuccessMail(UserToken user, String message, INode node, List<RequestMail> mailTo,
        INodeShareV2 is)
    {
        try
        {
            if ( is.getSharedUserType()==1)
            {
                sendEmailToGroupMembers(user, message, node, is);
            }else{
                PatternRegUtil.checkMailLegal(is.getSharedUserEmail());
                if(!PatternRegUtil.checkMailLegal(is.getSharedUserEmail())){
                	return;
                }
                RequestMail rm = new RequestMail();
                rm.setEmail(is.getSharedUserEmail());
                mailTo.add(rm);
            }
        }
        catch (Exception e)
        {
            logger.warn("send share mail failed." + e.getMessage());
        }
    }
    
    private void sendMails(UserToken user, long nodeId, String message, INode node, List<RequestMail> mailTo)
    {
        try
        {
            
            if (!mailTo.isEmpty())
            {
                sendShareMail(user, node, mailTo, message);
                new MailMsgClient(ufmClientService).setMailMsg(userTokenManager.getToken(),
                    user.getCloudUserId(),
                    nodeId,
                    assembleMailMsgReq(MailMsg.SOURCE_SHARE, null, message));
            }
        }
        catch (BaseRunException e)
        {
            logger.warn("send share mail failed." + e.getMessage());
        }
    }
    
    @Override
    public void updateShare(UserToken user, long nodeId, long userId, byte userType, String authType) throws RestException {
        StringBuilder sb = new StringBuilder();
        sb.append(apiPath).append("shareships/").append(user.getCloudUserId()).append('/').append(nodeId);
        Map<String, String> headers = assembleToken();

        RestPutShareRequestV2 rv = new RestPutShareRequestV2();
        rv.setRoleName(authType);
        rv.setSharedUserList(new ArrayList<>());

        SharedUserV2 su = new SharedUserV2();
        su.setId(userId);
        su.setType(userType);
        rv.getSharedUserList().add(su);

        TextResponse response = ufmClientService.performJsonPutTextResponse(sb.toString(), headers, rv);
        if (response.getStatusCode() != HttpStatus.OK.value()) {
            RestException exception = JsonUtils.stringToObject(response.getResponseBody(), RestException.class);
            throw exception;
        }
    }
    
    @Override
    public void cancelAllShare(UserToken user, long ownerId, long iNodeId) throws BaseRunException
    {
        shareClient.cancelAllShare(user, ownerId, iNodeId);
    }
    
    @Override
    public void deleteShare(UserToken user, long ownerId, long sharedUserId, String sharedUserType,
        long iNodeId) throws RestException
    {
        shareClient.deleteShare(user, ownerId, sharedUserId, sharedUserType, iNodeId);
    }
    
    @Override
    public List<UserToken> getListUser(String userName)
    {
        RequestSearchUser rs = new RequestSearchUser();
        List<UserToken> list = new ArrayList<UserToken>(10);
        rs.setKeyword(userName);
        rs.setType("auto");
        rs.setLimit(DEFAULT_LIST_ADUSER_NUMBER);
        TextResponse restResponse = uamClientService.performJsonPostTextResponse("/api/v2/users/search",
            assembleToken(),
            rs);
        if (restResponse.getStatusCode() == HttpStatus.OK.value())
        {
            ResponseADSearchUser rc = JsonUtils.stringToObject(restResponse.getResponseBody(),
                ResponseADSearchUser.class);
            List<ADSearchUser> resUser = rc.getUsers();
            UserToken userToken;
            for (ADSearchUser ad : resUser)
            {
                userToken = new UserToken();
                userToken.setAppId(ad.getAppId());
                userToken.setCloudUserId(ad.getCloudUserId());
                userToken.setName(ad.getName());
                userToken.setDepartment(ad.getDescription());
                userToken.setLoginName(ad.getLoginName());
                userToken.setStatus(ad.getStatus());
                userToken.setEmail(ad.getEmail());
                list.add(userToken);
            }
        }
        return list;
    }
    
    
    @Override
    public List<RestGroup> getListGroupByNickName(String groupName)
    {
        List<Order> orders = new ArrayList<Order>(2);
        orders.add(new Order("name", "ASC"));
        return groupService.getGroupList(groupName,
            0L,
            DEFAULT_LIST_GROUP_NUM,
            DEFAULT_LIST_GROUP_TYPE,
            orders).getGroups();
    }
    
    @Override
    public Page<INodeShare> getShareUserList(UserToken user, long ownerId, long iNodeId,
        PageRequest pageRequest) throws BaseRunException
    {
        try
        {
            SharePageV2 list = shareClient.getShareUserList(user, ownerId, iNodeId, pageRequest);
            List<INodeShare> nodeShareList = new ArrayList<INodeShare>(10);
            if (null != list)
            {
                List<INodeShareV2> shareList = list.getContents();
                List<INodeShareV2> userList = new ArrayList<INodeShareV2>(shareList.size());
                fillNoneUserList(nodeShareList, shareList, userList);
                fillUserList(nodeShareList, userList);
                Collections.sort(shareList, new INodeShareSharedNameSorterV2());
                Collections.sort(nodeShareList, new ShareComparator(LIST_SHARE_DIRECT));
                return new PageImpl<INodeShare>(nodeShareList, pageRequest, list.getTotalCount());
            }
            return new PageImpl<INodeShare>(nodeShareList, pageRequest, 0);
        }
        catch (RestException e)
        {
            logger.error("failed to get ShareUserList", e);
            throw new BusinessException(e);
        }
        
    }

    private void fillUserList(List<INodeShare> nodeShareList, List<INodeShareV2> userList)
    {
        INodeShare tempShare = null;
        for (INodeShareV2 inodeShare : userList)
        {
            tempShare = new INodeShare();
            tempShare.setSharedUserName(inodeShare.getSharedUserName());
            tempShare.setSharedUserLoginName(inodeShare.getSharedUserLoginName());
            tempShare.setSharedDepartment(inodeShare.getSharedUserDescrip());
            tempShare.setSharedUserId(inodeShare.getSharedUserId());
            tempShare.setSharedUserType(inodeShare.getSharedUserType());
            tempShare.setSharedUserName(inodeShare.getSharedUserName());
            tempShare.setRoleName(inodeShare.getRoleName());
            nodeShareList.add(tempShare);
        }
    }

    private void fillNoneUserList(List<INodeShare> nodeShareList, List<INodeShareV2> shareList,
        List<INodeShareV2> userList)
    {
        INodeShare tempShare = null;
        for (INodeShareV2 inodeShare : shareList)
        {
            if (inodeShare.getSharedUserType()==0)
            {
                userList.add(inodeShare);
                continue;
            }
            tempShare = new INodeShare();
            tempShare.setSharedUserName(inodeShare.getSharedUserName());
            tempShare.setSharedUserLoginName(inodeShare.getSharedUserLoginName());
            tempShare.setSharedDepartment(inodeShare.getSharedUserDescrip());
            tempShare.setSharedUserId(inodeShare.getSharedUserId());
            tempShare.setSharedUserType(inodeShare.getSharedUserType());
            tempShare.setSharedUserName(inodeShare.getSharedUserName());
            tempShare.setRoleName(inodeShare.getRoleName());
            nodeShareList.add(tempShare);
        }
    }
    
    private byte getUserType(String userType)
    {
        if (userType.equals(Constants.SPACE_TYPE_USER))
        {
            return UserType.TYPE_USER;
        }
        if (userType.equals(Constants.SPACE_TYPE_ORGANIZATION))
        {
            return UserType.TYPE_DEPT;
        }
        
        return UserType.TYPE_GROUP;
    }
    
    private void sendShareMail(UserToken userToken, INode node, List<RequestMail> mailTo, String message)
        throws BaseRunException
    {
        String from = userToken.getName();
        
        RestMailSendRequest rs = new RestMailSendRequest();
        rs.setType("share");
        rs.setMailTo(mailTo);
        
        List<RequestAttribute> params = new ArrayList<RequestAttribute>(1);
        RequestAttribute ra = new RequestAttribute();
        ra.setName("message");
        if (StringUtils.isNotBlank(message))
        {
            ra.setValue(message);
        }
        
        params.add(ra);
        
        ra = new RequestAttribute();
        ra.setName("nodeName");
        ra.setValue("");
        if (StringUtils.isNotBlank(node.getName()))
        {
            ra.setValue(node.getName());
        }
        
        params.add(ra);
        
        ra = new RequestAttribute();
        ra.setName("sender");
        if (StringUtils.isNotBlank(from))
        {
            ra.setValue(from);
        }
        
        params.add(ra);
        
        ra = new RequestAttribute();
        ra.setName("linkCode");
        ra.setValue("");
        params.add(ra);
        
        ra = new RequestAttribute();
        ra.setName("linkUrl");
        ra.setValue("");
        params.add(ra);
        
        ra = new RequestAttribute();
        ra.setName("plainAccessCode");
        ra.setValue("");
        params.add(ra);
        
        ra = new RequestAttribute();
        ra.setName("start");
        ra.setValue("");
        params.add(ra);
        
        ra = new RequestAttribute();
        ra.setName("end");
        ra.setValue("");
        params.add(ra);
        
        ra = new RequestAttribute();
        ra.setName("type");
        ra.setValue(Byte.toString(node.getType()));
        params.add(ra);
        
        ra = new RequestAttribute();
        ra.setName("inodeId");
        ra.setValue(String.valueOf(node.getId()));
        params.add(ra);
        
        ra = new RequestAttribute();
        ra.setName("ownerId");
        ra.setValue(userToken.getCloudUserId().toString());
        params.add(ra);
        
        rs.setParams(params);
        
        TextResponse restResponse = uamClientService.performJsonPostTextResponse(Constants.API_MAILS,
            assembleToken(),
            rs);
        if (restResponse.getStatusCode() != HttpStatus.OK.value())
        {
            throw new BusinessException();
        }
    }
    
    @PostConstruct
    void init()
    {
        this.shareClient = new ShareClient(ufmClientService);
    }
    
    private Map<String, String> assembleToken()
    {
        Map<String, String> headers = new HashMap<String, String>(1);
        headers.put("Authorization", userTokenManager.getToken());
        return headers;
    }
    
    private void sendEmailToGroupMembers(UserToken user, String message, INode node, INodeShareV2 is)
    {
        List<Order> orders = new ArrayList<Order>(2);
        orders.add(new Order("groupRole", "ASC"));
        RestGroupMemberOrderRequest groupOrder = new RestGroupMemberOrderRequest(null, 0L,
            DEFAULT_LIMIT_GROUP, DEFAULT_LIST_GROUP_USER_TYPE, orders);
        GroupMembershipsList groupMembershipsList = groupMemberService.getUserList(is.getSharedUserId(),
            groupOrder);
        List<GroupMembershipsInfo> groupMembershipsInfoes = groupMembershipsList.getMemberships();
        List<RequestMail> mailTo = new ArrayList<RequestMail>(BusinessConstants.INITIAL_CAPACITIES);
        RequestMail rm;
        User userInfo;
        for (GroupMembershipsInfo groupMembershipsInfo : groupMembershipsInfoes)
        {
            userInfo = userService.getUserByCloudUserId(groupMembershipsInfo.getMember().getUserId());
            if(!PatternRegUtil.checkMailLegal(userInfo.getEmail())){
            	continue;
            }
            rm = new RequestMail();
            rm.setEmail(userInfo.getEmail());
            mailTo.add(rm);
        }
        sendShareMail(user, node, mailTo, message);
        new MailMsgClient(ufmClientService).setMailMsg(userTokenManager.getToken(),
            node.getOwnedBy(),
            node.getId(),
            assembleMailMsgReq(MailMsg.SOURCE_SHARE, null, message));
    }
    
    private RestMailMsgSetRequest assembleMailMsgReq(String source, String subject, String message)
    {
        RestMailMsgSetRequest rl = new RestMailMsgSetRequest();
        rl.setSource(source);
        rl.setSubject(subject);
        rl.setMessage(message);
        return rl;
    }
}
