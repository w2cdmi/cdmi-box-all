package com.huawei.sharedrive.isystem.monitor.dao.impl;

import javax.annotation.Resource;

import org.springframework.orm.ibatis.SqlMapClientTemplate;
import org.springframework.stereotype.Service;

import com.huawei.sharedrive.isystem.monitor.dao.ProcessInfoDao;
import com.huawei.sharedrive.isystem.monitor.domain.ProcessInfo;

import pw.cdmi.box.dao.impl.CacheableSqlMapClientDAO;
@Service("ProcessInfoDao")
public class ProcessInfoDaoImpl extends CacheableSqlMapClientDAO implements ProcessInfoDao
{
    @Resource(name="monitorSqlMapClientTemplate")
    private SqlMapClientTemplate monitorSqlMapClientTemplate;
    @Override
    public void insert(ProcessInfo processInfo)
    {
        monitorSqlMapClientTemplate.insert("ProcessInfo.insert",processInfo);
    }

    @Override
    public void update(ProcessInfo processInfo)
    {
        monitorSqlMapClientTemplate.update("ProcessInfo.update",processInfo);
        
    }

    @Override
    public ProcessInfo get(ProcessInfo filter)
    {
        // TODO Auto-generated method stub
        return (ProcessInfo)monitorSqlMapClientTemplate.queryForObject("ProcessInfo.select",filter);
    }
   
}
