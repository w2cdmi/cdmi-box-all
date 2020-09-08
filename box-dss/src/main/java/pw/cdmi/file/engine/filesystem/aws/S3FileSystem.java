package pw.cdmi.file.engine.filesystem.aws;

import java.io.InputStream;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.commons.lang.StringUtils;
import org.jets3t.service.ServiceException;
import org.jets3t.service.model.MultipartPart;
import org.jets3t.service.model.MultipartUpload;
import org.jets3t.service.model.S3Object;
import org.jets3t.service.security.AWSCredentials;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import pw.cdmi.core.exception.InnerException;
import pw.cdmi.core.log.Level;
import pw.cdmi.core.utils.MethodLogAble;
import pw.cdmi.file.engine.filesystem.FSMultipartObject;
import pw.cdmi.file.engine.filesystem.FSMultipartPart;
import pw.cdmi.file.engine.filesystem.FileSystemManager;
import pw.cdmi.file.engine.filesystem.cache.FileCacheClient;
import pw.cdmi.file.engine.filesystem.exception.FSException;
import pw.cdmi.file.engine.filesystem.exception.FileNotFoundException;
import pw.cdmi.file.engine.filesystem.exception.FileSystemIOException;
import pw.cdmi.file.engine.filesystem.model.FSEndpoint;
import pw.cdmi.file.engine.filesystem.model.FileSystem;
import pw.cdmi.file.engine.filesystem.model.cloud.S3BucketInfo;
import pw.cdmi.file.engine.filesystem.uds.support.UDSJets3tProperties;
import pw.cdmi.file.engine.filesystem.uds.support.UDSRestS3Service;
import pw.cdmi.file.engine.object.domain.FileObject;

@Service("s3FileSystem")
public class S3FileSystem extends FileSystem<S3FSObject> {
	private static final Logger LOGGER = LoggerFactory.getLogger(S3FileSystem.class);

	@Autowired
	@Qualifier("s3FileSystemManager")
	private FileSystemManager fileSystemManager;

	@Autowired
	private FileCacheClient fileCacheClient;

	@MethodLogAble(Level.INFO)
	public S3FSObject transToFSObject(FSEndpoint fsEndpoint, FileObject fileObject) throws FSException {
		S3FSObject fsObject = new S3FSObject(fsEndpoint, getNewKey(fileObject));

		if (fileObject.getObjectLength() >= 0L) {
			fsObject.setLength(fileObject.getObjectLength());
		}

		if(StringUtils.isNotBlank(fileObject.getObjectName())) {
			fsObject.setName(fileObject.getObjectName());
		}

		return fsObject;
	}

	@MethodLogAble(Level.INFO)
	public S3FSObject transToFSObject(String path) throws FSException {
		S3FSObject fsObject = new S3FSObject(path);

		return fsObject;
	}

	@MethodLogAble(Level.INFO)
	public S3FSObject doPut(S3FSObject fsObject, InputStream inputStream) throws FSException {
		try {
			UDSRestS3Service s3Service = getS3Service(fsObject.getBucket());
			S3Object s3Object = new S3Object(fsObject.getObjectKey());
			s3Object.setDataInputStream(inputStream);
			s3Object.setContentType("text/plain");
			s3Object.setContentLength(fsObject.getLength());

/*
			if(StringUtils.isNotBlank(fsObject.getName())) {
				s3Object.setContentDisposition("attachment;filename*=utf-8''" + fsObject.getName()); //华为云不需要转码成ISO-8859-1
			}
*/

			s3Object = s3Service.putObject(fsObject.getBucket().getName(), s3Object);
			fsObject.setLength(s3Object.getContentLength());
			return fsObject;
		} catch (Exception e) {
			LOGGER.warn("put object failed! [ " + fsObject.logFormat() + " ]", e);
			throw new FileSystemIOException(e);
		}
	}

