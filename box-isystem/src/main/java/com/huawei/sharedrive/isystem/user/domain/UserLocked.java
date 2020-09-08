package com.huawei.sharedrive.isystem.user.domain;

import java.io.Serializable;
import java.util.Date;

/**
 * 用戶鎖定功能demo
 * 
 * @author h90005572
 * 
 */
public class UserLocked implements Serializable
{
    
    /**
     * 
     */
    private static final long serialVersionUID = 2703066201200238970L;
    
    /** 用戶userid */
    private String userName;
    
    /** 用戶登錄錯誤時間 */
    private Date loginDate;
    
    /** 用戶登錄錯誤次數 */
    private int loginTimes;
    
    public String getUserName()
    {
        return userName;
    }
    
    public void setUserName(String userName)
    {
        this.userName = userName;
    }
    
    public Date getLoginDate()
    {
        return loginDate != null ? new Date(loginDate.getTime()) : null;
    }
    
    public void setLoginDate(Date loginDate)
    {
        this.loginDate = loginDate != null ? new Date(loginDate.getTime()) : null;
    }
    
    public int getLoginTimes()
    {
        return loginTimes;
    }
    
    public void setLoginTimes(int loginTimes)
    {
        this.loginTimes = loginTimes;
    }
    
}
