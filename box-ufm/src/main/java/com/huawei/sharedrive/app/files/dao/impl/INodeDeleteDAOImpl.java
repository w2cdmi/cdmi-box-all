package com.huawei.sharedrive.app.files.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.huawei.sharedrive.app.core.domain.OrderV1;
import com.huawei.sharedrive.app.files.dao.INodeDeleteDAO;
import com.huawei.sharedrive.app.files.domain.INode;

import pw.cdmi.box.dao.impl.AbstractDAOImpl;
import pw.cdmi.box.domain.Limit;
import pw.cdmi.core.utils.HashTool;

/**
 * 
 * @author c00110381
 * 
 */
@Service("iNodeDeleteDAO")
@SuppressWarnings("deprecation")
public class INodeDeleteDAOImpl extends AbstractDAOImpl implements INodeDeleteDAO
{
    private static final int TABLE_COUNT = 500;
    
    @Override
    public void create(INode iNode)
    {
        iNode.setTableSuffix(getTableSuffix(iNode));
        sqlMapClientTemplate.insert("INodeDelete.insert", iNode);
    }
    
    @Override
    public int delete(INode iNode)
    {
        iNode.setTableSuffix(getTableSuffix(iNode));
        return sqlMapClientTemplate.delete("INodeDelete.delete", iNode);
    }
    
    @Override
    public INode get(INode iNode)
    {
        iNode.setTableSuffix(getTableSuffix(iNode));
        return (INode) sqlMapClientTemplate.queryForObject("INodeDelete.get", iNode);
    }
    
    @Override
    public INode get(long ownerId, long inodeId)
    {
        INode iNode = new INode();
        iNode.setOwnedBy(ownerId);
        iNode.setId(inodeId);
        iNode.setTableSuffix(getTableSuffix(iNode));
        return (INode) sqlMapClientTemplate.queryForObject("INodeDelete.get", iNode);
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public List<INode> getINodeByStatus(INode filter, OrderV1 order, Limit limit)
    {
        filter.setTableSuffix(getTableSuffix(filter));
        
        Map<String, Object> map = new HashMap<String, Object>(3);
        map.put("filter", filter);
        map.put("order", order);
        map.put("limit", limit);
        
        return sqlMapClientTemplate.queryForList("INodeDelete.getbystatus", map);
    }
    
    @Override
    public void update(INode iNode)
    {
        iNode.setTableSuffix(getTableSuffix(iNode));
        sqlMapClientTemplate.update("INodeDelete.update", iNode);
    }
    
    private int getTableSuffix(INode iNode)
    {
        long ownerId = iNode.getOwnedBy();
        if (ownerId <= 0)
        {
            throw new IllegalArgumentException("illegal owner id " + ownerId);
        }
        return getTableSuffix(ownerId);
    }
    
    private int getTableSuffix(long ownerId)
    {
        int database = (int) (HashTool.apply(String.valueOf(ownerId)) % TABLE_COUNT);
        return database;
    }
    
}
