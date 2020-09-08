package pw.cdmi.file.engine.filesystem.tencentcloud;


import com.qcloud.cos.COSClient;
import org.apache.commons.lang.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pw.cdmi.common.alarm.Alarm;
import pw.cdmi.common.alarm.AlarmHelper;
import pw.cdmi.core.log.Level;
import pw.cdmi.core.utils.EDToolsEnhance;
import pw.cdmi.core.utils.MethodLogAble;

import pw.cdmi.file.engine.core.alarm.StorageFailedAlarm;
import pw.cdmi.file.engine.core.config.SystemConfigContainer;
import pw.cdmi.file.engine.core.job.ThreadPool;
import pw.cdmi.file.engine.filesystem.FileSystemManager;
import pw.cdmi.file.engine.filesystem.exception.*;
import pw.cdmi.file.engine.filesystem.model.FSAccessPath;
import pw.cdmi.file.engine.filesystem.model.FSDefinition;
import pw.cdmi.file.engine.filesystem.model.FSEndpoint;



import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 腾讯云文件管理
 * Created by nick.qu on 2018/1/5.
 */
@Service("tencentCloudSystemManager")
public class TencentCloudFileSystemManager implements FileSystemManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(TencentCloudFileSystemManager.class);

    private static final int QCLOUD_COS_ENDPOINT_SPLIT_COUNT_SIX = 6;

    // 对于腾讯云存储，所有的文件放置在固定的一个bucket中,默认bucket名称为filepro-cos-bucket-1;存在多个时候自动加1；
    private static final String DEFAULT_BUCKET = "filepro-cos-bucket";

    private static final String ROOT_FOLDER_PREFIX = "filepro-";

    @Autowired
    private AlarmHelper alarmHelper;

    @Autowired
    private StorageFailedAlarm storageFailedAlarm;



    public FSDefinition getDefinition() {
        return FSDefinition.TENCENT_COS_FileSystem;
    }

    @MethodLogAble(Level.INFO)
    public FSEndpoint createFSEndpoint(FSEndpoint endpoint) throws FSException {
        if (!isApproved(endpoint, TencentCloudFileSystemManager.QCLOUD_COS_ENDPOINT_SPLIT_COUNT_SIX)) {
            String message = "endpoint Info [" + endpoint.logFormat() + "] is not approved";
            if (LOGGER.isWarnEnabled()) {
                LOGGER.warn(message);
                throw new WrongFileSystemArgsException(message);
            }
        }

        COSClientInfo cosClientInfo=new COSClientInfo(endpoint);


        LOGGER.info("create qcloud os filesystem with ip: {}, port: {}, ak: {}",
                new Object[] { cosClientInfo.dns, cosClientInfo.getHttpPort(), cosClientInfo.getAccessKey() });

       // <bucketname_APPID>.coscd.myqcloud.com //根据此规则建立


        COSClient cosClient=null;
        try {
            cosClient=cosClientInfo.getCosClient();
            endpoint.addAccessPaths(new FSAccessPath(cosClientInfo.getBucketName()));
        }catch (Exception e){
            throw new FSException(e);
        }finally {
            cosClient.shutdown();
        }

        endpoint.setMaxUtilization(Float.valueOf(0.0F));
        endpoint.setRetrieval(Float.valueOf(0.0F));

        Map<String, String> map = EDToolsEnhance.encode(cosClientInfo.secretKey);
        LOGGER.info("change crypt in dataserver.FSEndpoint");
        String encryptedSecretKey = (String) map.get("encryptedContent");
        String encryptKey = map.get("encryptedKey").toString();
        StringBuilder newEndpoint = new StringBuilder(cosClientInfo.dns);
        newEndpoint.append(":").append(cosClientInfo.httpPort);
        newEndpoint.append(":").append(cosClientInfo.httpsPort);
        newEndpoint.append(":").append(cosClientInfo.accessKey);
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

        TencentRefreshEndpointTask task = null;
        for (int i = 0; i < retry; i++) {
            task = new TencentRefreshEndpointTask(succCount, endpoint, latch);
            ThreadPool.execute(task);
        }
        try {
            int timeout = SystemConfigContainer.getInteger("fs.storage.check.failed.timeout", Integer.valueOf(120));
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
        if (!isApproved(endpoint, TencentCloudFileSystemManager.QCLOUD_COS_ENDPOINT_SPLIT_COUNT_SIX)) {
            String message = "Endpoint Info [" + endpoint.logFormat() + "] is not approved";
            if (LOGGER.isWarnEnabled()) {
                LOGGER.warn(message);
                throw new WrongFileSystemArgsException(message);
            }

        }
        COSClientInfo cosClientInfo=new COSClientInfo(endpoint);
        endpoint.getAccessPaths().clear();

        endpoint.setMaxUtilization(Float.valueOf(0.0F));
        endpoint.setRetrieval(Float.valueOf(0.0F));

        Map<String, String> map = EDToolsEnhance.encode(cosClientInfo.secretKey);
        LOGGER.info("change crypt in dataserver.FSEndpoint");
        String encryptedSecretKey = (String) map.get("encryptedContent");
        String encryptKey = (String) map.get("encryptedKey");
        StringBuilder newEndpoint = new StringBuilder(cosClientInfo.dns)
                .append(":").append(cosClientInfo.httpPort)
                .append(":")
                .append(cosClientInfo.httpsPort)
                .append(":")
                .append(cosClientInfo.accessKey)
                .append(":").append(encryptedSecretKey).append(":").append(encryptKey);
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
            if(true) {
                endpoint.setAvailable(true);
                endpoint.setWriteAble(true);
            }
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
