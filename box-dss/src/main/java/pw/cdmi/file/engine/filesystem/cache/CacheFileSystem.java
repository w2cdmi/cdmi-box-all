package pw.cdmi.file.engine.filesystem.cache;

import pw.cdmi.core.log.Level;
import pw.cdmi.core.utils.MethodLogAble;
import pw.cdmi.file.engine.filesystem.cache.exception.CacheFSException;
import pw.cdmi.file.engine.filesystem.model.FSObject;

public abstract class CacheFileSystem
{
  private CacheFileSystemConfig config;

  public CacheFileSystemConfig getConfig()
  {
    return this.config;
  }

  public void setConfig(CacheFileSystemConfig config)
  {
    this.config = config;
  }

  public boolean isSupport(FSObject fsObject)
  {
    return isSupport(fsObject.getLength());
  }

  public boolean isSupport(CacheFSObject cacheFSObject)
  {
    return isSupport(cacheFSObject.getLength());
  }

  private boolean isSupport(long objectLength)
  {
    if ((objectLength > getConfig().getRangeStart()) && (objectLength <= getConfig().getRangeEnd()))
    {
      return true;
    }

    return false;
  }

  @MethodLogAble(Level.INFO)
  public abstract boolean putObject(CacheFSObject paramCacheFSObject)
    throws CacheFSException;

  @MethodLogAble(Level.INFO)
  public abstract CacheFSObject getObject(String paramString)
    throws CacheFSException;

  @MethodLogAble(Level.INFO)
  public abstract CacheFSObject getObject(String paramString, int paramInt1, int paramInt2)
    throws CacheFSException;

  @MethodLogAble(Level.INFO)
  public abstract boolean deleteObject(String paramString)
    throws CacheFSException;
}