package com.huawei.sharedrive.uam.exception;

import org.springframework.http.HttpStatus;

public class NoSuchSecurityLevelException extends BaseRunException
{
    private static final long serialVersionUID = 1826643366776562778L;
    
    public NoSuchSecurityLevelException()
    {
        super(HttpStatus.NOT_FOUND, ErrorCode.NO_SUCH_SECUTIRY_LEVEL.getCode(),
            ErrorCode.NO_SUCH_SECUTIRY_LEVEL.getMessage());
    }
    
    public NoSuchSecurityLevelException(Throwable e)
    {
        super(e, HttpStatus.NOT_FOUND, ErrorCode.NO_SUCH_SECUTIRY_LEVEL.getCode(),
            ErrorCode.NO_SUCH_SECUTIRY_LEVEL.getMessage());
    }
    
    public NoSuchSecurityLevelException(String excepMessage)
    {
        super(HttpStatus.NOT_FOUND, ErrorCode.NO_SUCH_SECUTIRY_LEVEL.getCode(),
            ErrorCode.NO_SUCH_SECUTIRY_LEVEL.getMessage(), excepMessage);
    }
    
}
