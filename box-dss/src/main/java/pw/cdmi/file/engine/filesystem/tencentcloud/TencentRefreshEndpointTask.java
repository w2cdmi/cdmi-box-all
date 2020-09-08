package pw.cdmi.file.engine.filesystem.tencentcloud;

//import com.qcloud.cos.COSClient;
//import com.qcloud.cos.ClientConfig;
//import com.qcloud.cos.meta.InsertOnly;
//import com.qcloud.cos.request.DelFileRequest;
//import com.qcloud.cos.request.UploadFileRequest;
//import com.qcloud.cos.sign.Credentials;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.model.ObjectMetadata;
import com.qcloud.cos.model.PutObjectRequest;
import com.qcloud.cos.model.PutObjectResult;
import org.apache.commons.io.IOUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pw.cdmi.file.engine.filesystem.aws.S3FSObject;
import pw.cdmi.file.engine.filesystem.exception.FSException;
import pw.cdmi.file.engine.filesystem.exception.WrongFileSystemArgsException;
import pw.cdmi.file.engine.filesystem.model.FSEndpoint;
import pw.cdmi.file.engine.filesystem.model.cloud.S3BucketInfo;
import pw.cdmi.file.engine.filesystem.model.cloud.S3Userinfo;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;


public class TencentRefreshEndpointTask implements Runnable {
    private static Logger logger = LoggerFactory.getLogger(TencentRefreshEndpointTask.class);

    private static final byte[] TEMP_DATA = "0".getBytes(Charset.forName("UTF-8"));

    private static final String VERIFICATION_FILE_PREFIX = "check_";

    private static final String VERIFICATION_FILE_SUFFIX = ".tmp";

    private FSEndpoint endpoint;

    private CountDownLatch latch;

    private AtomicInteger succCount;

    public TencentRefreshEndpointTask(AtomicInteger succCount, FSEndpoint endpoint, CountDownLatch latch) {
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

        InputStream is = null;


        is = new ByteArrayInputStream(TEMP_DATA);



        S3FSObject tempFSObject = generateTempS3FSObject(endpoint);
        tempFSObject.setLength(TEMP_DATA.length);
        FSEndpoint fsEndpoint= endpoint;
        COSClientInfo cosClientInfo=new COSClientInfo(fsEndpoint);

        S3BucketInfo bucket = tempFSObject.getBucket();

        cosClientInfo.setSecretKey( bucket.getUserInfo().getSk());
        COSClient cosClient= cosClientInfo.getCosClient();



        try {
            ObjectMetadata objectMetadata=new ObjectMetadata();
            objectMetadata.setContentLength((long) is.available());
            PutObjectRequest pubobjRequest=new PutObjectRequest(cosClientInfo.getBucketName(),tempFSObject.getObjectKey(),is,objectMetadata);
//
            cosClient.putObject(pubobjRequest);
            tempFSObject.setLength(objectMetadata.getContentLength());

        } catch (Exception e) {
            logger.warn("Create Temp File [ " + tempFSObject.logFormat() + " ] Failed.", e);
            return false;
        } finally {
            IOUtils.closeQuietly(is);
        }
//
        try {
            cosClient.deleteObject(cosClientInfo.getBucketName(), tempFSObject.getObjectKey());
        } catch (Exception e) {
            logger.warn("Delete Temp File [ " + tempFSObject.logFormat() + " ] Failed.", e);
            return false;
        }
        cosClient.shutdown();
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