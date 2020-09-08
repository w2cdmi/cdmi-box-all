package com.huawei.sharedrive.uam.exception;

import org.apache.shiro.authc.AuthenticationException;

public class UserLockedWebException extends AuthenticationException
{
    private static final long serialVersionUID = 4521705604814731896L;
    
    public UserLockedWebException()
    {
        super();
    }
    
    public UserLockedWebException(String message)
    {
        super(message);
    }
    
    public UserLockedWebException(String message, Throwable cause)
    {
        super(message, cause);
    }
    
}
