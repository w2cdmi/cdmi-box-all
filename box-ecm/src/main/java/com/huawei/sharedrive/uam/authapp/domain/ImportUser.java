package com.huawei.sharedrive.uam.authapp.domain;

import java.io.Serializable;

import com.huawei.sharedrive.uam.user.domain.User;

public class ImportUser implements Serializable
{
    private static final long serialVersionUID = 2830850552125651742L;
    
    private User user;
    
    private boolean parseSucess;
    
    private String errorCode;
    
    public User getUser()
    {
        return user;
    }
    
    public void setUser(User user)
    {
        this.user = user;
    }
    
    public boolean isParseSucess()
    {
        return parseSucess;
    }
    
    public void setParseSucess(boolean parseSucess)
    {
        this.parseSucess = parseSucess;
    }
    
    public String getErrorCode()
    {
        return errorCode;
    }
    
    public void setErrorCode(String errorCode)
    {
        this.errorCode = errorCode;
    }
    
}
