package com.huawei.sharedrive.app.exception;

import org.springframework.http.HttpStatus;

public class NoSuchDestException extends BaseRunException
{
    private static final long serialVersionUID = 1826643366776562778L;
    
    public NoSuchDestException()
    {
        super(HttpStatus.NOT_FOUND, ErrorCode.NO_SUCH_DEST.getCode(), ErrorCode.NO_SUCH_DEST.getMessage());
    }
    
    public NoSuchDestException(String excepMessage)
    {
        super(HttpStatus.NOT_FOUND, ErrorCode.NO_SUCH_DEST.getCode(), ErrorCode.NO_SUCH_DEST.getMessage(),
            excepMessage);
    }
    
    public NoSuchDestException(Throwable e)
    {
        super(e, HttpStatus.NOT_FOUND, ErrorCode.NO_SUCH_DEST.getCode(), ErrorCode.NO_SUCH_DEST.getMessage());
    }
    
}
