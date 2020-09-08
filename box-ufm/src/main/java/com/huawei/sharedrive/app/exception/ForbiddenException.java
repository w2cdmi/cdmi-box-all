package com.huawei.sharedrive.app.exception;

import org.springframework.http.HttpStatus;

public class ForbiddenException extends BaseRunException
{
    
    private static final long serialVersionUID = 2294022749211059949L;
    
    public ForbiddenException()
    {
        super(HttpStatus.FORBIDDEN, ErrorCode.FORBIDDEN_OPER.getCode(), ErrorCode.FORBIDDEN_OPER.getMessage());
    }
    
    public ForbiddenException(String excepMessage)
    {
        super(HttpStatus.FORBIDDEN, ErrorCode.FORBIDDEN_OPER.getCode(), ErrorCode.FORBIDDEN_OPER.getMessage(),excepMessage);
    }
    
    public ForbiddenException(Throwable e)
    {
        
        super(e, HttpStatus.FORBIDDEN, ErrorCode.FORBIDDEN_OPER.getCode(),
            ErrorCode.FORBIDDEN_OPER.getMessage());
        
    }
    
}
