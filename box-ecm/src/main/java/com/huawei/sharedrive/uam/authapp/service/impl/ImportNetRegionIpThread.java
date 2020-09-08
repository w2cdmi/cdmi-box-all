package com.huawei.sharedrive.uam.authapp.service.impl;

import java.util.List;
import java.util.Locale;

import com.huawei.sharedrive.uam.enterprise.domain.ImportNetRegionIp;
import com.huawei.sharedrive.uam.enterprise.service.NetworkRegionExcelService;

public class ImportNetRegionIpThread implements Runnable
{
    private List<ImportNetRegionIp> importist;
    
    private long id;
    
    private long accountId;
    
    private NetworkRegionExcelService excelServiceImpl;
    
    private Locale locale;
    
    public ImportNetRegionIpThread(List<ImportNetRegionIp> importist, long id, Locale locale,
        NetworkRegionExcelService excelServiceImpl, long accountId)
    {
        this.importist = importist;
        this.excelServiceImpl = excelServiceImpl;
        this.id = id;
        this.locale = locale;
        this.accountId = accountId;
        
    }
    
    @Override
    public void run()
    {
        excelServiceImpl.importNetRegionIpListToDB(importist, id, locale, accountId);
    }
    
}
