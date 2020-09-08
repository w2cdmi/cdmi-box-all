/*
 * 版权声明(Copyright Notice)：
 *      Copyright(C) 2017-2017 聚数科技成都有限公司。保留所有权利。
 *      Copyright(C) 2017-2017 www.cdmi.pw Inc. All rights reserved. 
 * 
 *      警告：本内容仅限于聚数科技成都有限公司内部传阅，禁止外泄以及用于其他的商业目
 */ 
package pw.cdmi.file.engine.object.domain;

import java.util.Date;
import java.util.TreeSet;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import pw.cdmi.file.engine.core.ibatis.Namingspace;

/**
 * 分片文件对象
 * 
 * @author s90006125
 * 
 */
@SuppressWarnings("PMD.LooseCoupling")
@Namingspace("multipartFileObject")
@JsonIgnoreProperties({"serialVersionUID", "uploadID", "partSize", "objectID", "storagePath", "objectLength",
    "inputStream", "sha1", "callBackKey", "status", "", ""})
public class MultipartFileObject extends FileObject
{
    private static final long serialVersionUID = -3100999471663615122L;
    
    private String uploadID;
    
    /** 每一个分片的大小，由于不同的文件系统的分片大小可能不一样，所以需要返回该值，然客户端根据需要动态分片 */
    private long partSize;

    /**
     * Part的ETag值
     */
    private String etag;
    
    /**
     * 分片的CRC值
     */
    private String partCRC;
    /**
     * 开始执行合并的时间
     */
    private Date mergeAt;
    
    /**
     * 执行合并的次数
     */
    private int mergeTimes;
    
    /**
     * 执行清理的次数
     */
    private int clearTimes;
    
    @JsonProperty("parts")
    private TreeSet<MultipartPart> parts = new TreeSet<MultipartPart>();
    
    public MultipartFileObject()
    {
    }
    
    public MultipartFileObject(String objectID)
    {
        super(objectID);
    }
    
    public MultipartFileObject(String objectID, String callBackKey)
    {
        this(objectID);
        this.setCallBackKey(callBackKey);
    }
    
    public String getUploadID()
    {
        return uploadID;
    }
    
    public void setUploadID(String uploadID)
    {
        this.uploadID = uploadID;
    }
    
    public TreeSet<MultipartPart> getParts()
    {
        if (null == this.parts)
        {
            parts = new TreeSet<MultipartPart>();
        }
        return parts;
    }
    
    public void setParts(TreeSet<MultipartPart> parts)
    {
        this.parts = parts;
    }
    
    public long getPartSize()
    {
        return partSize;
    }
    
    public void setPartSize(long partSize)
    {
        this.partSize = partSize;
    }

    public Date getMergeAt()
    {
        if(null != this.mergeAt)
        {
            return (Date)this.mergeAt.clone();
        }
        return null;
    }

    public void setMergeAt(Date mergeAt)
    {
        if(null != mergeAt)
        {
            this.mergeAt = (Date)mergeAt.clone();
        }
        else
        {
            this.mergeAt = null;
        }
    }

    public int getMergeTimes()
    {
        return mergeTimes;
    }

    public void setMergeTimes(int mergeTimes)
    {
        this.mergeTimes = mergeTimes;
    }

    public int getClearTimes()
    {
        return clearTimes;
    }

    public void setClearTimes(int clearTimes)
    {
        this.clearTimes = clearTimes;
    }

	public String getEtag() {
		return etag;
	}

	public void setEtag(String etag) {
		this.etag = etag;
	}

	public String getPartCRC() {
		return partCRC;
	}

	public void setPartCRC(String partCRC) {
		this.partCRC = partCRC;
	}
}
