package pw.cdmi.file.engine.filesystem.uds.loadbalance;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pw.cdmi.file.engine.core.job.Task;

public class RefreshUDSTask extends Task
{
  private static final Logger LOGGER = LoggerFactory.getLogger(RefreshUDSTask.class);

  public void execute()
  {
    try
    {
      LoadBalanceUDSArithmetic.refreshAll();
    }
    catch (Exception e)
    {
      LOGGER.error("Refresh OBS list failed.", e);
    }
  }

  public String getName()
  {
    return RefreshUDSTask.class.getCanonicalName();
  }
}