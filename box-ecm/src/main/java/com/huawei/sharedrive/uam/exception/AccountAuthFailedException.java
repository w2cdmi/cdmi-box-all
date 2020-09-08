package com.huawei.sharedrive.uam.exception;

import org.springframework.http.HttpStatus;

public class AccountAuthFailedException extends BaseRunNoStackException
{
    private static final long serialVersionUID = -4196387110067643274L;
    
    public AccountAuthFailedException()
    {
        super(HttpStatus.UNAUTHORIZED, ErrorCode.ACCOUNTUNAUTHORIZED.getCode(),
            ErrorCode.ACCOUNTUNAUTHORIZED.getMessage());
    }
    
    public AccountAuthFailedException(String msg)
    {
        super(HttpStatus.UNAUTHORIZED, ErrorCode.ACCOUNTUNAUTHORIZED.getCode(),
            ErrorCode.ACCOUNTUNAUTHORIZED.getMessage(), msg);
    }
    
    public AccountAuthFailedException(Throwable e)
    {
        
        super(e, HttpStatus.UNAUTHORIZED, ErrorCode.ACCOUNTUNAUTHORIZED.getCode(),
            ErrorCode.ACCOUNTUNAUTHORIZED.getMessage());
        
    }
}
