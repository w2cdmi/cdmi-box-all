package pw.cdmi.box.disk.share.service.impl;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import pw.cdmi.box.disk.client.api.FolderClient;
import pw.cdmi.box.disk.client.api.LinkClient;
import pw.cdmi.box.disk.client.api.MailMsgClient;
import pw.cdmi.box.disk.client.domain.mailmsg.MailMsg;
import pw.cdmi.box.disk.client.domain.node.INode;
import pw.cdmi.box.disk.client.domain.node.RestFolderInfo;
import pw.cdmi.box.disk.client.domain.node.RestFolderLists;
import pw.cdmi.box.disk.client.domain.node.basic.BasicNodeListRequest;
import pw.cdmi.box.disk.client.domain.share.*;
import pw.cdmi.box.disk.files.service.FolderService;
import pw.cdmi.box.disk.group.domain.GroupConstants;
import pw.cdmi.box.disk.group.domain.GroupMembershipsInfo;
import pw.cdmi.box.disk.group.domain.GroupMembershipsList;
import pw.cdmi.box.disk.group.domain.RestGroupMemberOrderRequest;
import pw.cdmi.box.disk.group.service.GroupMemberService;
import pw.cdmi.box.disk.httpclient.rest.common.Constants;
import pw.cdmi.box.disk.httpclient.rest.request.RestMailMsgSetRequest;
import pw.cdmi.box.disk.oauth2.domain.UserToken;
import pw.cdmi.box.disk.share.domain.*;
import pw.cdmi.box.disk.share.domain.mail.RequestAttribute;
import pw.cdmi.box.disk.share.domain.mail.RequestMail;
import pw.cdmi.box.disk.share.service.LinkService;
import pw.cdmi.box.disk.system.service.CustomizeLogoService;
import pw.cdmi.box.disk.system.service.SecurityService;
import pw.cdmi.box.disk.teamspace.domain.ResourceRole;
import pw.cdmi.box.disk.teamspace.domain.RestNodePermissionInfo;
import pw.cdmi.box.disk.user.domain.User;
import pw.cdmi.box.disk.user.service.UserService;
import pw.cdmi.box.disk.user.service.UserTokenManager;
import pw.cdmi.box.disk.utils.BusinessConstants;
import pw.cdmi.box.disk.utils.CommonTools;
import pw.cdmi.box.disk.utils.PatternRegUtil;
import pw.cdmi.common.domain.CustomizeLogo;
import pw.cdmi.core.exception.*;
import pw.cdmi.core.restrpc.RestClient;
import pw.cdmi.core.restrpc.domain.TextResponse;
import pw.cdmi.core.utils.JsonUtils;

import javax.annotation.Resource;
import java.util.*;

@Component("linkService")
public class LinkServiceImpl implements LinkService {

    private static final String EMAIL_SPLIT_CHAR = ";";

    private static Logger logger = LoggerFactory.getLogger(LinkServiceImpl.class);

    @Autowired
    private SecurityService securityService;

    private static final int MAX_MAIL_SEND_SIZE = 50;

    @Autowired
    private CustomizeLogoService customizeLogoService;

    @Autowired
    private FolderService folderService;

    @Resource
    private RestClient uamClientService;

    @Resource
    private RestClient ufmClientService;

    @Autowired
    private UserService userService;

    @Autowired
    private GroupMemberService groupMemberService;

    @Autowired
    private UserTokenManager userTokenManager;

    @Override
    public boolean allowAnonyAccess() {
        if (null == securityService.getSecurityConfig().getLinkIsAnon()) {
            return false;
        }
        return securityService.getSecurityConfig().getLinkIsAnon();
    }

    @Override
    public INodeLinkView createLink(UserToken user, long ownerId, long iNodeId, LinkRequest request)
            throws BaseRunException {
        INodeLink iNodeLinkV2 = new LinkClient(ufmClientService, uamClientService).createLink(user,
                ownerId,
                iNodeId,
                request);
        if (null == iNodeLinkV2) {
            throw new NoSuchLinkException();
        }
        INodeLinkView nodeLink = assembleNodeLinkView(iNodeLinkV2);
        return nodeLink;
    }

