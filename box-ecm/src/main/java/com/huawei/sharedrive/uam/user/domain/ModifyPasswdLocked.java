package com.huawei.sharedrive.uam.user.domain;

import java.io.Serializable;
import java.util.Date;

public class ModifyPasswdLocked implements Serializable
{
    
    /**
     * 
     */
    private static final long serialVersionUID = 2703066201200238970L;
    
    private String userName;
    
    private Date loginDate;
    
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
