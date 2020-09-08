package pw.cdmi.file.engine.filesystem.normal;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import pw.cdmi.common.file.FileUtils;
import pw.cdmi.core.log.Level;
import pw.cdmi.core.utils.BaseConvertUtils;
import pw.cdmi.core.utils.MethodLogAble;
import pw.cdmi.core.utils.RandomGUID;
import pw.cdmi.file.engine.core.config.SystemConfigContainer;
import pw.cdmi.file.engine.filesystem.FSMultipartObject;
import pw.cdmi.file.engine.filesystem.FSMultipartPart;
import pw.cdmi.file.engine.filesystem.FileSystemManager;
import pw.cdmi.file.engine.filesystem.cache.FileCacheClient;
import pw.cdmi.file.engine.filesystem.exception.*;
import pw.cdmi.file.engine.filesystem.io.CountInputStream;
import pw.cdmi.file.engine.filesystem.io.LimitedSizeInputStream;
import pw.cdmi.file.engine.filesystem.io.RetryLimitedSizeInputStream;
import pw.cdmi.file.engine.filesystem.io.StreamWriter;
import pw.cdmi.file.engine.filesystem.model.FSEndpoint;
import pw.cdmi.file.engine.filesystem.model.FileSystem;
import pw.cdmi.file.engine.filesystem.model.NormalFSObject;
import pw.cdmi.file.engine.filesystem.model.NormalFile;
import pw.cdmi.file.engine.object.domain.FileObject;

import java.io.*;
import java.util.*;

@Service("normalFileSystem")
public class NormalFileSystem extends FileSystem<NormalFSObject> {
	private static final String SUFFIX_SLICE_DIR = "_slice_folder";
	private static final String SLICE_FILE_NAME_PATTERN = "\\d{13}_\\w+_\\d+";
	private static final String SLICE_FILE_NAME_PATTERN_PREFIX = "\\d{13}_\\w+_";
	private static final String SLICE_FILE_NAME_DELIMITER = "_";
	private static final String SLICE_TEMP_FILE_NAME_SUFFIX = "_temp_part";
	private static final String SLICE_TEMP_FILE_NAME_PATTERN = "\\d{13}_\\w+_\\d+_temp_part";
	private static final Logger LOGGER = LoggerFactory.getLogger(NormalFileSystem.class);

	@Autowired
	@Qualifier("normalFileSystemManager")
	private FileSystemManager fileSystemManager;

	@Autowired
	private FileCacheClient fileCacheClient;
	private PathGenerator pathGenerator;
	private final Object lock = new Object();

	public PathGenerator getPathGenerator() {
		if (this.pathGenerator == null) {
			synchronized (this.lock) {
				if (this.pathGenerator == null) {
					this.pathGenerator = ((PathGenerator) BaseConvertUtils.toObjectInstance(
							SystemConfigContainer.getConfigValue("fs.normal.subfolder.path.generator"),
							new PathGenerator()));
				}
			}
		}

		return this.pathGenerator;
	}

	@MethodLogAble(Level.INFO)
	public NormalFSObject transToFSObject(FSEndpoint fsEndpoint, FileObject fileObject) throws FSException {
		NormalFSObject fsObject = null;

		fsObject = new NormalFSObject(fsEndpoint, getPathGenerator().generate(fileObject), getNewKey(fileObject));

		if (fileObject.getObjectLength() >= 0L) {
			fsObject.setLength(fileObject.getObjectLength());
		}

		return fsObject;
	}

	@MethodLogAble(Level.INFO)
	public NormalFSObject transToFSObject(String path) throws FSException {
		NormalFSObject fsObject = new NormalFSObject(path);

		return fsObject;
	}