    /**
     * @throws RestException
     */
    @Override
    public void deleteLinkById(UserToken user, INode inode, String linkCode) throws BaseRunException,
            RestException {
        new LinkClient(ufmClientService, uamClientService).deleteLinkById(user, inode, linkCode);
    }

    /**
     * @throws RestException
     */
    @Override
    public void deleteAllLink(UserToken user, INode inode) throws BaseRunException, RestException {
        new LinkClient(ufmClientService, uamClientService).deleteAllLink(user, inode);
    }

    @Override
    public RestLinkDynamicResponse updateDynamicLink(String linkCode, String receiver) throws RestException {
        return new LinkClient(ufmClientService, uamClientService).updateDynamicLink(linkCode, receiver);
    }

    /**
     * @throws RestException
     */
    @Override
    public RestLinkFolderLists listLinkFolder(RestLinkListRequest request) throws BaseRunException,
            RestException {
        return new LinkClient(ufmClientService, uamClientService).listLinkFolder(request);
    }

    @Override
    public List<INodeLinkView> getLinkByINodeId(UserToken user, long ownerId, long iNodeId)
            throws BaseRunException {
        List<INodeLink> iNodeLinkV2s = new LinkClient(ufmClientService, uamClientService).getLinksByINodeId(user,
                ownerId,
                iNodeId);
        List<INodeLinkView> nodeLinks = new ArrayList<INodeLinkView>(iNodeLinkV2s.size());
        for (INodeLink item : iNodeLinkV2s) {
            nodeLinks.add(assembleNodeLinkView(item));
        }
        return nodeLinks;
    }

    @Override
    public INodeLinkView getLinkByLinkCode(UserToken user, String linkCode, String accessCode)
            throws BaseRunException {
        LinkAndNodeV2 nodeLink = getNodeInfoByLinkCode(user, linkCode, accessCode);
        INodeLink tempLink = nodeLink.getLink();
        INodeLinkView link = assembleNodeLinkView(tempLink);
        return link;
    }

    @Override
    public boolean getLinkStatusByLinkCode(UserToken user, String linkCode, long ownerId, long iNodeId)
            throws BaseRunException {
        if (user == null) {
            logger.error("user ownerId or linkCode is null, linkCode:");
            throw new BadRquestException();
        }
        INodeLink iNodeLinkV2 = new LinkClient(ufmClientService, uamClientService).getLinksById(user,
                ownerId,
                iNodeId,
                linkCode);

        if (isLinkNotEffective(iNodeLinkV2)) {
            logger.error("inode not effective, EffectiveAt:" + iNodeLinkV2.getEffectiveAt().toString());
            return false;
        }

        if (isLinkExpired(iNodeLinkV2)) {
            String deleteLinkPath = Constants.API_PATH_OF_LINK + user.getCloudUserId() + '/'
                    + iNodeLinkV2.getiNodeId();
            ufmClientService.performDelete(deleteLinkPath, assembleToken());
            return true;
        }
        return false;
    }

    @Override
    public LinkAndNodeV2 getNodeInfoByLinkCode(UserToken user, String linkCode, String accessCode) throws BaseRunException {
        return new LinkClient(ufmClientService, uamClientService).getNodeInfoByLinkCode(user, linkCode, accessCode);
    }

    @Override
    public RestFolderLists listFolderLinkByFilter(BasicNodeListRequest listFolderRequest, INode node,
                                                  UserToken user, String linkCode) throws BaseRunException, RestException {
        String accessCode = CommonTools.getAccessCode(linkCode);
        LinkAndNodeV2 linkAndNode = getNodeInfoByLinkCode(user, linkCode, accessCode);
        INodeLink iNodeLink = linkAndNode.getLink();
        INode iNodeRoot = new INode();

        RestFolderInfo rFolder = linkAndNode.getFolder();
        RestFileInfoV2 rFile = linkAndNode.getFile();

        if (null != rFile) {
            iNodeRoot.setName(rFile.getName());
            iNodeRoot.setType(rFile.getType());
            iNodeRoot.setSize(rFile.getSize());
        } else {
            iNodeRoot.setName(rFolder.getName());
            iNodeRoot.setType(rFolder.getType());
        }

        if (node.getId() != iNodeLink.getiNodeId()) {
            INode inode = folderService.getNodeInfoCheckType(user,
                    iNodeLink.getOwnedBy(),
                    node.getId(),
                    INode.TYPE_FOLDER_ALL);

            return new FolderClient(ufmClientService).listNodesbyFilter(listFolderRequest, inode, user);
        }
        iNodeRoot.setId(node.getId());
        iNodeRoot.setOwnedBy(iNodeLink.getOwnedBy());
        return new FolderClient(ufmClientService).listNodesbyFilter(listFolderRequest, iNodeRoot, user);
    }

