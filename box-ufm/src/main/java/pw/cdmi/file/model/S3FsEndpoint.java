
/*
 * 版权声明(Copyright Notice)：
 * Copyright(C) 2017-2018 聚数科技成都有限公司。保留所有权利。
 * Copyright(C) 2017-2018 www.cdmi.pw Inc. All rights reserved.
 * 警告：本内容仅限于聚数科技成都有限公司内部传阅，禁止外泄以及用于其他的商业项目
 */
package pw.cdmi.file.model;

import pw.cdmi.core.exception.InvalidParamException;
import pw.cdmi.core.utils.EDToolsEnhance;
import pw.cdmi.file.domain.FsEndpoint;

import java.io.Serializable;

/************************************************************
 * @Description:
 * <pre> endpoint 定义</pre>
 * @author Rox
 * @version 3.0.1
 * @Project Alpha CDMI Service Platform, storbox-ufm Component. 2018/4/24
 ************************************************************/
public class S3FsEndpoint implements Serializable{
    private String domain;
    private int port;
    private String accessKey;
    private String secretKey;

    public S3FsEndpoint(FsEndpoint endpoint) {
        this(endpoint.getEndpoint());
    }

    //"obs.cn-north-1.myhwclouds.com:80:80:CFVM5YVJNN7QBQO3DCQ0:d2NjX2NyeXB0ATQxNDU1:d2NjX2NyeXB0ATQxN";
    public S3FsEndpoint(String endpoint) {
        String[] split = parse(endpoint);

        //格式，固定为5个[]
        if(split.length < 6) {
            throw new InvalidParamException("Invalid endpoint definition: " + endpoint);
        }

        domain = split[0];
        port = Integer.parseInt(split[1]);
        accessKey = split[3];
        secretKey = EDToolsEnhance.decode(split[4], split[5]);
    }

    protected static String[] parse(String path) {
        String[] split = path.split(":");


        split[0] = split[0];
        //去掉]结尾
        split[4] = split[4];

        return split;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
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
}
