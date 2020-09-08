package com.huawei.sharedrive.app.exception;

import org.springframework.http.HttpStatus;

public class NoSuchApplicationException extends BaseRunException
{
    
    private static final long serialVersionUID = -838855225585187355L;
    
    public NoSuchApplicationException()
    {
        super(HttpStatus.NOT_FOUND, ErrorCode.NO_SUCH_APPLICATION.getCode(),
            ErrorCode.NO_SUCH_APPLICATION.getMessage());
    }
    
    public NoSuchApplicationException(String excepMessage)
    {
        super(HttpStatus.NOT_FOUND, ErrorCode.NO_SUCH_APPLICATION.getCode(),
            ErrorCode.NO_SUCH_APPLICATION.getMessage(), excepMessage);
    }
}
