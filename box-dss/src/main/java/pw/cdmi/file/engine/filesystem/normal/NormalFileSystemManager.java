package pw.cdmi.file.engine.filesystem.normal;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pw.cdmi.common.alarm.Alarm;
import pw.cdmi.common.alarm.AlarmHelper;
import pw.cdmi.common.file.FileUtils;
import pw.cdmi.core.log.Level;
import pw.cdmi.core.utils.MethodLogAble;
import pw.cdmi.file.engine.core.alarm.LackOfSpaceAlarm;
import pw.cdmi.file.engine.core.alarm.StorageFailedAlarm;
import pw.cdmi.file.engine.core.config.SystemConfigContainer;
import pw.cdmi.file.engine.filesystem.FileSystemManager;
import pw.cdmi.file.engine.filesystem.exception.*;
import pw.cdmi.file.engine.filesystem.exception.FileNotFoundException;
import pw.cdmi.file.engine.filesystem.model.FSAccessPath;
import pw.cdmi.file.engine.filesystem.model.FSDefinition;
import pw.cdmi.file.engine.filesystem.model.FSEndpoint;
import pw.cdmi.file.engine.filesystem.model.NormalFile;
import pw.cdmi.file.engine.filesystem.support.FSCheckTask;

import java.io.*;
import java.nio.charset.Charset;
import java.text.DecimalFormat;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@Service("normalFileSystemManager")
public class NormalFileSystemManager implements FileSystemManager {
	private static final Logger LOGGER = LoggerFactory.getLogger(NormalFileSystemManager.class);
	private static final String VERIFICATION_DIR_NAME = "temp";
	private static final String VERIFICATION_FILE_PREFIX = "check_";
	private static final String VERIFICATION_FILE_SUFFIX = ".tmp";
	private static final String VERIFICATION_FILE_SPLIT = "_";
	private static final String READABLE_VERIFICATION_FILE_SPLIT = "check_readable_file_7471417624053548979.tmp";
	private static final String VERIFICATION_FILE_NAME_PATTERN = "check_\\d{13}_\\d{19}\\.tmp";
	private static final byte[] TEMP_DATA = "0".getBytes(Charset.forName("UTF-8"));
	private static final long VERIFICATION_FILE_EXPIRED = 1800000L;

	@Autowired
	private AlarmHelper alarmHelper;

	@Autowired
	private LackOfSpaceAlarm lackOfSpaceAlarm;

	@Autowired
	private StorageFailedAlarm storageFailedAlarm;

	public FSDefinition getDefinition() {
		return FSDefinition.NORMAL_FileSystem;
	}

	@MethodLogAble(Level.INFO)
	public FSEndpoint createFSEndpoint(FSEndpoint endpoint) throws FSException {
		if (!isApproved(endpoint)) {
			String message = "Endpoint Info [" + endpoint + "] is not approved";
			LOGGER.warn(message);
			throw new WrongFileSystemArgsException(message);
		}

		endpoint.setFsType(getDefinition().getType());

		StringBuilder path = new StringBuilder(StringUtils.trimToEmpty(endpoint.getEndpoint()));

		NormalFile folder = new NormalFile(path.toString());
		if ((!folder.exists()) || (folder.isFile())) {
			String message = "Endpoint [" + endpoint + "] is not exist";
			if (LOGGER.isWarnEnabled()) {
				LOGGER.warn(message);
				throw new WrongFileSystemArgsException(message);
			}
		}

		if (!path.toString().endsWith(NormalFile.separator)) {
			path.append(NormalFile.separator);
		}
		endpoint.setEndpoint(path.toString());
		try {
			StringBuilder accessPath = new StringBuilder(folder.getCanonicalPath());
			if (!accessPath.toString().endsWith(NormalFile.separator)) {
				accessPath.append(NormalFile.separator);
			}

			FSAccessPath fsAccessPath = new FSAccessPath(accessPath.toString());

			createCheckReadFile(fsAccessPath);

			endpoint.addAccessPaths(fsAccessPath);
		} catch (IOException e) {
			LOGGER.error("Create FSEndpoint Failed.", e);
			throw new FileSystemIOException(e);
		}

		return endpoint;
	}

