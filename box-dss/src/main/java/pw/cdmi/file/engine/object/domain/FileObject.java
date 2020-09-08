/*
 * 版权声明(Copyright Notice)：
 *      Copyright(C) 2017-2017 聚数科技成都有限公司。保留所有权利。
 *      Copyright(C) 2017-2017 www.cdmi.pw Inc. All rights reserved. 
 * 
 *      警告：本内容仅限于聚数科技成都有限公司内部传阅，禁止外泄以及用于其他的商业目
 */ 
package pw.cdmi.file.engine.object.domain;

import java.io.InputStream;
import java.util.Arrays;

import pw.cdmi.common.log.LogFormat;
import pw.cdmi.file.engine.core.common.AutoToStringEntity;
import pw.cdmi.file.engine.core.ibatis.Namingspace;

/**
 * object 文件对象
 * 
 * @author s90006125
 * 
 */
@Namingspace("fileObject")
public class FileObject extends AutoToStringEntity implements LogFormat
{
    private static final long serialVersionUID = 8176898428297877828L;
    
    /** 对象ID */
    private String objectID;
    
    /** 对象存储路径 */
    private String storagePath;

    private String objectName;

    private long objectLength;
    
    private transient InputStream inputStream;
    
    private byte[] data;
    
    private String sha1;
    
    private String callBackKey;
    
    private FileObjectStatus status = FileObjectStatus.UPLOADING;
    
    public FileObject()
    {
    }
    
    public FileObject(String objectID)
    {
        this.objectID = objectID;
    }
    
    public String getObjectID()
    {
        return objectID;
    }
    
    public void setObjectID(String objectID)
    {
        this.objectID = objectID;
    }
    
    public String getStoragePath()
    {
        return storagePath;
    }
    
    public void setStoragePath(String storagePath)
    {
        this.storagePath = storagePath;
    }
    
    public InputStream getInputStream()
    {
        return inputStream;
    }
    
    public void setInputStream(InputStream inputStream)
    {
        this.inputStream = inputStream;
    }
    
    public byte[] getData()
    {
        if(null != this.data)
        {
            return Arrays.copyOf(this.data, this.data.length);
        }
        return new byte[0];
    }
    
    public void setData(byte[] data)
    {
        if(null != data)
        {
            this.data = Arrays.copyOf(data, data.length);
        }
        else
        {
            this.data = null;
        }
    }

    public String getObjectName() {
        return objectName;
    }

    public void setObjectName(String objectName) {
        this.objectName = objectName;
    }

    public long getObjectLength()
    {
        return objectLength;
    }
    
    public void setObjectLength(long objectLength)
    {
        this.objectLength = objectLength;
    }
    
    public String getSha1()
    {
        return sha1;
    }
    
    public void setSha1(String sha1)
    {
        this.sha1 = sha1;
    }
    
    public String getCallBackKey()
    {
        return callBackKey;
    }
    
    public void setCallBackKey(String callBackKey)
    {
        this.callBackKey = callBackKey;
    }
    
    public FileObjectStatus getStatus()
    {
        return status;
    }
    
    public void setStatus(FileObjectStatus status)
    {
        this.status = status;
    }
    
    @Override
    public String logFormat()
    {
        StringBuilder sb = new StringBuilder(FileObject.class.getCanonicalName()).append(START)
            .append("objectID=")
            .append(this.objectID)
            .append(SPLIT)
            .append(this.getStoragePath())
            .append(SPLIT)
            .append("objectLength=")
            .append(this.objectLength)
            .append(SPLIT)
            .append("sha1=")
            .append(this.sha1)
            .append(SPLIT)
            .append("callBackKey=")
            .append(this.callBackKey)
            .append(END);
        return sb.toString();
    }
}
