package com.huawei.sharedrive.uam.exception;

import org.springframework.http.HttpStatus;

public class PasswordInitException extends BaseRunException
{
    private static final long serialVersionUID = 3792468704127719114L;
    
    public PasswordInitException()
    {
        super(HttpStatus.UNAUTHORIZED, ErrorCode.NEEDCHANGEPASSWORD.getCode(),
            ErrorCode.NEEDCHANGEPASSWORD.getMessage());
    }
    
    public PasswordInitException(String msg)
    {
        super(HttpStatus.UNAUTHORIZED, ErrorCode.NEEDCHANGEPASSWORD.getCode(),
            ErrorCode.NEEDCHANGEPASSWORD.getMessage(), msg);
    }
    
    public PasswordInitException(Throwable e)
    {
        
        super(e, HttpStatus.UNAUTHORIZED, ErrorCode.NEEDCHANGEPASSWORD.getCode(),
            ErrorCode.NEEDCHANGEPASSWORD.getMessage());
        
    }
    
}
