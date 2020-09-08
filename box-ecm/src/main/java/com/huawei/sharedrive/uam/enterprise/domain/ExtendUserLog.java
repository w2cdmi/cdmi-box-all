package com.huawei.sharedrive.uam.enterprise.domain;

import pw.cdmi.common.log.UserLog;

public class ExtendUserLog
{
    private UserLog userLog;
    
    private long accountId;
    
    public UserLog getUserLog()
    {
        return userLog;
    }
    
    public void setUserLog(UserLog userLog)
    {
        this.userLog = userLog;
    }
    
    public long getAccountId()
    {
        return accountId;
    }
    
    public void setAccountId(long accountId)
    {
        this.accountId = accountId;
    }
}