	public FSEndpoint refreshFSEndpoint(FSEndpoint endpoint) throws FSException {
		CountDownLatch latch = new CountDownLatch(endpoint.getAccessPaths().size());

		endpoint.setAvailable(false);
		endpoint.setWriteAble(false);

		for (FSAccessPath accessPath : endpoint.getAccessPaths()) {
			accessPath.setAvailable(false);

			startCheckThread(endpoint, accessPath, latch);
		}

		try {
			if (!latch.await(SystemConfigContainer.getInteger("fs.storage.check.failed.timeout", Integer.valueOf(120))
					.intValue(), TimeUnit.SECONDS)) {
				LOGGER.warn("refreshFSEndpoint timeout");
			}
		} catch (InterruptedException e) {
			String message = "Check  [ " + endpoint + " ] State Failed.";
			LOGGER.warn(message, e);
			throw new UnknownFSException(message, e);
		}

		if (!endpoint.isAvailable()) {
			LOGGER.warn("No Available AccessPath For [ " + endpoint.toString() + " ]");
			return endpoint;
		}

		refreshSpaceInfo(endpoint);
		return endpoint;
	}

	public FSEndpoint updateFSEndpoint(FSEndpoint endpoint) throws FSException {
		endpoint.getAccessPaths().clear();
		return endpoint;
	}

	protected void createCheckReadFile(FSAccessPath accessPath) throws IOException, FileNotFoundException {
		NormalFile temp = getCheckReadFile(accessPath);

		if (!temp.exists()) {
			createTempFile(temp);
		}
	}

	private NormalFile getCheckReadFile(FSAccessPath accessPath) {
		StringBuilder filePath = new StringBuilder(accessPath.getPath());
		if (!accessPath.getPath().endsWith(NormalFile.separator)) {
			filePath.append(NormalFile.separator);
		}
		filePath.append(VERIFICATION_DIR_NAME).append(NormalFile.separator).append(READABLE_VERIFICATION_FILE_SPLIT);

		return new NormalFile(filePath.toString());
	}

	private boolean isApproved(FSEndpoint endpoint) {
		if (StringUtils.isBlank(endpoint.getEndpoint())) {
			return false;
		}

		if ((endpoint.getMaxUtilization() == null) || (endpoint.getRetrieval() == null)) {
			return false;
		}

		return true;
	}

	private void refreshSpaceInfo(FSEndpoint endpoint) {
		FSAccessPath accesspath = null;

		for (FSAccessPath ap : endpoint.getAccessPaths()) {
			if (ap.isAvailable()) {
				accesspath = ap;
				break;
			}
			if (FSCheckTask.getRunningTask(ap.getPath()) == null) {
				accesspath = ap;
			}
		}

		if (accesspath == null) {
			LOGGER.warn("No Useful AccessPath For [ " + endpoint.toString() + " ]");
			endpoint.setAvailable(false);
			return;
		}

		NormalFile file = new NormalFile(accesspath.getPath());

		long totalSize = file.getTotalSpace();
		long usedSize = totalSize - file.getUsableSpace();
		endpoint.refreshSpaceInfo(totalSize, usedSize);

		Alarm alarm = new LackOfSpaceAlarm(this.lackOfSpaceAlarm, endpoint.getEndpoint(),
				decimalFormat(endpoint.getMaxUtilization()));
		if (endpoint.isNoSpace()) {
			this.alarmHelper.sendAlarm(alarm);
		} else {
			this.alarmHelper.sendRecoverAlarm(alarm);
		}
	}

	private void createTempFile(NormalFile temp) throws IOException, FileNotFoundException {
		FileOutputStream fop = null;
		try {
			fop = new FileOutputStream(temp);
			fop.write(TEMP_DATA);
			fop.flush();
		} finally {
			if (fop != null) {
				fop.close();
			}

		}

		if (!temp.exists()) {
			String message = "Temp File For Check ReadAble is Not exists.";
			LOGGER.warn(message);
			throw new FileNotFoundException(message);
		}
	}

	private boolean canRead(FSAccessPath accessPath) {
		NormalFile temp = getCheckReadFile(accessPath);
		if (!temp.exists()) {
			LOGGER.warn("Temp File For Check Readable is not exists.");
			return false;
		}

		FileInputStream in = null;
		try {
			in = new FileInputStream(temp);
			byte[] buffer = new byte[TEMP_DATA.length];
			int len = in.read(buffer);
			return len == TEMP_DATA.length;
		} catch (RuntimeException e) {
			LOGGER.warn("Read Temp File Failed.", e);
			return false;
		} catch (Exception e) {
			LOGGER.warn("Read Temp File Failed.", e);
			return false;
		} finally {
			IOUtils.closeQuietly(in);
		}
	}

