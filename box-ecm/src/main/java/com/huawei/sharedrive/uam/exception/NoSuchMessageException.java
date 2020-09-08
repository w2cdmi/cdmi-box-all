package com.huawei.sharedrive.uam.exception;

import org.springframework.http.HttpStatus;

public class NoSuchMessageException extends BaseRunException
{
    private static final long serialVersionUID = 1826643366776562778L;
    
    public NoSuchMessageException()
    {
        super(HttpStatus.NOT_FOUND, ErrorCode.NO_SUCH_MESSAGE.getCode(),
            ErrorCode.NO_SUCH_MESSAGE.getMessage());
    }
    
    public NoSuchMessageException(Throwable e)
    {
        super(e, HttpStatus.NOT_FOUND, ErrorCode.NO_SUCH_MESSAGE.getCode(),
            ErrorCode.NO_SUCH_MESSAGE.getMessage());
    }
    
    public NoSuchMessageException(String excepMessage)
    {
        super(HttpStatus.NOT_FOUND, ErrorCode.NO_SUCH_MESSAGE.getCode(),
            ErrorCode.NO_SUCH_MESSAGE.getMessage(), excepMessage);
    }
    
}