    @Override
    public void sendLinkMail(UserToken userToken, long ownerId, long iNodeId, String linkUrl,
                             String plainAccessCode, String emails, String message) throws BaseRunException, RestException {
        String linkCode = getLinkCode(linkUrl);
        String emailsResult = emails;
        if (emailsResult.endsWith(EMAIL_SPLIT_CHAR)) {
            emailsResult = emailsResult.substring(0, emailsResult.length() - 1);
        }
        String[] email = emailsResult.split(EMAIL_SPLIT_CHAR);
        if (email.length > MAX_MAIL_SEND_SIZE) {
            logger.error("email receiver exceed the max num(50), length:" + email.length);
            throw new BadRquestException("invalid email rule");
        }
        List<RequestMail> mailTo = new ArrayList<RequestMail>(BusinessConstants.INITIAL_CAPACITIES);
        Map<String, RequestMail> mailToUser = new HashMap<>();
        RequestMail rm;
        for (String to : email) {
            try {
                if (to.endsWith(GroupConstants.GROUP_EMAIL)) {
                    fillGroupMail(mailTo, to);
                } else if (to.endsWith(GroupConstants.DEPARTMENT_EMAIL)) {
                    String token = userToken.getToken();
                    fillDepartmentMail(token, mailToUser, to);
                } else {
                    if (!PatternRegUtil.checkMailLegal(to)) {
                        return;
                    }
                    rm = new RequestMail();
                    rm.setEmail(to);
                    mailToUser.put(to, rm);
                }
            } catch (NumberFormatException e) {
                logger.warn("share link witn email error", e);
                continue;
            }
        }
        try {
            INode node = folderService.getNodeInfo(userToken, ownerId, iNodeId);
            String nodeName = node.getName();

            List<RequestAttribute> params = buildLinkMailPara(userToken,
                    linkUrl,
                    plainAccessCode,
                    message,
                    linkCode,
                    nodeName);
            for (String string : mailToUser.keySet()) {
                mailTo.add(mailToUser.get(string));
            }
            new LinkClient(ufmClientService, uamClientService).sendMail(mailTo, params);
            new MailMsgClient(ufmClientService).setMailMsg(userTokenManager.getToken(),
                    ownerId,
                    iNodeId,
                    assembleMailMsgReq(MailMsg.SOURCE_LINK, null, message));
        } catch (RestException e) {
            logger.warn("send link mail failed." + e.getMessage());
        }
    }

    private void fillGroupMail(List<RequestMail> mailTo, String to) {
        String[] groupIds = StringUtils.split(to, GroupConstants.GROUP_EMAIL);
        if (groupIds == null) {
            return;
        }
        User userInfo = null;
        RequestMail rm = null;
        long groupId = Long.parseLong(groupIds[0]);
        RestGroupMemberOrderRequest groupOrder = new RestGroupMemberOrderRequest();
        GroupMembershipsList groupMembershipsList = groupMemberService.getUserList(groupId, groupOrder);
        List<GroupMembershipsInfo> groupMembershipsInfo = groupMembershipsList.getMemberships();
        for (GroupMembershipsInfo gm : groupMembershipsInfo) {
            userInfo = userService.getUserByCloudUserId(gm.getMember().getUserId());
            if (!PatternRegUtil.checkMailLegal(userInfo.getEmail())) {
                continue;
            }
            rm = new RequestMail();
            rm.setEmail(userInfo.getEmail());
            mailTo.add(rm);
        }
    }

