package com.huawei.sharedrive.app.exception;

import org.springframework.http.HttpStatus;

public class NoSuchSourceException extends BaseRunException
{
    private static final long serialVersionUID = 1826643366776562778L;
    
    public NoSuchSourceException()
    {
        super(HttpStatus.NOT_FOUND, ErrorCode.NO_SUCH_SOURCE.getCode(), ErrorCode.NO_SUCH_SOURCE.getMessage());
    }
    
    public NoSuchSourceException(String excepMessage)
    {
        super(HttpStatus.NOT_FOUND, ErrorCode.NO_SUCH_SOURCE.getCode(),
            ErrorCode.NO_SUCH_SOURCE.getMessage(), excepMessage);
    }
    
    public NoSuchSourceException(Throwable e)
    {
        super(e, HttpStatus.NOT_FOUND, ErrorCode.NO_SUCH_SOURCE.getCode(),
            ErrorCode.NO_SUCH_SOURCE.getMessage());
    }
    
}
