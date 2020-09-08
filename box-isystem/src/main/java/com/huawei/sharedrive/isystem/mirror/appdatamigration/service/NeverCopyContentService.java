package com.huawei.sharedrive.isystem.mirror.appdatamigration.service;

import java.util.List;

import com.huawei.sharedrive.isystem.mirror.appdatamigration.domain.MigrationEverydayProcess;
import com.huawei.sharedrive.isystem.mirror.appdatamigration.domain.NeverCopyContent;
import com.huawei.sharedrive.isystem.mirror.domain.CopyPolicy;
import com.huawei.sharedrive.isystem.mirror.domain.CopyTask;

public interface NeverCopyContentService
{
    /**
     * 添加一条迁移错误记录，该记录绑定到某一天和某条记录上
     * @param migrationEverydayProcess
     * @param copyTask
     * @param reason
     */
    void insert(CopyPolicy copyPolicy,MigrationEverydayProcess migrationEverydayProcess,CopyTask copyTask,String reason);
    
    /**
     * 列举一个策略下的失败的记录
     * @param policyId
     * @return
     */
    List<NeverCopyContent> getNeverCopyContentByPolicyId(int policyId);
    
    /**
     * 列举一个策略下某天的记录
     * @param parent
     * @return
     */
    List<NeverCopyContent> getNeverCopyContentByEveryDayProcessId(String parent);
}
