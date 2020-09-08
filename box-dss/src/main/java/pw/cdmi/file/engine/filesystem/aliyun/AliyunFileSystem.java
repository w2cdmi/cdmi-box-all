package pw.cdmi.file.engine.filesystem.aliyun;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import com.aliyun.oss.model.*;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.aliyun.oss.ClientException;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.OSSException;

import pw.cdmi.core.exception.InnerException;
import pw.cdmi.core.log.Level;
import pw.cdmi.core.utils.MethodLogAble;
import pw.cdmi.file.engine.filesystem.FSMultipartObject;
import pw.cdmi.file.engine.filesystem.FSMultipartPart;
import pw.cdmi.file.engine.filesystem.FileSystemManager;
import pw.cdmi.file.engine.filesystem.aws.S3FSObject;
import pw.cdmi.file.engine.filesystem.cache.FileCacheClient;
import pw.cdmi.file.engine.filesystem.exception.FSException;
import pw.cdmi.file.engine.filesystem.exception.FileNotFoundException;
import pw.cdmi.file.engine.filesystem.exception.FileSystemIOException;
import pw.cdmi.file.engine.filesystem.model.FSEndpoint;
import pw.cdmi.file.engine.filesystem.model.FileSystem;
import pw.cdmi.file.engine.filesystem.model.cloud.S3BucketInfo;
import pw.cdmi.file.engine.object.domain.FileObject;
import pw.cdmi.file.engine.object.domain.MultipartFileObject;
import pw.cdmi.file.engine.object.domain.MultipartPart;
import pw.cdmi.file.engine.object.manager.MultipartFileObjectManager;

@Service("aliyunFileSystem")
public class AliyunFileSystem extends FileSystem<S3FSObject> {
	private static final Logger LOGGER = LoggerFactory.getLogger(AliyunFileSystem.class);

	@Autowired
	@Qualifier("aliyunFileSystemManager")
	private FileSystemManager fileSystemManager;
	@Autowired
	private FileCacheClient fileCacheClient;
	@Autowired
	@Qualifier("multipartFileObjectManager")
	private MultipartFileObjectManager multipartFileObjectManager;

	public FileSystemManager getFSManager() {
		return this.fileSystemManager;
	}

	@MethodLogAble(Level.INFO)
	@Override
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
	@Override
	public S3FSObject transToFSObject(String path) throws FSException {
		S3FSObject fsObject = new S3FSObject(path);

		return fsObject;
	}

	@MethodLogAble(Level.INFO)
	@Override
	public S3FSObject doPut(S3FSObject fsObject, InputStream inputStream) throws FSException {
		try {
			String[] infos = fsObject.getFSEndpoint().getEndpoint().split(":");
			String dns = StringUtils.trimToEmpty(infos[0]);

			S3BucketInfo bucket = fsObject.getBucket();

			OSSClient ossClient = new OSSClient(dns, bucket.getUserInfo().getAk(), bucket.getUserInfo().getSk());
			ObjectMetadata metadata = new ObjectMetadata();

/*
			if(StringUtils.isNotBlank(fsObject.getName())) {
				//设置文件名元数据，以使用云存储直接下载时，也能获取到正确的文件名
				metadata.setContentDisposition("attachment;filename*=utf-8''" + new String(fsObject.getName().getBytes("utf-8"), "ISO-8859-1"));
			}
*/

			ossClient.putObject(bucket.getName(), fsObject.getObjectKey(), inputStream, metadata);
			SimplifiedObjectMeta objectMeta = ossClient.getSimplifiedObjectMeta(bucket.getName(), fsObject.getObjectKey());
			fsObject.setLength(objectMeta.getSize());
			ossClient.shutdown();
			return fsObject;
		} catch (Exception e) {
			LOGGER.warn("put object failed! [ " + fsObject.logFormat() + " ]", e);
			throw new FileSystemIOException(e);
		}
	}

