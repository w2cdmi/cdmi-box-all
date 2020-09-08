package com.huawei.sharedrive.app.exception;

import org.springframework.http.HttpStatus;

public class NoSuchItemsException extends BaseRunException
{
    private static final long serialVersionUID = 1826643366776562778L;
    
    public NoSuchItemsException()
    {
        super(HttpStatus.NOT_FOUND, ErrorCode.NO_SUCH_ITEM.getCode(),
            ErrorCode.NO_SUCH_ITEM.getMessage());
    }
    
    public NoSuchItemsException(Throwable e)
    {
        super(e, HttpStatus.NOT_FOUND, ErrorCode.NO_SUCH_ITEM.getCode(),
            ErrorCode.NO_SUCH_ITEM.getMessage());
    }
    
    public NoSuchItemsException(String excepMessage)
    {
        super(HttpStatus.NOT_FOUND, ErrorCode.NO_SUCH_ITEM.getCode(),
            ErrorCode.NO_SUCH_ITEM.getMessage(),excepMessage);
    }
    
}