    private void fillDepartmentMail(String token, Map<String, RequestMail> mailToUser, String to) {
        String[] departmentIds = StringUtils.split(to, GroupConstants.DEPARTMENT_EMAIL);
        if (departmentIds == null) {
            return;
        }
        RequestMail rm = null;
        Map<String, String> headerMap = new HashMap<String, String>(1);
        headerMap.put("Authorization", token);
        TextResponse response = uamClientService.performJsonPostTextResponse("/api/v2/users/getUsersByDepId",
                headerMap,
                departmentIds[0]);
        if (response.getStatusCode() == HttpStatus.OK.value()) {
            String responseBody = response.getResponseBody();
            @SuppressWarnings("unchecked")
            List<UserToken> users = (List<UserToken>) JsonUtils.stringToList(responseBody, List.class, UserToken.class);
            for (UserToken userToken : users) {
                if (!PatternRegUtil.checkMailLegal(userToken.getEmail())) {
                    continue;
                }
                rm = new RequestMail();
                rm.setEmail(userToken.getEmail());
                mailToUser.put(userToken.getEmail(), rm);
            }
        }
    }

    private List<RequestAttribute> buildLinkMailPara(UserToken userToken, String linkUrl,
                                                     String plainAccessCode, String message, String linkCode, String nodeName) {
        List<RequestAttribute> params = new ArrayList<RequestAttribute>(BusinessConstants.INITIAL_CAPACITIES);
        RequestAttribute ra = new RequestAttribute();
        ra.setName("message");
        ra.setValue(message);
        params.add(ra);

        ra = new RequestAttribute();
        ra.setName("nodeName");
        ra.setValue(nodeName);
        params.add(ra);

        ra = new RequestAttribute();
        ra.setName("sender");
        if (StringUtils.isNotBlank(userToken.getName())) {
            ra.setValue(userToken.getName());
        }
        params.add(ra);

        ra = new RequestAttribute();
        ra.setName("linkCode");
        ra.setValue(linkCode);
        params.add(ra);

        ra = new RequestAttribute();
        ra.setName("linkUrl");
        ra.setValue(linkUrl);
        params.add(ra);

        ra = new RequestAttribute();
        ra.setName("plainAccessCode");
        ra.setValue(plainAccessCode);
        params.add(ra);

        ra = new RequestAttribute();
        ra.setName("start");
        ra.setValue("");
        params.add(ra);

        ra = new RequestAttribute();
        ra.setName("end");
        ra.setValue("");
        params.add(ra);
        return params;
    }

    @Override
    public void sendLinkMailForUpdate(UserToken userToken, long ownerId, long iNodeId, String linkCode,
                                      String emails) throws BaseRunException, RestException {
        String emailsResult = emails;
        if (emailsResult.endsWith(EMAIL_SPLIT_CHAR)) {
            emailsResult = emailsResult.substring(0, emailsResult.length() - 1);
        }
        String[] email = emailsResult.split(EMAIL_SPLIT_CHAR);
        if (email.length > MAX_MAIL_SEND_SIZE) {
            logger.error("email receiver exceed the max num(50), length:" + email.length);
            throw new BadRquestException("invalid email rule");
        }
        List<RequestMail> mailTo = new ArrayList<RequestMail>(BusinessConstants.INITIAL_CAPACITIES);
        RequestMail rm;
        for (String to : email) {
            try {
                if (!PatternRegUtil.checkMailLegal(to)) {
                    return;
                }
                rm = new RequestMail();
                rm.setEmail(to);
                mailTo.add(rm);
            } catch (BusinessException e) {
                logger.warn("share link witn email error", e);
                continue;
            }
        }
        try {
            String fullUrl = getLinkBasePath() + LinkService.LINK_PREFIX + linkCode;
            List<RequestAttribute> params = buildLinkMailPara(userToken,
                    fullUrl,
                    "",
                    null,
                    linkCode,
                    folderService.getNodeInfo(userToken, ownerId, iNodeId).getName());
            new LinkClient(ufmClientService, uamClientService).sendMail(mailTo, params);
        } catch (RestException e) {
            logger.warn("send link mail failed." + e.getMessage());
        }

    }

