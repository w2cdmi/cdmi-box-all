/*
 * 版权声明(Copyright Notice)：
 *      Copyright(C) 2017-2017 聚数科技成都有限公司。保留所有权利。
 *      Copyright(C) 2017-2017 www.cdmi.pw Inc. All rights reserved. 
 * 
 *      警告：本内容仅限于聚数科技成都有限公司内部传阅，禁止外泄以及用于其他的商业目
 */ 
package pw.cdmi.file.engine.object.domain;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 文件分片
 * 
 * @author s90006125
 * 
 */
public class MultipartPart implements Comparable<MultipartPart>, Serializable
{
    private static final long serialVersionUID = 7316326347440912218L;

    private int partId;
    
    @JsonProperty("size")
    private long partSize;
    
    private String eTag;
    private Long partCRC;
    
    public MultipartPart(){
    	
    }
    public MultipartPart(int partId)
    {
        this.partId=partId;
    }
    
    public MultipartPart(int partId, long partSize, String eTag, Long partCRC)
    {
        this(partId);
        this.partSize = partSize;
        this.eTag = eTag;
        this.partId=partId;
        this.setPartCRC(partCRC);
    }
    
    @Override
    public int compareTo(MultipartPart part)
    {
        return this.getPartId() - part.getPartId();
    }
    
    @Override
    public boolean equals(Object obj)
    {
        if(obj instanceof MultipartPart)
        {
            MultipartPart part = (MultipartPart)obj;
            return this.compareTo(part) == 0;
        }
        return false;
        
    }
    
    public long getPartSize()
    {
        return partSize;
    }
    
    @Override
    public int hashCode()
    {
        return this.getPartId();
    }

	public String geteTag() {
		return eTag;
	}

	public void seteTag(String eTag) {
		this.eTag = eTag;
	}

	public Long getPartCRC() {
		return partCRC;
	}

	public void setPartCRC(Long partCRC) {
		this.partCRC = partCRC;
	}
	public int getPartId() {
		return partId;
	}
	public void setPartId(int partId) {
		this.partId = partId;
	}
	
}
