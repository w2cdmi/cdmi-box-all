package com.huawei.sharedrive.app.exception;

import org.springframework.http.HttpStatus;

public class NoSuchFileException extends BaseRunException
{
    private static final long serialVersionUID = 1826643366776562778L;
    
    public NoSuchFileException()
    {
        super(HttpStatus.NOT_FOUND, ErrorCode.NO_SUCH_FILE.getCode(), ErrorCode.NO_SUCH_FILE.getMessage());
    }
    
    public NoSuchFileException(String excepMessage)
    {
        super(HttpStatus.NOT_FOUND, ErrorCode.NO_SUCH_FILE.getCode(), ErrorCode.NO_SUCH_FILE.getMessage(),
            excepMessage);
    }
    
    public NoSuchFileException(Throwable e)
    {
        super(e, HttpStatus.NOT_FOUND, ErrorCode.NO_SUCH_FILE.getCode(), ErrorCode.NO_SUCH_FILE.getMessage());
    }
    
}