	@MethodLogAble(Level.INFO)
	public NormalFSObject doPut(NormalFSObject fsObject, InputStream inputStream) throws FSException {
		try {
			if (checkObjectExist(fsObject)) {
				throw new FileAlreadyExistException(fsObject);
			}

			if (!prepareFolder(fsObject.getNormalFile())) {
				throw new FileSystemIOException("create folder for [" + fsObject + "] failed.");
			}

			if (!fsObject.getNormalFile().createNewFile()) {
				throw new FileSystemIOException("create tempFile failed. [ " + fsObject + " ]");
			}

		} catch (IOException e) {
			if (!fsObject.getNormalFile().delete()) {
				LOGGER.warn("Fail to delete the file" + fsObject.getPath());
			}
			throw new FileSystemIOException("create tempFile failed. [ " + fsObject + " ]", e);
		}

		OutputStream os = null;
		try {
			os = new FileOutputStream(fsObject.getNormalFile());

			CountInputStream cis = new CountInputStream(inputStream);
			StreamWriter.write(cis, os);

			NormalFSObject result = (NormalFSObject) fsObject.clone();
			result.setLength(cis.getTotalReadSize());
			return result;
		} catch (RuntimeException e) {
			if (!fsObject.getNormalFile().delete()) {
				LOGGER.warn("Fail to delete the file" + fsObject.getPath());
			}
			throw new FileSystemIOException("Create File [ " + fsObject + " ] Failed.", e);
		} catch (Exception e) {
			if (!fsObject.getNormalFile().delete()) {
				LOGGER.warn("Fail to delete the file" + fsObject.getPath());
			}
			throw new FileSystemIOException("Create File [ " + fsObject + " ] Failed.", e);
		} finally {
			IOUtils.closeQuietly(os);
			os = null;
		}
	}

	@MethodLogAble(Level.INFO)
	public NormalFSObject doGetObject(NormalFSObject fsObject, Long start, Long end) throws FSException {
		long fileLength = getFsObjectLength(fsObject);

		if ((start == null) || (start.longValue() >= fileLength)) {
			start = Long.valueOf(0L);
		}

		if ((end == null) || (end.longValue() < start.longValue()) || (end.longValue() >= fileLength)) {
			end = Long.valueOf(fileLength - 1L);
		}

		try {
			return getNormalObject(fsObject, start.longValue(), end.longValue());
		} catch (FSException e) {
			if (isSliceFile(fsObject)) {
				return getSliceObject(fsObject, start.longValue(), end.longValue());
			}
			LOGGER.error("is not slice file, file not exists.");
			throw e;
		}
	}

	@MethodLogAble(Level.INFO)
	public boolean doDelete(NormalFSObject fsObject) throws FSException {
		if (fsObject.getNormalFile().delete()) {
			return true;
		}
		if (isSliceFile(fsObject)) {
			return new NormalFile(getSliceFileDir(fsObject)).delete();
		}

		return false;
	}

	@MethodLogAble(Level.INFO)
	public boolean checkObjectExist(NormalFSObject fsObject) throws FSException {
		if (fsObject.getNormalFile().exists()) {
			return true;
		}
		if (isSliceFile(fsObject)) {
			return new NormalFile(getSliceFileDir(fsObject)).exists();
		}

		return false;
	}

	@MethodLogAble(Level.INFO)
	public NormalFSObject doCopy(NormalFSObject srcFsObject, NormalFSObject destFsObject) throws FSException {
		InputStream in = null;
		try {
			in = doGetObject(srcFsObject, null, null).getInputStream();
			return doPut(destFsObject, in);
		} finally {
			IOUtils.closeQuietly(in);
		}
	}

	@MethodLogAble(Level.INFO)
	public FSMultipartObject doMultipartStartUpload(NormalFSObject fsObject) throws FSException {
		String fsdir = getSliceFileDir(fsObject);

		String uploadID = new RandomGUID().getValueAfterMD5() + System.currentTimeMillis();

		if (new NormalFile(fsdir).mkdirs()) {
			FSMultipartObject partObject = new FSMultipartObject(fsObject, uploadID);
			partObject.setPartSize(5242880L);
			return partObject;
		}

		throw new FileSystemIOException("mkdir for slice [" + fsObject + "] failed. dir is: " + fsdir);
	}

