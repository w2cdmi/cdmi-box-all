package com.huawei.sharedrive.isystem.syslog.domain;

import java.io.Serializable;

public class UserLogTypeDomain implements Serializable
{
    private static final long serialVersionUID = -2575032747474978476L;

    private UserLogType userLogType;
    
    private String operatrDetails;

    public UserLogType getUserLogType()
    {
        return userLogType;
    }

    public void setUserLogType(UserLogType userLogType)
    {
        this.userLogType = userLogType;
    }

    public String getOperatrDetails()
    {
        return operatrDetails;
    }

    public void setOperatrDetails(String operatrDetails)
    {
        this.operatrDetails = operatrDetails;
    }
    
    
}
