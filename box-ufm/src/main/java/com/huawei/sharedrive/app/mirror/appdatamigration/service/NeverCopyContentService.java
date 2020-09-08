package com.huawei.sharedrive.app.mirror.appdatamigration.service;

import com.huawei.sharedrive.app.mirror.appdatamigration.domain.MigrationEverydayProcess;
import com.huawei.sharedrive.app.mirror.domain.CopyPolicy;
import com.huawei.sharedrive.app.mirror.domain.CopyTask;

public interface NeverCopyContentService
{
    /**
     * 添加一条迁移错误记录，该记录绑定到某一天和某条记录上
     * @param migrationEverydayProcess
     * @param copyTask
     * @param reason
     */
    void insert(CopyPolicy copyPolicy,MigrationEverydayProcess migrationEverydayProcess,CopyTask copyTask,String reason);
}
