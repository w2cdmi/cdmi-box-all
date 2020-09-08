package com.huawei.sharedrive.uam.enterprise.service.impl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.huawei.sharedrive.uam.enterprise.dao.NetworkRegionIpExcelImportDao;
import com.huawei.sharedrive.uam.enterprise.domain.NetWorkRegionIpExcelImport;
import com.huawei.sharedrive.uam.enterprise.service.NetworkRegionIpExcelImportService;

@Service
public class NetworkRegionIpExcelImportServiceImpl implements NetworkRegionIpExcelImportService
{
    @Autowired
    private NetworkRegionIpExcelImportDao excelImportDao;
    
    @Override
    public List<NetWorkRegionIpExcelImport> getByAppId(String appId)
    {
        return excelImportDao.getByAppId(appId);
    }
    
    public List<NetWorkRegionIpExcelImport> getListExcelImport(long accountId)
    {
        return excelImportDao.getList(accountId);
    }
    
    @Override
    public NetWorkRegionIpExcelImport getById(long id)
    {
        return excelImportDao.getById(id);
    }
    
    @Override
    public int delete(long id)
    {
        return excelImportDao.delete(id);
        
    }
    
    @Override
    public long insert(String appId, long accountId)
    {
        List<NetWorkRegionIpExcelImport> list = getByAppId(appId);
        int size = list.size();
        NetWorkRegionIpExcelImport excelImport = null;
        for (int i = 0; i < list.size(); i++)
        {
            if (size < NetWorkRegionIpExcelImport.MAX_IMPORT)
            {
                break;
            }
            
            excelImport = list.get(i);
            if (!(excelImport.getStatus() == NetWorkRegionIpExcelImport.IMPORTING))
            {
                delete(excelImport.getId());
                size--;
            }
        }
        if (size >= NetWorkRegionIpExcelImport.MAX_IMPORT)
        {
            return 0;
        }
        
        excelImport = new NetWorkRegionIpExcelImport();
        Date date = new Date();
        excelImport.setRuntime(date);
        excelImport.setAppId(appId);
        excelImport.setStatus(NetWorkRegionIpExcelImport.IMPORTING);
        excelImport.setAccountId(accountId);
        return excelImportDao.insert(excelImport);
    }
    
    @Override
    public void update(NetWorkRegionIpExcelImport excelImport)
    {
        Date date = new Date();
        excelImport.setCompleteTime(date);
        excelImportDao.update(excelImport);
        
    }
    
}
