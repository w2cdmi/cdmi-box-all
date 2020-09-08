package pw.cdmi.file.engine.filesystem.aliyun;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aliyun.oss.OSSClient;

import pw.cdmi.file.engine.filesystem.aws.S3FSObject;
import pw.cdmi.file.engine.filesystem.exception.FSException;
import pw.cdmi.file.engine.filesystem.model.FSEndpoint;
import pw.cdmi.file.engine.filesystem.model.cloud.S3BucketInfo;
import pw.cdmi.file.engine.filesystem.model.cloud.S3Userinfo;

public class AliyunRefreshEndpointTask implements Runnable {
	private static Logger logger = LoggerFactory.getLogger(AliyunRefreshEndpointTask.class);

	private static final byte[] TEMP_DATA = "0".getBytes(Charset.forName("UTF-8"));
	private static final String VERIFICATION_FILE_PREFIX = "check_";
	private static final String VERIFICATION_FILE_SUFFIX = ".tmp";
	private FSEndpoint endpoint;
	private CountDownLatch latch;
	private AtomicInteger succCount;

	public AliyunRefreshEndpointTask(AtomicInteger succCount, FSEndpoint endpoint, CountDownLatch latch) {
		this.succCount = succCount;
		this.endpoint = endpoint;
		this.latch = latch;
	}

	public void run() {
		try {
			if (check(this.endpoint)) {
				this.succCount.incrementAndGet();
			}
		} catch (FSException e) {
			logger.warn("Check Endpoint [ " + this.endpoint.logFormat() + " ] Failed.", e);
		}
		this.latch.countDown();
	}

	private boolean check(FSEndpoint endpoint) throws FSException {
		S3FSObject tempFSObject = generateTempS3FSObject(endpoint);
		InputStream is = null;
		
		tempFSObject.setLength(TEMP_DATA.length);
		is = new ByteArrayInputStream(TEMP_DATA);
		S3BucketInfo bucketinfo = tempFSObject.getBucket();
		S3Userinfo userinfo = bucketinfo.getUserInfo();
		String[] infos = endpoint.getEndpoint().split(":");
		String dns = StringUtils.trimToEmpty(infos[0]);
		// 创建OSSClient实例
		OSSClient ossClient = new OSSClient(dns, userinfo.getAk(), userinfo.getSk());
		try {
			ossClient.putObject(bucketinfo.getName(), tempFSObject.getObjectKey(), is);
			
		} catch (Exception e) {
			logger.warn("Create Temp File [ " + tempFSObject.logFormat() + " ] Failed.", e);
			return false;
		} finally {
			IOUtils.closeQuietly(is);
		}

		try {
			ossClient.deleteObject(bucketinfo.getName(), tempFSObject.getObjectKey());
		} catch (Exception e) {
			logger.warn("Delete Temp File [ " + tempFSObject.logFormat() + " ] Failed.", e);
			return false;
		}
		return true;
	}

	private S3FSObject generateTempS3FSObject(FSEndpoint endpoint) {
		S3FSObject fsObject = new S3FSObject(endpoint,
				VERIFICATION_FILE_PREFIX + UUID.randomUUID().toString() + VERIFICATION_FILE_SUFFIX);

		fsObject.getFSEndpoint().setAvailable(true);
		fsObject.getFSAccessPath().setAvailable(true);

		return fsObject;
	}
}