	@Override
	protected FileCacheClient getFileCacheClient() {
		return this.fileCacheClient;
	}

	@Override
	public S3FSObject doGetObject(S3FSObject fsObject, Long start, Long end) throws FSException {
		try {
			String[] infos = fsObject.getFSEndpoint().getEndpoint().split(":");
			String dns = StringUtils.trimToEmpty(infos[0]);

			S3BucketInfo bucket = fsObject.getBucket();

			OSSClient ossClient = new OSSClient(dns, bucket.getUserInfo().getAk(), bucket.getUserInfo().getSk());
			GetObjectRequest request = new GetObjectRequest(bucket.getName(), fsObject.getObjectKey());
			if(start == null) {
				start = -1L;
			}
			if(end == null) {
				end = -1L;
			}
			request.setRange(start, end);
			OSSObject ossobject = ossClient.getObject(request);

			if (ossobject != null) {
				fsObject.setInputStream(ossobject.getObjectContent());
				fsObject.setLength(ossobject.getResponse().getContentLength());
				return fsObject;
			} else {
				throw new FileNotFoundException("文件沒有找到！");
			}
		} catch (OSSException e) {
			throw new FileSystemIOException(e);
		}
	}

	@Override
	public boolean doDelete(S3FSObject fsObject) throws FSException {
		try {
			String[] infos = fsObject.getFSEndpoint().getEndpoint().split(":");
			String dns = StringUtils.trimToEmpty(infos[0]);

			S3BucketInfo bucket = fsObject.getBucket();

			OSSClient ossClient = new OSSClient(dns, bucket.getUserInfo().getAk(), bucket.getUserInfo().getSk());
			ossClient.deleteObject(bucket.getName(), fsObject.getObjectKey());
			;
			return true;
		} catch (OSSException e) {
			throw new FileSystemIOException(e);
		} catch (ClientException e) {
			throw new FileSystemIOException(e);
		}
	}

	@Override
	public boolean checkObjectExist(S3FSObject fsObject) throws FSException {
		try {
			String[] infos = fsObject.getFSEndpoint().getEndpoint().split(":");
			String dns = StringUtils.trimToEmpty(infos[0]);

			S3BucketInfo bucket = fsObject.getBucket();

			OSSClient ossClient = new OSSClient(dns, bucket.getUserInfo().getAk(), bucket.getUserInfo().getSk());
			return ossClient.doesObjectExist(bucket.getName(), fsObject.getObjectKey());
		} catch (OSSException e) {
			throw new FileSystemIOException(e);
		} catch (ClientException e) {
			throw new FileSystemIOException(e);
		}
	}

	@Override
	protected S3FSObject doCopy(S3FSObject srcFsObject, S3FSObject destFsObject) throws FSException {
		try {
			String[] infos = destFsObject.getFSEndpoint().getEndpoint().split(":");
			String dns = StringUtils.trimToEmpty(infos[0]);

			S3BucketInfo srcbucket = srcFsObject.getBucket();
			S3BucketInfo destbucket = destFsObject.getBucket();

			OSSClient ossClient = new OSSClient(dns, destbucket.getUserInfo().getAk(),
					destbucket.getUserInfo().getSk());
			ossClient.copyObject(srcbucket.getName(), srcFsObject.getObjectKey(), destbucket.getName(),
					destFsObject.getObjectKey());
			destFsObject.setLength(srcFsObject.getLength());
			return destFsObject;
		} catch (OSSException e) {
			throw new FileSystemIOException(e);
		} catch (ClientException e) {
			throw new FileSystemIOException(e);
		}
	}

