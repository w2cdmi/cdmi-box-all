package com.huawei.sharedrive.uam.exception;

import org.springframework.http.HttpStatus;

public class InternalServerErrorException extends BaseRunException
{
    
    private static final long serialVersionUID = 3631569260487626777L;
    
    public InternalServerErrorException(Throwable e)
    {
        super(e, HttpStatus.INTERNAL_SERVER_ERROR, ErrorCode.INTERNAL_SERVER_ERROR.getCode(),
            ErrorCode.INTERNAL_SERVER_ERROR.getMessage());
    }
    
    public InternalServerErrorException()
    {
        super(HttpStatus.INTERNAL_SERVER_ERROR, ErrorCode.INTERNAL_SERVER_ERROR.getCode(),
            ErrorCode.INTERNAL_SERVER_ERROR.getMessage());
    }
    
    public InternalServerErrorException(String excepMessage)
    {
        super(HttpStatus.INTERNAL_SERVER_ERROR, ErrorCode.INTERNAL_SERVER_ERROR.getCode(),
            ErrorCode.INTERNAL_SERVER_ERROR.getMessage(), excepMessage);
    }
    
    public InternalServerErrorException(String excepMessage, Throwable e)
    {
        super(e, HttpStatus.INTERNAL_SERVER_ERROR, ErrorCode.INTERNAL_SERVER_ERROR.getCode(), excepMessage);
    }
}
