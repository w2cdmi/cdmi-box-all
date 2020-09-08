package com.huawei.sharedrive.isystem.mirror.appdatamigration.manager;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.huawei.sharedrive.isystem.mirror.appdatamigration.domain.MigrationEverydayProcess;
import com.huawei.sharedrive.isystem.mirror.appdatamigration.domain.MigrationProcessInfo;
import com.huawei.sharedrive.isystem.mirror.appdatamigration.domain.NeverCopyContent;
import com.huawei.sharedrive.isystem.mirror.appdatamigration.service.MigrationEverydayProcessService;
import com.huawei.sharedrive.isystem.mirror.appdatamigration.service.MigrationProcessInfoService;
import com.huawei.sharedrive.isystem.mirror.appdatamigration.service.NeverCopyContentService;

@Service("appDataMigrationManager")
public class AppDataMigrationManager
{
    @Autowired
    private MigrationEverydayProcessService migrationEverydayProcessService;
    
    @Autowired
    private MigrationProcessInfoService migrationProcessInfoService;
    
    @Autowired
    private NeverCopyContentService neverCopyContentService;
    
    public List<MigrationEverydayProcess> getMigrationEverydayProcessById(String parent)
    {
        return migrationEverydayProcessService.getMigrationEverydayProcessById(parent);
    }
    
    public List<MigrationProcessInfo> getMigrationProcessInfoByPolicyId(int policyId)
    {
        return migrationProcessInfoService.getMigrationProcessInfoByPolicyId(policyId);
    }
    
    public MigrationProcessInfo getMigrationProcessInfo(String id)
    {
        return migrationProcessInfoService.getMigrationProcessInfo(id);
    }
    
    public List<NeverCopyContent> getNeverCopyContentByPolicyId(int policyId)
    {
        return neverCopyContentService.getNeverCopyContentByPolicyId(policyId);
    }
    
    public List<NeverCopyContent> getNeverCopyContentByEveryDayProcessId(String parent)
    {
        return neverCopyContentService.getNeverCopyContentByEveryDayProcessId(parent);
    }
}
