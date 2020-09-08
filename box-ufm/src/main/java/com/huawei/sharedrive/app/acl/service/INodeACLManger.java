package com.huawei.sharedrive.app.acl.service;

import java.util.List;

import com.huawei.sharedrive.app.acl.domain.ACL;
import com.huawei.sharedrive.app.acl.domain.INodeACL;
import com.huawei.sharedrive.app.acl.domain.INodeACLList;
import com.huawei.sharedrive.app.exception.BaseRunException;
import com.huawei.sharedrive.app.oauth2.domain.UserToken;

import pw.cdmi.box.domain.Limit;
import pw.cdmi.box.domain.Order;

/**
 * @author t00159390
 * 
 */
public interface INodeACLManger
{
    /**
     * 添加acl
     * 
     * @param user
     * @param iNodeACL
     * @return
     * @throws BaseRunException
     */
    INodeACL addINodeACL(UserToken user, INodeACL iNodeACL) throws BaseRunException;
    
    /**
     * 根据ID删除acl
     * 
     * @param user
     * @param ownedBy
     * @param id
     * @throws BaseRunException
     */
    void deleteINodeACLById(UserToken user, long ownedBy, long id) throws BaseRunException;
    
    /**
     * 删除节点所有的acl
     * 
     * @param user
     * @param ownedBy
     * @param nodeId
     * @throws BaseRunException
     */
    void deleteINodeAllACLs(UserToken user, long ownedBy, long nodeId) throws BaseRunException;
    
    /**
     * 通过id获取ACL
     * 
     * @param user
     * @param ownedBy
     * @param id
     * @return
     * @throws BaseRunException
     */
    INodeACL getINodeACLById(UserToken user, long ownedBy, long id) throws BaseRunException;
    
    /**
     * 列举节点的访问控制
     * 
     * @param user
     * @param ownedBy
     * @param iNodeId
     * @param orderList
     * @param limit
     * @return
     * @throws BaseRunException
     */
    INodeACLList listINodeACLs(UserToken user, long ownedBy, long iNodeId, List<Order> orderList, Limit limit)
        throws BaseRunException;
    
    /**
     * 列举拥有者所有访问控制
     * 
     * @param user
     * @param ownedBy
     * @param orderList
     * @param limit
     * @return
     * @throws BaseRunException
     */
    INodeACLList listAllACLs(UserToken user, long ownedBy, List<Order> orderList, Limit limit)
        throws BaseRunException;
    
    /**
     * 修改acl
     * 
     * @param user
     * @param iNodeACL
     * @return
     * @throws BaseRunException
     */
    INodeACL modifyINodeACLById(UserToken user, INodeACL iNodeACL) throws BaseRunException;
    
    /**
     * 获取指定用户的资源访问控制权限项
     * 
     * @param userToken
     * @param ownerId
     * @param nodeId
     * @param userId
     * @param userType
     * @throws BaseRunException
     */
    ACL getINodePermissionsByUser(UserToken userToken, Long ownerId, Long nodeId, long userId, String userType)
        throws BaseRunException;
    
    /**
     * 获取指定外链的资源访问控制权限项
     * 
     * @param userToken
     * @param ownerId
     * @param nodeId
     * @param userId
     * @param userType
     * @throws BaseRunException
     */
    ACL getINodePermissionsByLink(UserToken userToken, Long ownerId, Long nodeId, String linkCode)
        throws BaseRunException;

    
    /**
     * 当前节点设置是否对其他人可见权限，有就不管 ，没有就添加
     * 
     * @param userToken
     * @param ownerId
     * @param nodeId
     * @param userId
     * @param userType
     * @throws BaseRunException
     */

	INodeACL modifyNodeIsVisibleACL(UserToken userToken, INodeACL iNodeACL, String isavalible);
	/**
     * 获取当前节点是否对其他人可见权限
     * 
     * @param userToken
     * @param ownerId
     * @param nodeId
     * @param userId
     * @param userType
     * @throws BaseRunException
     */
	INodeACL getNodeIsVisibleACL(UserToken userToken, INodeACL inodeRole);
}