	@MethodLogAble(Level.INFO)
	public NormalFSMultipartPart<NormalFSObject> doMultipartUploadPart(NormalFSObject fsObject, String uploadID,
			int partNumber, InputStream inputStream) throws FSException {
		long timestamp = getTimestamp(fsObject, partNumber);

		String fileName = timestamp + SLICE_FILE_NAME_DELIMITER + uploadID + SLICE_FILE_NAME_DELIMITER + partNumber;

		NormalFSObject partFSObjectTempFile = getPartFSObject(fsObject, fileName + SLICE_TEMP_FILE_NAME_SUFFIX);
		partFSObjectTempFile = doPut(partFSObjectTempFile, inputStream);

		NormalFSObject partFSObjectRealFile = getPartFSObject(fsObject, fileName);

		if (!renameTo(partFSObjectTempFile, partFSObjectRealFile)) {
			String message = "rename part temp file : " + fileName + SLICE_TEMP_FILE_NAME_SUFFIX + " to real file : "
					+ fileName + " for sliceObject : " + fsObject.logFormat() + " failed.";
			LOGGER.error(message);
			if (!partFSObjectTempFile.getNormalFile().delete()) {
				LOGGER.warn("delete slice temp part file failed.[{} {}]", fsObject.logFormat(),
						fileName + SLICE_TEMP_FILE_NAME_SUFFIX);
			}
			throw new FileRenameFailedException(message);
		}

		NormalFSMultipartPart<NormalFSObject> part = new NormalFSMultipartPart<NormalFSObject>(partFSObjectRealFile, partNumber, timestamp);

		return part;
	}

	@MethodLogAble(Level.INFO)
	public FSMultipartObject doMultipartCompleteUpload(NormalFSObject fsObject, String uploadID) throws FSException {
		if (!checkObjectExist(fsObject)) {
			throw new pw.cdmi.file.engine.filesystem.exception.FileNotFoundException(
					"Slice File [ " + fsObject + " ] not exist.");
		}

		long length = getFsObjectLength(fsObject);
		fsObject.setLength(length);

		return new FSMultipartObject(fsObject, uploadID);
	}

	@MethodLogAble(Level.INFO)
	public boolean doMultipartAbortUpload(NormalFSObject fsObject, String uploadID) throws FSException {
		String fsdir = getSliceFileDir(fsObject);

		return new NormalFile(fsdir).delete();
	}

	@MethodLogAble(Level.INFO)
	public SortedSet<FSMultipartPart<NormalFSObject>> doMultipartListParts(NormalFSObject fsObject, String uploadID)
			throws FSException {
		return listFSMultipartPart(fsObject);
	}

	public boolean renameTo(NormalFSObject fsObject, NormalFSObject newFsObject) throws FSException {
		if (isSliceFile(fsObject)) {
			String message = "slice file unsupported this operation";
			LOGGER.warn(message);
			throw new UnsupportedOperationException(message);
		}

		if (!checkObjectExist(fsObject)) {
			String message = "fsObject [ " + fsObject.getPath() + " ] is not exists.";
			LOGGER.warn(message);
			throw new pw.cdmi.file.engine.filesystem.exception.FileNotFoundException(message);
		}

		if (newFsObject.getNormalFile().exists()) {
			String message = "fsObject [ " + fsObject.getPath() + " ] already exists.";
			LOGGER.warn(message);
			throw new FileAlreadyExistException(message);
		}

		try {
			if (fsObject.getNormalFile().renameTo(newFsObject.getNormalFile())) {
				return checkObjectExist(newFsObject);
			}

			return false;
		} catch (Exception e) {
			String message = "fsObject [ " + fsObject.getPath() + " ] renameTo [ " + newFsObject.getPath()
					+ " ] failed.";
			LOGGER.warn(message);
			throw new FileRenameFailedException(message, e);
		}
	}

	public boolean deleteRealMultipartObject(NormalFSObject fsObject) throws FSException {
		String sliceFileDir = getSliceFileDir(fsObject);
		NormalFile sliceDir = new NormalFile(sliceFileDir);

		if ((!sliceDir.isDirectory()) || (!sliceDir.exists())) {
			return true;
		}
		LOGGER.info("delete dir : {}", sliceFileDir);
		return sliceDir.delete();
	}

	protected FileCacheClient getFileCacheClient() {
		return this.fileCacheClient;
	}

