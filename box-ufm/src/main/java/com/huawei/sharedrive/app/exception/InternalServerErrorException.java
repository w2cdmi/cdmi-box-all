package com.huawei.sharedrive.app.exception;

import org.springframework.http.HttpStatus;

public class InternalServerErrorException extends BaseRunException
{
    private static final long serialVersionUID = -7144581466157474902L;
    
    public InternalServerErrorException(String excepMessage)
    {
        super(HttpStatus.INTERNAL_SERVER_ERROR, ErrorCode.INTERNAL_SERVER_ERROR.getCode(),
            ErrorCode.INTERNAL_SERVER_ERROR.getMessage(),excepMessage);
    }
    
    public InternalServerErrorException(String excepMessage, Throwable e)
    {
        super(HttpStatus.INTERNAL_SERVER_ERROR, ErrorCode.INTERNAL_SERVER_ERROR.getCode(),
            ErrorCode.INTERNAL_SERVER_ERROR.getMessage(),excepMessage, e);
    }
    
    public InternalServerErrorException()
    {
        super(HttpStatus.INTERNAL_SERVER_ERROR, ErrorCode.INTERNAL_SERVER_ERROR.getCode(),
            ErrorCode.INTERNAL_SERVER_ERROR.getMessage());
    }
    
    public InternalServerErrorException(Throwable e)
    {
        super(e, HttpStatus.INTERNAL_SERVER_ERROR, ErrorCode.INTERNAL_SERVER_ERROR.getCode(),
            ErrorCode.INTERNAL_SERVER_ERROR.getMessage());
    }
}
