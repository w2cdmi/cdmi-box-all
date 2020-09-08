package pw.cdmi.file.engine.filesystem.manage.cache;

import pw.cdmi.file.engine.filesystem.exception.UnknownFSException;
import pw.cdmi.file.engine.filesystem.model.FSAccessPath;
import pw.cdmi.file.engine.filesystem.model.FSEndpoint;
import pw.cdmi.file.engine.filesystem.model.FSEndpointStatus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class FSEndpointCache
{
  private static final List<FSEndpoint> ALL_FSENDPOINTS = new ArrayList<FSEndpoint>(1);

  private static final Map<String, Map<String, FSEndpoint>> ALL_FSENDPOINTS_FSTYPE = new ConcurrentHashMap<String, Map<String, FSEndpoint>>();

  private static final ReadWriteLock LOCK = new ReentrantReadWriteLock(true);

  private static final Logger LOGGER = LoggerFactory.getLogger(FSEndpointCache.class);

  private static final List<FSEndpoint> WRITEABLE_FSENDPOINTS = new ArrayList<FSEndpoint>(1);

  private static final List<FSEndpoint> ENABLE_FSENDPOINTS = new ArrayList<FSEndpoint>(1);

  public static void clearAll()
  {
    try
    {
      LOCK.writeLock().lock();

      WRITEABLE_FSENDPOINTS.clear();
      ALL_FSENDPOINTS.clear();
      ALL_FSENDPOINTS_FSTYPE.clear();
      ENABLE_FSENDPOINTS.clear();
    }
    finally
    {
      try
      {
        LOCK.writeLock().unlock();
      }
      catch (Exception e)
      {
        LOGGER.error("Unlock Failed.", e);
      }
    }
  }

  public static List<FSEndpoint> getAllEndpoints()
  {
    try
    {
      LOCK.readLock().lock();
      List<FSEndpoint> endpoints = new ArrayList<FSEndpoint>(ALL_FSENDPOINTS.size());
      endpoints.addAll(ALL_FSENDPOINTS);
      return endpoints;
    }
    finally
    {
      try
      {
        LOCK.readLock().unlock();
      }
      catch (Exception e)
      {
        LOGGER.warn("Unlock Failed.", e);
      }
    }
  }

  public static List<FSEndpoint> getAllWriteAbleEndpoints()
  {
    try
    {
      LOCK.readLock().lock();
      List<FSEndpoint> endpoints = new ArrayList<FSEndpoint>(WRITEABLE_FSENDPOINTS.size());
      endpoints.addAll(WRITEABLE_FSENDPOINTS);
      return endpoints;
    }
    finally
    {
      try
      {
        LOCK.readLock().unlock();
      }
      catch (Exception e)
      {
        LOGGER.warn("Unlock Failed.", e);
      }
    }
  }

  public static List<FSEndpoint> getAllEnableEndpoints()
  {
    try
    {
      LOCK.readLock().lock();
      List<FSEndpoint> endpoints = new ArrayList<FSEndpoint>(ENABLE_FSENDPOINTS.size());
      endpoints.addAll(ENABLE_FSENDPOINTS);
      return endpoints;
    }
    finally
    {
      try
      {
        LOCK.readLock().unlock();
      }
      catch (Exception e)
      {
        LOGGER.warn("Unlock Failed.", e);
      }
    }
  }

  public static FSEndpoint getFSEndpoint(String fsType, String endpointId)
  {
    try
    {
      LOCK.readLock().lock();
      Map<String, FSEndpoint> endpoints = (Map<String, FSEndpoint>)ALL_FSENDPOINTS_FSTYPE.get(StringUtils.lowerCase(fsType));
      if (endpoints == null)
      {
        return null;
      }

      return (FSEndpoint)endpoints.get(endpointId);
    }
    finally
    {
      try
      {
        LOCK.readLock().unlock();
      }
      catch (Exception e)
      {
        LOGGER.warn("", e);
      }
    }
  }

  public static void refreshLocalCache(List<FSEndpoint> allEndpoints)
  {
    Map<String, Map<String,FSEndpoint>> allFSTypeMap = new HashMap<String, Map<String,FSEndpoint>>(1);

    List<FSEndpoint> writeAbleFsEndpoint = new ArrayList<FSEndpoint>(1);

    List<FSEndpoint> enableFsEndpoint = new ArrayList<FSEndpoint>(1);
    boolean hasAvailable = false;
    String fsType = null;
    Map<String,FSEndpoint> fsEndpoints = null;

    for (FSEndpoint endpoint : allEndpoints)
    {
      fsType = StringUtils.lowerCase(endpoint.getFsType());
      fsEndpoints = allFSTypeMap.get(fsType);
      if (fsEndpoints == null)
      {
        fsEndpoints = new HashMap<String,FSEndpoint>(1);
        allFSTypeMap.put(fsType, fsEndpoints);
      }
      fsEndpoints.put(endpoint.getId(), endpoint);

      if (FSEndpointStatus.ENABLE.equals(endpoint.getStatus()))
      {
        enableFsEndpoint.add(endpoint);
      }

      if (endpoint.isAvailable())
      {
        FSEndpoint fsEndpoint = refreshFSAccessPathForAvailable(endpoint);
        if (!fsEndpoint.getAccessPaths().isEmpty())
        {
          hasAvailable = true;
          recordReadWrite(writeAbleFsEndpoint, endpoint);
        }
        else
        {
          LOGGER.warn("No Available Path For [ " + fsEndpoint.logFormat() + " ] ");
        }
      }
    }
    updateCache(allEndpoints, allFSTypeMap, writeAbleFsEndpoint, enableFsEndpoint);
    if (!hasAvailable)
    {
      LOGGER.warn("No Available FSEndpoint");
    }
  }

  private static void recordReadWrite(List<FSEndpoint> writeAbleFsEndpoint, FSEndpoint endpoint)
  {
    if (!endpoint.isReadOnly())
    {
      FSEndpoint fsEndpoint = refreshFSAccessPathForWriteable(endpoint);
      if (!fsEndpoint.getAccessPaths().isEmpty())
      {
        writeAbleFsEndpoint.add(fsEndpoint);
      }
      else
      {
        LOGGER.warn("No WriteAble Path For [ " + fsEndpoint.logFormat() + " ] ");
      }
    }
  }

  private static FSEndpoint refreshFSAccessPathForAvailable(FSEndpoint endpoint)
  {
    try
    {
      FSEndpoint fsEndpoint = (FSEndpoint)endpoint.clone();

      fsEndpoint.setAccessPaths(new ArrayList<FSAccessPath>(1));

      for (FSAccessPath accessPath : endpoint.getAccessPaths())
      {
        if (accessPath.isAvailable())
        {
          fsEndpoint.addAccessPaths(accessPath);
        }
      }

      return fsEndpoint;
    }
    catch (CloneNotSupportedException e)
    {
      throw new UnknownFSException("Clone FSEndpoint Failed", e);
    }
  }

  private static FSEndpoint refreshFSAccessPathForWriteable(FSEndpoint endpoint)
  {
    try
    {
      FSEndpoint fsEndpoint = (FSEndpoint)endpoint.clone();

      fsEndpoint.setAccessPaths(new ArrayList<FSAccessPath>(1));

      for (FSAccessPath accessPath : endpoint.getAccessPaths())
      {
        if ((accessPath.isAvailable()) && (accessPath.isWriteAble()))
        {
          fsEndpoint.addAccessPaths(accessPath);
        }
      }

      return fsEndpoint;
    }
    catch (CloneNotSupportedException e)
    {
      throw new UnknownFSException("Clone FSEndpoint Failed", e);
    }
  }

  private static void updateCache(List<FSEndpoint> allEndpoints, Map<String, Map<String, FSEndpoint>> allFSTypeMap, List<FSEndpoint> writeAbleFsEndpoint, List<FSEndpoint> enableFsEndpoint)
  {
    try
    {
      LOCK.writeLock().lock();

      clearAll();

      ALL_FSENDPOINTS.addAll(allEndpoints);

      if (!writeAbleFsEndpoint.isEmpty())
      {
        WRITEABLE_FSENDPOINTS.addAll(writeAbleFsEndpoint);
      }
      else
      {
        LOGGER.warn("No WriteAble FSEndpoint");
      }

      if (!enableFsEndpoint.isEmpty())
      {
        ENABLE_FSENDPOINTS.addAll(enableFsEndpoint);
      }
      else
      {
        LOGGER.warn("No Enable FSEndpoint");
      }

      ALL_FSENDPOINTS_FSTYPE.putAll(allFSTypeMap);
    }
    finally
    {
      try
      {
        LOCK.writeLock().unlock();
      }
      catch (Exception e)
      {
        LOGGER.warn("", e);
      }
    }
  }
}