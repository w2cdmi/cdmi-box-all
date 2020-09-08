package com.huawei.sharedrive.uam.security.domain;

import java.io.Serializable;
import java.util.Locale;

public class UserSpecial implements Serializable
{
    private static final long serialVersionUID = -3537999519157986986L;
    
    private String userName;
    
    private UserSpecialType specialType;
    
    public UserSpecial()
    {
        super();
    }
    
    public UserSpecial(String userName, UserSpecialType specialType)
    {
        super();
        this.userName = userName.toUpperCase(Locale.ENGLISH);
        this.specialType = specialType;
    }
    
    public String getUserName()
    {
        return userName;
    }
    
    public void setUserName(String userName)
    {
        this.userName = userName.toUpperCase(Locale.ENGLISH);
    }
    
    public UserSpecialType getSpecialType()
    {
        return specialType;
    }
    
    public void setSpecialType(UserSpecialType specialType)
    {
        this.specialType = specialType;
    }
}
