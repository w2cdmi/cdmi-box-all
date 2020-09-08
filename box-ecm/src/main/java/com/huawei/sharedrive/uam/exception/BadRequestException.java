package com.huawei.sharedrive.uam.exception;

import org.springframework.http.HttpStatus;

public class BadRequestException extends BaseRunException
{
    private static final long serialVersionUID = 7508598408320744144L;
    
    public BadRequestException()
    {
        super(HttpStatus.BAD_REQUEST, ErrorCode.BAD_REQUEST.getCode(), ErrorCode.BAD_REQUEST.getMessage());
    }
    
    public BadRequestException(String msg)
    {
        super(HttpStatus.BAD_REQUEST, ErrorCode.BAD_REQUEST.getCode(), ErrorCode.BAD_REQUEST.getMessage(),
            msg);
    }
    
    public BadRequestException(String excption, String msg)
    {
        super(HttpStatus.BAD_REQUEST, ErrorCode.BAD_REQUEST.getCode(), excption, msg);
    }
    
    public BadRequestException(Throwable e)
    {
        
        super(e, HttpStatus.BAD_REQUEST, ErrorCode.BAD_REQUEST.getCode(), ErrorCode.BAD_REQUEST.getMessage());
        
    }
    
}
