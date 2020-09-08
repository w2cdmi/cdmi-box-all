/**
 * 
 */
package com.huawei.sharedrive.app.share.service;

import java.util.List;

import com.huawei.sharedrive.app.exception.BaseRunException;
import com.huawei.sharedrive.app.files.domain.INode;
import com.huawei.sharedrive.app.oauth2.domain.UserToken;
import com.huawei.sharedrive.app.openapi.domain.share.MySharesPage;
import com.huawei.sharedrive.app.openapi.domain.share.RestSharePageRequestV2;
import com.huawei.sharedrive.app.openapi.domain.share.SharePageV2;
import com.huawei.sharedrive.app.share.domain.INodeShare;
import com.huawei.sharedrive.app.share.domain.INodeShareDelete;
import com.huawei.sharedrive.app.share.domain.SharedUser;
import com.huawei.sharedrive.app.user.domain.User;

import pw.cdmi.box.domain.Limit;
import pw.cdmi.box.domain.Order;

/**
 * 
 */
public interface ShareService
{
    
    /**
     * 添加共享关系列表
     * 
     * @param user
     * @param ownerId
     * @param shareList
     * @param nodeId
     * @param role
     * @param message
     * @return
     * @throws BaseRunException
     */
    @SuppressWarnings("PMD.ExcessiveParameterList")
    List<INodeShare> addShareListV2(UserToken user, Long ownerId, List<SharedUser> shareList, long nodeId, String role, String message,String additionalLog)
        throws BaseRunException;
    
    
    /**
     * 取消共享
     * 
     * @param user
     * @param ownerId
     * @param iNodeId
     * @throws BaseRunException
     */
    List<INodeShare> cancelAllShareV2(UserToken user, long ownerId, long iNodeId) throws BaseRunException;
    
    
    /**
     * 根据用户ID 用户类型删除共享关系
     * 
     * @param groupId
     * @param userType
     */
    void deleteShareGroup(UserToken user,long groupId,byte userType);
    
    
    /**
     * 删除单条共享关系
     * 
     * @param user
     * @param ownerId
     * @param sharedUserId
     * @param sharedUserType
     * @param iNodeId
     */
    void deleteShareV2(UserToken user, long ownerId, long sharedUserId, byte sharedUserType, long iNodeId)
        throws BaseRunException;
    
    
    /**
     * 系统删除用户的处理
     * 
     * @param userId
     */
    void deleteUserFromSystem(long userId);
    
    
    
    MySharesPage listMyShares(UserToken user, RestSharePageRequestV2 req)throws BaseRunException;
    
    /**
     * 更新用户名
     * 
     * @param user
     */
    void updateUsername(User user);

    SharePageV2 getShareUserListOrderV2(UserToken user, long ownerId, long inodeId, List<Order> orderList,
        Limit limit) throws BaseRunException;

    /**
     * 增加转发记录
     * @param ownerId 拥有者Id
     * @param nodeId iNode Id
     * @param fromId 转发者Id
     * @param toId 接收者Id
     * @return true,生成一条新的转发记录；false，转发已经存在，未生成新的记录。
     */
    boolean addForwardRecord(INodeShare share) throws BaseRunException;


	List<INodeShare> addLinkShare(UserToken curUser, long ownerId, long inodeId,String linkCode, String roleName);
	
	List<INodeShare> addLinkShare(UserToken curUser, long ownerId, String nodeNames,String linkCode, String roleName);


	void deleteLinkShare(UserToken curUser, INodeShare iNodeShare, String linkCode);


	List<INodeShare> deleteAllShare(INode inode, long createdBy) throws BaseRunException;


	void updateNodeNameAndSize(INode inode, long createdBy);


	void updateStatus(INode inode, long createdBy, byte status);


	void addShareDelete(INodeShareDelete iNodeShareDelete);

}
