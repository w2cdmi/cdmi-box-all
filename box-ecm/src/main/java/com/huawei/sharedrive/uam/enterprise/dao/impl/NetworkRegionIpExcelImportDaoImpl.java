package com.huawei.sharedrive.uam.enterprise.dao.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.huawei.sharedrive.uam.enterprise.dao.NetworkRegionIpExcelImportDao;
import com.huawei.sharedrive.uam.enterprise.domain.NetWorkRegionIpExcelImport;
import com.huawei.sharedrive.uam.enterprise.service.NetworkRegionIpExcelGenerateService;

import pw.cdmi.box.dao.impl.CacheableSqlMapClientDAO;

@Service
@SuppressWarnings({"unchecked", "deprecation"})
public class NetworkRegionIpExcelImportDaoImpl extends CacheableSqlMapClientDAO implements
    NetworkRegionIpExcelImportDao
{
    
    @Autowired
    private NetworkRegionIpExcelGenerateService networkRegionIpExcelGenerateService;
    
    @Override
    public List<NetWorkRegionIpExcelImport> getByAppId(String id)
    {
        return sqlMapClientTemplate.queryForList("NetworkRegionIpExcelImport.getByAppID", id);
    }
    
    @Override
    public NetWorkRegionIpExcelImport getById(long id)
    {
        
        return (NetWorkRegionIpExcelImport) sqlMapClientTemplate.queryForObject("NetworkRegionIpExcelImport.getByID",
            id);
    }
    
    @Override
    public int delete(long id)
    {
        return sqlMapClientTemplate.delete("NetworkRegionIpExcelImport.delete", id);
    }
    
    @Override
    public long insert(NetWorkRegionIpExcelImport excelImport)
    {
        long id = networkRegionIpExcelGenerateService.getNextId();
        excelImport.setId(id);
        sqlMapClientTemplate.insert("NetworkRegionIpExcelImport.insert", excelImport);
        return id;
    }
    
    @Override
    public void update(NetWorkRegionIpExcelImport excelImport)
    {
        sqlMapClientTemplate.update("NetworkRegionIpExcelImport.update", excelImport);
    }
    
    @Override
    public List<NetWorkRegionIpExcelImport> getList(long accountId)
    {
        return sqlMapClientTemplate.queryForList("NetworkRegionIpExcelImport.getList", accountId);
    }
    
    @Override
    public long getMaxId()
    {
        long selMaxUserId;
        Object maxUserIdObject = sqlMapClientTemplate.queryForObject("NetworkRegionIpExcelImport.getMaxId",
            null);
        selMaxUserId = maxUserIdObject == null ? 0L : (long) maxUserIdObject;
        return selMaxUserId;
    }
    
}
