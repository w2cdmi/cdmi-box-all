package pw.cdmi.file.engine.filesystem.model;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pw.cdmi.core.log.Level;
import pw.cdmi.core.utils.MethodLogAble;
import pw.cdmi.file.engine.filesystem.FSMultipartObject;
import pw.cdmi.file.engine.filesystem.FSMultipartPart;
import pw.cdmi.file.engine.filesystem.FileSystemManager;
import pw.cdmi.file.engine.filesystem.cache.CacheFSObject;
import pw.cdmi.file.engine.filesystem.cache.FileCacheClient;
import pw.cdmi.file.engine.filesystem.cache.ObjectSourceLoader;
import pw.cdmi.file.engine.filesystem.exception.FSEndpointUnavailableException;
import pw.cdmi.file.engine.filesystem.exception.FSException;
import pw.cdmi.file.engine.filesystem.exception.NoEnoughSpaceException;
import pw.cdmi.file.engine.filesystem.extend.FileSystemContext;
import pw.cdmi.file.engine.filesystem.extend.FileSystemHock;
import pw.cdmi.file.engine.filesystem.extend.MultipartParam;
import pw.cdmi.file.engine.object.domain.FileObject;

public abstract class FileSystem<T extends FSObject> {
	private static final Logger LOGGER = LoggerFactory.getLogger(FileSystem.class);

	private List<FileSystemHock<T>> hocks = new ArrayList<FileSystemHock<T>>(1);

	public void regiestHock(FileSystemHock<T> hock) {
		this.hocks.add(hock);
	}

	public abstract FileSystemManager getFSManager();

	public abstract T transToFSObject(FSEndpoint paramFSEndpoint, FileObject paramFileObject) throws FSException;

	public abstract T transToFSObject(String paramString) throws FSException;

	@MethodLogAble(Level.INFO)
	public T putObject(T fsObject, InputStream inputStream) throws FSException {
		FileSystemContext context = FileSystemContext.getCurrent(this);
		for (FileSystemHock<T> hock : this.hocks) {
			hock.beforePutObject(context, fsObject, inputStream);
		}

		doBeforePut(fsObject);

		ByteArrayInputStream byteArrayInputStream = null;

		if ((inputStream != null) && (isSupportCache()) && (isSupportCacheBuffered(fsObject.getLength()))) {
			byte[] buffer = transToCacheByteBuffer(inputStream);

			byteArrayInputStream = new ByteArrayInputStream(buffer);
			byteArrayInputStream.mark(buffer.length);
			inputStream = byteArrayInputStream;
		}

		T outputObj = doPut(fsObject, inputStream);

		doAfterPut(fsObject, outputObj);

		putToCache(fsObject, byteArrayInputStream);

		byteArrayInputStream = null;

		for (FileSystemHock<T> hock : this.hocks) {
			hock.afterPutObject(context, fsObject, inputStream, outputObj);
		}

		return outputObj;
	}

	public abstract T doPut(T paramT, InputStream paramInputStream) throws FSException;

	@SuppressWarnings("unchecked")
	@MethodLogAble(Level.INFO)
	public T getObject(T fsObject) throws FSException {
		FileSystemContext context = FileSystemContext.getCurrent(this);
		for (FileSystemHock<T> hock : this.hocks) {
			hock.beforeGetObject(context, fsObject);
		}

		CacheFSObject cacheFSObject = getFromCache(fsObject);
		if (cacheFSObject != null) {
			LOGGER.info("get from cache success.");
			fsObject.setLength(cacheFSObject.getLength());
			fsObject.setInputStream(cacheFSObject.getInputStream());
			return fsObject;
		}

		T cacheObject = null;
		try {
			cacheObject = (T) fsObject.clone();
		} catch (CloneNotSupportedException e) {
			LOGGER.warn("clone fsObject failed.", e);
		}

		T object = doGetObject(fsObject, null, null);
		byte[] buffer;
		if ((object != null) && (isSupportCache()) && (object.getInputStream() != null)) {
			ByteArrayInputStream byteArrayInputStream = null;
			if (isSupportCacheBuffered(fsObject.getLength())) {
				buffer = transToCacheByteBuffer(object.getInputStream());
				byteArrayInputStream = new ByteArrayInputStream(buffer);
				byteArrayInputStream.mark(buffer.length);

				ByteArrayInputStream out = new ByteArrayInputStream(buffer);
				out.mark(buffer.length);
				object.setInputStream(out);
			}
			if (cacheObject != null) {
				cacheObject.setLength(object.getLength());
				putToCache(cacheObject, byteArrayInputStream);
			}

			byteArrayInputStream = null;
		}

		for (FileSystemHock<T> hock : this.hocks) {
			hock.afterGetObject(context, fsObject, object);
		}

		return object;
	}

	protected abstract FileCacheClient getFileCacheClient();

	@MethodLogAble(Level.INFO)
	public T getObject(T fsObject, Long start, Long end) throws FSException {
		FileSystemContext context = FileSystemContext.getCurrent(this);
		for (FileSystemHock<T> hock : this.hocks) {
			hock.beforeGetObject(context, fsObject, start, end);
		}

		T obj = doGetObject(fsObject, start, end);

		for (FileSystemHock<T> hock : this.hocks) {
			hock.afterGetObject(context, fsObject, start, end, obj);
		}

		return obj;
	}