    @Override
    public void sendDynamicMail(String linkCode, String plainAccessCode, String email, String message)
            throws BaseRunException, RestException {
        List<RequestMail> mailTo = new ArrayList<RequestMail>(BusinessConstants.INITIAL_CAPACITIES);

        RequestMail rm = new RequestMail();
        rm.setEmail(email);
        mailTo.add(rm);

        try {
            new LinkClient(ufmClientService, uamClientService).sendDynamicMail(mailTo,
                    message,
                    linkCode,
                    plainAccessCode);
        } catch (RestException e) {
            logger.warn("send link mail failed." + e.getMessage());
        }

    }

    @Override
    public INodeLinkView updateLink(UserToken user, long ownerId, long iNodeId, LinkRequest request,
                                    String linkCode) throws RestException {
        INodeLink rc = new LinkClient(ufmClientService, uamClientService).updateLink(user,
                ownerId,
                iNodeId,
                request,
                linkCode);
        INodeLinkView nodeLink = assembleNodeLinkView(rc);

        return nodeLink;
    }

    @Override
    public long vaildLinkOperACL(UserToken user, INode inode, String oper) throws BaseRunException {
        return 0;
    }

    private INodeLinkView assembleNodeLinkView(INodeLink rc) {
        INodeLinkView nodeLink = new INodeLinkView();

        if (null != rc.getEffectiveAt()) {
            nodeLink.setEffectiveAt(rc.getEffectiveAt());
        }

        if (null != rc.getExpireAt()) {
            nodeLink.setExpireAt(rc.getExpireAt());
        }

        nodeLink.setId(String.valueOf(rc.getId()));

        if (rc.getModifiedAt() != null) {
            nodeLink.setModifiedAt(rc.getModifiedAt());
        }

        nodeLink.setModifiedBy(rc.getModifiedBy());
        nodeLink.setOwnedBy(rc.getOwnedBy());
        nodeLink.setPassword(rc.getPassword());
        nodeLink.setPlainAccessCode(rc.getPlainAccessCode());
        nodeLink.setNeedLogin(rc.isNeedLogin());
        //此处没有使用查询出来的URL，而是重新计算
        nodeLink.setUrl(getLinkBasePath() + LinkService.LINK_PREFIX + rc.getId());
        nodeLink.setCreator(rc.getCreator());
        nodeLink.setUpload(false);
        nodeLink.setDownload(false);
        nodeLink.setPreview(false);
        if (ResourceRole.VIEWER.equals(rc.getRole())) {
            nodeLink.setDownload(true);
            nodeLink.setPreview(true);
        } else if (ResourceRole.UPLOADER.equals(rc.getRole())) {
            nodeLink.setUpload(true);
        } else if (ResourceRole.UPLOAD_VIEWER.equals(rc.getRole())) {
            nodeLink.setUpload(true);
            nodeLink.setDownload(true);
            nodeLink.setPreview(true);
        } else if (ResourceRole.DOWNLOADER.equals(rc.getRole())) {
            nodeLink.setDownload(true);
        } else if (ResourceRole.PREVIEWER.equals(rc.getRole())) {
            nodeLink.setPreview(true);
        }
        nodeLink.setAccessCodeMode(rc.getAccessCodeMode());

        if (!CollectionUtils.isEmpty(rc.getIdentities())) {
            nodeLink.setIdentities(buildIdentities(rc.getIdentities()));
        }
        return nodeLink;
    }

    private String buildIdentities(List<LinkIdentityInfo> identities) {
        StringBuffer result = new StringBuffer("");
        for (LinkIdentityInfo item : identities) {
            result.append(item.getIdentity());
            result.append(';');
        }
        String strIdentity = result.toString();
        return strIdentity.substring(0, strIdentity.length() - 1);
    }

    private Map<String, String> assembleToken() {
        Map<String, String> headers = new HashMap<String, String>(1);
        headers.put("Authorization", userTokenManager.getToken());
        return headers;
    }