	@Override
	protected FSMultipartObject doMultipartStartUpload(S3FSObject fsObject) throws FSException {
		try {
			String[] infos = fsObject.getFSEndpoint().getEndpoint().split(":");
			String dns = StringUtils.trimToEmpty(infos[0]);

			S3BucketInfo bucket = fsObject.getBucket();

			OSSClient ossClient = new OSSClient(dns, bucket.getUserInfo().getAk(), bucket.getUserInfo().getSk());

			InitiateMultipartUploadRequest request = new InitiateMultipartUploadRequest(bucket.getName(),
					fsObject.getObjectKey());
			InitiateMultipartUploadResult result = ossClient.initiateMultipartUpload(request);
			String uploadId = result.getUploadId();
			return new FSMultipartObject(fsObject, uploadId);
		} catch (OSSException e) {
			throw new FileSystemIOException(e);
		} catch (ClientException e) {
			throw new FileSystemIOException(e);
		}
	}

	@Override
	protected FSMultipartPart<S3FSObject> doMultipartUploadPart(S3FSObject fsObject, String uploadId, int partNumber,
			InputStream inputStream) throws FSException {
		try {
			String[] infos = fsObject.getFSEndpoint().getEndpoint().split(":");
			String dns = StringUtils.trimToEmpty(infos[0]);

			S3BucketInfo bucket = fsObject.getBucket();

			OSSClient ossClient = new OSSClient(dns, bucket.getUserInfo().getAk(), bucket.getUserInfo().getSk());

			UploadPartRequest uploadPartRequest = new UploadPartRequest();
			uploadPartRequest.setBucketName(bucket.getName());
			uploadPartRequest.setKey(fsObject.getObjectKey());
			uploadPartRequest.setUploadId(uploadId);
			uploadPartRequest.setInputStream(inputStream);
			// 设置分片大小，除最后一个分片外，其它分片要大于100KB
			uploadPartRequest.setPartSize(fsObject.getLength());
			// 设置分片号，范围是1~10000，
			uploadPartRequest.setPartNumber(partNumber);
			UploadPartResult uploadPartResult = ossClient.uploadPart(uploadPartRequest);
			PartETag partETag = uploadPartResult.getPartETag();

			FSMultipartPart<S3FSObject> part = new FSMultipartPart<S3FSObject>(fsObject, partNumber, partETag.getETag(),
					partETag.getPartSize(), partETag.getPartCRC());
			LOGGER.info("part : " + part + ";  " + fsObject.getLength());
			return part;
		} catch (OSSException e) {
			throw new FileSystemIOException(e);
		} catch (ClientException e) {
			throw new FileSystemIOException(e);
		}
	}

	@Override
	public FSMultipartObject doMultipartCompleteUpload(S3FSObject fsObject, String uploadId) throws FSException {
		List<PartETag> partETags = new ArrayList<PartETag>();
		try {
			String[] infos = fsObject.getFSEndpoint().getEndpoint().split(":");
			String dns = StringUtils.trimToEmpty(infos[0]);

			S3BucketInfo bucket = fsObject.getBucket();

			// FIXME 獲取分片列表,這裏可能有問題，需要檢查slice_fileobject
			MultipartFileObject multfile = multipartFileObjectManager.listMultipartParts(fsObject.getObjectKey());
			if (multfile == null || multfile.getParts().size() == 0) {
				String message = "FsObject [ " + fsObject.logFormat() + " ] Not Exists";
				LOGGER.warn(message);
				throw new FileNotFoundException(message);
			}
			TreeSet<MultipartPart> parts = multfile.getParts();
			PartETag partEtag = null;
			for (MultipartPart part : parts) {
				partEtag = new PartETag(part.getPartId(), part.geteTag(), part.getPartSize(), part.getPartCRC());
				partETags.add(partEtag);
			}

			OSSClient ossClient = new OSSClient(dns, bucket.getUserInfo().getAk(), bucket.getUserInfo().getSk());

			Collections.sort(partETags, new Comparator<PartETag>() {
				@Override
				public int compare(PartETag p1, PartETag p2) {
					return p1.getPartNumber() - p2.getPartNumber();
				}
			});
			CompleteMultipartUploadRequest completeMultipartUploadRequest = new CompleteMultipartUploadRequest(
					bucket.getName(), fsObject.getObjectKey(), uploadId, partETags);
			ossClient.completeMultipartUpload(completeMultipartUploadRequest);

			SimplifiedObjectMeta objectMeta = ossClient.getSimplifiedObjectMeta(bucket.getName(),
					fsObject.getObjectKey());
			fsObject.setLength(objectMeta.getSize());
			partETags.clear();
			return new FSMultipartObject(fsObject, uploadId);
		} catch (OSSException e) {
			throw new FileSystemIOException(e);
		} catch (ClientException e) {
			throw new FileSystemIOException(e);
		}
	}

