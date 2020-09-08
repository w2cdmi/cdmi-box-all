package pw.cdmi.file.engine.filesystem.cache;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pw.cdmi.file.engine.filesystem.cache.exception.CacheFSException;

public final class WriteToCacheUtils
{
  private static final Logger LOGGER = LoggerFactory.getLogger(AsyncLoadTask.class);

  public static void writeToCacheByReload(List<CacheFileSystem> cacheFileSystems, ObjectSourceLoader<?> sourceLoader)
    throws CacheFSException
  {
    if ((sourceLoader == null) || (sourceLoader.getFSObject() == null))
    {
      String message = "sourceLoader is null, or fsObject is null.";
      LOGGER.warn(message);
      throw new CacheFSException(message);
    }

    CacheFSObject cacheFSObject = null;
    for (CacheFileSystem cacheFileSystem : cacheFileSystems)
    {
      if (!cacheFileSystem.isSupport(sourceLoader.getFSObject()))
      {
        LOGGER.info(CacheFileSystem.class + " not support [ " + sourceLoader.getFSObject() + " ]");
      }
      else
      {
        cacheFSObject = sourceLoader.load();
        if ((cacheFSObject == null) || (cacheFSObject.getInputStream() == null))
        {
          LOGGER.warn("load object from storage failed. not exist");
          return;
        }

        tryPutObjectToCache(cacheFSObject, cacheFileSystem);
      }
    }
  }

  private static void tryPutObjectToCache(CacheFSObject cacheFSObject, CacheFileSystem cacheFileSystem)
  {
    try {
      cacheFileSystem.putObject(cacheFSObject);
    }
    catch (CacheFSException e)
    {
      LOGGER.warn("write to cached [ " + cacheFileSystem.getClass() + " ] failed. excpetion : {}", 
        e.getMessage());
    }
    finally
    {
      IOUtils.closeQuietly(cacheFSObject.getInputStream());
    }
  }

  public static void writeToCacheByMarkSupportedInputStream(List<CacheFileSystem> cacheFileSystems, CacheFSObject cacheFSObject)
    throws CacheFSException
  {
    if ((cacheFSObject == null) || (cacheFSObject.getInputStream() == null) || 
      (!cacheFSObject.getInputStream().markSupported()))
    {
      String message = "cacheFSObject is null, or inputStream not support mark.";
      LOGGER.warn(message);
      throw new CacheFSException(message);
    }

    for (CacheFileSystem cacheFileSystem : cacheFileSystems)
    {
      if (!cacheFileSystem.isSupport(cacheFSObject))
      {
        LOGGER.info(CacheFileSystem.class + " not support [ " + cacheFSObject + " ]");
      }
      else
      {
        tryRestInputStream(cacheFSObject);

        tryPutObjectToCache(cacheFSObject, cacheFileSystem);
      }
    }
  }

  private static void tryRestInputStream(CacheFSObject cacheFSObject) throws CacheFSException
  {
    try {
      cacheFSObject.getInputStream().reset();
    }
    catch (IOException e)
    {
      LOGGER.error("reset failed.", e);
      throw new CacheFSException(e);
    }
  }
}