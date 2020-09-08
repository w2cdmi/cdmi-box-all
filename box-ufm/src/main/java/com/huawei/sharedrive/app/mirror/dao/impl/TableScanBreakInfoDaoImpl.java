package com.huawei.sharedrive.app.mirror.dao.impl;

import org.springframework.stereotype.Component;

import com.huawei.sharedrive.app.mirror.dao.TableScanBreakInfoDao;
import com.huawei.sharedrive.app.mirror.domain.TableScanBreakInfo;

import pw.cdmi.box.dao.impl.AbstractDAOImpl;

@Component
public class TableScanBreakInfoDaoImpl extends AbstractDAOImpl implements TableScanBreakInfoDao
{
    @SuppressWarnings("deprecation")
    @Override
    public int delete(String sysTaskId)
    {
        return sqlMapClientTemplate.delete("TableScanBreakInfo.deleteById", sysTaskId);
    }
    @SuppressWarnings("deprecation")
    @Override
    public TableScanBreakInfo getTableScanBreakInfobyId(String sysTaskId)
    {
        return (TableScanBreakInfo) sqlMapClientTemplate.queryForObject("TableScanBreakInfo.getTableScanBreakInfoById", sysTaskId);
    }
    @SuppressWarnings("deprecation")
    @Override
    public void insert(TableScanBreakInfo tableScanBreakInfo)
    {
        sqlMapClientTemplate.insert("TableScanBreakInfo.insertTableScanBreakInfo", tableScanBreakInfo);
    }
    
    
}
