package pw.cdmi.file.engine.filesystem.tencentcloud;

import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.region.Region;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pw.cdmi.file.engine.filesystem.exception.WrongFileSystemArgsException;
import pw.cdmi.file.engine.filesystem.model.FSEndpoint;

/**
 * Created by quxiangqian on 2018/1/11.
 */
public class COSClientInfo {
    private static final Logger LOGGER = LoggerFactory.getLogger(COSClientInfo.class);


    String bucketName;
    String dns = "";
    String httpPort  = "";
    String httpsPort = "";
    String accessKey = "";
    String secretKey = "";
    String region="";
    String provider = null;

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }



    public COSClientInfo(FSEndpoint endpoint) {
        this.parseCosClient(endpoint);
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }



    public String getBucketName() {
        return bucketName;
    }

    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }


    public String getDns() {
        return dns;
    }

    public void setDns(String dns) {
        this.dns = dns;
    }

    public String getHttpPort() {
        return httpPort;
    }

    public void setHttpPort(String httpPort) {
        this.httpPort = httpPort;
    }

    public String getHttpsPort() {
        return httpsPort;
    }

    public void setHttpsPort(String httpsPort) {
        this.httpsPort = httpsPort;
    }

    public String getAccessKey() {
        return accessKey;
    }

    public void setAccessKey(String accessKey) {
        this.accessKey = accessKey;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    /**
     *  解析客户端参数
     * @param fs
     */
    public void parseCosClient(FSEndpoint fs){
        String[] infos = fs.getEndpoint().split(":");

        this.dns = StringUtils.trimToEmpty(infos[0]);
        this.httpPort = StringUtils.trimToEmpty(infos[1]);
        this.httpsPort = StringUtils.trimToEmpty(infos[2]);
        this.accessKey = StringUtils.trimToEmpty(infos[3]);
        this.secretKey = StringUtils.trimToEmpty(infos[4]);
        String[] domain_split = dns.split("\\.");
        if (dns!=null&&domain_split.length < 3) {
            String message = "dns Info [" + fs.logFormat() + "] is not approved";
            if (LOGGER.isWarnEnabled()) {
                LOGGER.warn(message);
                throw new WrongFileSystemArgsException(message);
            }
        }

        if (infos.length > 6) {
            this.provider = StringUtils.trimToEmpty(infos[6]);
        }
        this.bucketName=domain_split[0];
        this.region=domain_split[2];

    }

    /**
     * 获取Cos客户端
     * @return
     */
    public COSClient getCosClient()
    {
        COSCredentials cred = new BasicCOSCredentials(this.accessKey, this.secretKey);
        ClientConfig clientConfig = new ClientConfig(new Region(this.region));
        return new COSClient(cred, clientConfig);
    }


}