	private boolean canWrite(FSAccessPath accessPath) {
		NormalFile verificationDir = new NormalFile(accessPath.getPath() + VERIFICATION_DIR_NAME);
		NormalFile temp = null;
		try {
			temp = new NormalFile(NormalFile.createTempFile(
					VERIFICATION_FILE_PREFIX + System.currentTimeMillis() + VERIFICATION_FILE_SPLIT,
					VERIFICATION_FILE_SUFFIX, verificationDir));

			createTempFile(temp);
		} catch (RuntimeException e) {
			LOGGER.warn("Create Temp File Failed In Dir [" + FileUtils.getCanonicalPathWithOutException(verificationDir)
					+ " ] .", e);
			return false;
		} catch (Exception e) {
			LOGGER.warn("Create Temp File Failed In Dir [" + FileUtils.getCanonicalPathWithOutException(verificationDir)
					+ " ] .", e);
			return false;
		} finally {
			if (temp != null) {
				if (!temp.delete()) {
					LOGGER.warn("Delete Temp File Failed [ {} ] , Please check mount point [ {} ]",
							FileUtils.getCanonicalPathWithOutException(temp), accessPath.toString());
				}
			}
		}
		if (temp != null) {
			if (!temp.delete()) {
				LOGGER.warn("Delete Temp File Failed [ {} ] , Please check mount point [ {} ]",
						FileUtils.getCanonicalPathWithOutException(temp), accessPath.toString());
			}

		}

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("AccessPath [ " + accessPath.toString() + " ] pass check.");
		}

		try {
			deleteTempFile(verificationDir);
		} catch (Exception e) {
			LOGGER.warn("Clear Temp Files Failed. [ " + e.getMessage() + " ]", e);
			return false;
		}

		return true;
	}

	private void checkAccessPath(FSEndpoint endpoint, FSAccessPath accessPath) {
		if (canRead(accessPath)) {
			accessPath.setAvailable(true);
			endpoint.setAvailable(true);

			accessPath.setWriteAble(canWrite(accessPath));
			if (accessPath.isWriteAble()) {
				endpoint.setWriteAble(true);
			}
		} else {
			accessPath.setAvailable(false);
			accessPath.setWriteAble(false);
		}
	}

	private String decimalFormat(Number number) {
		if (number == null) {
			return "0";
		}
		DecimalFormat df = new DecimalFormat("#0.00");

		return df.format(number);
	}

	private void deleteTempFile(NormalFile verificationDir) {
		File[] files = verificationDir.listFiles(new TempFileFilter());

		if ((files == null) || (files.length == 0)) {
			return;
		}

		long currentTime = System.currentTimeMillis();
		long createTime = 0L;
		String tempFileName = null;
		for (File tempfile : files) {
			tempFileName = tempfile.getName();
			createTime = Long.parseLong(tempFileName.split(VERIFICATION_FILE_SPLIT)[1]);

			if (currentTime - createTime > VERIFICATION_FILE_EXPIRED) {
				if (!tempfile.delete()) {
					LOGGER.warn("Delete Check Temp File Failed [ {} ]",
							FileUtils.getCanonicalPathWithOutException(tempfile));
				}
			}
		}
	}

	private void startCheckThread(final FSEndpoint endpoint, final FSAccessPath accessPath, final CountDownLatch latch)
			throws RepetitiveTaskException {
		FSCheckTask task = FSCheckTask.getRunningTask(accessPath.getPath());

		if (task != null) {
			if (task.isTimeOut()) {
				LOGGER.warn("Task [ " + task.getTaskKey() + " ] Is TimeOut.");
				task.interrupt();
			} else {
				String message = "Task [ " + task.getTaskKey() + " ] Is Already Exist.";
				LOGGER.warn(message);

				latch.countDown();
				throw new RepetitiveTaskException(message);
			}

		}

		long timeout = SystemConfigContainer.getLong("fs.storage.check.task.timeout", Long.valueOf(30L)).longValue()
				* 60L * 1000L;

		new FSCheckTask(accessPath.getPath(), timeout) {
			public void doCheck() {
				endpoint.setAvailable(false);

				NormalFileSystemManager.this.checkAccessPath(endpoint, accessPath);

				Alarm alarm = new StorageFailedAlarm(NormalFileSystemManager.this.storageFailedAlarm,
						accessPath.getPath());

				if ((!endpoint.isWriteAble()) || (!endpoint.isAvailable())) {
					NormalFileSystemManager.this.alarmHelper.sendAlarm(alarm);
				} else {
					NormalFileSystemManager.this.alarmHelper.sendRecoverAlarm(alarm);
				}

				latch.countDown();
			}
		}.start();
	}

	private static class TempFileFilter implements FileFilter {
		public boolean accept(File tempfile) {
			if (tempfile.getName().matches(VERIFICATION_FILE_NAME_PATTERN)) {
				return true;
			}
			return false;
		}
	}
}