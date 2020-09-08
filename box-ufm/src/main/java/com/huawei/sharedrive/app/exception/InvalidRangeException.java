package com.huawei.sharedrive.app.exception;

import org.springframework.http.HttpStatus;

public class InvalidRangeException extends BaseRunException
{
    private static final long serialVersionUID = -7508183764484699278L;
    
    public InvalidRangeException()
    {
        super(HttpStatus.BAD_REQUEST, ErrorCode.INVALID_RANGE.getCode(), ErrorCode.INVALID_RANGE.getMessage());
    }
    
    public InvalidRangeException(String excepMessage)
    {
        super(HttpStatus.BAD_REQUEST, ErrorCode.INVALID_RANGE.getCode(),
            ErrorCode.INVALID_RANGE.getMessage(), excepMessage);
    }
    
    public InvalidRangeException(Throwable e)
    {
        super(e, HttpStatus.BAD_REQUEST, ErrorCode.INVALID_RANGE.getCode(),
            ErrorCode.INVALID_RANGE.getMessage());
    }
}
