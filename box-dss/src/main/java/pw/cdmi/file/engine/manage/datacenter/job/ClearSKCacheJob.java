package pw.cdmi.file.engine.manage.datacenter.job;

import org.springframework.stereotype.Service;

import pw.cdmi.common.job.JobExecuteContext;
import pw.cdmi.common.job.JobExecuteRecord;
import pw.cdmi.common.job.quartz.QuartzJobTask;
import pw.cdmi.file.engine.filesystem.uds.UDSFSObject;

/**
 * 定时清除SKcache
 * 
 * @author q90003805
 *         
 */
@Service("clearSKCacheJob")
public class ClearSKCacheJob extends QuartzJobTask
{
    
    @Override
    public void doTask(JobExecuteContext context, JobExecuteRecord record)
    {
        UDSFSObject.clearExpiredCache();
    }
    
}
