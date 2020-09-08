package pw.cdmi.file.engine.filesystem.cache;

import java.util.ArrayList;
import java.util.List;

import pw.cdmi.file.engine.filesystem.cache.exception.CacheFSException;

public class AsyncLoadTask
{
  private ObjectSourceLoader<?> sourceLoader;
  private List<CacheFileSystem> cacheFileSystems = new ArrayList<CacheFileSystem>(1);

  public AsyncLoadTask(ObjectSourceLoader<?> sourceLoader)
  {
    this.sourceLoader = sourceLoader;
  }

  public void doLoader() throws CacheFSException
  {
    WriteToCacheUtils.writeToCacheByReload(this.cacheFileSystems, this.sourceLoader);
  }

  public void addCacheFileSystem(CacheFileSystem cacheFileSystem)
  {
    this.cacheFileSystems.add(cacheFileSystem);
  }

  public boolean isSupport(CacheFileSystem cacheFileSystem)
  {
    if (this.sourceLoader != null)
    {
      return cacheFileSystem.isSupport(this.sourceLoader.getFSObject());
    }

    return false;
  }
}