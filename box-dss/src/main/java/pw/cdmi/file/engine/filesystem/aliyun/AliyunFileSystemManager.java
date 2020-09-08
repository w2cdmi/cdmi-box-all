package pw.cdmi.file.engine.filesystem.aliyun;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aliyun.oss.ClientException;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.model.Bucket;
import com.aliyun.oss.model.CreateBucketRequest;
import com.aliyun.oss.model.StorageClass;

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
import pw.cdmi.file.engine.filesystem.exception.FSException;
import pw.cdmi.file.engine.filesystem.exception.FileSystemIOException;
import pw.cdmi.file.engine.filesystem.exception.UnknownFSException;
import pw.cdmi.file.engine.filesystem.exception.WrongFileSystemArgsException;
import pw.cdmi.file.engine.filesystem.model.FSAccessPath;
import pw.cdmi.file.engine.filesystem.model.FSDefinition;
import pw.cdmi.file.engine.filesystem.model.FSEndpoint;
import pw.cdmi.file.engine.filesystem.support.service.FSEndpointService;

@Service("aliyunFileSystemManager")
public class AliyunFileSystemManager implements FileSystemManager {
	private static final Logger LOGGER = LoggerFactory.getLogger(AliyunFileSystemManager.class);
	private static final int ALIYUN_OSS_ENDPOINT_SPLIT_COUNT_SIX = 6;
	private static final String BUCKET_PREFIX = "storbox-";

	@Autowired
	private AlarmHelper alarmHelper;

	@Autowired
	private StorageFailedAlarm storageFailedAlarm;

	@Autowired
	private FSEndpointService fsEndpointService;

	public FSDefinition getDefinition() {
		return FSDefinition.ALIYUN_OSS_FileSystem;
	}

	@MethodLogAble(Level.INFO)
	public FSEndpoint createFSEndpoint(FSEndpoint endpoint) throws FSException {
		if (!isApproved(endpoint, ALIYUN_OSS_ENDPOINT_SPLIT_COUNT_SIX)) {
			String message = "endpoint Info [" + endpoint.logFormat() + "] is not approved";
			if (LOGGER.isWarnEnabled()) {
				LOGGER.warn(message);
				throw new WrongFileSystemArgsException(message);
			}
		}

		String[] infos = endpoint.getEndpoint().split(":");
		String dns = StringUtils.trimToEmpty(infos[0]);
		String httpPort = StringUtils.trimToEmpty(infos[1]);
		String httpsPort = StringUtils.trimToEmpty(infos[2]);
		String accessKey = StringUtils.trimToEmpty(infos[3]);
		String secretKey = StringUtils.trimToEmpty(infos[4]);
		String provider = null;
		if(infos.length > 6){
			provider = StringUtils.trimToEmpty(infos[6]);
		}

		LOGGER.info("create qingcloud os filesystem with ip: {}, port: {}, ak: {}",
				new Object[] { dns, httpPort, accessKey });

		// 伍偉增加
		String[] domain_split = dns.split("\\.");
		if (domain_split == null || domain_split.length < 3) {
			String message = "dns Info [" + endpoint.logFormat() + "] is not approved";
			if (LOGGER.isWarnEnabled()) {
				LOGGER.warn(message);
				throw new WrongFileSystemArgsException(message);
			}
		}

		try {
			// 创建OSSClient实例
			OSSClient ossClient = new OSSClient(dns, accessKey, secretKey);

			List<Bucket> buckets = ossClient.listBuckets();
			if ((buckets != null) && (buckets.size() != 0)) {
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
				for (Bucket bucket : buckets) {
					endpoint.addAccessPaths(new FSAccessPath(bucket.getName()));
				}
			} else {
				String bucketName = "";
				int bucketNumber = SystemConfigContainer.getInteger("uds.buckets.number", Integer.valueOf(10))
						.intValue();
				try {
					RandomGUID randomGUID = null;
					FSAccessPath fsAccessPath = null;
					for (int i = 0; i < bucketNumber; i++) {
						randomGUID = new RandomGUID();
						bucketName = BUCKET_PREFIX + randomGUID.getValueAfterMD5();
						// 创建Bucket
						ossClient.createBucket(bucketName);
						CreateBucketRequest request = new CreateBucketRequest(bucketName);
						request.addParameter("StorageClass", "AI");
						if(provider != null && "ALIAI".equals(provider)){
							request.setStorageClass(StorageClass.IA);
						}
						ossClient.createBucket(request);
						LOGGER.info("-- Create bucket succeed. Bucket name : " + bucketName);

						fsAccessPath = new FSAccessPath(bucketName);
						endpoint.addAccessPaths(fsAccessPath);
					}
				} catch (Exception e) {
					LOGGER.error("Create bucket failed, name: " + bucketName, e);
					throw new FileSystemIOException("QingCloud OS init failed", e);
				}
			}
			// 关闭ossClient
			ossClient.shutdown();
		} catch (FSException e) {
			throw new FSException(e);
		} catch (OSSException e) {
			LOGGER.error(e.getMessage(), e);
			throw new UnknownFSException("Aliyun OSS init failed", e);
		} catch (ClientException e) {
			LOGGER.error(e.getMessage(), e);
			throw new UnknownFSException("Aliyun OSS init failed", e);
		}

		endpoint.setMaxUtilization(Float.valueOf(0.0F));
		endpoint.setRetrieval(Float.valueOf(0.0F));

		Map<String, String> map = EDToolsEnhance.encode(secretKey);
		LOGGER.info("change crypt in dataserver.FSEndpoint");
		String encryptedSecretKey = (String) map.get("encryptedContent");
		String encryptKey = (String) map.get("encryptedKey");
		StringBuilder newEndpoint = new StringBuilder(dns);
		newEndpoint.append(":").append(httpPort);
		newEndpoint.append(":").append(httpsPort);
		newEndpoint.append(":").append(accessKey);
		newEndpoint.append(":").append(encryptedSecretKey);
		newEndpoint.append(":").append(encryptKey);
		endpoint.setEndpoint(newEndpoint.toString());

		return endpoint;
	}

