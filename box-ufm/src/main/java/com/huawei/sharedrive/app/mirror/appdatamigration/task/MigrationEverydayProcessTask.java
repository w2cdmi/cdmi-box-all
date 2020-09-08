package com.huawei.sharedrive.app.mirror.appdatamigration.task;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.huawei.sharedrive.app.mirror.appdatamigration.domain.MigrationProcessInfo;
import com.huawei.sharedrive.app.mirror.appdatamigration.manager.AppDataMigrationManager;
import com.huawei.sharedrive.app.mirror.appdatamigration.service.MigrationEverydayProcessService;
import com.huawei.sharedrive.app.mirror.appdatamigration.service.MigrationProcessInfoService;
import com.huawei.sharedrive.app.mirror.domain.CopyPolicy;

import pw.cdmi.common.job.JobExecuteContext;
import pw.cdmi.common.job.JobExecuteRecord;
import pw.cdmi.common.job.quartz.QuartzJobTask;

@Component("MigrationEverydayProcessTask")
public class MigrationEverydayProcessTask extends QuartzJobTask
{
    private static final Logger LOGGER = LoggerFactory.getLogger(MigrationEverydayProcessTask.class);
    
    @Autowired
    private MigrationEverydayProcessService migrationEverydayProcessService;
    
    @Autowired
    private MigrationProcessInfoService migrationProcessInfoService;
    
    @Autowired
    private AppDataMigrationManager appDataMigrationManager;
    
    
    
    @Override
    public void doTask(JobExecuteContext arg0, JobExecuteRecord arg1)
    {
        LOGGER.info("start to end and create a day process for migration process");
        List<CopyPolicy> lstMigrationPolicy = appDataMigrationManager.lstMigrationPolicy();
        if(lstMigrationPolicy.isEmpty())
        {
            LOGGER.info("lstMigrationPolicy is null or empty");
            return;
        }
        
        MigrationProcessInfo migrationProcessInfo = null;
        for(CopyPolicy copyPolicy : lstMigrationPolicy)
        {
            migrationProcessInfo = migrationProcessInfoService.getLastUnDoneMigrationProcess(copyPolicy.getId());
            if(migrationProcessInfo == null)
            {
                LOGGER.info("get migrationProcessInfo by policy id:"+copyPolicy.getId()+" is null");
                continue;
            }
            migrationEverydayProcessService.create(migrationProcessInfo);
            LOGGER.info("create a new day process success, MigrationProcessInfo.id is:"+migrationProcessInfo.getId());
        }
        LOGGER.info("end...");
    }

}
