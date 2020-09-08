package com.huawei.sharedrive.app.acl.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import com.huawei.sharedrive.app.acl.dao.INodeACLDAO;
import com.huawei.sharedrive.app.acl.domain.INodeACL;
import com.huawei.sharedrive.app.exception.InvalidParamException;

import pw.cdmi.box.dao.impl.AbstractDAOImpl;
import pw.cdmi.box.domain.Limit;
import pw.cdmi.box.domain.Order;
import pw.cdmi.core.utils.HashTool;

@Service("iNodeACLDAO")
@SuppressWarnings("deprecation")
public class INodeACLDAOImpl extends AbstractDAOImpl implements INodeACLDAO
{
    private static final long BASE_NODE_ID = 0;
    
    private final static int TABLE_COUNT = 100;
    
    private int getTableSuffix(INodeACL iNodeACL)
    {
        long ownerId = iNodeACL.getOwnedBy();
        if (ownerId <= 0)
        {
            throw new InvalidParamException("illegal owner id " + ownerId);
        }
        return getTableSuffix(ownerId);
    }
    
    private int getTableSuffix(long ownerId)
    {
        int table = (int) (HashTool.apply(String.valueOf(ownerId)) % TABLE_COUNT);
        
        return table;
    }
    
    @Override
    public void create(INodeACL iNodeACL)
    {
        iNodeACL.setTableSuffix(getTableSuffix(iNodeACL));
        sqlMapClientTemplate.insert("INodeACL.insert", iNodeACL);
    }
    
    @Override
    public void updateById(INodeACL iNodeACL)
    {
        iNodeACL.setTableSuffix(getTableSuffix(iNodeACL));
        sqlMapClientTemplate.update("INodeACL.updateRole", iNodeACL);
    }
    
    @Override
    public void delete(long ownedBy, long id)
    {
        INodeACL iNodeACL = new INodeACL();
        iNodeACL.setOwnedBy(ownedBy);
        iNodeACL.setId(id);
        iNodeACL.setTableSuffix(getTableSuffix(iNodeACL));
        sqlMapClientTemplate.delete("INodeACL.delete", iNodeACL);
    }
    
    @Override
    public void deleteByUser(long ownedBy, String userId, String userType)
    {
        INodeACL iNodeACL = new INodeACL();
        iNodeACL.setOwnedBy(ownedBy);
        iNodeACL.setAccessUserId(userId);
        iNodeACL.setUserType(userType);
        
        iNodeACL.setTableSuffix(getTableSuffix(iNodeACL));
        sqlMapClientTemplate.delete("INodeACL.deleteByUser", iNodeACL);
    }
    
    @Override
    public void deleteByResourceAndUser(long ownedBy, long iNodeId, String userId, String userType)
    {
        INodeACL iNodeACL = new INodeACL();
        iNodeACL.setOwnedBy(ownedBy);
        iNodeACL.setiNodeId(iNodeId);
        iNodeACL.setAccessUserId(userId);
        iNodeACL.setUserType(userType);
        
        iNodeACL.setTableSuffix(getTableSuffix(iNodeACL));
        sqlMapClientTemplate.delete("INodeACL.deleteByResourceAndUser", iNodeACL);
    }
    
    @Override
    public INodeACL get(long ownedBy, long id)
    {
        INodeACL iNodeACL = new INodeACL();
        iNodeACL.setOwnedBy(ownedBy);
        iNodeACL.setId(id);
        
        iNodeACL.setTableSuffix(getTableSuffix(iNodeACL));
        return (INodeACL) sqlMapClientTemplate.queryForObject("INodeACL.get", iNodeACL);
    }
    
