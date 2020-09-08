/*
 * 版权声明(Copyright Notice)：
 *      Copyright(C) 2017-2017 聚数科技成都有限公司。保留所有权利。
 *      Copyright(C) 2017-2017 www.cdmi.pw Inc. All rights reserved.
 *
 *      警告：本内容仅限于聚数科技成都有限公司内部传阅，禁止外泄以及用于其他的商业目
 */
package pw.cdmi.file.domain;

import pw.cdmi.common.log.LogFormat;

import java.io.InputStream;
import java.io.Serializable;
import java.util.Arrays;

/**
 * object 文件对象
 *
 * @author s90006125
 */
public class FileObject implements LogFormat, Serializable {

    /**
     * 对象ID
     */
    private String objectId;

    /**
     * 对象存储路径
     */
    private String path;

    private String sha1;

    private long length;

    private FileObjectStatus status = FileObjectStatus.UPLOADING;

    public FileObject() {
    }

    public FileObject(String objectId) {
        this.objectId = objectId;
    }

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getSha1() {
        return sha1;
    }

    public void setSha1(String sha1) {
        this.sha1 = sha1;
    }

    public long getLength() {
        return length;
    }

    public void setLength(long length) {
        this.length = length;
    }

    public FileObjectStatus getStatus() {
        return status;
    }

    public void setStatus(FileObjectStatus status) {
        this.status = status;
    }

    @Override
    public String logFormat() {
        StringBuilder sb = new StringBuilder(FileObject.class.getCanonicalName()).append(START)
                .append("objectID=")
                .append(this.objectId)
                .append(SPLIT)
                .append(this.getPath())
                .append(SPLIT)
                .append("objectLength=")
                .append(this.getLength())
                .append(SPLIT)
                .append("sha1=")
                .append(this.sha1)
                .append(END);
        return sb.toString();
    }
}