	@MethodLogAble(Level.INFO)
	public S3FSObject doGetObject(S3FSObject fsObject, Long start, Long end) throws FSException {
		try {
			UDSRestS3Service s3Service = getS3Service(fsObject.getBucket());
			S3Object s3Object = s3Service.getObject(fsObject.getBucket().getName(), fsObject.getObjectKey(), null, null,
					null, null, start, end);

			if (s3Object != null) {
				fsObject.setInputStream(s3Object.getDataInputStream());
				fsObject.setLength(s3Object.getContentLength());
				return fsObject;
			}
			return null;
		} catch (ServiceException e) {
			throw transException(e);
		}
	}

	@MethodLogAble(Level.INFO)
	public boolean doDelete(S3FSObject fsObject) throws FSException {
		try {
			UDSRestS3Service s3Service = getS3Service(fsObject.getBucket());

			s3Service.deleteObject(fsObject.getBucket().getName(), fsObject.getObjectKey());

			return true;
		} catch (ServiceException e) {
			throw transException(e);
		}
	}

	protected boolean doAfterDelete(S3FSObject fsObject) {
		return true;
	}

	@MethodLogAble(Level.INFO)
	public boolean checkObjectExist(S3FSObject fsObject) throws FSException {
		try {
			UDSRestS3Service s3Service = getS3Service(fsObject.getBucket());

			return s3Service.isObjectInBucket(fsObject.getBucket().getName(), fsObject.getObjectKey());
		} catch (ServiceException e) {
			LOGGER.warn("maybe object does not exsit! [ " + fsObject.logFormat() + " ]", e);
			throw transException(e);
		}
	}

	@MethodLogAble(Level.INFO)
	public S3FSObject doCopy(S3FSObject srcFsObject, S3FSObject destFsObject) throws FSException {
		try {
			UDSRestS3Service s3Service = getS3Service(destFsObject.getBucket());

			s3Service.copyObject(srcFsObject.getBucket().getName(), srcFsObject.getObjectKey(),
					destFsObject.getBucket().getName(), new S3Object(destFsObject.getObjectKey()), false);

			destFsObject.setLength(srcFsObject.getLength());
			return destFsObject;
		} catch (ServiceException e) {
			throw transException(e);
		}
	}

	@MethodLogAble(Level.INFO)
	public FSMultipartObject doMultipartStartUpload(S3FSObject fsObject) throws FSException {
		try {
			UDSRestS3Service s3Service = getS3Service(fsObject.getBucket());

			MultipartUpload mobj = s3Service.multipartStartUpload(fsObject.getBucket().getName(),
					new S3Object(fsObject.getObjectKey()));

			return new FSMultipartObject(fsObject, mobj.getUploadId());
		} catch (ServiceException e) {
			throw transException(e);
		}
	}

	@MethodLogAble(Level.INFO)
	public FSMultipartPart<S3FSObject> doMultipartUploadPart(S3FSObject fsObject, String uploadID, int partNumber,
			InputStream inputStream) throws FSException {
		try {
			UDSRestS3Service s3Service = getS3Service(fsObject.getBucket());

			S3Object s3Object = new S3Object(fsObject.getObjectKey());

			s3Object.setDataInputStream(inputStream);

			s3Object.setContentType("text/plain");
			s3Object.setContentLength(fsObject.getLength());

			MultipartUpload mobj = new MultipartUpload(uploadID, fsObject.getBucket().getName(),
					fsObject.getObjectKey());

			s3Service.multipartUploadPart(mobj, Integer.valueOf(partNumber), s3Object);

			FSMultipartPart<S3FSObject> part = new FSMultipartPart<S3FSObject>(fsObject, partNumber);

			LOGGER.info("part : " + part + ";  " + fsObject.getLength());

			return part;
		} catch (ServiceException e) {
			throw transException(e);
		}
	}

