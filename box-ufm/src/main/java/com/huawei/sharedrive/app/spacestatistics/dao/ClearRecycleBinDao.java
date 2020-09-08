package com.huawei.sharedrive.app.spacestatistics.dao;

import java.util.Date;
import java.util.List;

import com.huawei.sharedrive.app.spacestatistics.domain.ClearRecycleBinRecord;

public interface ClearRecycleBinDao
{
    List<ClearRecycleBinRecord> getRecords();
    
    List<Long> getAccountIds();
    
    void delete(ClearRecycleBinRecord record);
    
    void deleteByTime(long userId, Date date);
    
    void insert(ClearRecycleBinRecord record);
}
