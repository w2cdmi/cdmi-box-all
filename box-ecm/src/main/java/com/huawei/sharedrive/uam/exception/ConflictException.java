package com.huawei.sharedrive.uam.exception;

import org.springframework.http.HttpStatus;

public class ConflictException extends BaseRunException
{
    
    private static final long serialVersionUID = 7530749762149579894L;
    
    public ConflictException()
    {
        super(HttpStatus.CONFLICT, ErrorCode.CONFLICT_USER.getCode(), ErrorCode.CONFLICT_USER.getMessage());
    }
    
    public ConflictException(Throwable e)
    {
        super(e, HttpStatus.CONFLICT, ErrorCode.CONFLICT_USER.getCode(), ErrorCode.CONFLICT_USER.getMessage());
    }
}
