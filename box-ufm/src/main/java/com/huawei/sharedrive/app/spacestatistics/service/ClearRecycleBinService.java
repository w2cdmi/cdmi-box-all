package com.huawei.sharedrive.app.spacestatistics.service;

import java.util.List;

import com.huawei.sharedrive.app.spacestatistics.domain.ClearRecycleBinRecord;

public interface ClearRecycleBinService
{
    List<ClearRecycleBinRecord> getRecords();
    
    List<Long> getAccountIds();
    
    void deleteRecycleBinRecord(List<ClearRecycleBinRecord> records);
    
    void updateUserSpace(List<ClearRecycleBinRecord> records);
    
    void updateAccountSpace(List<Long> accountIds);
}
