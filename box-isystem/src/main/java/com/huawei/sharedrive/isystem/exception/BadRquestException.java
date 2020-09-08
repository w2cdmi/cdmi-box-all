package com.huawei.sharedrive.isystem.exception;

import org.springframework.http.HttpStatus;

public class BadRquestException extends BaseRunException
{
    private static final long serialVersionUID = 7508598408320744144L;
    
    public BadRquestException()
    {
        
        super(HttpStatus.BAD_REQUEST, ErrorCode.BAD_REQUEST.getCode(), ErrorCode.BAD_REQUEST.getMessage());
        
    }
    
    public BadRquestException(String msg)
    {
        super(HttpStatus.BAD_REQUEST, ErrorCode.BAD_REQUEST.getCode(), ErrorCode.BAD_REQUEST.getMessage(), msg);
    }
    
    public BadRquestException(Throwable e)
    {
        
        super(e, HttpStatus.BAD_REQUEST, ErrorCode.BAD_REQUEST.getCode(), ErrorCode.BAD_REQUEST.getMessage());
        
    }
    
}
