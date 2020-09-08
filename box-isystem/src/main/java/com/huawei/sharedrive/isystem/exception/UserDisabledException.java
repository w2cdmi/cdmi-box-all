package com.huawei.sharedrive.isystem.exception;

import org.apache.shiro.authc.AuthenticationException;

/**
 * 终端禁用
 * 
 * @author h90005572
 * 
 */
public class UserDisabledException extends AuthenticationException
{
    private static final long serialVersionUID = -2147093566523637264L;
    
    public UserDisabledException()
    {
        super();
    }
    
    public UserDisabledException(String message, Throwable cause)
    {
        super(message, cause);
    }
    
}
