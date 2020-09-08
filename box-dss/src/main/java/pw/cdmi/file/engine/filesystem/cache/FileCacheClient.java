package pw.cdmi.file.engine.filesystem.cache;

import pw.cdmi.common.job.ThreadPool;
import pw.cdmi.common.log.LoggerUtil;
import pw.cdmi.file.engine.filesystem.cache.exception.CacheFSException;
import pw.cdmi.file.engine.filesystem.model.FSObject;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileCacheClient
{
  private static final Logger LOGGER = LoggerFactory.getLogger(FileCacheClient.class);

  private List<CacheFileSystem> readList = new ArrayList<CacheFileSystem>(1);

  private List<CacheFileSystem> asyncWriteList = new ArrayList<CacheFileSystem>(1);

  private List<CacheFileSystem> syncWriteList = new ArrayList<CacheFileSystem>(1);

  private BlockingQueue<AsyncLoadTask> asyncLoadTaskQueue = null;

  private int bufferGate = 524288;

  private boolean cacheSupported = true;

  public FileCacheClient(FileCacheClientProperties cacheProperties, List<CacheFileSystem> readList, List<CacheFileSystem> asyncWriteList, List<CacheFileSystem> syncWriteList)
  {
    LOGGER.info("======= Init File Cache Client [ cacheSupported: " + cacheProperties.isCacheSupported() + 
      ", asyncLoadTaskQueueSize: " + cacheProperties.getAsyncLoadTaskQueueSize() + 
      ", monitorAsyncLoadTaskQueueThreadNumber: " + cacheProperties.getMonitorAsyncLoadTaskQueueThreadNumber() + 
      ", bufferGate: " + cacheProperties.getBufferGate() + " ]=======");

    this.cacheSupported = cacheProperties.isCacheSupported();

    if (!cacheSupportedValue())
    {
      return;
    }

    this.readList = readList;
    this.asyncWriteList = asyncWriteList;
    this.syncWriteList = syncWriteList;

    if (!asyncWriteList.isEmpty())
    {
      this.asyncLoadTaskQueue = new LinkedBlockingQueue(cacheProperties.getAsyncLoadTaskQueueSize());
      Runnable monitorAsyncLoadTaskQueueThread = null;
      for (int i = 0; i <= cacheProperties.getMonitorAsyncLoadTaskQueueThreadNumber(); i++)
      {
        monitorAsyncLoadTaskQueueThread = createMonitorAsyncLoadTaskThread();
        ThreadPool.execute(monitorAsyncLoadTaskQueueThread);
      }
    }

    this.bufferGate = cacheProperties.getBufferGate();
  }

  public void putObject(FSObject fsObject, ObjectSourceLoader<?> sourceLoader, ByteArrayInputStream inputStream)
    throws CacheFSException
  {
    if (!cacheSupportedValue())
    {
      LOGGER.debug("cache not supported");
      return;
    }

    if (sourceLoader != null)
    {
      AsyncLoadTask task = new AsyncLoadTask(sourceLoader);
      commitAsyncLoadTask(task);
    }

    if (inputStream != null)
    {
      CacheFSObject cacheFSObject = new CacheFSObject(fsObject.getObjectKey());
      cacheFSObject.setLength(fsObject.getLength());
      cacheFSObject.setInputStream(inputStream);

      WriteToCacheUtils.writeToCacheByMarkSupportedInputStream(this.syncWriteList, cacheFSObject);
    }
    else if (sourceLoader != null)
    {
      WriteToCacheUtils.writeToCacheByReload(this.syncWriteList, sourceLoader);
    }
  }

  public CacheFSObject getObject(CacheFSObject cacheFSObject)
    throws CacheFSException
  {
    if (!cacheSupportedValue())
    {
      LOGGER.debug("cache not supported");
      return null;
    }

    CacheFSObject object = null;

    for (CacheFileSystem cacheFileSystem : this.readList)
    {
      object = cacheFileSystem.getObject(cacheFSObject.getCacheKey());

      if (object != null)
      {
        return object;
      }
    }

    return null;
  }

  public void deleteObject(CacheFSObject cacheFSObject) throws CacheFSException
  {
    if (!cacheSupportedValue())
    {
      LOGGER.debug("cache not supported");
      return;
    }

    for (CacheFileSystem cacheFileSystem : this.asyncWriteList)
    {
      cacheFileSystem.deleteObject(cacheFSObject.getCacheKey());
    }

    for (CacheFileSystem cacheFileSystem : this.syncWriteList)
    {
      cacheFileSystem.deleteObject(cacheFSObject.getCacheKey());
    }
  }

  public long getBufferGate()
  {
    return this.bufferGate;
  }

  public boolean supportBuffered(long objectLength)
  {
    return (getBufferGate() > 0L) && (objectLength <= getBufferGate());
  }

  public boolean cacheSupportedValue()
  {
    return this.cacheSupported;
  }

  private Runnable createMonitorAsyncLoadTaskThread()
  {
    return new Runnable()
    {
      public void run()
      {
        while (true)
        {
          LoggerUtil.regiestThreadLocalLog();
          tryMonitorTask();
        }
      }

      private void tryMonitorTask()
      {
        try
        {
          AsyncLoadTask task = (AsyncLoadTask)FileCacheClient.this.asyncLoadTaskQueue.take();
          if (task == null)
          {
            return;
          }

          task.doLoader();
        }
        catch (InterruptedException e)
        {
          FileCacheClient.LOGGER.warn("take task from asyncLoadTaskQueue failed.", e);
        }
        catch (CacheFSException e)
        {
          FileCacheClient.LOGGER.warn("loader file to cache failed.", e);
        }
        catch (Exception e)
        {
          FileCacheClient.LOGGER.warn("do loader failed.");
        }
      }
    };
  }

  private void commitAsyncLoadTask(AsyncLoadTask task)
  {
    boolean needAsyncTask = false;
    for (CacheFileSystem cacheFileSystem : this.asyncWriteList)
    {
      if (task.isSupport(cacheFileSystem))
      {
        task.addCacheFileSystem(cacheFileSystem);
        needAsyncTask = true;
      }

    }

    if (needAsyncTask)
    {
      if (!this.asyncLoadTaskQueue.offer(task))
      {
        LOGGER.warn("add to asyncLoadTaskQueue failed. queue is full.");
      }
    }
  }
}