	@MethodLogAble(Level.INFO)
	public FSMultipartObject doMultipartCompleteUpload(S3FSObject fsObject, String uploadID) throws FSException {
		try {
			UDSRestS3Service s3Service = getS3Service(fsObject.getBucket());

			if (!checkObjectExist(fsObject)) {
				MultipartUpload mobj = new MultipartUpload(uploadID, fsObject.getBucket().getName(),
						fsObject.getObjectKey());
				s3Service.multipartCompleteUpload(mobj);
			}

			S3Object s3Object = s3Service.getObject(fsObject.getBucket().getName(), fsObject.getObjectKey());
			if (s3Object != null) {
				fsObject.setLength(s3Object.getContentLength());
			} else {
				String message = "FsObject [ " + fsObject.logFormat() + " ] Not Exists";
				LOGGER.warn(message);
				throw new FileNotFoundException(message);
			}
			return new FSMultipartObject(fsObject, uploadID);
		} catch (ServiceException e) {
			throw transException(e);
		}
	}

	@MethodLogAble(Level.INFO)
	public boolean doMultipartAbortUpload(S3FSObject fsObject, String uploadID) throws FSException {
		try {
			UDSRestS3Service s3Service = getS3Service(fsObject.getBucket());

			MultipartUpload mobj = new MultipartUpload(uploadID, fsObject.getBucket().getName(),
					fsObject.getObjectKey());

			s3Service.multipartAbortUpload(mobj);

			return true;
		} catch (ServiceException e) {
			throw transException(e);
		}
	}

	@MethodLogAble(Level.INFO)
	public SortedSet<FSMultipartPart<S3FSObject>> doMultipartListParts(S3FSObject fsObject, String uploadID)
			throws FSException {
		try {
			UDSRestS3Service s3Service = getS3Service(fsObject.getBucket());

			MultipartUpload mobj = new MultipartUpload(uploadID, fsObject.getBucket().getName(),
					fsObject.getObjectKey());

			List<MultipartPart> s3Parts = s3Service.multipartListParts(mobj);

			if (s3Parts == null) {
				return null;
			}

			SortedSet<FSMultipartPart<S3FSObject>> parts = new TreeSet<FSMultipartPart<S3FSObject>>();

			FSMultipartPart<S3FSObject> fsp = null;
			for (MultipartPart s3p : s3Parts) {
				fsp = new FSMultipartPart<S3FSObject>((S3FSObject) fsObject.clone(), s3p.getPartNumber().intValue());

				((S3FSObject) fsp.getFSObject()).setLength(s3p.getSize().longValue());

				parts.add(fsp);
			}

			return parts;
		} catch (CloneNotSupportedException e) {
			LOGGER.warn("Clone UDSFSObject Failed.", e);
			throw new InnerException(e);
		} catch (ServiceException e) {
			throw transException(e);
		}
	}

	private UDSRestS3Service getS3Service(S3BucketInfo bucket) throws ServiceException {
		UDSRestS3Service s3Service = new UDSRestS3Service(getAkSk(bucket),
				UDSJets3tProperties.getInstance(bucket.getUserInfo().getEndpointInfo()));
		return s3Service;
	}

	private AWSCredentials getAkSk(S3BucketInfo bucket) {
		return new AWSCredentials(bucket.getUserInfo().getAk(), bucket.getUserInfo().getSk());
	}

	public FileSystemManager getFSManager() {
		return this.fileSystemManager;
	}

	private FSException transException(ServiceException e) {
		int responseCode = e.getResponseCode();
		if (404 == responseCode) {
			return new FileNotFoundException(e.toString());
		}

		return new FileSystemIOException(e);
	}

	@MethodLogAble(Level.INFO)
	public boolean renameTo(S3FSObject fsObject, S3FSObject newFsObject) throws FSException {
		throw new UnsupportedOperationException("uds filesystem unsupported this operation");
	}

	public boolean deleteRealMultipartObject(S3FSObject inputObj) throws FSException {
		throw new UnsupportedOperationException("uds filesystem unsupported this operation");
	}

	protected FileCacheClient getFileCacheClient() {
		return this.fileCacheClient;
	}
}