package com.huawei.sharedrive.app.spacestatistics.service;

import com.huawei.sharedrive.app.spacestatistics.domain.ClearRecycleBinRecord;

public interface RecordingClearTrashService
{
    void put(ClearRecycleBinRecord clearRecycleBinRecord);
}
