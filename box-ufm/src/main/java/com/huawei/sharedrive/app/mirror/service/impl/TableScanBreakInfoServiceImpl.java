package com.huawei.sharedrive.app.mirror.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.huawei.sharedrive.app.mirror.dao.TableScanBreakInfoDao;
import com.huawei.sharedrive.app.mirror.domain.TableScanBreakInfo;
import com.huawei.sharedrive.app.mirror.service.TableScanBreakInfoService;

@Service("tableScanBreakInfoService")
public class TableScanBreakInfoServiceImpl implements TableScanBreakInfoService
{
    @Autowired
    private TableScanBreakInfoDao tableScanBreakInfoDao;
    
    @Override
    public int delete(String sysTaskId)
    {
        return tableScanBreakInfoDao.delete(sysTaskId);
    }
    
    @Override
    public TableScanBreakInfo getTableScanBreakInfobyId(String sysTaskId)
    {
        return tableScanBreakInfoDao.getTableScanBreakInfobyId(sysTaskId);
    }
    
    @Override
    public void insert(TableScanBreakInfo tableScanBreakInfo) {
        tableScanBreakInfoDao.insert(tableScanBreakInfo);
    }
    
}
