package com.huawei.sharedrive.app.dataserver.dao.impl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.huawei.sharedrive.app.dataserver.dao.ResourceGroupNodeDao;
import com.huawei.sharedrive.app.dataserver.domain.ResourceGroupNode;

import pw.cdmi.box.dao.impl.AbstractDAOImpl;

@Service("resourceGroupNodeDao")
@SuppressWarnings("deprecation")
public class ResourceGroupNodeDaoImpl extends AbstractDAOImpl implements ResourceGroupNodeDao
{
    @Override
    public void create(ResourceGroupNode node)
    {
        sqlMapClientTemplate.insert("ResourceGroupNode.insert", node);
    }
    
    @Override
    public void delete(ResourceGroupNode node)
    {
        sqlMapClientTemplate.delete("ResourceGroupNode.delete", node);
    }
    
    @Override
    public void deleteByDC(int dcid)
    {
        sqlMapClientTemplate.delete("ResourceGroupNode.deleteByDC", dcid);
    }
    
    @Override
    public void update(ResourceGroupNode node)
    {
        sqlMapClientTemplate.update("ResourceGroupNode.update", node);
    }
    
    @Override
    public void updateRegionByDC(int dcid, int regionID)
    {
        Map<String, Object> params = new HashMap<String, Object>(2);
        params.put("dcid", dcid);
        params.put("regionID", regionID);
        sqlMapClientTemplate.update("ResourceGroupNode.updateRegionByDC", params);
    }

    @Override
    public ResourceGroupNode getResourceGroupNodeByManagerIp(String managerIp)
    {        
        return  (ResourceGroupNode) sqlMapClientTemplate.queryForObject("ResourceGroupNode.getNodeByManagerIp", managerIp);
    }
}