	public abstract T doGetObject(T paramT, Long paramLong1, Long paramLong2) throws FSException;

	@MethodLogAble(Level.INFO)
	public boolean deleteObject(T fsObject) throws FSException {
		FileSystemContext context = FileSystemContext.getCurrent(this);
		for (FileSystemHock<T> hock : this.hocks) {
			hock.beforeDeleteObject(context, fsObject);
		}

		doDelete(fsObject);

		boolean result = false;
		if (doAfterDelete(fsObject)) {
			if (getFileCacheClient().cacheSupportedValue()) {
				deleteCache(fsObject);
			}

			result = true;
		}

		for (FileSystemHock<T> hock : this.hocks) {
			hock.afterDeleteObject(context, fsObject, result);
		}

		return result;
	}

	public abstract boolean doDelete(T paramT) throws FSException;

	public abstract boolean checkObjectExist(T paramT) throws FSException;

	@MethodLogAble(Level.INFO)
	public T copyObject(T srcFsObject, T destFsObject) throws FSException {
		FileSystemContext context = FileSystemContext.getCurrent(this);
		for (FileSystemHock<T> hock : this.hocks) {
			hock.beforeCopyObject(context, srcFsObject, destFsObject);
		}

		doBeforeCopy(srcFsObject, destFsObject);

		T outputObj = doCopy(srcFsObject, destFsObject);

		doAfterCopy(srcFsObject, outputObj);

		for (FileSystemHock<T> hock : this.hocks) {
			hock.afterCopyObject(context, srcFsObject, destFsObject, outputObj);
		}

		return outputObj;
	}

	protected abstract T doCopy(T paramT1, T paramT2) throws FSException;

	@MethodLogAble(Level.INFO)
	public FSMultipartObject multipartStartUpload(T srcFsObject) throws FSException {
		FileSystemContext context = FileSystemContext.getCurrent(this);
		for (FileSystemHock<T> hock : this.hocks) {
			hock.beforeMultipartStartUpload(context, srcFsObject);
		}

		FSMultipartObject obj = doMultipartStartUpload(srcFsObject);

		for (FileSystemHock<T> hock : this.hocks) {
			hock.afterMultipartStartUpload(context, srcFsObject, obj);
		}

		return obj;
	}

	protected abstract FSMultipartObject doMultipartStartUpload(T paramT) throws FSException;

	@MethodLogAble(Level.INFO)
	public FSMultipartPart<T> multipartUploadPart(T fsObject, String uploadID, int partNumber, InputStream inputStream)
			throws FSException {
		FileSystemContext context = FileSystemContext.getCurrent(this);
		for (FileSystemHock<T> hock : this.hocks) {
			hock.beforeMultipartUploadPart(context, fsObject, uploadID, partNumber, inputStream);
		}
		doBeforeMultipartUploadPart(fsObject, uploadID, partNumber);
		FSMultipartPart<T> part = doMultipartUploadPart(fsObject, uploadID, partNumber, inputStream);
		doAfterMultipartUploadPart(fsObject, uploadID, partNumber, part);

		for (FileSystemHock<T> hock : this.hocks) {
			MultipartParam multipartParam = new MultipartParam(uploadID, partNumber,part.getETag(),part.getPartCRC(),part.getPartSize());
			hock.afterMultipartUploadPart(context, fsObject, multipartParam, inputStream, part);
		}
		return part;
	}

	protected abstract FSMultipartPart<T> doMultipartUploadPart(T paramT, String paramString, int paramInt,
			InputStream paramInputStream) throws FSException;

	@MethodLogAble(Level.INFO)
	public FSMultipartObject multipartCompleteUpload(T fsObject, String uploadID) throws FSException {
		FileSystemContext context = FileSystemContext.getCurrent(this);
		for (FileSystemHock<T> hock : this.hocks) {
			hock.beforeMultipartCompleteUpload(context, fsObject, uploadID);
		}

		FSMultipartObject obj = doMultipartCompleteUpload(fsObject, uploadID);

		for (FileSystemHock<T> hock : this.hocks) {
			hock.afterMultipartCompleteUpload(context, fsObject, uploadID, obj);
		}

		return obj;
	}

	public abstract FSMultipartObject doMultipartCompleteUpload(T paramT, String paramString) throws FSException;

	@MethodLogAble(Level.INFO)
	public boolean multipartAbortUpload(T fsObject, String uploadID) throws FSException {
		FileSystemContext context = FileSystemContext.getCurrent(this);
		for (FileSystemHock<T> hock : this.hocks) {
			hock.beforeMultipartAbortUpload(context, fsObject, uploadID);
		}

		boolean result = doMultipartAbortUpload(fsObject, uploadID);

		for (FileSystemHock<T> hock : this.hocks) {
			hock.afterMultipartAbortUpload(context, fsObject, uploadID, result);
		}

		return result;
	}

