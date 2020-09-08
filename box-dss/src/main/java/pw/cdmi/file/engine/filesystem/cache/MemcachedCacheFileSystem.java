package pw.cdmi.file.engine.filesystem.cache;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;

import pw.cdmi.common.cache.CacheClient;
import pw.cdmi.common.cache.exception.CacheException;
import pw.cdmi.core.log.Level;
import pw.cdmi.core.utils.MethodLogAble;
import pw.cdmi.file.engine.filesystem.cache.exception.CacheFSException;
import pw.cdmi.file.engine.filesystem.model.FSObject;

public class MemcachedCacheFileSystem extends CacheFileSystem
{
  private static final Logger LOGGER = LoggerFactory.getLogger(MemcachedCacheFileSystem.class);

  @Autowired(required=false)
  @Qualifier("fileSystemCacheClient")
  private CacheClient cacheClient;

  @Value("${cache.supported}")
  private boolean cacheSupported;
  private int acceptMaxConcurrent = 30;

  private AtomicInteger currentConcurrent = new AtomicInteger();

  @Value("${cache.filesystem.exception.timeout}")
  private long exceptionTimeout = 600000L;

  private long lastCacheExceptionTime = 0L;

  private static final ReadWriteLock LOCK = new ReentrantReadWriteLock(true);

  @MethodLogAble(Level.INFO)
  public boolean isSupport(FSObject fsObject)
  {
    if (!super.isSupport(fsObject))
    {
      return false;
    }

    if (this.currentConcurrent.get() >= this.acceptMaxConcurrent)
    {
      LOGGER.warn("current concurrent [ {} ] exceed acceptMaxConcurrent [ {} ]", Integer.valueOf(this.currentConcurrent.get()), Integer.valueOf(this.acceptMaxConcurrent));
      return false;
    }

    return true;
  }

  @MethodLogAble(Level.INFO)
  public boolean isSupport(CacheFSObject cacheFSObject)
  {
    if (!super.isSupport(cacheFSObject))
    {
      return false;
    }

    if (this.currentConcurrent.get() >= this.acceptMaxConcurrent)
    {
      LOGGER.warn("current concurrent [ {} ] exceed acceptMaxConcurrent [ {} ]", Integer.valueOf(this.currentConcurrent.get()), Integer.valueOf(this.acceptMaxConcurrent));
      return false;
    }

    return true;
  }

  @MethodLogAble(Level.INFO)
  public boolean putObject(CacheFSObject cacheFSObject)
    throws CacheFSException
  {
    if (waitForCacheException())
    {
      return false;
    }
    byte[] data = null;
    try
    {
      if (!checkConcurrent())
      {
        LOGGER.warn("current concurrent [ {} ] exceed acceptMaxConcurrent [ {} ]", Integer.valueOf(this.currentConcurrent.get()), Integer.valueOf(this.acceptMaxConcurrent));
        return false;
      }

      if (cacheFSObject.getInputStream() == null)
      {
        data = new byte[0];
      }
      else
      {
        try
        {
          data = IOUtils.toByteArray(cacheFSObject.getInputStream());
        }
        catch (IOException e)
        {
          String message = "Trans To Byte Array Failed";
          LOGGER.warn(message, e);
          throw new CacheFSException(message, e);
        }
      }

      if (data.length != cacheFSObject.getLength())
      {
        String message = "Trans To Byte Array Failed";
        LOGGER.warn(message);
        throw new CacheFSException(message);
      }

      if (this.cacheSupported)
      {
        return this.cacheClient.setCacheNoExpire(cacheFSObject.getCacheKey(), data);
      }

      return false;
    }
    catch (CacheException e)
    {
      setLastCacheExceptionTime(System.currentTimeMillis());
      throw e;
    }
    finally
    {
      this.currentConcurrent.decrementAndGet();
      data = null;
    }
  }

