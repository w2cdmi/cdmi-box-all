package com.huawei.sharedrive.uam.openapi.domain;

import java.io.Serializable;

public class RestAppStatisticsResponse implements Serializable
{
    private static final long serialVersionUID = -6575661159768984394L;
    
    private Long fileCount;
    
    private Long spaceCount;
    
    private Long spaceUsed;
    
    private String type;
    
    public Long getFileCount()
    {
        return fileCount;
    }
    
    public Long getSpaceCount()
    {
        return spaceCount;
    }
    
    public Long getSpaceUsed()
    {
        return spaceUsed;
    }
    
    public String getType()
    {
        return type;
    }
    
    public void setFileCount(Long fileCount)
    {
        this.fileCount = fileCount;
    }
    
    public void setSpaceCount(Long spaceCount)
    {
        this.spaceCount = spaceCount;
    }
    
    public void setSpaceUsed(Long spaceUsed)
    {
        this.spaceUsed = spaceUsed;
    }
    
    public void setType(String type)
    {
        this.type = type;
    }
    
}