package pw.cdmi.file.engine.filesystem.aws;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pw.cdmi.core.utils.EDToolsEnhance;
import pw.cdmi.file.engine.core.config.SystemConfigContainer;
import pw.cdmi.file.engine.filesystem.exception.FSException;
import pw.cdmi.file.engine.filesystem.exception.UnrecognizedFileException;
import pw.cdmi.file.engine.filesystem.manage.cache.FSEndpointCache;
import pw.cdmi.file.engine.filesystem.model.FSAccessPath;
import pw.cdmi.file.engine.filesystem.model.FSDefinition;
import pw.cdmi.file.engine.filesystem.model.FSEndpoint;
import pw.cdmi.file.engine.filesystem.model.FSObject;
import pw.cdmi.file.engine.filesystem.model.cloud.S3BucketInfo;
import pw.cdmi.file.engine.filesystem.model.cloud.S3EndPointInfo;
import pw.cdmi.file.engine.filesystem.model.cloud.S3Userinfo;

public class S3FSObject extends FSObject
{
  private static final long serialVersionUID = 1L;
  private static final Logger LOGGER = LoggerFactory.getLogger(S3FSObject.class);
  private static final String ENDPOINT_SPLIT = ":";
  private static final Map<String, SkCache> SK_MAP = new ConcurrentHashMap<String, SkCache>(100);

  private static final ConcurrentHashMap<String, Object> LOCK_MAP = new ConcurrentHashMap<String, Object>(
    100);
  private static final long SK_CACHE_DEFAULT_TIMEOUT = 86400000L;
  private S3BucketInfo bucket;



  public static void clearExpiredCache()
  {
    long currentTime = System.currentTimeMillis();
    String[] keys = (String[])SK_MAP.keySet().toArray(new String[SK_MAP.size()]);
    long timout = SystemConfigContainer.getLong("system.sk.cache.timeout", 
      Long.valueOf(SK_CACHE_DEFAULT_TIMEOUT)).longValue();

    for (String key : keys)
    {
      SkCache cache = (SkCache)SK_MAP.get(key);
      if ((cache != null) && (currentTime - cache.createAt > timout))
      {
        LOCK_MAP.remove(key);
        SK_MAP.remove(key);
      }
    }
  }

  public S3FSObject(String path) throws UnrecognizedFileException
  {
    super(path);
  }

  public S3FSObject(FSEndpoint endpoint, String objectKey)
  {
    super(endpoint);
    setObjectKey(objectKey);
    this.fsAccessPath = getFSEndpoint().getFsAccessPathManager().select(objectKey);
    this.bucket = transToS3Bucket(endpoint, getFSAccessPath());
  }

  /**
   * 构造方法用于测试，场景中注意不要使用此方法
   * @param endpoint
   * @param objectKey
   * @param s3BucketInfo
   */
  public S3FSObject(FSEndpoint endpoint, String objectKey,S3BucketInfo s3BucketInfo)
  {
    super(endpoint);
    setObjectKey(objectKey);
    //this.fsAccessPath = getFSEndpoint().getFsAccessPathManager().select(objectKey);
    this.bucket =s3BucketInfo;
  }

  public S3BucketInfo getBucket()
  {
    return this.bucket;
  }

  protected void initByPath(String path)
    throws UnrecognizedFileException
  {
    try
    {
      String[] temp = path.substring(1, path.length() - 1).split(Pattern.quote("]["));

      if ("V2_HWY".equals(temp[1]))
      {
        this.fsDefinition = FSDefinition.findFSDefinition(temp[0]);

        this.fsEndpoint = getFSEndpoint(this.fsDefinition, temp[2]);

        this.fsAccessPath = new FSAccessPath(temp[3]);

        setObjectKey(temp[4]);
      }
      else
      {
        this.fsDefinition = FSDefinition.findFSDefinition(temp[0]);

        this.fsEndpoint = this.fsDefinition.createFSEndpointInstants();

        this.fsEndpoint.setEndpoint(temp[1]);
        this.fsEndpoint.setFsType(this.fsDefinition.getType());
        this.fsEndpoint.setMaxUtilization(Float.valueOf(0.0F));
        this.fsEndpoint.setRetrieval(Float.valueOf(0.0F));

        this.fsAccessPath = new FSAccessPath(temp[2]);

        setObjectKey(temp[3]);
      }

      this.bucket = transToS3Bucket(getFSEndpoint(), getFSAccessPath());
    }
    catch (Exception e)
    {
      String message = "Init FSObject By Path Failed. [ " + path + " ]";
      LOGGER.warn(message, e);
      throw new UnrecognizedFileException(message, e);
    }
  }

  public void setObjectKey(String objectKey)
  {
    super.setObjectKey(objectKey);
  }

  public String getObjectKey()
  {
    return super.getObjectKey();
  }

  protected FSEndpoint getFSEndpoint(FSDefinition fsDefinition, String endpointId)
    throws FSException
  {
    FSEndpoint fsEndpoint = FSEndpointCache.getFSEndpoint(fsDefinition.getType(), endpointId);

    if (fsEndpoint == null)
    {
      LOGGER.warn("Cann't not find available fsEndpoint");
      fsEndpoint = fsDefinition.createFSEndpointInstants();

      fsEndpoint.setEndpoint(endpointId);
      fsEndpoint.setFsType(fsDefinition.getType());
      fsEndpoint.setMaxUtilization(Float.valueOf(0.0F));
      fsEndpoint.setRetrieval(Float.valueOf(0.0F));
    }

    return fsEndpoint;
  }

  private static S3BucketInfo transToS3Bucket(FSEndpoint endpoint, FSAccessPath accessPath)
  {
    String[] infos = endpoint.getEndpoint().split(ENDPOINT_SPLIT);

    String sk = getSk(infos[3], infos[4], infos[5]);
    S3Userinfo s3UserInfo = new S3Userinfo(infos[3], sk);
    s3UserInfo.setEndpointInfo(new S3EndPointInfo(infos[0], infos[1], infos[2]));

    S3BucketInfo s3BucketInfo = new S3BucketInfo(accessPath.getPath(), s3UserInfo);

    return s3BucketInfo;
  }

  private static String getSk(String ak, String encryptSk, String encryptKey)
  {
    SkCache cache = (SkCache)SK_MAP.get(ak);
    if (cache == null)
    {
      Object lock = getLock(ak);
      synchronized (lock)
      {
        cache = (SkCache)SK_MAP.get(ak);
        if (cache == null)
        {
          String sk = EDToolsEnhance.decode(encryptSk, encryptKey);
          cache = new SkCache(sk, System.currentTimeMillis());
          SK_MAP.put(ak, cache);
        }
      }
    }
    return cache.sk;
  }

  private static Object getLock(String ak)
  {
    Object lock = LOCK_MAP.get(ak);
    if (lock == null)
    {
      lock = new Object();
      Object value = LOCK_MAP.putIfAbsent(ak, lock);
      if (value != null)
      {
        lock = value;
      }
    }
    return lock;
  }

  private static final class SkCache
  {
    private String sk;
    private long createAt;

    private SkCache(String sk, long createAt)
    {
      this.sk = sk;
      this.createAt = createAt;
    }
  }
}