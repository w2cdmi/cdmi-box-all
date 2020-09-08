
/*
 * 版权声明(Copyright Notice)：
 * Copyright(C) 2017-2018 聚数科技成都有限公司。保留所有权利。
 * Copyright(C) 2017-2018 www.cdmi.pw Inc. All rights reserved.
 * 警告：本内容仅限于聚数科技成都有限公司内部传阅，禁止外泄以及用于其他的商业项目
 */
package pw.cdmi.file.engine;

import com.obs.services.ObsClient;
import com.obs.services.model.HttpMethodEnum;
import com.obs.services.model.TemporarySignatureRequest;
import com.obs.services.model.TemporarySignatureResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import pw.cdmi.file.model.S3FsEndpoint;
import pw.cdmi.file.model.S3FsObject;

import java.io.IOException;

/************************************************************
 * @Description:
 * <pre>华为obs</pre>
 * @author Rox
 * @version 3.0.1
 * @Project Alpha CDMI Service Platform, storbox-ufm Component. 2018/4/24
 ************************************************************/
@Component
public class HuaweiObsClient implements CloudFilesClient {
    private final static Logger LOGGER = LoggerFactory.getLogger(HuaweiObsClient.class);

    // URL有效期
    private long expireSeconds = 24 * 60 * 60;

    @Override
    public String getName() {
        return "uds";
    }

    @Override
    public String getDownloadUrl(S3FsEndpoint endpoint, S3FsObject fsObject) {
        try {
            // 创建ObsClient实例
            ObsClient obsClient = new ObsClient(endpoint.getAccessKey(), endpoint.getSecretKey(), endpoint.getDomain());

            // 下载对象
            TemporarySignatureRequest request = new TemporarySignatureRequest(HttpMethodEnum.GET, expireSeconds);
            request.setBucketName(fsObject.getBucket());
            request.setObjectKey(fsObject.getFilename());
            TemporarySignatureResponse response = obsClient.createTemporarySignature(request);

            // 关闭obsClient
            obsClient.close();

            return response.getSignedUrl();
        } catch (Exception e) {
            LOGGER.warn("Failed to get download url of file: domain={}, bucket={}, file={}", endpoint.getDomain(), fsObject.getBucket(), fsObject.getFilename());
            LOGGER.warn("Failed to get download url of file", e);
        }

        return null;
    }

    public long getExpireSeconds() {
        return expireSeconds;
    }

    public void setExpireSeconds(long expireSeconds) {
        this.expireSeconds = expireSeconds;
    }
}
