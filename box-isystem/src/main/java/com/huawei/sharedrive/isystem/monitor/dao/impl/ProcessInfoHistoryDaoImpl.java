package com.huawei.sharedrive.isystem.monitor.dao.impl;

import javax.annotation.Resource;

import org.springframework.orm.ibatis.SqlMapClientTemplate;
import org.springframework.stereotype.Service;

import com.huawei.sharedrive.isystem.monitor.dao.ProcessInfoHistoryDao;
import com.huawei.sharedrive.isystem.monitor.domain.ProcessInfo;

import pw.cdmi.box.dao.impl.CacheableSqlMapClientDAO;
@Service("ProcessInfoHistoryDao")
public class ProcessInfoHistoryDaoImpl extends CacheableSqlMapClientDAO implements ProcessInfoHistoryDao
{
    @Resource(name="monitorSqlMapClientTemplate")
    private SqlMapClientTemplate monitorSqlMapClientTemplate;
    @Override
    public void insert(ProcessInfo processInfo)
    {
        monitorSqlMapClientTemplate.insert("ProcessInfoHistory.insert",processInfo);
    }


}