	@Override
	public boolean doMultipartAbortUpload(S3FSObject fsObject, String uploadId) throws FSException {
		try {
			String[] infos = fsObject.getFSEndpoint().getEndpoint().split(":");
			String dns = StringUtils.trimToEmpty(infos[0]);

			S3BucketInfo bucket = fsObject.getBucket();

			// 创建OSSClient实例
			OSSClient ossClient = new OSSClient(dns, bucket.getUserInfo().getAk(), bucket.getUserInfo().getSk());
			// 取消分片上传，其中uploadId来自于initiateMultipartUpload
			AbortMultipartUploadRequest abortMultipartUploadRequest = new AbortMultipartUploadRequest(bucket.getName(),
					fsObject.getObjectKey(), uploadId);
			ossClient.abortMultipartUpload(abortMultipartUploadRequest);
			return true;
		} catch (OSSException e) {
			throw new FileSystemIOException(e);
		} catch (ClientException e) {
			throw new FileSystemIOException(e);
		}
	}

	@Override
	public SortedSet<FSMultipartPart<S3FSObject>> doMultipartListParts(S3FSObject fsObject, String uploadId)
			throws FSException {
		try {
			String[] infos = fsObject.getFSEndpoint().getEndpoint().split(":");
			String dns = StringUtils.trimToEmpty(infos[0]);

			S3BucketInfo bucket = fsObject.getBucket();
			// 创建OSSClient实例
			OSSClient ossClient = new OSSClient(dns, bucket.getUserInfo().getAk(), bucket.getUserInfo().getSk());

			// 列举所有已上传的分片
			PartListing partListing = null;
			ListPartsRequest listPartsRequest = new ListPartsRequest(bucket.getName(), fsObject.getObjectKey(),
					uploadId);

			SortedSet<FSMultipartPart<S3FSObject>> parts = new TreeSet<FSMultipartPart<S3FSObject>>();
			FSMultipartPart<S3FSObject> fsp = null;
			do {
				// FIXME 如果分片超過1000，是否有問題，尚不確定
				partListing = ossClient.listParts(listPartsRequest);
				for (PartSummary part : partListing.getParts()) {
					fsp = new FSMultipartPart<S3FSObject>((S3FSObject) fsObject.clone(), part.getPartNumber(),
							part.getETag(), part.getSize());
					((S3FSObject) fsp.getFSObject()).setLength(part.getSize());
					parts.add(fsp);
				}
				listPartsRequest.setPartNumberMarker(partListing.getNextPartNumberMarker());
			} while (partListing.isTruncated());

			// 关闭client
			ossClient.shutdown();

			return parts;
		} catch (CloneNotSupportedException e) {
			LOGGER.warn("Clone Aliyun OSS Object Failed.", e);
			throw new InnerException(e);
		} catch (OSSException e) {
			throw new FileSystemIOException(e);
		} catch (ClientException e) {
			throw new FileSystemIOException(e);
		}
	}

	@Override
	public boolean deleteRealMultipartObject(S3FSObject paramT) throws FSException {
		throw new UnsupportedOperationException("aliyun oss filesystem unsupported this operation");
	}

	@Override
	public boolean renameTo(S3FSObject paramT1, S3FSObject paramT2) throws FSException {
		throw new UnsupportedOperationException("aliyun oss filesystem unsupported this operation");
	}

}