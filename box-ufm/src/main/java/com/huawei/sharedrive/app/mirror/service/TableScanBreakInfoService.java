package com.huawei.sharedrive.app.mirror.service;

import com.huawei.sharedrive.app.mirror.domain.TableScanBreakInfo;

public interface TableScanBreakInfoService
{
    

    void insert(TableScanBreakInfo tableScanBreakInfo);
    
    TableScanBreakInfo getTableScanBreakInfobyId(String sysTaskId);
    
    int delete(String sysTaskId);
    
}
