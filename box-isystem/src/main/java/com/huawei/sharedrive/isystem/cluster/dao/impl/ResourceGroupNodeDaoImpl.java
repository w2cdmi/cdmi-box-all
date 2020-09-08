package com.huawei.sharedrive.isystem.cluster.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.huawei.sharedrive.isystem.cluster.dao.ResourceGroupNodeDao;
import com.huawei.sharedrive.isystem.cluster.domain.ResourceGroupNode;

import pw.cdmi.box.dao.impl.AbstractDAOImpl;

@Service("resourceGroupNodeDao")
@SuppressWarnings("deprecation")
public class ResourceGroupNodeDaoImpl extends AbstractDAOImpl implements ResourceGroupNodeDao
{
    @Override
    public void deleteByDC(int dcid)
    {
        sqlMapClientTemplate.delete("ResourceGroupNode.deleteByDC", dcid);
    }
    
    @Override
    public void create(ResourceGroupNode node)
    {
        sqlMapClientTemplate.insert("ResourceGroupNode.insert", node);
    }
    
    @Override
    public void updateRegionByDC(int dcid, int regionID)
    {
        Map<String, Object> paramsMap = new HashMap<String, Object>(2);
        paramsMap.put("dcid", dcid);
        paramsMap.put("regionID", regionID);
        sqlMapClientTemplate.update("ResourceGroupNode.updateRegionByDC", paramsMap);
    }
    
    @Override
    public void delete(ResourceGroupNode node)
    {
        sqlMapClientTemplate.delete("ResourceGroupNode.delete", node);
    }
    
    @Override
    public void update(ResourceGroupNode node)
    {
        sqlMapClientTemplate.update("ResourceGroupNode.update", node);
    }
    
    @SuppressWarnings("unchecked")
    public List<ResourceGroupNode> getResourceGroupNodeByDcID(int dcid)
    {
        return sqlMapClientTemplate.queryForList("ResourceGroupNode.selectByDcID", dcid);
    }
    
    /*
     * 根据managerip 查找 数据中心的名称
     */
    @Override
    public ResourceGroupNode getResourceGroupNodeByManagerIP(String managerIP)
    {
        return (ResourceGroupNode) sqlMapClientTemplate.queryForObject("ResourceGroupNode.selectByManagerIP",
            managerIP);
    }
    
    /*
     * 根据innerIp（私网）查找 数据中心的名称
     */
    @Override
    public ResourceGroupNode getResourceGroupNodeByInnerIP(String inneraddr)
    {
        return (ResourceGroupNode) sqlMapClientTemplate.queryForObject("ResourceGroupNode.selectByInnerIP",
            inneraddr);
    }
}
