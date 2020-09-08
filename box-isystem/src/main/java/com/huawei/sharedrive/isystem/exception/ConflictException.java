package com.huawei.sharedrive.isystem.exception;

import org.springframework.http.HttpStatus;

public class ConflictException extends BaseRunException
{
    
    private static final long serialVersionUID = 7530749762149579894L;
    
    public ConflictException()
    {
        super(HttpStatus.CONFLICT, ErrorCode.TOKENUNAUTHORIZED.getCode(),
            ErrorCode.TOKENUNAUTHORIZED.getMessage());
    }
    
    public ConflictException(Throwable e)
    {
        super(e, HttpStatus.CONFLICT, ErrorCode.TOKENUNAUTHORIZED.getCode(),
            ErrorCode.TOKENUNAUTHORIZED.getMessage());
    }
}