    @Override
    public INodeACL getByResourceAndUser(long ownedBy, long nodeId, String user, String userType)
    {
        INodeACL iNodeACL = new INodeACL();
        iNodeACL.setOwnedBy(ownedBy);
        iNodeACL.setiNodeId(nodeId);
        iNodeACL.setAccessUserId(user);
        iNodeACL.setUserType(userType);
        
        iNodeACL.setTableSuffix(getTableSuffix(iNodeACL));
        return (INodeACL) sqlMapClientTemplate.queryForObject("INodeACL.getByResourceAndUser", iNodeACL);
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public List<INodeACL> getAll(long ownerBy, List<Order> orderList, Limit limit)
    {
        INodeACL iNodeACL = new INodeACL();
        iNodeACL.setOwnedBy(ownerBy);
        iNodeACL.setTableSuffix(getTableSuffix(iNodeACL));
        
        Map<String, Object> map = new HashMap<String, Object>(3);
        if (CollectionUtils.isNotEmpty(orderList))
        {
            map.put("orderBy", getOrderByStr(orderList));
        }
        map.put("filter", iNodeACL);
        map.put("limit", limit);
        
        return sqlMapClientTemplate.queryForList("INodeACL.getAll", map);
    }
    
    @Override
    public long getAllCount(long ownerBy)
    {
        INodeACL iNodeACL = new INodeACL();
        iNodeACL.setOwnedBy(ownerBy);
        iNodeACL.setTableSuffix(getTableSuffix(iNodeACL));
        return (long) sqlMapClientTemplate.queryForObject("INodeACL.getAllCount", iNodeACL);
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public List<INodeACL> getByResource(long ownerBy, long inodeID, List<Order> orderList, Limit limit)
    {
        INodeACL iNodeACL = new INodeACL();
        iNodeACL.setOwnedBy(ownerBy);
        iNodeACL.setiNodeId(inodeID);
        iNodeACL.setTableSuffix(getTableSuffix(iNodeACL));
        
        Map<String, Object> map = new HashMap<String, Object>(3);
        if (CollectionUtils.isNotEmpty(orderList))
        {
            map.put("orderBy", getOrderByStr(orderList));
        }
        map.put("filter", iNodeACL);
        map.put("limit", limit);
        
        return sqlMapClientTemplate.queryForList("INodeACL.getByResource", map);
    }
    
    @Override
    public long getByResourceCount(long ownerBy, long inodeID)
    {
        INodeACL iNodeACL = new INodeACL();
        iNodeACL.setOwnedBy(ownerBy);
        iNodeACL.setiNodeId(inodeID);
        iNodeACL.setTableSuffix(getTableSuffix(iNodeACL));
        return (long) sqlMapClientTemplate.queryForObject("INodeACL.getByResourceCount", iNodeACL);
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public List<INodeACL> getAllNoLink(long ownerBy, List<Order> orderList, Limit limit)
    {
        INodeACL iNodeACL = new INodeACL();
        iNodeACL.setOwnedBy(ownerBy);
        // 需要排除外链
        iNodeACL.setUserType(INodeACL.TYPE_LINK);
        iNodeACL.setTableSuffix(getTableSuffix(iNodeACL));
        
        Map<String, Object> map = new HashMap<String, Object>(3);
        if (CollectionUtils.isNotEmpty(orderList))
        {
            map.put("orderBy", getOrderByStr(orderList));
        }
        map.put("filter", iNodeACL);
        map.put("limit", limit);
        
        return sqlMapClientTemplate.queryForList("INodeACL.getAllNoLink", map);
    }
    
    @Override
    public long getAllCountNoLink(long ownerBy)
    {
        INodeACL iNodeACL = new INodeACL();
        iNodeACL.setOwnedBy(ownerBy);
        // 需要排除外链
        iNodeACL.setUserType(INodeACL.TYPE_LINK);
        iNodeACL.setTableSuffix(getTableSuffix(iNodeACL));
        return (long) sqlMapClientTemplate.queryForObject("INodeACL.getAllCountNoLink", iNodeACL);
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public List<INodeACL> getByResourceNoLink(long ownerBy, long inodeID, List<Order> orderList, Limit limit)
    {
        INodeACL iNodeACL = new INodeACL();
        iNodeACL.setOwnedBy(ownerBy);
        iNodeACL.setiNodeId(inodeID);
        // 需要排除外链
        iNodeACL.setUserType(INodeACL.TYPE_LINK);
        iNodeACL.setTableSuffix(getTableSuffix(iNodeACL));
        
        Map<String, Object> map = new HashMap<String, Object>(3);
        if (CollectionUtils.isNotEmpty(orderList))
        {
            map.put("orderBy", getOrderByStr(orderList));
        }
        map.put("filter", iNodeACL);
        map.put("limit", limit);
        
        return sqlMapClientTemplate.queryForList("INodeACL.getByResourceNoLink", map);
    }
    
    @Override
    public long getByResourceCountNoLink(long ownerBy, long inodeID)
    {
        INodeACL iNodeACL = new INodeACL();
        iNodeACL.setOwnedBy(ownerBy);
        iNodeACL.setiNodeId(inodeID);
        // 需要排除外链
        iNodeACL.setUserType(INodeACL.TYPE_LINK);
        iNodeACL.setTableSuffix(getTableSuffix(iNodeACL));
        return (long) sqlMapClientTemplate.queryForObject("INodeACL.getByResourceCountNoLink", iNodeACL);
    }
    
    @Override
    public void updateByResourceAndUser(INodeACL iNodeACL)
    {
        iNodeACL.setTableSuffix(getTableSuffix(iNodeACL));
        sqlMapClientTemplate.update("INodeACL.updateRoleByResourceAndUser", iNodeACL);
        
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public List<INodeACL> getAllByUser(long ownedBy, String accessUserID, String userType)
    {
        INodeACL iNodeACL = new INodeACL();
        iNodeACL.setOwnedBy(ownedBy);
        iNodeACL.setUserType(userType);
        iNodeACL.setAccessUserId(accessUserID);
        iNodeACL.setTableSuffix(getTableSuffix(iNodeACL));
        return sqlMapClientTemplate.queryForList("INodeACL.getUserAllINodeACLOfCloudUserID", iNodeACL);
    }
    @SuppressWarnings("unchecked")
    @Override
    public List<INodeACL> getAllByOwnedBy(long ownedBy, String userType)
    {
        INodeACL iNodeACL = new INodeACL();
        iNodeACL.setOwnedBy(ownedBy);
        iNodeACL.setUserType(userType);
        iNodeACL.setTableSuffix(getTableSuffix(iNodeACL));
        return sqlMapClientTemplate.queryForList("INodeACL.getAllByOwnedBy", iNodeACL);
    }
    @Override
    public long getMaxINodeACLId(long ownedBy)
    {
        INodeACL iNodeACL = new INodeACL();
        iNodeACL.setOwnedBy(ownedBy);
        iNodeACL.setTableSuffix(getTableSuffix(iNodeACL));
        Object maxINodeACLId = sqlMapClientTemplate.queryForObject("INodeACL.getMaxINodeACLId", iNodeACL);
        if (maxINodeACLId == null)
        {
            return BASE_NODE_ID;
        }
        return (Long) maxINodeACLId;
    }
    
    private String getOrderByStr(List<Order> orderList)
    {
        StringBuffer orderBy = new StringBuffer();
        for (Order order : orderList)
        {
            orderBy.append(order.getField()).append(' ').append(order.getDirection()).append(',');
        }
        orderBy = orderBy.deleteCharAt(orderBy.length() - 1);
        return orderBy.toString();
    }
    
    @Override
    public void deleteSpaceAll(Long teamSpaceId)
    {
        INodeACL iNodeACL = new INodeACL();
        iNodeACL.setOwnedBy(teamSpaceId);
        iNodeACL.setTableSuffix(getTableSuffix(iNodeACL));
        sqlMapClientTemplate.update("INodeACL.deleteSpaceAll", iNodeACL);
    }
    
    @Override
    public int deleteByResource(long ownedBy, long nodeId)
    {
        INodeACL iNodeACL = new INodeACL();
        iNodeACL.setOwnedBy(ownedBy);
        iNodeACL.setiNodeId(nodeId);
        
        iNodeACL.setTableSuffix(getTableSuffix(iNodeACL));
        return (int) sqlMapClientTemplate.delete("INodeACL.deleteByResource", iNodeACL);
    }
}
