package pw.cdmi.file.engine.filesystem.support;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pw.cdmi.common.log.LoggerUtil;

public abstract class FSCheckTask extends Thread
{
  private static final Logger LOGGER = LoggerFactory.getLogger(FSCheckTask.class);

  private static final Map<String, FSCheckTask> BEING_CHECKED_MAP = new ConcurrentHashMap();
  private long startTime;
  private long timeout;
  private String taskKey;

  public FSCheckTask(String taskKey, long timeout)
  {
    this.taskKey = taskKey;
    this.timeout = timeout;
  }

  public static FSCheckTask getRunningTask(String taskKey)
  {
    return (FSCheckTask)BEING_CHECKED_MAP.get(taskKey);
  }

  public void run()
  {
    BEING_CHECKED_MAP.put(getTaskKey(), this);

    this.startTime = System.currentTimeMillis();
    LoggerUtil.regiestThreadLocalLog();
    try
    {
      doCheck();
    }
    catch (Exception e)
    {
      LOGGER.error("FS [ " + this.taskKey + " ] Check Failed", e);
    }
    finally
    {
      BEING_CHECKED_MAP.remove(getTaskKey());
    }
  }

  public boolean isTimeOut()
  {
    return System.currentTimeMillis() - this.startTime > this.timeout;
  }

  public String getTaskKey()
  {
    return this.taskKey;
  }

  public void interrupt()
  {
    try
    {
      super.interrupt();
    }
    catch (Exception e)
    {
      LOGGER.warn("Task [ " + getTaskKey() + " ] Interrupt Failed.", e);
    }
    finally
    {
      BEING_CHECKED_MAP.remove(getTaskKey());
    }
  }

  public abstract void doCheck();
}