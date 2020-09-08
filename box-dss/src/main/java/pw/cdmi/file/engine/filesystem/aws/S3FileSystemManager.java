package pw.cdmi.file.engine.filesystem.aws;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.lang.StringUtils;
import org.jets3t.service.ServiceException;
import org.jets3t.service.model.S3Bucket;
import org.jets3t.service.security.AWSCredentials;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pw.cdmi.common.alarm.Alarm;
import pw.cdmi.common.alarm.AlarmHelper;
import pw.cdmi.core.log.Level;
import pw.cdmi.core.utils.EDToolsEnhance;
import pw.cdmi.core.utils.MethodLogAble;
import pw.cdmi.core.utils.RandomGUID;
import pw.cdmi.file.engine.core.alarm.StorageFailedAlarm;
import pw.cdmi.file.engine.core.config.SystemConfigContainer;
import pw.cdmi.file.engine.core.job.ThreadPool;
import pw.cdmi.file.engine.filesystem.FileSystemManager;
import pw.cdmi.file.engine.filesystem.aws.support.S3Jets3tProperties;
import pw.cdmi.file.engine.filesystem.aws.support.S3RestS3Service;
import pw.cdmi.file.engine.filesystem.exception.FSException;
import pw.cdmi.file.engine.filesystem.exception.FileSystemIOException;
import pw.cdmi.file.engine.filesystem.exception.UnknownFSException;
import pw.cdmi.file.engine.filesystem.exception.WrongFileSystemArgsException;
import pw.cdmi.file.engine.filesystem.model.FSAccessPath;
import pw.cdmi.file.engine.filesystem.model.FSDefinition;
import pw.cdmi.file.engine.filesystem.model.FSEndpoint;
import pw.cdmi.file.engine.filesystem.model.cloud.S3EndPointInfo;
import pw.cdmi.file.engine.filesystem.support.service.FSEndpointService;