	public abstract boolean doMultipartAbortUpload(T paramT, String paramString) throws FSException;

	@MethodLogAble(Level.INFO)
	public SortedSet<FSMultipartPart<T>> multipartListParts(T fsObject, String uploadID) throws FSException {
		FileSystemContext context = FileSystemContext.getCurrent(this);
		for (FileSystemHock<T> hock : this.hocks) {
			hock.beforeMultipartListParts(context, fsObject, uploadID);
		}

		SortedSet<FSMultipartPart<T>> obj = doMultipartListParts(fsObject, uploadID);

		for (FileSystemHock<T> hock : this.hocks) {
			hock.afterMultipartListParts(context, fsObject, uploadID, obj);
		}

		return obj;
	}

	public abstract SortedSet<FSMultipartPart<T>> doMultipartListParts(T paramT, String paramString) throws FSException;

	public abstract boolean deleteRealMultipartObject(T paramT) throws FSException;

	protected void doBeforePut(T inputObj) throws FSException {
		if ((inputObj.getFSEndpoint() == null) || (inputObj.getFSAccessPath() == null)) {
			throw new FSEndpointUnavailableException("FSEndpoint is Unavailable [ " + inputObj.logFormat() + " ]");
		}

		if (inputObj.getFSEndpoint().isNoSpace()) {
			throw new NoEnoughSpaceException("No EnoughSpace For [ " + inputObj.logFormat() + " ]");
		}
	}

	protected void doAfterPut(T inputObj, T outputObj) throws FSException {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("do nothing...");
		}
	}

	protected boolean doAfterDelete(T fsObject) throws FSException {
		return !checkObjectExist(fsObject);
	}

	protected void doBeforeCopy(T srcFsObject, T outputObj) throws FSException {
		if ((outputObj.getFSEndpoint() == null) || (outputObj.getFSAccessPath() == null)) {
			throw new FSEndpointUnavailableException("FSEndpoint is Unavailable [ " + outputObj.logFormat() + " ]");
		}

		if (outputObj.getFSEndpoint().isNoSpace()) {
			throw new NoEnoughSpaceException("No EnoughSpace For [ " + outputObj.logFormat() + " ]");
		}
	}

	protected void doAfterCopy(T srcFsObject, T outputObj) throws FSException {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("do nothing...");
		}
	}

	protected void doBeforeMultipartUploadPart(T fsObject, String uploadID, int partNumber) throws FSException {
		if ((fsObject.getFSEndpoint() == null) || (fsObject.getFSAccessPath() == null)) {
			throw new FSEndpointUnavailableException("FSEndpoint is Unavailable [ " + fsObject.logFormat() + " ]");
		}

		if (fsObject.getFSEndpoint().isNoSpace()) {
			throw new NoEnoughSpaceException("No EnoughSpace For [ " + fsObject.logFormat() + " ]");
		}
	}

	protected void doAfterMultipartUploadPart(T fsObject, String uploadID, int partNumber, FSMultipartPart<T> part) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("do nothing...");
		}
	}

	protected String getNewKey(FileObject fileObject) {
		return String.valueOf(System.currentTimeMillis()) + '_' + fileObject.getObjectID();
	}

	public abstract boolean renameTo(T paramT1, T paramT2) throws FSException;

	private boolean isSupportCache() {
		if ((getFileCacheClient() == null) || (!getFileCacheClient().cacheSupportedValue())) {
			return false;
		}

		return true;
	}

	private boolean isSupportCacheBuffered(long objectLength) {
		if ((getFileCacheClient() == null) || (!getFileCacheClient().supportBuffered(objectLength))) {
			return false;
		}

		return true;
	}

	private byte[] transToCacheByteBuffer(InputStream inputStream) throws FSException {
		try {
			return IOUtils.toByteArray(inputStream);
		} catch (IOException e) {
			String message = "Trans To Byte Array Failed.";
			LOGGER.warn(message, e);
			throw new FSException(message, e);
		} finally {
			IOUtils.closeQuietly(inputStream);
		}
	}

	private void putToCache(T fsObject, ByteArrayInputStream inputStream) {
		if (!isSupportCache()) {
			return;
		}

		try {
			getFileCacheClient().putObject(fsObject, new ObjectSourceLoader<T>(this, fsObject), inputStream);
		} catch (Exception e) {
			LOGGER.warn("put object to cached failed. exception : {}", e.getMessage());
		} finally {
			IOUtils.closeQuietly(inputStream);
		}
	}

	private CacheFSObject getFromCache(T fsObject) {
		if (getFileCacheClient() == null) {
			return null;
		}

		try {
			return getFileCacheClient().getObject(new CacheFSObject(fsObject.getObjectKey()));
		} catch (Exception e) {
			LOGGER.warn("get object from cached failed. exception : {}", e.getMessage());
		}

		return null;
	}

	private void deleteCache(T fsObject) {
		if (getFileCacheClient() == null) {
			return;
		}

		try {
			getFileCacheClient().deleteObject(new CacheFSObject(fsObject.getObjectKey()));
		} catch (Exception e) {
			LOGGER.warn("delete object from cached failed.", e);
		}
	}
}