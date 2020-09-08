package com.huawei.sharedrive.uam.enterprise.dao;

import java.util.List;

import com.huawei.sharedrive.uam.enterprise.domain.NetWorkRegionIpExcelImport;

public interface NetworkRegionIpExcelImportDao
{
    
    List<NetWorkRegionIpExcelImport> getByAppId(String id);
    
    List<NetWorkRegionIpExcelImport> getList(long accountId);
    
    NetWorkRegionIpExcelImport getById(long id);
    
    int delete(long id);
    
    long insert(NetWorkRegionIpExcelImport excelImport);
    
    void update(NetWorkRegionIpExcelImport excelImport);
    
    long getMaxId();
    
}
