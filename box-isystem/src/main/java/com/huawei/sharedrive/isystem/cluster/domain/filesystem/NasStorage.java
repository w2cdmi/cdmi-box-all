/*
 * Copyright Notice:
 *      Copyright  1998-2009, Huawei Technologies Co., Ltd.  ALL Rights Reserved.
 *
 *      Warning: This computer software sourcecode is protected by copyright law
 *      and international treaties. Unauthorized reproduction or distribution
 *      of this sourcecode, or any portion of it, may result in severe civil and
 *      criminal penalties, and will be prosecuted to the maximum extent
 *      possible under the law.
 */
package com.huawei.sharedrive.isystem.cluster.domain.filesystem;

import java.math.BigDecimal;
import java.text.DecimalFormat;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.huawei.sharedrive.isystem.cluster.FileSystemConstant;
import com.huawei.sharedrive.thrift.filesystem.StorageInfo;

/**
 * 
 * @author s90006125
 *
 */
public class NasStorage extends BaseStorageInfo
{
    private static final long serialVersionUID = 898646869000804205L;

    @NotNull
    @Size(min = 1, max = 128)
    private String path;
    
    private boolean noSpace = false;
    
    @NotNull
    @Min(value = 1)  
    @Max(value = 99)
    private int maxUtilization = 0;
    
    @NotNull
    @Min(value = 1)  
    @Max(value = 99)
    private int retrieval = 0;
    
    // 单位：字节
    private long spaceSize = 0L;
    
    // 单位：字节
    private long usedSize = 0L;
    
    private String usedRatio;
    
    public NasStorage()
    {
    }
    
    public NasStorage(StorageInfo storageInfo)
    {
        super(storageInfo);
        this.setPath(storageInfo.getEndpoint());
        this.setNoSpace(storageInfo.isNoSpace());
        this.setMaxUtilization(((Double)storageInfo.getMaxUtilization()).intValue());
        this.setRetrieval(((Double)storageInfo.getRetrieval()).intValue());
        this.setSpaceSize(storageInfo.getSpaceSize());
        this.setUsedSize(storageInfo.getUsedSize());
        DecimalFormat df=new   java.text.DecimalFormat("#.##");   
        this.setUsedRatio(df.format(computeUtilization(this.getUsedSize(), this.getSpaceSize())));
    }
    
    public String getPath()
    {
        return path;
    }

    public void setPath(String path)
    {
        this.path = path;
    }

    public boolean isNoSpace()
    {
        return noSpace;
    }

    public void setNoSpace(boolean noSpace)
    {
        this.noSpace = noSpace;
    }

    public int getMaxUtilization()
    {
        return maxUtilization;
    }

    public void setMaxUtilization(int maxUtilization)
    {
        this.maxUtilization = maxUtilization;
    }

    public int getRetrieval()
    {
        return retrieval;
    }

    public void setRetrieval(int retrieval)
    {
        this.retrieval = retrieval;
    }

    public long getSpaceSize()
    {
        return spaceSize;
    }

    public void setSpaceSize(long spaceSize)
    {
        this.spaceSize = spaceSize;
    }

    public long getUsedSize()
    {
        return usedSize;
    }

    public void setUsedSize(long usedSize)
    {
        this.usedSize = usedSize;
    }

    @Override
    public String getFsType()
    {
        return FileSystemConstant.FILE_SYSTEM_NAS;
    }

    public String getUsedRatio()
    {
        return usedRatio;
    }

    public void setUsedRatio(String usedRatio)
    {
        this.usedRatio = usedRatio;
    }
    
    private float computeUtilization(long usedSize, long totalSize)
    {
        BigDecimal used = new BigDecimal(usedSize);
        BigDecimal total = new BigDecimal(totalSize);
        
        return used.floatValue() / total.floatValue() * 100F;
    }
}