	public FSEndpoint refreshFSEndpoint(FSEndpoint endpoint) throws FSException {
		endpoint.setMaxUtilization(Float.valueOf(0.0F));
		endpoint.setRetrieval(Float.valueOf(0.0F));

		AtomicInteger succCount = new AtomicInteger(0);

		CountDownLatch latch = new CountDownLatch(1);

		int retry = SystemConfigContainer.getInteger("fs.uds.check.retry", Integer.valueOf(3)).intValue();

		AliyunRefreshEndpointTask task = null;
		for (int i = 0; i < retry; i++) {
			task = new AliyunRefreshEndpointTask(succCount, endpoint, latch);
			ThreadPool.execute(task);
		}
		try {
			int timeout = SystemConfigContainer.getInteger("fs.storage.check.failed.timeout", Integer.valueOf(120))
					.intValue();

			if (!latch.await(timeout, TimeUnit.SECONDS)) {
				LOGGER.warn("refreshFSEndpoint timeout");
			}
		} catch (Exception e) {
			String message = "Check  [ " + endpoint + " ] State Failed.";
			LOGGER.warn(message, e);
			throw new UnknownFSException(message, e);
		}

		handleCheckResult(endpoint, succCount.get());

		return endpoint;
	}

	public FSEndpoint updateFSEndpoint(FSEndpoint endpoint) throws FSException {
		if (!isApproved(endpoint, ALIYUN_OSS_ENDPOINT_SPLIT_COUNT_SIX)) {
			String message = "Endpoint Info [" + endpoint.logFormat() + "] is not approved";
			if (LOGGER.isWarnEnabled()) {
				LOGGER.warn(message);
				throw new WrongFileSystemArgsException(message);
			}

		}

		String[] infos = endpoint.getEndpoint().split(":");
		String dns = StringUtils.trimToEmpty(infos[0]);
		String httpPort = StringUtils.trimToEmpty(infos[1]);
		String httpsPort = StringUtils.trimToEmpty(infos[2]);
		String accessKey = StringUtils.trimToEmpty(infos[3]);
		String secretKey = StringUtils.trimToEmpty(infos[4]);

		LOGGER.info("update uds filesystem with ip: {}, port: {}, ak: {}", new Object[] { dns, httpPort, accessKey });

		// 伍偉增加
		String[] domain_split = dns.split(".");
		if (domain_split == null || domain_split.length < 4) {
			String message = "dns Info [" + endpoint.logFormat() + "] is not approved";
			if (LOGGER.isWarnEnabled()) {
				LOGGER.warn(message);
				throw new WrongFileSystemArgsException(message);
			}
		}

		try {
			// 创建OSSClient实例
			OSSClient ossClient = new OSSClient(dns, accessKey, secretKey);
			List<Bucket> buckets = ossClient.listBuckets();

			if ((buckets == null) || (buckets.size() == 0)) {
				String message = "User bucket not exsit, change failed!";
				LOGGER.error(message);
				throw new WrongFileSystemArgsException(message);
			}
			// 关闭ossClient
			ossClient.shutdown();
		} catch (FSException e) {
			throw new FSException(e);
		} catch (OSSException e) {
			LOGGER.error(e.getMessage(), e);
			throw new UnknownFSException("Aliyun OSS init failed", e);
		} catch (ClientException e) {
			LOGGER.error(e.getMessage(), e);
			throw new UnknownFSException("Aliyun OSS init failed", e);
		}

		endpoint.getAccessPaths().clear();

		endpoint.setMaxUtilization(Float.valueOf(0.0F));
		endpoint.setRetrieval(Float.valueOf(0.0F));

		Map<String, String> map = EDToolsEnhance.encode(secretKey);
		LOGGER.info("change crypt in dataserver.FSEndpoint");
		String encryptedSecretKey = (String) map.get("encryptedContent");
		String encryptKey = (String) map.get("encryptedKey");
		StringBuilder newEndpoint = new StringBuilder(dns).append(":").append(httpPort).append(":").append(httpsPort)
				.append(":").append(accessKey).append(":").append(encryptedSecretKey).append(":").append(encryptKey);
		endpoint.setEndpoint(newEndpoint.toString());

		return endpoint;
	}

	private boolean isApproved(FSEndpoint endpoint, int currentSize) {
		if (StringUtils.isBlank(endpoint.getEndpoint())) {
			return false;
		}
		if (endpoint.getEndpoint().split(":").length != currentSize) {
			return false;
		}

		return true;
	}

	private void handleCheckResult(FSEndpoint endpoint, int succCount) {
		String[] infos = endpoint.getEndpoint().split(":");
		StringBuilder alarmInfo = new StringBuilder(infos[0]).append(":").append(infos[1]).append(":").append(infos[3]);

		Alarm alarm = new StorageFailedAlarm(this.storageFailedAlarm, alarmInfo.toString());

		if (succCount > 0) {
			endpoint.setAvailable(true);
			endpoint.setWriteAble(true);
			for (FSAccessPath path : endpoint.getAccessPaths()) {
				path.setAvailable(true);
				path.setWriteAble(true);
			}
			this.alarmHelper.sendRecoverAlarm(alarm);
		} else {
			endpoint.setAvailable(false);
			endpoint.setWriteAble(false);
			LOGGER.warn("Endpoint [ " + endpoint.logFormat() + " ] Check Failed.");
			this.alarmHelper.sendAlarm(alarm);
		}
	}
}