
/*
 * 版权声明(Copyright Notice)：
 * Copyright(C) 2017-2018 聚数科技成都有限公司。保留所有权利。
 * Copyright(C) 2017-2018 www.cdmi.pw Inc. All rights reserved.
 * 警告：本内容仅限于聚数科技成都有限公司内部传阅，禁止外泄以及用于其他的商业项目
 */
package pw.cdmi.file.engine;

import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.http.HttpMethodName;
import com.qcloud.cos.model.GeneratePresignedUrlRequest;
import com.qcloud.cos.region.Region;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import pw.cdmi.file.model.S3FsEndpoint;
import pw.cdmi.file.model.S3FsObject;

import java.util.Date;

/************************************************************
 * @Description:
 * <pre>腾讯云COS</pre>
 * @author Rox
 * @version 3.0.1
 * @Project Alpha CDMI Service Platform, storbox-ufm Component. 2018/4/24
 ************************************************************/
@Component
public class TencentCosClient implements CloudFilesClient {
    private final static Logger LOGGER = LoggerFactory.getLogger(TencentCosClient.class);

    // URL有效期
    private long expireSeconds = 24 * 60 * 60;

    @Override
    public String getName() {
        return "tencent_cloud_cos";
    }

    @Override
    public String getDownloadUrl(S3FsEndpoint endpoint, S3FsObject fsObject) {
        try {
            // 1 初始化用户身份信息(appid, secretId, secretKey)
            COSCredentials cred = new BasicCOSCredentials(endpoint.getAccessKey(), endpoint.getSecretKey());

            // 2 设置bucket的区域, COS地域的简称请参照 https://cloud.tencent.com/document/product/436/6224
            String region = getRegion(endpoint.getDomain());
            ClientConfig clientConfig = new ClientConfig(new Region(region));

            // 3 生成cos客户端
            COSClient cosclient = new COSClient(cred, clientConfig);

            GeneratePresignedUrlRequest req = new GeneratePresignedUrlRequest(fsObject.getBucket(), fsObject.getFilename(), HttpMethodName.GET);

            // 设置签名过期时间(可选), 最大允许设置签名一个月有效, 若未进行设置, 则默认使用ClientConfig中的签名过期时间(5分钟)
            // 这里设置签名过期时间
            Date expirationDate = new Date(System.currentTimeMillis() + expireSeconds * 1000);
            req.setExpiration(expirationDate);

            String url = cosclient.generatePresignedUrl(req).toString();

            //返回的是HTTP，统一替换成HTTPS
            if(url.startsWith("http://")) {
                url = "https://" + url.substring(7);
            }

            return url;
        } catch (Exception e) {
            LOGGER.warn("Failed to get download url of file: domain={}, bucket={}, file={}", endpoint.getDomain(), fsObject.getBucket(), fsObject.getFilename());
            LOGGER.warn("Failed to get download url of file", e);
        }

        return null;
    }

    //filepro-cos-bucket-1255692311.cos.ap-chengdu.myqcloud.com
    private String getRegion(String domain) {
        String[] split = domain.split("\\.");

        if(split.length > 4){
            return split[2];
        }

        return "ap-chengdu";
    }

    public long getExpireSeconds() {
        return expireSeconds;
    }

    public void setExpireSeconds(long expireSeconds) {
        this.expireSeconds = expireSeconds;
    }
}