@Service("s3FileSystemManager")
public class S3FileSystemManager
  implements FileSystemManager
{
  private static final Logger LOGGER = LoggerFactory.getLogger(S3FileSystemManager.class);
  private static final int S3_ENDPOINT_SPLIT_COUNT_SIX = 6;
  private static final String BUCKET_PREFIX = "s3bucket-";

  @Autowired
  private AlarmHelper alarmHelper;

  @Autowired
  private StorageFailedAlarm storageFailedAlarm;

	@Autowired
	private FSEndpointService   fsEndpointService;

  public FSDefinition getDefinition()
  {
    return FSDefinition.AWS_S3_FileSystem;
  }

	@MethodLogAble(Level.INFO)
	public FSEndpoint createFSEndpoint(FSEndpoint endpoint) throws FSException {
		if (!isApproved(endpoint, S3_ENDPOINT_SPLIT_COUNT_SIX)) {
			String message = "endpoint Info [" + endpoint.logFormat() + "] is not approved";
			if (LOGGER.isWarnEnabled()) {
				LOGGER.warn(message);
				throw new WrongFileSystemArgsException(message);
			}
			
		}
		
		String[] infos = endpoint.getEndpoint().split(":");
		String ip = StringUtils.trimToEmpty(infos[0]);
		String httpPort = StringUtils.trimToEmpty(infos[1]);
		String httpsPort = StringUtils.trimToEmpty(infos[2]);
		String accessKey = StringUtils.trimToEmpty(infos[3]);
		String secretKey = StringUtils.trimToEmpty(infos[4]);
		
		LOGGER.info("create aws s3 filesystem with ip: {}, port: {}, ak: {}", new Object[]{ip, httpPort, accessKey});
		
		S3EndPointInfo endPointInfo = new S3EndPointInfo(ip, httpPort, httpsPort);
		AWSCredentials credentials = new AWSCredentials(accessKey, secretKey);
		S3RestS3Service s3Service = null;
		try {
			s3Service = new S3RestS3Service(credentials, S3Jets3tProperties.getInstance(endPointInfo));
			S3Bucket[] buckets = s3Service.listAllBuckets();
			if ((buckets != null) && (buckets.length != 0)) {
				List<FSEndpoint> FSEndpointLst = fsEndpointService.getAllFSEndpointsForCurrentDevice();
				// 一个数据中心只允许添加一个ak相同的OBS帐号
				for (FSEndpoint f : FSEndpointLst) {
					if (f.getEndpoint().contains(accessKey)) {
						String message = "User bucket already exsit, init failed!";
						LOGGER.error(message);
						throw new WrongFileSystemArgsException(message);
					}
				}
				// OBS对应多数据中心
				for (S3Bucket bucket : buckets) {
					endpoint.addAccessPaths(new FSAccessPath(bucket.getName()));
				}
			} else {
				String bucketName = "";
				//FIXME 這裡全局配置會導致無法映射多個廠家的云存儲系統
				int bucketNumber = SystemConfigContainer.getInteger("uds.buckets.number", Integer.valueOf(100)).intValue();
				try {
					RandomGUID randomGUID = null;
					FSAccessPath fsAccessPath = null;
					for (int i = 0; i < bucketNumber; i++) {
						randomGUID = new RandomGUID();
						bucketName = BUCKET_PREFIX + randomGUID.getValueAfterMD5();
						s3Service.createBucket(bucketName);
						LOGGER.info("-- Create bucket succeed. Bucket name : " + bucketName);
						
						fsAccessPath = new FSAccessPath(bucketName);
						endpoint.addAccessPaths(fsAccessPath);
					}
				} catch (Exception e) {
					LOGGER.error("Create bucket failed, name: " + bucketName, e);
					throw new FileSystemIOException("UDS init failed", e);
				}
			}
		} catch (FSException e) {
			throw new FSException(e);
		} catch (ServiceException e) {
			throw new FileSystemIOException(e);
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			throw new UnknownFSException("AWS S3 init failed", e);
		}
		
		endpoint.setMaxUtilization(Float.valueOf(0.0F));
		endpoint.setRetrieval(Float.valueOf(0.0F));
		
		Map<String, String> map = EDToolsEnhance.encode(secretKey);
		LOGGER.info("change crypt in dataserver.FSEndpoint");
		String encryptedSecretKey = (String) map.get("encryptedContent");
		String encryptKey = (String) map.get("encryptedKey");
		StringBuilder newEndpoint = new StringBuilder(ip);
		newEndpoint.append(":").append(httpPort);
		newEndpoint.append(":").append(httpsPort);
		newEndpoint.append(":").append(accessKey);
		newEndpoint.append(":").append(encryptedSecretKey);
		newEndpoint.append(":").append(encryptKey);
		endpoint.setEndpoint(newEndpoint.toString());
		
		return endpoint;
	}

  public FSEndpoint refreshFSEndpoint(FSEndpoint endpoint)
    throws FSException
  {
    endpoint.setMaxUtilization(Float.valueOf(0.0F));
    endpoint.setRetrieval(Float.valueOf(0.0F));

    AtomicInteger succCount = new AtomicInteger(0);

    CountDownLatch latch = new CountDownLatch(1);

    int retry = SystemConfigContainer.getInteger("fs.uds.check.retry", Integer.valueOf(3)).intValue();

    S3RefreshEndpointTask task = null;
    for (int i = 0; i < retry; i++)
    {
      task = new S3RefreshEndpointTask(succCount, endpoint, latch);
      ThreadPool.execute(task);
    }
    try
    {
      int timeout = SystemConfigContainer.getInteger("fs.storage.check.failed.timeout", 
        Integer.valueOf(120)).intValue();

      if (!latch.await(timeout, TimeUnit.SECONDS))
      {
        LOGGER.warn("refreshFSEndpoint timeout");
      }
    }
    catch (Exception e)
    {
      String message = "Check  [ " + endpoint + " ] State Failed.";
      LOGGER.warn(message, e);
      throw new UnknownFSException(message, e);
    }

    handleCheckResult(endpoint, succCount.get());

    return endpoint;
  }

  public FSEndpoint updateFSEndpoint(FSEndpoint endpoint)
    throws FSException
  {
    if (!isApproved(endpoint, S3_ENDPOINT_SPLIT_COUNT_SIX))
    {
      String message = "Endpoint Info [" + endpoint.logFormat() + "] is not approved";
      if (LOGGER.isWarnEnabled())
      {
        LOGGER.warn(message);
        throw new WrongFileSystemArgsException(message);
      }

    }

    String[] infos = endpoint.getEndpoint().split(":");
    String ip = StringUtils.trimToEmpty(infos[0]);
    String httpPort = StringUtils.trimToEmpty(infos[1]);
    String httpsPort = StringUtils.trimToEmpty(infos[2]);
    String accessKey = StringUtils.trimToEmpty(infos[3]);
    String secretKey = StringUtils.trimToEmpty(infos[4]);

    LOGGER.info("update AWS S3 filesystem with ip: {}, port: {}, ak: {}", new Object[] { ip, httpPort, accessKey });

    S3EndPointInfo endPointInfo = new S3EndPointInfo(ip, httpPort, httpsPort);

    AWSCredentials credentials = new AWSCredentials(accessKey, secretKey);

    S3RestS3Service s3Service = null;
    try
    {
      s3Service = new S3RestS3Service(credentials, S3Jets3tProperties.getInstance(endPointInfo));

      S3Bucket[] buckets = s3Service.listAllBuckets();

      if ((buckets == null) || (buckets.length == 0))
      {
        String message = "User bucket not exsit, change failed!";
        LOGGER.error(message);
        throw new WrongFileSystemArgsException(message);
      }
    }
    catch (FSException e)
    {
      throw new FSException(e);
    }
    catch (ServiceException e)
    {
      throw new FileSystemIOException(e);
    }
    catch (Exception e)
    {
      LOGGER.error(e.getMessage(), e);
      throw new UnknownFSException("AWS S3 init failed", e);
    }

    endpoint.getAccessPaths().clear();

    endpoint.setMaxUtilization(Float.valueOf(0.0F));
    endpoint.setRetrieval(Float.valueOf(0.0F));

    Map<String, String> map = EDToolsEnhance.encode(secretKey);
    LOGGER.info("change crypt in dataserver.FSEndpoint");
    String encryptedSecretKey = (String)map.get("encryptedContent");
    String encryptKey = (String)map.get("encryptedKey");
    StringBuilder newEndpoint = new StringBuilder(ip).append(":")
      .append(httpPort)
      .append(":")
      .append(httpsPort)
      .append(":")
      .append(accessKey)
      .append(":")
      .append(encryptedSecretKey)
      .append(":")
      .append(encryptKey);
    endpoint.setEndpoint(newEndpoint.toString());

    return endpoint;
  }

  private boolean isApproved(FSEndpoint endpoint, int currentSize)
  {
    if (StringUtils.isBlank(endpoint.getEndpoint()))
    {
      return false;
    }
    if (endpoint.getEndpoint().split(":").length != currentSize)
    {
      return false;
    }

    return true;
  }

  private void handleCheckResult(FSEndpoint endpoint, int succCount)
  {
    String[] infos = endpoint.getEndpoint().split(":");
    StringBuilder alarmInfo = new StringBuilder(infos[0]).append(":")
      .append(infos[1])
      .append(":")
      .append(infos[3]);

    Alarm alarm = new StorageFailedAlarm(this.storageFailedAlarm, alarmInfo.toString());

    if (succCount > 0)
    {
      endpoint.setAvailable(true);
      endpoint.setWriteAble(true);
      for (FSAccessPath path : endpoint.getAccessPaths())
      {
        path.setAvailable(true);
        path.setWriteAble(true);
      }
      this.alarmHelper.sendRecoverAlarm(alarm);
    }
    else
    {
      endpoint.setAvailable(false);
      endpoint.setWriteAble(false);
      LOGGER.warn("Endpoint [ " + endpoint.logFormat() + " ] Check Failed.");
      this.alarmHelper.sendAlarm(alarm);
    }
  }
}