package com.huawei.sharedrive.app.exception;

import org.springframework.http.HttpStatus;

public class BadRequestException extends BaseRunException
{
    private static final long serialVersionUID = 5928641424976905468L;
    
    public BadRequestException()
    {
        super(HttpStatus.BAD_REQUEST, ErrorCode.BAD_REQUEST.getCode(), ErrorCode.BAD_REQUEST.getMessage());
    }
    
    public BadRequestException(String excepMessage)
    {
        super(HttpStatus.BAD_REQUEST, ErrorCode.BAD_REQUEST.getCode(), ErrorCode.BAD_REQUEST.getMessage(),
            excepMessage);
    }
    
    public BadRequestException(Throwable e)
    {
        super(e, HttpStatus.BAD_REQUEST, ErrorCode.BAD_REQUEST.getCode(), ErrorCode.BAD_REQUEST.getMessage());
    }
}
