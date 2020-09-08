package com.huawei.sharedrive.app.spacestatistics.domain;

import java.io.Serializable;

public class AccountStatisticsInfo implements Serializable
{
    private static final long serialVersionUID = 1282180703907019283L;
    
    private Long accountId;
    
    private Long currentSpace;
    
    private Long currentFiles;
    
    public static final String CACHE_KEY_PRIFIX_ACCOUNTSPACE = "AccountInfo_";
    
    public Long getAccountId()
    {
        return accountId;
    }
    
    public void setAccountId(Long accountId)
    {
        this.accountId = accountId;
    }
    
    public Long getCurrentSpace()
    {
        return currentSpace;
    }
    
    public void setCurrentSpace(Long currentSpace)
    {
        this.currentSpace = currentSpace;
    }
    
    public Long getCurrentFiles()
    {
        return currentFiles;
    }
    
    public void setCurrentFiles(Long currentFiles)
    {
        this.currentFiles = currentFiles;
    }
    
}
