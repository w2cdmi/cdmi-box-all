package pw.cdmi.box.disk.share.service;

import java.util.List;
import java.util.Map;

import pw.cdmi.box.disk.client.domain.node.INode;
import pw.cdmi.box.disk.client.domain.node.RestFolderLists;
import pw.cdmi.box.disk.client.domain.node.basic.BasicNodeListRequest;
import pw.cdmi.box.disk.client.domain.share.*;
import pw.cdmi.box.disk.oauth2.domain.UserToken;
import pw.cdmi.box.disk.share.domain.*;
import pw.cdmi.box.disk.teamspace.domain.RestNodePermissionInfo;
import pw.cdmi.core.exception.BaseRunException;
import pw.cdmi.core.exception.RestException;

public interface LinkService
{
    
    String LINK_PREFIX = "p/";
    
    boolean allowAnonyAccess();
    
    /**
     * 
     * @param user
     * @param iNodeLink
     * @return
     * @throws BaseRunException
     */
    INodeLinkView createLink(UserToken user, long ownerId, long iNodeId, LinkRequest request)
        throws BaseRunException;
    
    /**
     * 
     * @param inode
     * @throws RestException
     */
    void deleteLinkById(UserToken user, INode inode, String linkCode) throws BaseRunException, RestException;
    
    /**
     * 
     * @param inode
     * @throws RestException
     */
    void deleteAllLink(UserToken user, INode inode) throws BaseRunException, RestException;
    
    RestLinkDynamicResponse updateDynamicLink(String linkCode, String receiver) throws RestException;
    
    /**
     * 
     * @param user
     * @param iNodeId
     * @param linktype
     * @return
     * @throws BaseRunException
     */
    List<INodeLinkView> getLinkByINodeId(UserToken user, long ownerId, long iNodeId) throws BaseRunException;
    
    /**
     * 
     * @param user
     * @param linkCode
     * @return
     * @throws BaseRunException
     */
    INodeLinkView getLinkByLinkCode(UserToken user, String linkCode, String accessCode)
        throws BaseRunException;
    
    /**
     * 
     * @param user
     * @param linkCode
     * @return
     * @throws BaseRunException
     */
    boolean getLinkStatusByLinkCode(UserToken user, String linkCode, long ownerId, long id)
        throws BaseRunException;
    
    /**
     * 
     * @param user
     * @param inodetype
     * @param linkCode
     * @return
     * @throws BaseRunException
     */
    LinkAndNodeV2 getNodeInfoByLinkCode(UserToken user, String linkCode, String accessCode)
        throws BaseRunException;
    
    /**
     * 
     * @param user
     * @param linkCode
     * @param order
     * @param limit
     * @return
     * @throws BaseRunException
     */
    RestFolderLists listFolderLinkByFilter(BasicNodeListRequest listFolderRequest, INode node, UserToken user,
        String linkCode) throws BaseRunException, RestException;
    
    /**
     * 
     * @param userToken
     * @param ownerId
     * @param iNodeId
     * @param linkUrl
     * @param emails
     * @return
     * @throws BaseRunException
     */
    @SuppressWarnings("PMD.ExcessiveParameterList")
    void sendLinkMail(UserToken userToken, long ownerId, long iNodeId, String linkUrl,
        String plainAccessCode, String emails, String message) throws BaseRunException, RestException;
    
    void sendLinkMailForUpdate(UserToken userToken, long ownerId, long iNodeId, String linkCode, String emails)
        throws BaseRunException, RestException;
    
    void sendDynamicMail(String linkCode, String plainAccessCode, String email, String message)
        throws BaseRunException, RestException;
    
    /**
     * 
     * @param user
     * @param inodeLink
     * @return
     * @throws BaseRunException
     */
    INodeLinkView updateLink(UserToken user, long ownerId, long iNodeId, LinkRequest request, String linkCode)
        throws RestException;
    
    /**
     * 
     * @param user
     * @param linkCode
     * @param Oper
     * @throws BaseRunException
     */
    long vaildLinkOperACL(UserToken user, INode inode, String oper) throws BaseRunException;
    
    RestNodePermissionInfo getNodePermission(long ownerId, long nodeId, String linkCode);
    
    /**
     * 
     * @param request
     * @param ownerId
     * @return
     * @throws BaseRunException
     * @throws RestException
     */
    RestLinkFolderLists listLinkFolder(RestLinkListRequest request) throws BaseRunException, RestException;

	RestLinkApproveList listLinkApprove(RestLinkApproveListRequest request);

	void createLinkApprove(UserToken user, long ownerId, INodeLinkApprove request);

	void updateApproveStatus(INodeLinkApprove restLinkApprove);

	INodeLinkApprove getLinkApproveByLinkCode(String linkCode);

	LinkAndNodeV2 getLinkOnlyByLinkCode(String linkCode);
	
    RestLinkApproveDetail getLinkApproveDetailByLinkCode(String linkCode);

	RestLinkApproveList listAllLinkApprove(RestLinkApproveListRequest request);

	void createLinkApproveUsers(UserToken user, long ownerId, List<Map<String, Object>> deptInfos, INodeLinkApprove linkApprove);
}
