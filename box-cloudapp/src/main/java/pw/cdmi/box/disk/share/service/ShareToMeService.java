package pw.cdmi.box.disk.share.service;

import pw.cdmi.box.disk.client.domain.node.INode;
import pw.cdmi.box.disk.oauth2.domain.UserToken;
import pw.cdmi.box.disk.share.domain.INodeShare;
import pw.cdmi.box.disk.share.domain.SharePage;
import pw.cdmi.box.domain.Page;
import pw.cdmi.box.domain.Pageable;
import pw.cdmi.core.exception.ForbiddenException;
import pw.cdmi.core.exception.RestException;

public interface ShareToMeService
{
    
    /**
     * 
     * @param user
     * @param ownerId
     * @param iNodeId
     * @param sharedUserId
     * @param userType
     */
    void cancelShare(UserToken user, long ownerId, long iNodeId, byte userType) throws RestException;
    
    Page<INodeShare> listShareToMe(UserToken user, String name, long sharedUserId, Pageable pageRequest)
        throws RestException;
    
    /**
     * 
     * @param user
     * @param sharedUserId
     * @param pageRequest
     * @return
     */
    SharePage listShareToMeForClient(UserToken user, long sharedUserId, Pageable pageRequest);
    
    /**
     * 
     * @param user
     * @param node
     * @param oper
     * @return
     */
    void validatePermission(UserToken user, INode node, String oper) throws ForbiddenException;
}
