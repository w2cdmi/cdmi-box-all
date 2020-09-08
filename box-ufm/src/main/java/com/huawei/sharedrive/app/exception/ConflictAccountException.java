package com.huawei.sharedrive.app.exception;

import org.springframework.http.HttpStatus;

public class ConflictAccountException extends BaseRunException
{

    private static final long serialVersionUID = -8946639787817446547L;

    public ConflictAccountException()
    {
        super(HttpStatus.CONFLICT, ErrorCode.CONFLICT_ACCOUNT.getCode(), ErrorCode.CONFLICT_ACCOUNT.getMessage());
    }
    
    public ConflictAccountException(String msg)
    {
        super(HttpStatus.CONFLICT, ErrorCode.CONFLICT_ACCOUNT.getCode(), ErrorCode.CONFLICT_ACCOUNT.getMessage(), msg);
    }
    
}
