package com.huawei.sharedrive.uam.enterprise.service;

import java.util.List;

import com.huawei.sharedrive.uam.enterprise.domain.NetWorkRegionIpExcelImport;

public interface NetworkRegionIpExcelImportService
{
    
    List<NetWorkRegionIpExcelImport> getByAppId(String appId);
    
    List<NetWorkRegionIpExcelImport> getListExcelImport(long accountId);
    
    NetWorkRegionIpExcelImport getById(long id);
    
    int delete(long id);
    
    long insert(String appId, long accountId);
    
    void update(NetWorkRegionIpExcelImport excelImport);
    
}
