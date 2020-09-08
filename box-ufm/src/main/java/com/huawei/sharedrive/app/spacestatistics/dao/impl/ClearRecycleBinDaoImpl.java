package com.huawei.sharedrive.app.spacestatistics.dao.impl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.ibatis.SqlMapClientTemplate;
import org.springframework.stereotype.Service;

import com.huawei.sharedrive.app.spacestatistics.dao.ClearRecycleBinDao;
import com.huawei.sharedrive.app.spacestatistics.domain.ClearRecycleBinRecord;

@SuppressWarnings("deprecation")
@Service("clearRecycleBinDao")
public class ClearRecycleBinDaoImpl implements ClearRecycleBinDao
{
    
    @Autowired
    protected SqlMapClientTemplate sqlMapClientTemplate;
    
    @SuppressWarnings("unchecked")
    @Override
    public List<ClearRecycleBinRecord> getRecords()
    {
        return (List<ClearRecycleBinRecord>) sqlMapClientTemplate.queryForList("ClearRecycleBinRecord.getRecords");
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public List<Long> getAccountIds()
    {
        return (List<Long>) sqlMapClientTemplate.queryForList("ClearRecycleBinRecord.getAccountIds");
    }
    
    @Override
    public void delete(ClearRecycleBinRecord record)
    {
        if (record == null)
        {
            return;
        }
        sqlMapClientTemplate.delete("ClearRecycleBinRecord.delete", record);
    }
    
    @Override
    public void deleteByTime(long userId, Date date)
    {
        ClearRecycleBinRecord record = new ClearRecycleBinRecord();
        record.setCreatedAt(date);
        record.setOwnedBy(userId);
        sqlMapClientTemplate.delete("ClearRecycleBinRecord.delete", record);
    }
    
    @Override
    public void insert(ClearRecycleBinRecord record)
    {
        if (record == null)
        {
            return;
        }
        sqlMapClientTemplate.insert("ClearRecycleBinRecord.insert", record);
        
    }
    
}
