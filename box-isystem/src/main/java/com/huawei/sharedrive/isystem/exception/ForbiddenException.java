package com.huawei.sharedrive.isystem.exception;

import org.springframework.http.HttpStatus;

public class ForbiddenException extends BaseRunException
{
    
    private static final long serialVersionUID = -1216559425084843515L;
    
    public ForbiddenException()
    {
        super(HttpStatus.FORBIDDEN, ErrorCode.FORBIDDEN_OPER.getCode(), ErrorCode.FORBIDDEN_OPER.getMessage());
    }
    
    public ForbiddenException(Throwable e)
    {
        
        super(e, HttpStatus.FORBIDDEN, ErrorCode.FORBIDDEN_OPER.getCode(),
            ErrorCode.FORBIDDEN_OPER.getMessage());
        
    }
    
}
