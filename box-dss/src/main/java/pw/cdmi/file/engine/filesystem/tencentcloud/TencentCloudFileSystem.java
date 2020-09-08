package pw.cdmi.file.engine.filesystem.tencentcloud;



//import com.qcloud.cos.COSClient;
//import com.qcloud.cos.ClientConfig;
//import com.qcloud.cos.request.GetFileInputStreamRequest;
//import com.qcloud.cos.request.UploadFileRequest;
//import com.qcloud.cos.request.UploadSliceFileRequest;
//import com.qcloud.cos.sign.Credentials;

import com.aliyun.oss.ClientException;
import com.aliyun.oss.OSSException;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.exception.CosClientException;
import com.qcloud.cos.exception.CosServiceException;
import com.qcloud.cos.model.*;
import org.apache.commons.lang.StringUtils;
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

import java.io.InputStream;
import java.util.*;

/**
 * Created by nick.qu on 2018/1/5.
 */
@Service("tencentCloudFileSystem")
public class TencentCloudFileSystem extends FileSystem<S3FSObject> {
    @Autowired
    private static final Logger LOGGER = LoggerFactory.getLogger(TencentCloudFileSystem.class);

    @Autowired
    @Qualifier("tencentCloudSystemManager")
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

        return new S3FSObject(path);
    }

    @MethodLogAble(Level.INFO)
    @Override
    public S3FSObject doPut(S3FSObject fsObject, InputStream inputStream) throws FSException {
        try {
            FSEndpoint fsEndpoint = fsObject.getFSEndpoint();
            COSClientInfo cosClientInfo = new COSClientInfo(fsEndpoint);

            S3BucketInfo bucket = fsObject.getBucket();

            cosClientInfo.setSecretKey(bucket.getUserInfo().getSk());
            ObjectMetadata objectMetadata = new ObjectMetadata();
            //使用S3FSObject中的文件长度，不能使用inputStream.available().
            objectMetadata.setContentLength(fsObject.getLength());

/*
            if(StringUtils.isNotBlank(fsObject.getName())) {
                //设置文件名元数据，以使用云存储直接下载时，也能获取到正确的文件名
                objectMetadata.setContentDisposition("attachment;filename*=utf-8''" + new String(fsObject.getName().getBytes("utf-8"), "ISO-8859-1")); //腾讯云需要将utf-8转码成ISO-8859-1
            }
*/

            COSClient cosClient = cosClientInfo.getCosClient();

            PutObjectRequest pubobjRequest = new PutObjectRequest(cosClientInfo.getBucketName(), "/" + fsObject.getObjectKey(), inputStream, objectMetadata);
//              ObjectMetadata tagetobjectMetadata= cosClient.getObjectMetadata(cosClientInfo.getBucketName(),"/"+fsObject.getObjectKey());
            //pubobjRequest.setStorageClass(StorageClass.Standard_IA); //设置低频存储
            cosClient.putObject(pubobjRequest);
            fsObject.setLength(objectMetadata.getContentLength());
            cosClient.shutdown();
            return fsObject;

        } catch (OSSException e) {
            LOGGER.warn("put object failed! [ " + fsObject.logFormat() + " ]", e);
            throw new FileSystemIOException(e);
        } catch (Exception e) {
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
            FSEndpoint fsEndpoint = fsObject.getFSEndpoint();
            COSClientInfo cosClientInfo = new COSClientInfo(fsEndpoint);

            S3BucketInfo bucket = fsObject.getBucket();

            cosClientInfo.setSecretKey(bucket.getUserInfo().getSk());
            COSClient cosClient = cosClientInfo.getCosClient();
            
            GetObjectRequest getObjectRequest = new GetObjectRequest(cosClientInfo.getBucketName(), fsObject.getObjectKey());
            if(start != null && end != null){
            	getObjectRequest.setRange(start, end);
            }
            COSObject cosObject = cosClient.getObject(getObjectRequest);

            if (cosObject != null) {
                InputStream inputStream = cosObject.getObjectContent();
                fsObject.setInputStream(inputStream);
                //使用Meta中的文件长度，不能使用inputStream.available().
                fsObject.setLength(cosObject.getObjectMetadata().getContentLength());
                return fsObject;
            } else {
                throw new FileNotFoundException("File not found");
            }
        } catch (OSSException e) {
            throw new FileSystemIOException(e);
        } catch (Exception e) {
            throw new FileSystemIOException(e);
        }
    }

    @Override
    public boolean doDelete(S3FSObject fsObject) throws FSException {
        boolean issuccess=false;
        try {
            FSEndpoint fsEndpoint= fsObject.getFSEndpoint();
            COSClientInfo cosClientInfo=new COSClientInfo(fsEndpoint);

            S3BucketInfo bucket = fsObject.getBucket();

            cosClientInfo.setSecretKey( bucket.getUserInfo().getSk());
            COSClient cosClient= cosClientInfo.getCosClient();

            cosClient.deleteObject(cosClientInfo.getBucketName(), "/"+fsObject.getObjectKey());

            issuccess=true;
            cosClient.shutdown();

        } catch (OSSException e) {
            LOGGER.warn("put object failed! [ " + fsObject.logFormat() + " ]", e);

            throw new FileSystemIOException(e);
        }
        return issuccess;
    }

    @Override
    public boolean checkObjectExist(S3FSObject fsObject) throws FSException {
        boolean issuccess=false;
        try {
            FSEndpoint fsEndpoint= fsObject.getFSEndpoint();
            COSClientInfo cosClientInfo=new COSClientInfo(fsEndpoint);

            S3BucketInfo bucket = fsObject.getBucket();

            cosClientInfo.setSecretKey( bucket.getUserInfo().getSk());
            COSClient cosClient= cosClientInfo.getCosClient();
            cosClient.shutdown();
            issuccess=cosClient.doesObjectExist(cosClientInfo.getBucketName(),"/"+fsObject.getObjectKey());

        } catch (OSSException e) {
            LOGGER.warn("put object failed! [ " + fsObject.logFormat() + " ]", e);

            throw new FileSystemIOException(e);
        }
        return issuccess;
    }

    @Override
    protected S3FSObject doCopy(S3FSObject srcFsObject, S3FSObject destFsObject) throws FSException {
        try {
            FSEndpoint fsEndpoint= srcFsObject.getFSEndpoint();
            COSClientInfo cosClientInfo=new COSClientInfo(fsEndpoint);

            S3BucketInfo bucket = srcFsObject.getBucket();

            cosClientInfo.setSecretKey( bucket.getUserInfo().getSk());
            COSClient cosClient= cosClientInfo.getCosClient();
            destFsObject.setLength(srcFsObject.getLength());
            cosClient.copyObject(cosClientInfo.getBucketName(),"/"+srcFsObject.getObjectKey(),cosClientInfo.getBucketName(),"/"+destFsObject.getObjectKey());
            return destFsObject;
        } catch (OSSException e) {
            throw new FileSystemIOException(e);
        }

    }

    @Override
    protected FSMultipartObject doMultipartStartUpload(S3FSObject fsObject) throws FSException {

        try {
            FSEndpoint fsEndpoint= fsObject.getFSEndpoint();
            COSClientInfo cosClientInfo=new COSClientInfo(fsEndpoint);

            S3BucketInfo bucket = fsObject.getBucket();

            cosClientInfo.setSecretKey( bucket.getUserInfo().getSk());
            COSClient cosClient= cosClientInfo.getCosClient();

            InitiateMultipartUploadRequest request = new InitiateMultipartUploadRequest(cosClientInfo.bucketName, "/"+fsObject.getObjectKey());
            request.setStorageClass(StorageClass.Standard_IA);
            InitiateMultipartUploadResult initResult = cosClient.initiateMultipartUpload(request);
            String uploadId =  initResult.getUploadId();
            cosClient.shutdown();
            return new FSMultipartObject(fsObject, uploadId);
        } catch (CosServiceException e) {
            throw new FileSystemIOException(e);
        } catch (CosClientException e) {
            throw new FileSystemIOException(e);
        }

    }

    /**
     * 执行每一个分片上传
     * @param fsObject
     * @param uploadId
     * @param partNumber
     * @param inputStream
     * @return
     * @throws FSException
     */
    @Override
    protected FSMultipartPart<S3FSObject> doMultipartUploadPart(S3FSObject fsObject, String uploadId, int partNumber,
                                                                InputStream inputStream) throws FSException {
        try {
            FSEndpoint fsEndpoint = fsObject.getFSEndpoint();
            COSClientInfo cosClientInfo = new COSClientInfo(fsEndpoint);

            S3BucketInfo bucket = fsObject.getBucket();

            cosClientInfo.setSecretKey(bucket.getUserInfo().getSk());
            COSClient cosClient = cosClientInfo.getCosClient();
            UploadPartRequest uploadPartRequest = new UploadPartRequest();
            uploadPartRequest.setBucketName(cosClientInfo.getBucketName());
            uploadPartRequest.setKey(fsObject.getObjectKey());
            uploadPartRequest.setUploadId(uploadId);
            // 设置分块的数据来源输入流
            uploadPartRequest.setInputStream(inputStream);
            // 设置分块的长度
            uploadPartRequest.setPartSize(fsObject.getLength()); // 设置数据长度
            uploadPartRequest.setPartNumber(partNumber);
            UploadPartResult uploadPartResult = cosClient.uploadPart(uploadPartRequest);
            PartETag partETag = uploadPartResult.getPartETag();

            FSMultipartPart<S3FSObject> part = new FSMultipartPart<S3FSObject>(fsObject, partNumber, partETag.getETag(),
                    fsObject.getLength());
            LOGGER.info("part : " + part + ";  " + fsObject.getLength());
            cosClient.shutdown();
            return part;
        }catch (CosServiceException e){
            throw new FSException(e);

        }catch (CosClientException e) {
            throw new FSException(e);
        }
    }

    @Override
    public FSMultipartObject doMultipartCompleteUpload(S3FSObject fsObject, String uploadId) throws FSException {

        try {
            FSEndpoint fsEndpoint = fsObject.getFSEndpoint();
            COSClientInfo cosClientInfo = new COSClientInfo(fsEndpoint);

            S3BucketInfo bucket = fsObject.getBucket();

            cosClientInfo.setSecretKey(bucket.getUserInfo().getSk());
            COSClient cosClient = cosClientInfo.getCosClient();
            List<PartETag> partETags = new LinkedList<>();

            MultipartFileObject multfile = multipartFileObjectManager.listMultipartParts(fsObject.getObjectKey());
            if (multfile == null || multfile.getParts().size() == 0) {
                String message = "FsObject [ " + fsObject.logFormat() + " ] Not Exists";
                LOGGER.warn(message);
                throw new FileNotFoundException(message);
            }
            TreeSet<MultipartPart> parts = multfile.getParts();
            PartETag partEtag = null;
            for (MultipartPart part : parts) {
                partEtag = new PartETag(part.getPartId(), part.geteTag());
                partETags.add(partEtag);
            }



            Collections.sort(partETags, new Comparator<PartETag>() {
                @Override
                public int compare(PartETag p1, PartETag p2) {
                    return p1.getPartNumber() - p2.getPartNumber();
                }
            });


            ObjectMetadata objectMeta = cosClient.getObjectMetadata(bucket.getName(),
                    fsObject.getObjectKey());
            CompleteMultipartUploadRequest completeMultipartUploadRequest =  new CompleteMultipartUploadRequest(cosClientInfo.getBucketName(), fsObject.getObjectKey(), uploadId, partETags);
            cosClient.completeMultipartUpload(completeMultipartUploadRequest);
            cosClient.shutdown();

            fsObject.setLength(objectMeta.getContentLength());
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
            FSEndpoint fsEndpoint= fsObject.getFSEndpoint();
            COSClientInfo cosClientInfo=new COSClientInfo(fsEndpoint);
            S3BucketInfo bucket = fsObject.getBucket();
            cosClientInfo.setSecretKey( bucket.getUserInfo().getSk());
            COSClient cosClient= cosClientInfo.getCosClient();
            AbortMultipartUploadRequest abortMultipartUploadRequest = new AbortMultipartUploadRequest(cosClientInfo.getBucketName(), "/"+fsObject.getObjectKey(), uploadId);
            cosClient.abortMultipartUpload(abortMultipartUploadRequest);
            cosClient.shutdown();
        } catch (CosServiceException e) {
            throw new FileSystemIOException(e);
        } catch (CosClientException e) {
            throw new FileSystemIOException(e);
        }
        return false;
    }

    @Override
    public SortedSet<FSMultipartPart<S3FSObject>> doMultipartListParts(S3FSObject fsObject, String uploadId)
            throws FSException {
        try {
            FSEndpoint fsEndpoint= fsObject.getFSEndpoint();
            COSClientInfo cosClientInfo=new COSClientInfo(fsEndpoint);
            S3BucketInfo bucket = fsObject.getBucket();
            cosClientInfo.setSecretKey( bucket.getUserInfo().getSk());
            COSClient cosClient= cosClientInfo.getCosClient();

            // 列举所有已上传的分片
            PartListing partListing = null;
            ListPartsRequest listPartsRequest = new ListPartsRequest(bucket.getName(), fsObject.getObjectKey(),
                    uploadId);

            SortedSet<FSMultipartPart<S3FSObject>> parts = new TreeSet<FSMultipartPart<S3FSObject>>();
            FSMultipartPart<S3FSObject> fsp = null;
            do {
                // FIXME 如果分片超過1000，是否有問題，尚不確定
                partListing = cosClient.listParts(listPartsRequest);
                for (PartSummary part : partListing.getParts()) {
                    fsp = new FSMultipartPart<S3FSObject>((S3FSObject) fsObject.clone(), part.getPartNumber(),
                            part.getETag(), part.getSize());
                    ((S3FSObject) fsp.getFSObject()).setLength(part.getSize());
                    parts.add(fsp);
                }
                listPartsRequest.setPartNumberMarker(partListing.getNextPartNumberMarker());
            } while (partListing.isTruncated());

            // 关闭client
            cosClient.shutdown();

            return parts;
        } catch (CloneNotSupportedException e) {
            LOGGER.warn("Clone Tencent OSS Object Failed.", e);
            throw new InnerException(e);
        } catch (OSSException e) {
            throw new FileSystemIOException(e);
        } catch (CosClientException e) {
            throw new FileSystemIOException(e);
        }

    }

    @Override
    public boolean deleteRealMultipartObject(S3FSObject paramT) throws FSException {
        throw new UnsupportedOperationException("Tencent cloud filesystem unsupported this operation");
    }

    @Override
    public boolean renameTo(S3FSObject paramT1, S3FSObject paramT2) throws FSException {

        throw new UnsupportedOperationException("Tencent cloud filesystem unsupported this operation");
    }
}
