
/*
 * 版权声明(Copyright Notice)：
 * Copyright(C) 2017-2018 聚数科技成都有限公司。保留所有权利。
 * Copyright(C) 2017-2018 www.cdmi.pw Inc. All rights reserved.
 * 警告：本内容仅限于聚数科技成都有限公司内部传阅，禁止外泄以及用于其他的商业项目
 */
package pw.cdmi.file.model;

import pw.cdmi.core.exception.InvalidParamException;
import pw.cdmi.file.domain.FileObject;

/************************************************************
 * @Description:
 * <pre> S3 标准对象</pre>
 * @author Rox
 * @version 3.0.1
 * @Project Alpha CDMI Service Platform, storbox-ufm Component. 2018/4/24
 ************************************************************/
public class S3FsObject {
    private String cloud;
    private String version;
    private String endpoint;
    private String bucket;
    private String filename;

    public S3FsObject(FileObject object) {
        this(object.getPath());
    }

    public S3FsObject(String path) {
        String[] split = parse(path);
        cloud = split[0];
        version = split[1];
        endpoint = split[2];
        bucket = split[3];
        filename = split[4];
    }

    //"[uds][V2_HWY][07736a7e-41ec-a2bf-6103-c2ca6e5921d3][csebucket-c73c7e2e406cee0d86824257bbcdabd1][1512386204408_05033978b104900e937d6521c6bca0d1]";
    protected static String[] parse(String path) {
        String[] split = path.split("]\\[");

        //格式，固定为5个[]
        if(split.length != 5) {
            throw new InvalidParamException("Invalid Object Storage Path: " + path);
        }

        //去掉[开头
        split[0] = split[0].substring(1);
        //去掉]结尾
        split[4] = split[4].substring(0, split[4].length() - 1);

        return split;
    }

    public String getCloud() {
        return cloud;
    }

    public void setCloud(String cloud) {
        this.cloud = cloud;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public String getBucket() {
        return bucket;
    }

    public void setBucket(String bucket) {
        this.bucket = bucket;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }
}
