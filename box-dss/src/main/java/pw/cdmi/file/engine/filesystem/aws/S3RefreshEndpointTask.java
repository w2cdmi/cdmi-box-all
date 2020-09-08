package pw.cdmi.file.engine.filesystem.aws;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pw.cdmi.file.engine.filesystem.FileSystemFactory;
import pw.cdmi.file.engine.filesystem.exception.FSException;
import pw.cdmi.file.engine.filesystem.model.FSEndpoint;
import pw.cdmi.file.engine.filesystem.model.FSObject;
import pw.cdmi.file.engine.filesystem.model.FileSystem;

public class S3RefreshEndpointTask implements Runnable {
	private static Logger logger = LoggerFactory.getLogger(S3RefreshEndpointTask.class);

	private static final byte[] TEMP_DATA = "0".getBytes(Charset.forName("UTF-8"));
	private static final String VERIFICATION_FILE_PREFIX = "check_";
	private static final String VERIFICATION_FILE_SUFFIX = ".tmp";
	private FSEndpoint endpoint;
	private CountDownLatch latch;
	private AtomicInteger succCount;

	public S3RefreshEndpointTask(AtomicInteger succCount, FSEndpoint endpoint, CountDownLatch latch) {
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
		FileSystem<FSObject> fileSystem = FileSystemFactory.getInstance(endpoint);
		InputStream is = null;
		try {
			tempFSObject.setLength(TEMP_DATA.length);
			is = new ByteArrayInputStream(TEMP_DATA);
			fileSystem.doPut(tempFSObject, is);
		} catch (Exception e) {
			logger.warn("Create Temp File [ " + tempFSObject.logFormat() + " ] Failed.", e);
			return false;
		} finally {
			IOUtils.closeQuietly(is);
		}

		try {
			return fileSystem.doDelete(tempFSObject);
		} catch (Exception e) {
			logger.warn("Delete Temp File [ " + tempFSObject.logFormat() + " ] Failed.", e);
		}
		return false;
	}

	private S3FSObject generateTempS3FSObject(FSEndpoint endpoint) {
		S3FSObject fsObject = new S3FSObject(endpoint,
				VERIFICATION_FILE_PREFIX + UUID.randomUUID().toString() + VERIFICATION_FILE_SUFFIX);

		fsObject.getFSEndpoint().setAvailable(true);
		fsObject.getFSAccessPath().setAvailable(true);

		return fsObject;
	}
}