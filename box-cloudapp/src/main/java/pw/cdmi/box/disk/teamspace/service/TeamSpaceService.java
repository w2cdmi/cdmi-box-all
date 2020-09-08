package pw.cdmi.box.disk.teamspace.service;

import java.util.List;

import pw.cdmi.box.disk.client.domain.node.INode;
import pw.cdmi.box.disk.oauth2.domain.UserToken;
import pw.cdmi.box.disk.share.domain.mail.RequestAttribute;
import pw.cdmi.box.disk.teamspace.domain.RestTeamMemberInfo;
import pw.cdmi.box.disk.teamspace.domain.RestTeamSpaceInfo;
import pw.cdmi.core.exception.BaseRunException;

public interface TeamSpaceService
{
    /**
     * 
     * @param user
     * @param ownerId
     * @param nodeId
     * @return
     * @throws BaseRunException
     */
    INode getNodeInfo(UserToken user, long ownerId, long nodeId) throws BaseRunException;
    
    /**
     * @param teamId
     * @param loginName
     * @param teamRole
     * @param keyWord
     * @return
     * @throws BaseRunException
     */
    RestTeamMemberInfo getMemberByLoginName(long teamId, String loginName, String teamRole, String keyWord)
        throws BaseRunException;
    
    /**
     * @param userToken
     * @param teamspace
     * @param emails
     * @param message
     * @throws BaseRunException
     */
    void sendAddMemberMail(UserToken userToken, RestTeamSpaceInfo teamspace, String emails, String message)
        throws BaseRunException;
    
    void sendEmailAllMember(String emails, List<RequestAttribute> params);
    
    RestTeamSpaceInfo getTeamSpace(long teamId, String token);
}
