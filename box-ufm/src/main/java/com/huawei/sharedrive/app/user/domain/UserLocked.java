package com.huawei.sharedrive.app.user.domain;

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
    
    private static final long serialVersionUID = 2703066201200238970L;
    
    /** 用戶登錄錯誤時間 */
    private Date loginDate;
    
    /** 用戶登錄錯誤次數 */
    private int loginTimes;
    
    /** 用戶objectSid */
    private String objectSid;
    
    public Date getLoginDate()
    {
        if (loginDate == null)
        {
            return null;
        }
        return (Date) loginDate.clone();
    }
    
    public int getLoginTimes()
    {
        return loginTimes;
    }
    
    public String getObjectSid()
    {
        return objectSid;
    }
    
    public void setLoginDate(Date loginDate)
    {
        if (loginDate == null)
        {
            this.loginDate = null;
        }
        else
        {
            this.loginDate = (Date) loginDate.clone();
        }
    }
    
    public void setLoginTimes(int loginTimes)
    {
        this.loginTimes = loginTimes;
    }
    
    public void setObjectSid(String objectSid)
    {
        this.objectSid = objectSid;
    }
    
}
