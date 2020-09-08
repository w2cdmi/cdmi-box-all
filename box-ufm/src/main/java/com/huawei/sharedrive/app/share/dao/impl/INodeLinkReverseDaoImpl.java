package com.huawei.sharedrive.app.share.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.huawei.sharedrive.app.core.domain.OrderV1;
import com.huawei.sharedrive.app.exception.InvalidParamException;
import com.huawei.sharedrive.app.share.dao.INodeLinkReverseDao;
import com.huawei.sharedrive.app.share.domain.INodeLink;

import pw.cdmi.box.dao.impl.AbstractDAOImpl;
import pw.cdmi.box.domain.Limit;
import pw.cdmi.core.utils.HashTool;

@Service("iNodeLinkReverseDao")
@SuppressWarnings("deprecation")
public class INodeLinkReverseDaoImpl extends AbstractDAOImpl implements INodeLinkReverseDao
{
    
    @Override
    public void createV2(INodeLink iNodeLink)
    {
        iNodeLink.setTableSuffix(getTableSuffixV2(iNodeLink));
        sqlMapClientTemplate.insert("INodeLinkReverse.insert", iNodeLink);
    }
    
    @Override
    public int deleteByNode(long ownerId, long nodeId, Byte accessCodeMode)
    {
        INodeLink iNodeLink = new INodeLink();
        iNodeLink.setOwnedBy(ownerId);
        iNodeLink.setiNodeId(nodeId);
        iNodeLink.setTableSuffix(getTableSuffixV2(iNodeLink));
        Map<String, Object> map = new HashMap<String, Object>(2);
        map.put("filter", iNodeLink);
        map.put("accessCodeMode", accessCodeMode);
        return sqlMapClientTemplate.delete("INodeLinkReverse.deleteByINodeID", map);
    }
    
    @Override
    public int deleteByOwner(long ownerId)
    {
        INodeLink iNodeLink = new INodeLink();
        iNodeLink.setOwnedBy(ownerId);
        iNodeLink.setTableSuffix(getTableSuffixV2(iNodeLink));
        return sqlMapClientTemplate.delete("INodeLinkReverse.deleteByOwner", iNodeLink);
    }
    
    @Override
    public void deleteV2(INodeLink iNodeLink)
    {
        iNodeLink.setTableSuffix(getTableSuffixV2(iNodeLink));
        sqlMapClientTemplate.delete("INodeLinkReverse.delete", iNodeLink);
    }
    
    @Override
    public int getCountByNode(long ownerId, long nodeId, Byte accessCodeMode)
    {
        INodeLink iNodeLink = new INodeLink();
        iNodeLink.setOwnedBy(ownerId);
        iNodeLink.setiNodeId(nodeId);
        iNodeLink.setTableSuffix(getTableSuffixV2(iNodeLink));
        Map<String, Object> map = new HashMap<String, Object>(2);
        map.put("filter", iNodeLink);
        map.put("accessCodeMode", accessCodeMode);
        return (Integer) sqlMapClientTemplate.queryForObject("INodeLinkReverse.listCountByINodeID", map);
    }
    
    @Override
    public int getCountByOwner(INodeLink filter)
    {
        filter.setTableSuffix(getTableSuffixV2(filter));
        Map<String, Object> map = new HashMap<String, Object>(4);
        map.put("filter", filter);
        return (Integer) sqlMapClientTemplate.queryForObject("INodeLinkReverse.listCountByOwner", map);
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public List<INodeLink> listByNode(long ownerId, long nodeId, Byte accessCodeMode)
    {
        INodeLink iNodeLink = new INodeLink();
        iNodeLink.setOwnedBy(ownerId);
        iNodeLink.setiNodeId(nodeId);
        iNodeLink.setTableSuffix(getTableSuffixV2(iNodeLink));
        Map<String, Object> map = new HashMap<String, Object>(2);
        map.put("filter", iNodeLink);
        map.put("accessCodeMode", accessCodeMode);
        return sqlMapClientTemplate.queryForList("INodeLinkReverse.listByINodeID", map);
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public List<INodeLink> listByOwner(INodeLink filter, OrderV1 order, Limit limit)
    {
        filter.setTableSuffix(getTableSuffixV2(filter));
        Map<String, Object> map = new HashMap<String, Object>(4);
        map.put("filter", filter);
        map.put("order", order);
        map.put("limit", limit);
        return sqlMapClientTemplate.queryForList("INodeLinkReverse.listByOwner", map);
    }
    
    @Override
    public void updateV2(INodeLink iNodeLink)
    {
        iNodeLink.setTableSuffix(getTableSuffixV2(iNodeLink));
        sqlMapClientTemplate.update("INodeLinkReverse.update", iNodeLink);
    }
    
    @Override
    public void upgradePassword(INodeLink iNodeLink)
    {
        iNodeLink.setTableSuffix(getTableSuffixV2(iNodeLink));
        sqlMapClientTemplate.update("INodeLinkReverse.upgradePassword", iNodeLink);
    }
    
	@Override
	public List<String> listAllLinkCodes(long ownerBy) {
		// TODO Auto-generated method stub
		Map<String, Object> map = new HashMap<String, Object>(4);
		map.put("tableSuffix", getTableSuffix(ownerBy));
		map.put("ownedBy", ownerBy);
		return sqlMapClientTemplate.queryForList("INodeLinkReverse.listAllLinkCodes", map);
	}
    
    
    private int getTableSuffix(long ownerId)
    {
        int table = (int) (HashTool.apply(String.valueOf(ownerId)) % INodeLinkDAOImpl.TABLE_COUNT);
        return table;
    }
    
    private int getTableSuffixV2(INodeLink iNodeLink)
    {
        long ownerId = iNodeLink.getOwnedBy();
        if (ownerId <= 0)
        {
            throw new InvalidParamException("illegal owner id " + ownerId);
        }
        return getTableSuffix(ownerId);
    }
    
}