	private String getSliceFileDir(NormalFSObject fsObject) throws GetCanonicalPathException {
		try {
			return fsObject.getNormalFile().getCanonicalPath() + SUFFIX_SLICE_DIR + NormalFile.separator;
		} catch (IOException e) {
			String message = "getCanonicalPath failed.";
			LOGGER.warn(message);
			throw new GetCanonicalPathException(message, e);
		}
	}

	private NormalFSObject getPartFSObject(NormalFSObject fsObject, String partFileName) {
		return new NormalFSObject(fsObject.getFSEndpoint(), fsObject.getFSAccessPath(),
				fsObject.getFolder() + fsObject.getObjectKey() + SUFFIX_SLICE_DIR + NormalFile.separator, partFileName);
	}

	private boolean isSliceFile(NormalFSObject fsObject) throws GetCanonicalPathException {
		NormalFile sliceDir = new NormalFile(getSliceFileDir(fsObject));

		return (!fsObject.getNormalFile().exists()) && (sliceDir.isDirectory()) && (sliceDir.exists());
	}

	private SortedSet<FSMultipartPart<NormalFSObject>> listFSMultipartPart(NormalFSObject fsObject) throws FSException {
		if (!isSliceFile(fsObject)) {
			String message = "FileObject [ " + fsObject.logFormat() + " ] is not exists, or not a slice file.";
			LOGGER.warn(message);
			throw new pw.cdmi.file.engine.filesystem.exception.FileNotFoundException(message);
		}

		NormalFile sliceDir = new NormalFile(getSliceFileDir(fsObject));

		SortedSet<FSMultipartPart<NormalFSObject>> slices = new TreeSet<FSMultipartPart<NormalFSObject>>();

		NormalFile[] files = sliceDir.listFiles();

		Map<Integer, NormalFSMultipartPart<NormalFSObject>> temp = new HashMap<Integer, NormalFSMultipartPart<NormalFSObject>>(1);

		if ((files != null) && (files.length > 0)) {
			for (NormalFile s : files) {
				enablePartFilter(fsObject, temp, s);
			}
		}

		for (FSMultipartPart<NormalFSObject> part : temp.values()) {
			slices.add(part);
		}

		return slices;
	}

	private void enablePartFilter(NormalFSObject fsObject, Map<Integer, NormalFSMultipartPart<NormalFSObject>> temp,
			NormalFile s) {
		String fileName = s.getName();

		if (fileName.matches(SLICE_FILE_NAME_PATTERN)) {
			String[] args = fileName.split(SLICE_FILE_NAME_DELIMITER);

			long timestamp = Long.parseLong(args[0]);

			Integer partNumber = Integer.valueOf(args[2]);

			NormalFSMultipartPart<NormalFSObject> last = (NormalFSMultipartPart<NormalFSObject>) temp.get(partNumber);

			NormalFSObject partObj = getPartFSObject(fsObject, s.getName());
			partObj.setLength(s.length());
			NormalFSMultipartPart<NormalFSObject> now = new NormalFSMultipartPart<NormalFSObject>(partObj, partNumber.intValue(), timestamp);

			if (last != null) {
				temp.put(partNumber, deleteRepeatedPart(now, last));
				return;
			}

			temp.put(partNumber, now);
		} else if (fileName.matches(SLICE_TEMP_FILE_NAME_PATTERN)) {
			if (s.delete()) {
				LOGGER.info("Delete Part Temp File Success. [{}]", FileUtils.getCanonicalPathWithOutException(s));
			} else {
				LOGGER.warn("Delete Part Temp File Failed. [{}]", FileUtils.getCanonicalPathWithOutException(s));
			}
		}
	}

