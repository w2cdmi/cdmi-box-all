package pw.cdmi.box.disk.share.service;

import java.util.List;

import pw.cdmi.box.disk.group.domain.RestGroup;
import pw.cdmi.box.disk.oauth2.domain.UserToken;
import pw.cdmi.box.disk.share.domain.INodeShare;
import pw.cdmi.box.disk.share.domain.INodeShareV2;
import pw.cdmi.box.domain.Page;
import pw.cdmi.box.domain.PageRequest;
import pw.cdmi.core.exception.BaseRunException;
import pw.cdmi.core.exception.RestException;

public interface ShareService
{
    
    /**
     * 
     * @param user
     * @param shareList
     * @param nodeId
     * @return
     * @throws BaseRunException
     */
    @SuppressWarnings("PMD.ExcessiveParameterList")
    String addShareList(UserToken user, List<INodeShareV2> shareList, long nodeId, String message,
        String authType, List<INodeShareV2> failedList) throws BaseRunException, RestException;
    
    /**
     * @param user
     * @param nodeId
     * @param userId
     * @param userType
     * @param authType
     * @return
     * @throws RestException
     */
    void updateShare(UserToken user, long nodeId, long userId, byte userType, String authType)
        throws BaseRunException, RestException;
    
    /**
     * 
     * @param user
     * @param ownerId
     * @param iNodeId
     * @throws BaseRunException
     */
    void cancelAllShare(UserToken user, long ownerId, long iNodeId) throws BaseRunException;
    
    /**
     * 
     * @param user
     * @param ownerId
     * @param sharedUserId
     * @param sharedUserType
     * @param iNodeId
     */
    void deleteShare(UserToken user, long ownerId, long sharedUserId, String sharedUserType, long iNodeId)
        throws RestException;
    
    Page<INodeShare> getShareUserList(UserToken user, long ownerId, long iNodeId, PageRequest pageRequest)
        throws BaseRunException;
    
    List<UserToken> getListUser(String userName);
    
    List<RestGroup> getListGroupByNickName(String groupName);
}
