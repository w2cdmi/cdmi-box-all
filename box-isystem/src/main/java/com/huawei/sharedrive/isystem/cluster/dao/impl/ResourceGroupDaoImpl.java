package com.huawei.sharedrive.isystem.cluster.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.huawei.sharedrive.isystem.cluster.dao.ResourceGroupDao;
import com.huawei.sharedrive.isystem.cluster.domain.ResourceGroup;

import pw.cdmi.box.dao.impl.AbstractDAOImpl;

@Service("ResourceGroupDAO")
@SuppressWarnings("deprecation")
public class ResourceGroupDaoImpl extends AbstractDAOImpl implements ResourceGroupDao
{
    @Override
    public ResourceGroup get(String managerIP, int managerPort)
    {
        Map<String, Object> paramsMap = new HashMap<String, Object>(2);
        paramsMap.put("managerIP", managerIP);
        paramsMap.put("managerPort", managerPort);
        return (ResourceGroup) sqlMapClientTemplate.queryForObject("ResourceGroup.selectByAddr", paramsMap);
    }
    
    @Override
    public ResourceGroup get(Integer id)
    {
        return (ResourceGroup) sqlMapClientTemplate.queryForObject("ResourceGroup.select", id);
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public List<ResourceGroup> getAll()
    {
        return sqlMapClientTemplate.queryForList("ResourceGroup.getAll");
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public List<ResourceGroup> getAllByDC(int dcid)
    {
        return sqlMapClientTemplate.queryForList("ResourceGroup.getAllByDC", dcid);
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public List<ResourceGroup> getAllByRegion(int regionID)
    {
        return sqlMapClientTemplate.queryForList("ResourceGroup.getAllByRegion", regionID);
    }
    
    @Override
    public void deleteByDC(int dcid)
    {
        sqlMapClientTemplate.delete("ResourceGroup.deleteByDC", dcid);
    }
    
    @Override
    public void delete(Integer id)
    {
        sqlMapClientTemplate.delete("ResourceGroup.delete", id);
    }
    
    @Override
    public void create(ResourceGroup dataCenter)
    {
        sqlMapClientTemplate.insert("ResourceGroup.insert", dataCenter);
    }
    
    @Override
    public void update(ResourceGroup resourceGroup)
    {
        sqlMapClientTemplate.update("ResourceGroup.update", resourceGroup);
    }
    
    @Override
    public void updateStatistic(ResourceGroup resourceGroup)
    {
        sqlMapClientTemplate.update("ResourceGroup.updateStatistic", resourceGroup);
    }
    
    @Override
    public int getNextAvailableDataCenterId()
    {
        Map<String, Object> map = new HashMap<String, Object>(2);
        map.put("param", "resourceGroupId");
        sqlMapClientTemplate.queryForObject("getNextId", map);
        Long id = (Long) map.get("returnid");
        return id.intValue();
    }
    
    @Override
    public void updateRegionByDC(int dcid, int regionID)
    {
        Map<String, Object> params = new HashMap<String, Object>(2);
        params.put("dcid", dcid);
        params.put("regionID", regionID);
        sqlMapClientTemplate.update("ResourceGroup.updateRegionByDC", params);
    }
    
    @Override
    public void updateStatus(int dcid, int status)
    {
        Map<String, Object> params = new HashMap<String, Object>(2);
        params.put("dcid", dcid);
        params.put("status", status);
        sqlMapClientTemplate.update("ResourceGroup.updateStatus", params);
    }

    @Override
    public void updataRWStatus(int dcid, int status)
    {
        Map<String, Object> params = new HashMap<String, Object>(2);
        params.put("dcid", dcid);
        params.put("rwStatus", status);
        sqlMapClientTemplate.update("ResourceGroup.updateRWStatus", params);
    }
}