	private NormalFSMultipartPart<NormalFSObject> deleteRepeatedPart(NormalFSMultipartPart<NormalFSObject> now,
			NormalFSMultipartPart<NormalFSObject> last) {
		if (last.getTimestamp() >= now.getTimestamp()) {
			NormalFile file = ((NormalFSObject) now.getFSObject()).getNormalFile();
			LOGGER.info("Parent: {}; DELETE: {}  {}; SAVE: {}  {}",
					new Object[] { file.getParentFile().getName(), file.getName(), Long.valueOf(file.length()),
							((NormalFSObject) last.getFSObject()).getNormalFile().getName(),
							Long.valueOf(((NormalFSObject) last.getFSObject()).getNormalFile().length()) });
			if (!file.delete()) {
				LOGGER.warn("Delete Repeated Part File Failed [{}]", FileUtils.getCanonicalPathWithOutException(file));
			}

			return last;
		}

		NormalFSObject fsObject = (NormalFSObject) last.getFSObject();
		NormalFile file = fsObject.getNormalFile();
		LOGGER.info("Parent: {}; DELETE: {}  {}; SAVE: {}  {}",
				new Object[] { file.getParentFile().getName(), file.getName(), Long.valueOf(file.length()),
						((NormalFSObject) now.getFSObject()).getNormalFile().getName(),
						Long.valueOf(((NormalFSObject) now.getFSObject()).getNormalFile().length()) });

		if (!file.delete()) {
			LOGGER.warn("Delete Repeated Part File Failed [{}]", FileUtils.getCanonicalPathWithOutException(file));
		}

		return now;
	}

	public NormalFSObject getNormalObject(NormalFSObject fsObject, long start, long end) throws FSException {
		try {
			long totalLength = end - start + 1L;

			fsObject.setLength(totalLength);

			LimitedSizeInputStream is = new LimitedSizeInputStream(getFileInputStream(fsObject.getNormalFile(), start),
					totalLength);

			fsObject.setInputStream(is);

			return fsObject;
		} catch (java.io.FileNotFoundException e) {
			String message = "Normal FSObject [" + fsObject + " ] not exist.";
			LOGGER.warn(message);
			throw new pw.cdmi.file.engine.filesystem.exception.FileNotFoundException(message, e);
		} catch (IOException e) {
			throw new FileSystemIOException(e);
		}
	}

	private NormalFSObject getSliceObject(NormalFSObject fsObject, long start, long end) throws FSException {
		SortedSet<FSMultipartPart<NormalFSObject>> parts = listFSMultipartPart(fsObject);
		try {
			long nextPartOffset = 0L;

			List<FSMultipartPart<NormalFSObject>> needParts = new ArrayList<FSMultipartPart<NormalFSObject>>(1);

			long firstPartStart = -1L;

			for (FSMultipartPart<NormalFSObject> part : parts) {
				nextPartOffset += part.getFSObject().getLength();

				if (start <= nextPartOffset) {
					needParts.add(part);

					if (firstPartStart < 0L) {
						firstPartStart = start - nextPartOffset + ((NormalFSObject) part.getFSObject()).getLength();
					}
				}

				if (end <= nextPartOffset - 1L) {
					break;
				}
			}

			Iterator<FSMultipartPart<NormalFSObject>> iterator = needParts.iterator();

			long startOffset = firstPartStart;

			Enumeration<InputStream> enumaration = new InputStreamEnumeration(this, iterator, startOffset);

			RetryLimitedSizeInputStream<NormalFSObject> bufInputStream = new RetryLimitedSizeInputStream<NormalFSObject>(
					new SequenceInputStream(enumaration), start, end, fsObject, this);

			fsObject.setInputStream(bufInputStream);
			fsObject.setLength(end - start + 1L);
			return fsObject;
		} catch (Exception e) {
			throw new UnknownFSException(e);
		}
	}

	public FileSystemManager getFSManager() {
		return this.fileSystemManager;
	}

	protected void doAfterPut(NormalFSObject inputObj, NormalFSObject outputObj) throws FSException {
		super.doAfterPut(inputObj, outputObj);

		if (inputObj.getLength() != outputObj.getLength()) {
			String message = "File Upload Failed. The Input File's Length is : " + inputObj.getLength()
					+ ", and The Output File's Length is : " + outputObj.getLength();
			LOGGER.warn(message);

			doDelete(outputObj);

			throw new InvalidFileLengthException(message);
		}
	}

