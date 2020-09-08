package com.huawei.sharedrive.uam.exception;

import org.springframework.http.HttpStatus;

public class NoSuchResourceStrategyException extends BaseRunException
{
    private static final long serialVersionUID = 1826643366776562778L;
    
    public NoSuchResourceStrategyException()
    {
        super(HttpStatus.NOT_FOUND, ErrorCode.NO_SUCH_RESOURCE_STRATEGY.getCode(),
            ErrorCode.NO_SUCH_RESOURCE_STRATEGY.getMessage());
    }
    
    public NoSuchResourceStrategyException(Throwable e)
    {
        super(e, HttpStatus.NOT_FOUND, ErrorCode.NO_SUCH_ANNOUNCEMENT.getCode(),
            ErrorCode.NO_SUCH_RESOURCE_STRATEGY.getMessage());
    }
    
    public NoSuchResourceStrategyException(String excepMessage)
    {
        super(HttpStatus.NOT_FOUND, ErrorCode.NO_SUCH_RESOURCE_STRATEGY.getCode(),
            ErrorCode.NO_SUCH_RESOURCE_STRATEGY.getMessage(), excepMessage);
    }
    
}
