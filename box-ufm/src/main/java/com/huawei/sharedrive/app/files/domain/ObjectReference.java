package com.huawei.sharedrive.app.files.domain;

import java.util.Date;

import org.apache.commons.lang.StringUtils;

import com.huawei.sharedrive.app.utils.Constants;

public class ObjectReference
{
    // Sha1摘要字符串长度
    public static final int SHA1_LENGTH = 40;
    
    private String blockMD5;
    
    /** 对象ID */
    private String id;
    
    /** 删除修改时间 */
    private Date lastDeleteTime;
    
    /** 引用计数 */
    private int refCount;
    
    /** 资源组ID */
    private int resourceGroupId;
    
    /** 对象的Sha1值 */
    private String sha1;
    
    /** 对象大小 */
    private long size;
    
    /** 安全标识 */
    private Integer securityLabel;
    
    /** 安全扫描引擎版本 */
    private String securityVersion;
    
    /** 分表信息 */
    private int tableSuffix;
    
    public ObjectReference()
    {
        
    }
    
    public ObjectReference(INode fileNode)
    {
        this.setId(fileNode.getObjectId());
        this.setSha1(fileNode.getMd5());
        this.setSize(fileNode.getSize());
        this.setBlockMD5(fileNode.getBlockMD5());
        this.setRefCount(1);
        this.setResourceGroupId(fileNode.getResourceGroupId());
    }
    
    public String getBlockMD5()
    {
        return blockMD5;
    }
    
    public String getId()
    {
        return id;
    }
    
    public Date getLastDeleteTime()
    {
        if (lastDeleteTime == null)
        {
            return null;
        }
        return (Date) lastDeleteTime.clone();
    }
    
    public int getRefCount()
    {
        return refCount;
    }
    
    public int getResourceGroupId()
    {
        return resourceGroupId;
    }
    
    public Integer getSecurityLabel()
    {
        return securityLabel;
    }
    
    public String getSecurityVersion()
    {
        return securityVersion;
    }
    
    public String getSha1()
    {
        return sha1;
    }
    
    public long getSize()
    {
        return size;
    }
    
    public int getTableSuffix()
    {
        return tableSuffix;
    }
    
    public void setBlockMD5(String blockMD5)
    {
        this.blockMD5 = blockMD5;
    }
    
    public void setId(String id)
    {
        this.id = id;
    }
    
    public void setLastDeleteTime(Date lastDeleteTime)
    {
        if (lastDeleteTime == null)
        {
            this.lastDeleteTime = null;
        }
        else
        {
            this.lastDeleteTime = (Date) lastDeleteTime.clone();
        }
    }
    
    public void setRefCount(int refCount)
    {
        this.refCount = refCount;
    }
    
    public void setResourceGroupId(int resourceGroupId)
    {
        this.resourceGroupId = resourceGroupId;
    }
    
    public void setSecurityLabel(Integer securityLabel)
    {
        this.securityLabel = securityLabel;
    }
    
    public void setSecurityVersion(String securityVersion)
    {
        this.securityVersion = securityVersion;
    }
    
    public void setSha1(String sha1)
    {
        this.sha1 = sha1;
    }
    
    public void setSize(long size)
    {
        this.size = size;
    }
    
    public void setTableSuffix(int tableSuffix)
    {
        this.tableSuffix = tableSuffix;
    }
    
    public boolean hasSameContents(ObjectReference srcObjRef)
    {
        if (this.getSize() != srcObjRef.getSize() || this.getLastDeleteTime() != null
            || this.getId().equalsIgnoreCase(srcObjRef.getId()))
        {
            return false;
        }
        // 不大于256字节的文件只比较整文件MD5
        if (this.getSize() <= Constants.SAMPLING_LENGTH_FOR_SMALLER_FILE)
        {
            return true;
        }
        else if (StringUtils.isNotBlank(srcObjRef.getBlockMD5()))
        {
            // 大于256字节的文件比较整文件MD5和抽样MD5
            if (srcObjRef.getBlockMD5().equals(this.getBlockMD5()))
            {
                return true;
            }
        }
        else if (srcObjRef.getSha1().length() == SHA1_LENGTH)
        {
            return true;
        }
        return false;
    }
    
}