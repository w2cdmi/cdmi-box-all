/*
 * 版权声明(Copyright Notice)：
 *      Copyright(C) 2017-2017 聚数科技成都有限公司。保留所有权利。
 *      Copyright(C) 2017-2017 www.cdmi.pw Inc. All rights reserved. 
 * 
 *      警告：本内容仅限于聚数科技成都有限公司内部传阅，禁止外泄以及用于其他的商业目
 */ 
package pw.cdmi.file.engine.object.rest;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 文件分片的兼容性对象
 * 
 * @author s90006125
 * 
 */
public class CompatibleMultipartPart implements Comparable<CompatibleMultipartPart>
{
    private int partId;
    
    /**
     * 废弃的字段，为了兼容老版本
     */
    @Deprecated
    @JsonProperty("partID")
    private int oldPartId;
    
    @JsonProperty("size")
    private long partSize;
    
    public CompatibleMultipartPart(int partId, long partSize)
    {
        this.partId = partId;
        this.oldPartId = partId;
        this.partSize = partSize;
    }
    
    @Override
    public int compareTo(CompatibleMultipartPart part)
    {
        return this.getPartId() - part.getPartId();
    }
    
    @Override
    public boolean equals(Object obj)
    {
        if(obj instanceof CompatibleMultipartPart)
        {
            CompatibleMultipartPart part = (CompatibleMultipartPart)obj;
            return this.compareTo(part) == 0;
        }
        else
        {
            return false;
        }
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
    
    public void setPartId(int partId)
    {
        this.partId = partId;
        this.oldPartId = partId;
    }
    
    @Deprecated
    public void setOldPartId(int partID)
    {
        this.partId = partID;
        this.oldPartId = partID;
    }
    
    public int getPartId()
    {
        return this.partId;
    }
    
    @Deprecated
    public int getOldPartId()
    {
        return this.oldPartId;
    }
}