    private String getLinkBasePath() {
        CustomizeLogo temp = customizeLogoService.getCustomize();
        if (null != temp) {
            try {
                StringBuffer webDomain = new StringBuffer(StringUtils.trimToEmpty(temp.getDomainName()));
                if ('/' != webDomain.charAt(webDomain.length() - 1)) {
                    webDomain.append('/');
                }
                return webDomain.toString();
            } catch (Exception e) {
                // TODO: handle exception
                String message = "Please Config WebDomain Name In ISystem.";
                logger.warn(message);
                throw new InternalServerErrorException(message);
            }

        }
        String message = "Please Config WebDomain Name In ISystem.";
        logger.warn(message);
        throw new InternalServerErrorException(message);
    }

    /**
     * @param linkUrl
     * @return
     */
    private String getLinkCode(String linkUrl) {
        if (linkUrl.indexOf("/") != -1) {
            return linkUrl.substring(linkUrl.lastIndexOf('/') + 1);
        }
        return "";
    }

    /**
     * @param inodeLink
     * @return
     */
    private boolean isLinkExpired(INodeLink inodeLink) {

        return inodeLink.getExpireAt() != null && new Date().after(inodeLink.getExpireAt());
    }

    /**
     * @param inodeLink
     * @return
     */
    private boolean isLinkNotEffective(INodeLink inodeLink) {

        return inodeLink.getEffectiveAt() != null && new Date().before(inodeLink.getExpireAt());
    }

    @Override
    public RestNodePermissionInfo getNodePermission(long ownerId, long nodeId, String linkCode) {
        return new LinkClient(ufmClientService, uamClientService).getNodePermission(ownerId, nodeId, linkCode);
    }

    private RestMailMsgSetRequest assembleMailMsgReq(String source, String subject, String message) {
        RestMailMsgSetRequest rl = new RestMailMsgSetRequest();
        rl.setSource(source);
        rl.setSubject(subject);
        rl.setMessage(message);
        return rl;
    }

    @Override
    public RestLinkApproveList listLinkApprove(RestLinkApproveListRequest request) {
        LinkClient linkClient = new LinkClient(ufmClientService, uamClientService);
        return linkClient.listLinkApprove(request);
    }


    @Override
    public RestLinkApproveList listAllLinkApprove(RestLinkApproveListRequest request) {
        LinkClient linkClient = new LinkClient(ufmClientService, uamClientService);
        return linkClient.listAllLinkApprove(request);
    }


    @Override
    public void createLinkApprove(UserToken user, long ownerId, INodeLinkApprove request) {
        // TODO Auto-generated method stub
        LinkClient linkClient = new LinkClient(ufmClientService, uamClientService);
        linkClient.creatLinkApprove(user, ownerId, request);
    }


    @Override
    public void updateApproveStatus(INodeLinkApprove restLinkApprove) {
        // TODO Auto-generated method stub
        LinkClient linkClient = new LinkClient(ufmClientService, uamClientService);
        linkClient.updateApproveStatus(restLinkApprove);

    }

    @Override
    public INodeLinkApprove getLinkApproveByLinkCode(String linkCode) {
        LinkClient linkClient = new LinkClient(ufmClientService, uamClientService);
        return linkClient.getLinkApproveByLinkCode(linkCode);
    }

    @Override
    public LinkAndNodeV2 getLinkOnlyByLinkCode(String linkCode) {
        // TODO Auto-generated method stub
        LinkClient linkClient = new LinkClient(ufmClientService, uamClientService);
        LinkAndNodeV2 iNodeLinkV2 = linkClient.getLinkOnlyByLinkCode(linkCode);

        return iNodeLinkV2;
    }

    @Override
    public RestLinkApproveDetail getLinkApproveDetailByLinkCode(String linkCode) {
        LinkClient linkClient = new LinkClient(ufmClientService, uamClientService);
        return linkClient.getLinkApproveDetailByLinkCode(linkCode);
    }

    @Override
    public void createLinkApproveUsers(UserToken user, long ownerId, List<Map<String, Object>> deptInfos, INodeLinkApprove linkApprove) {
        // TODO Auto-generated method stub
        LinkClient linkClient = new LinkClient(ufmClientService, uamClientService);
        linkClient.createLinkApproveUsers(deptInfos, linkApprove);
    }
}
