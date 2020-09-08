package com.huawei.sharedrive.app.spacestatistics.domain;

import java.io.Serializable;

public class UserStatisticsInfo implements Serializable
{
    private static final long serialVersionUID = -142899371614772723L;
    
    private Long lastStatisNode;
    
    private Long spaceUsed;
    
    private Long fileCount;
    
    public static final String CACHE_KEY_PRIFIX_USERSINFO = "UserInfo_";
    
    public Long getLastStatisNode()
    {
        return lastStatisNode;
    }
    
    public void setLastStatisNode(Long lastStatisNode)
    {
        this.lastStatisNode = lastStatisNode;
    }
    
    public Long getSpaceUsed()
    {
        return spaceUsed;
    }
    
    public void setSpaceUsed(Long spaceUsed)
    {
        this.spaceUsed = spaceUsed;
    }
    
    public Long getFileCount()
    {
        return this.fileCount;
    }
    
    public void setFileCount(Long fileCount)
    {
        this.fileCount = fileCount;
    }
    
}
