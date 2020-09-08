package com.huawei.sharedrive.app.mirror.manager;

import com.huawei.sharedrive.app.mirror.domain.CopyTask;
import com.huawei.sharedrive.thrift.mirror.app2dc.CopyTaskExeResult;

public interface BaseHandleCopyTask
{
    boolean handleDataIsExistingBeforeCopy(CopyTask task);
    
    void handleCopyResult(CopyTask task, CopyTaskExeResult result);
}
