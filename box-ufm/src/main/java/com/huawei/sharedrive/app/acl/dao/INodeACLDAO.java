package com.huawei.sharedrive.app.acl.dao;

import java.util.List;

import com.huawei.sharedrive.app.acl.domain.INodeACL;

import pw.cdmi.box.domain.Limit;
import pw.cdmi.box.domain.Order;

/**
 * 
 * @author c00110381
 * 
 */
public interface INodeACLDAO
{
    /**
     * 添加acl:定位 ownedBy, id
     * 
     * @param iNodeACL
     */
    void create(INodeACL iNodeACL);
    
    /**
     * 根据id更新acl:定位 ownedBy, id
     * 
     * @param iNodeACL
     */
    void updateById(INodeACL iNodeACL);
    
    /**
     * 根据用户和资源id更新acl
     * 
     * @param iNodeACL
     */
    void updateByResourceAndUser(INodeACL iNodeACL);
    
    /**
     * 删除指定acl
     * 
     * @param ownedBy
     * @param id
     */
    void delete(long ownedBy, long id);
    
    /**
     * 删除资源拥有者或空间所有acl
     * 
     * @param ownedBy
     */
    void deleteSpaceAll(Long ownedBy);

    /**
     * 删除用户所有acl
     * 
     * @param ownedBy
     * @param userId
     * @param userType
     */
    void deleteByUser(long ownedBy, String userId, String userType);
    
    /**
     * 删除资源所有acl
     * 
     * @param ownedBy
     * @param nodeId
     * @return
     */
    int deleteByResource(long ownedBy, long nodeId);
    
    /**
     * 根据用户和资源id删除对应acl
     * 
     * @param ownedBy
     * @param iNodeId
     * @param userId
     * @param userType
     */
    void deleteByResourceAndUser(long ownedBy, long iNodeId, String userId, String userType);
    /**
     * 获取指定acl
     * 
     * @param ownedBy
     * @param id
     * @return
     */
    INodeACL get(long ownedBy, long id);
    
    /**
     * 获取资源拥有者或团队空间的所有acl
     * 
     * @param ownerBy
     * @param orderList
     * @param limit
     * @return
     */
    List<INodeACL> getAll(long ownerBy, List<Order> orderList, Limit limit);
    
    /**
     * 获取资源拥有者或团队空间的acl总数
     * 
     * @param ownerBy
     * @return
     */
    long getAllCount(long ownerBy);
    
    /**
     * 获取指定资源的所有acl
     * 
     * @param ownerBy
     * @param nodeId
     * @param orderList
     * @param limit
     * @return
     */
    List<INodeACL> getByResource(long ownerBy, long nodeId, List<Order> orderList, Limit limit);
    
    /**
     * 获取指定资源的ACL总数
     * 
     * @param ownerBy
     * @param nodeId
     * @return
     */
    long getByResourceCount(long ownerBy, long nodeId);
 
    /**
     * 获取资源拥有者或团队空间的所有acl(不包括外链 ACL)
     * 
     * @param ownerBy
     * @param orderList
     * @param limit
     * @return
     */
    List<INodeACL> getAllNoLink(long ownerBy, List<Order> orderList, Limit limit);
    
    /**
     * 获取资源拥有者或团队空间的acl总数(不包括外链 ACL)
     * 
     * @param ownerBy
     * @return
     */
    long getAllCountNoLink(long ownerBy);
    
    /**
     * 获取指定资源的所有acl(不包括外链 ACL)
     * 
     * @param ownerBy
     * @param nodeId
     * @param orderList
     * @param limit
     * @return
     */
    List<INodeACL> getByResourceNoLink(long ownerBy, long nodeId, List<Order> orderList, Limit limit);
    
    /**
     * 获取指定资源的ACL总数(不包括外链 ACL)
     * 
     * @param ownerBy
     * @param nodeId
     * @return
     */
    long getByResourceCountNoLink(long ownerBy, long nodeId);
    

    /**
     * 获取用户对指定资源的acl
     * 
     * @param iNodeACL
     * @return
     */
    INodeACL getByResourceAndUser(long ownedBy, long nodeId, String userId, String userType);
    
    /**
     * 获取该用户对该空间所有的权限控制
     * @param ownedBy
     * @param user
     * @param userType
     * @return
     */
    List<INodeACL> getAllByUser(long ownedBy,String userId,String userType);
    
    /**
     * 获取指定用户INodeACLId当前最大值
     * 
     * @param ownedBy
     * @return
     */
    long getMaxINodeACLId(long ownedBy);

    
    /**
     * 获取owned对该空间的全部权限
     * 
     * @param ownedBy
     * @return
     */
	List<INodeACL> getAllByOwnedBy(long ownedBy, String userType);

}
