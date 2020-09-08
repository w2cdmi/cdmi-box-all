package pw.cdmi.file.engine.filesystem.model;

import org.apache.commons.lang.StringUtils;

import pw.cdmi.file.engine.filesystem.model.cloud.CloudFSEndpoint;

public enum FSDefinition {
                          NORMAL_FileSystem("NORMAL_FileSystem","normal"),
                          CSS_FileSystem("CSS_FileSystem","css"),
                          N8500_FileSystem("N8500_FileSystem","n8500"),
                          UDS_FileSystem("UDS_FileSystem","uds"),
                          NAS_FileSystem("NAS_FileSystem","nas"),
                          AWS_S3_FileSystem("AWS_S3_FileSystem","aws_s3"), // 亚马逊S3简单对象存储
                          QCLOUD_OS_FileSystem("QCLOUD_OS_FileSystem","qcloud_os"),// 青云OS对象存储
                          ALIYUN_OSS_FileSystem("ALIYUN_OSS_FileSystem","aliyun_oss"),// 阿里云OSS对象存储
                          TENCENT_COS_FileSystem("TENCENT_COS_FileSystem","tencent_cloud_cos");// 腾讯云COS对象存储

    private String name;

    private String type;

    private static final String SFM_PATH_START = "[";

    private static final String SFM_PATH_START_SPLIT = "][";

    private static final String V1R2_PATH_SPLIT = ":";

    private FSDefinition(String name, String type) {
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return this.name;
    }

    public String getType() {
        return this.type;
    }

    public static FSDefinition findFSDefinition(String type) {
        for (FSDefinition fs : values()) {
            if (fs.getType().equalsIgnoreCase(type)) {
                return fs;
            }
        }

        return null;
    }

    public static FSDefinition parseByPath(String path) {
        if (StringUtils.isBlank(path)) {
            return null;
        }

        if (path.startsWith(SFM_PATH_START)) {
            return findFSDefinition(path.substring(1, path.indexOf(SFM_PATH_START_SPLIT)));
        }

        return findFSDefinition(path.split(V1R2_PATH_SPLIT)[0]);
    }

    public FSEndpoint createFSEndpointInstants() {
        FSEndpoint endpoint = null;
        if (NAS_FileSystem.equals(this)) {
            endpoint = new NormalFSEndpoint();
        } else if (QCLOUD_OS_FileSystem.equals(this)) {
            endpoint = new CloudFSEndpoint();
        } else if (ALIYUN_OSS_FileSystem.equals(this)) {
            endpoint = new CloudFSEndpoint();
        } else if (AWS_S3_FileSystem.equals(this)) {
            endpoint = new CloudFSEndpoint();
        } else if (TENCENT_COS_FileSystem.equals(this)) {
            endpoint = new CloudFSEndpoint();
        } else {
            endpoint = new NormalFSEndpoint();
        }

        endpoint.setFsType(getType());

        return endpoint;
    }
}