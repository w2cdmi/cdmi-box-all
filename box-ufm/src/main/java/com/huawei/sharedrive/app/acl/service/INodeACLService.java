package com.huawei.sharedrive.app.acl.service;

import java.util.List;

import com.huawei.sharedrive.app.acl.domain.ACL;
import com.huawei.sharedrive.app.acl.domain.INodeACL;
import com.huawei.sharedrive.app.acl.domain.INodeACLList;
import com.huawei.sharedrive.app.acl.domain.ResourceRole;
import com.huawei.sharedrive.app.exception.BaseRunException;
import com.huawei.sharedrive.app.files.domain.INode;
import com.huawei.sharedrive.app.oauth2.domain.UserToken;

import pw.cdmi.box.domain.Limit;
import pw.cdmi.box.domain.Order;

public interface INodeACLService
{
    /**
     * 添加 ACL
     * 
     * @param iNodeACL
     * @return
     * @throws BaseRunException
     */
    INodeACL addINodeACL(INodeACL iNodeACL) throws BaseRunException;
    
    /**
     * 删除指定用户及节点ACL
     * 
     * @param ownerId
     * @param iNodeId
     * @param userId
     * @param userType
     */
    void deleteByResourceAndUser(long ownerId, long iNodeId, String userId, String userType);
    
    /**
     * 通过id删除ACL
     * 
     * @param iNodeACL
     * @throws BaseRunException
     */
    void deleteINodeACLById(long ownedBy, long id) throws BaseRunException;
    
    /**
     * 删除资源所有的ACL
     * 
     * @param ownedBy
     * @param nodeId
     * @throws BaseRunException
     */
    void deleteINodeAllACLs(long ownedBy, long nodeId) throws BaseRunException;
    
    /**
     * 通过user删除ACL
     * 
     * @param ownedBy
     * @param userId
     * @param userType
     * @throws BaseRunException
     */
    void deleteSpaceACLsByUser(long ownedBy, String userId, String userType) throws BaseRunException;
    
    /**
     * 删除空间所有的ACL
     * 
     * @param ownerId
     * @throws BaseRunException
     */
    void deleteSpaceAllACLs(long ownerId) throws BaseRunException;
    
    /**
     * 用户访问权限，实际用户
     * 
     * @param userId
     * @param userType 未使用
     * @param inode
     * @param userToken 
     * @return
     */
    ACL getACLForAccessUser(long userId, String userType, INode inode,String enterpriseId, UserToken userToken);
    
    /**
     * 用户访问权限，实际用户
     * 
     * @param userId
     * @param userType 未使用
     * @param inode
     * @return
     */
    ACL getACLForLink(String linkCode, INode inode);
    
    /**s
     * 通过user获取资源ACL
     * 
     * @param ownedBy
     * @param iNodeId
     * @param userId
     * @param userType
     * @return
     */
    INodeACL getByResourceAndUser(long ownedBy, long iNodeId, String userId, String userType);
    
    /**
     * 通过id获取ACL
     * 
     * @param ownerId
     * @param Id
     * @return
     */
    INodeACL getINodeACLById(long ownerId, long id);
    
    /**
     * 获取节点acl总数
     * 
     * @param ownerBy
     * @param nodeId
     * @return
     */
    long getINodeACLsCount(long ownerBy, long nodeId);
    
    /**
     * 实际用户
     * 
     * @param ownedBy
     * @param inodeId
     * @param userId
     * @param userType 未使用
     * @return
     */
    List<INodeACL> getINodeACLSelfAndAnyACLs(long ownedBy, long inodeId, long userId,String enterpriseId,UserToken userToken);
    
    
    
    /**
     * 列举拥有者所有访问控制
     * 
     * @param ownerId
     * @param orderList
     * @param limit
     * @return
     */
    INodeACLList listAllACLs(long ownerId, List<Order> orderList, Limit limit);
    
    /**
     * 列举节点的访问控制
     * 
     * @param ownerId
     * @param nodeId
     * @param orderList
     * @param limit
     * @return
     */
    INodeACLList listINodeACLs(long ownerId, long nodeId, List<Order> orderList, Limit limit);
    
    /**
     * 列举系统角色
     * 
     * @return
     */
    List<ResourceRole> listResourceRole();
    
    /**
     * 列举指定创建者的资源角色
     * 
     * @param createdBy
     * @return
     */
    List<ResourceRole> listResourceRole(long createdBy);
    
    /**
     * 更新acl
     * 
     * @param iNodeACL
     * @return
     * @throws BaseRunException
     */
    INodeACL modifyINodeACLById(INodeACL iNodeACL) throws BaseRunException;
    
    long vaildINodeOperACL(UserToken user, INode node, String oper) throws BaseRunException;
    
    long vaildINodeOperACL(UserToken user, INode node, String oper, boolean valiAccessCode)
        throws BaseRunException;
    /**
     * 对节点添加可见，不可见权限
     * 
     * @param createdBy
     * @return
     */
	INodeACL addINodeIsVisibleACL(INodeACL iNodeACL) throws BaseRunException;
	 /**
     * 获取节点是否设置不可见权限
     * 
     * @param createdBy
     * @return
     */
	INodeACL getNodeIsVisibleACL(INodeACL inodeRole);


    
}