	protected void doAfterCopy(NormalFSObject srcFsObject, NormalFSObject outputObj) throws FSException {
		super.doAfterCopy(srcFsObject, outputObj);

		if (srcFsObject.getLength() != outputObj.getLength()) {
			String message = "File Copy Failed. The Source File's Length is : " + srcFsObject.getLength()
					+ ", and The Dest File's Length is : " + outputObj.getLength();
			LOGGER.warn(message);

			doDelete(outputObj);

			throw new InvalidFileLengthException(message);
		}
	}

	private long getFsObjectLength(NormalFSObject fsObject) throws FSException {
		if (fsObject.getLength() > 0L) {
			return fsObject.getLength();
		}
		if (isSliceFile(fsObject)) {
			SortedSet<FSMultipartPart<NormalFSObject>> parts = listFSMultipartPart(fsObject);
			long len = 0L;

			for (FSMultipartPart<NormalFSObject> part : parts) {
				NormalFSObject partFsObject = (NormalFSObject) part.getFSObject();
				len += partFsObject.getFileLength();
			}
			return len;
		}

		return fsObject.getFileLength();
	}

	private boolean prepareFolder(NormalFile normalFile) {
		if (normalFile.getParentFile() == null) {
			return false;
		}

		if (!normalFile.getParentFile().exists()) {
			if (!normalFile.getParentFile().mkdirs()) {
				if (normalFile.getParentFile().exists()) {
					LOGGER.error("Create foleder failed. Folder: {} is exists",
							FileUtils.getCanonicalPathWithOutException(normalFile.getParentFile()));
					return true;
				}
				LOGGER.error("Create foleder failed. Folder: {} ",
						FileUtils.getCanonicalPathWithOutException(normalFile.getParentFile()));
				return false;
			}
		}

		return true;
	}

	private FileInputStream getFileInputStream(NormalFile file, long start) throws IOException, FSException {
		FileInputStream fis = null;
		boolean needClose = false;
		try {
			fis = new FileInputStream(file);
			if (start <= 0L) {
				return fis;
			}

			long n = 0L;

			n = fis.skip(start);

			if (n != start) {
				LOGGER.warn("getInputStream position:" + start + ",n:" + n + "; [ {} ] ",
						FileUtils.getCanonicalPathWithOutException(file));
			}
			return fis;
		} finally {
			if (needClose) {
				IOUtils.closeQuietly(fis);
			}
		}
	}

	private long getTimestamp(NormalFSObject fsObject, int partNumber) throws GetCanonicalPathException {
		NormalFile sliceDir = new NormalFile(getSliceFileDir(fsObject));

		File[] partFiles = sliceDir
				.listFiles(new PartFileFilenameFilter(new String[] { SLICE_FILE_NAME_PATTERN_PREFIX + partNumber,
						SLICE_FILE_NAME_PATTERN_PREFIX + partNumber + SLICE_TEMP_FILE_NAME_SUFFIX }));

		if ((partFiles == null) || (partFiles.length <= 0)) {
			return System.currentTimeMillis();
		}

		String fileName = null;
		long maxTimestamp = 0L;
		long temp = 0L;
		for (File s : partFiles) {
			fileName = s.getName();

			temp = Long.parseLong(fileName.split(SLICE_FILE_NAME_DELIMITER)[0]);

			if (temp > maxTimestamp) {
				maxTimestamp = temp;
			}
		}

		long timestamp = System.currentTimeMillis();

		if (timestamp <= maxTimestamp) {
			LOGGER.warn("timestamp for fsObject : {}, partNumber : {} is wrong, adjust from {} to {}.",
					new Object[] { fsObject.logFormat(), Integer.valueOf(partNumber), Long.valueOf(timestamp),
							Long.valueOf(maxTimestamp + 1L) });
			return maxTimestamp + 1L;
		}

		return timestamp;
	}

	private static final class PartFileFilenameFilter implements FilenameFilter {
		private String[] namePatterns = null;

		PartFileFilenameFilter(String[] namePatterns) {
			this.namePatterns = namePatterns;
		}

		public boolean accept(File dir, String name) {
			if (this.namePatterns == null) {
				return false;
			}
			for (String pattern : this.namePatterns) {
				if (name.matches(pattern)) {
					return true;
				}
			}

			return false;
		}
	}
}