  @MethodLogAble(Level.INFO)
  public CacheFSObject getObject(String key)
    throws CacheFSException
  {
    if (waitForCacheException())
    {
      return null;
    }
    try
    {
      if (!checkConcurrent())
      {
        LOGGER.warn("current concurrent [ {} ] exceed acceptMaxConcurrent [ {} ]", Integer.valueOf(this.currentConcurrent.get()), Integer.valueOf(this.acceptMaxConcurrent));
        return null;
      }

      if (this.cacheSupported)
      {
        byte[] content = (byte[])this.cacheClient.getCache(key);

        if (content == null)
        {
          return null;
        }

        ByteArrayInputStream is = new ByteArrayInputStream(content);

        CacheFSObject fsObject = new CacheFSObject(key);
        fsObject.setInputStream(is);
        fsObject.setLength(content.length);

        return fsObject;
      }

      return null;
    }
    catch (CacheException e)
    {
      setLastCacheExceptionTime(System.currentTimeMillis());
      throw e;
    }
    finally
    {
      this.currentConcurrent.decrementAndGet();
    }
  }

  @MethodLogAble(Level.INFO)
  public CacheFSObject getObject(String key, int start, int end)
    throws CacheFSException
  {
    if (waitForCacheException())
    {
      return null;
    }

    try
    {
      if (!checkConcurrent())
      {
        LOGGER.warn("current concurrent [ {} ] exceed acceptMaxConcurrent [ {} ]", Integer.valueOf(this.currentConcurrent.get()), Integer.valueOf(this.acceptMaxConcurrent));
        return null;
      }

      if (this.cacheSupported)
      {
        byte[] content = (byte[])this.cacheClient.getCache(key);

        if (content == null)
        {
          return null;
        }

        ByteArrayInputStream is = new ByteArrayInputStream(content, start, end - start + 1);

        CacheFSObject fsObject = new CacheFSObject(key);
        fsObject.setInputStream(is);
        fsObject.setLength(content.length);

        return fsObject;
      }

      return null;
    }
    catch (CacheException e)
    {
      setLastCacheExceptionTime(System.currentTimeMillis());
      throw e;
    }
    finally
    {
      this.currentConcurrent.decrementAndGet();
    }
  }

  @MethodLogAble(Level.INFO)
  public boolean deleteObject(String key)
    throws CacheFSException
  {
    if (this.cacheSupported)
    {
      return this.cacheClient.deleteCache(key);
    }

    return true;
  }

  public int getAcceptMaxConcurrent()
  {
    return this.acceptMaxConcurrent;
  }

  public void setAcceptMaxConcurrent(int acceptMaxConcurrent)
  {
    this.acceptMaxConcurrent = acceptMaxConcurrent;
  }

  private boolean checkConcurrent()
  {
    int current = this.currentConcurrent.incrementAndGet();
    LOGGER.debug("current concurrent is [ {} ].", Integer.valueOf(current));
    return current < this.acceptMaxConcurrent;
  }

  private void setLastCacheExceptionTime(long time)
  {
    try
    {
      LOCK.writeLock().lock();
      this.lastCacheExceptionTime = time;
    }
    finally
    {
      unlock(LOCK.writeLock());
    }
  }

  private void unlock(Lock lock)
  {
    try
    {
      lock.unlock();
    }
    catch (Exception e)
    {
      LOGGER.warn("Unlock Failed.", e);
    }
  }

  private boolean waitForCacheException()
  {
    if (this.lastCacheExceptionTime <= 0L)
    {
      return false;
    }

    long now = System.currentTimeMillis();
    if (now - this.lastCacheExceptionTime < this.exceptionTimeout)
    {
      LOGGER.warn("need waitForCacheException, lastCacheExceptionTime is {}, now is : {}", Long.valueOf(this.lastCacheExceptionTime), Long.valueOf(now));
      return true;
    }

    setLastCacheExceptionTime(-1L);

    return false;
  }
}