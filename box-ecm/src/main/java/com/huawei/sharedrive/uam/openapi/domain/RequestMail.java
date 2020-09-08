package com.huawei.sharedrive.uam.openapi.domain;

import java.io.Serializable;

public class RequestMail implements Serializable
{
    
    private static final long serialVersionUID = -1069650777799606331L;
    
    private String email;
    
    public String getEmail()
    {
        return email;
    }
    
    public void setEmail(String email)
    {
        this.email = email;
    }
    
}
