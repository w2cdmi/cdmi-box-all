package com.huawei.sharedrive.app.exception;

import org.springframework.http.HttpStatus;

public class ConflictUserException extends BaseRunException
{
    private static final long serialVersionUID = -1551040905880565493L;

    public ConflictUserException()
    {
        super(HttpStatus.CONFLICT, ErrorCode.CONFLICT_USER.getCode(), ErrorCode.CONFLICT_USER.getMessage());
    }
    
    public ConflictUserException(String msg)
    {
        super(HttpStatus.CONFLICT, ErrorCode.CONFLICT_USER.getCode(), ErrorCode.CONFLICT_USER.getMessage(), msg);
    }
    
}
