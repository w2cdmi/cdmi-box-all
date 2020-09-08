package pw.cdmi.file.engine.filesystem.manage;

import org.springframework.stereotype.Service;
import pw.cdmi.common.job.JobExecuteContext;
import pw.cdmi.common.job.JobExecuteRecord;
import pw.cdmi.common.job.quartz.QuartzJobTask;
import pw.cdmi.file.engine.core.spring.ext.BeanHolder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class RefreshStorageResourceTask extends QuartzJobTask
{
  private static final Logger LOGGER = LoggerFactory.getLogger(RefreshStorageResourceTask.class);
  private static FSEndpointManager fsEndpointManager;

  private FSEndpointManager getFSEndpoingManager()
  {
    if (fsEndpointManager == null)
    {
      fsEndpointManager = (FSEndpointManager)BeanHolder.getBean("fsEndpointManager");
    }

    return fsEndpointManager;
  }

  public void doTask(JobExecuteContext context, JobExecuteRecord record)
  {
    try
    {
      LOGGER.info("Start Reload StorageResource.");
      getFSEndpoingManager().refreshFSEndpointInfo();

      LOGGER.info("Reload StorageResource Succ.");
    }
    catch (Exception e)
    {
      LOGGER.error("Reload StorageResource failed.", e);
      record.setSuccess(Boolean.valueOf(false));
      record.setOutput(e.getMessage());
    }
  }
}