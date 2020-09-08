package com.huawei.sharedrive.app.mirror.dao;

import com.huawei.sharedrive.app.mirror.domain.TableScanBreakInfo;

/**
 * 
 * @author cWX348274
 *
 */
public interface TableScanBreakInfoDao
{
    void insert(TableScanBreakInfo tableScanBreakInfo);
    
    TableScanBreakInfo getTableScanBreakInfobyId(String sysTaskId);
    
    int delete(String sysTaskId);
    
}
