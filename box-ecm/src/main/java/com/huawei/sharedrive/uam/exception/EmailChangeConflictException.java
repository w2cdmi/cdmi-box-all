package com.huawei.sharedrive.uam.exception;

import org.springframework.http.HttpStatus;

public class EmailChangeConflictException extends BaseRunException
{
    
    private static final long serialVersionUID = 6692410086526456233L;
    
    public EmailChangeConflictException(String msg)
    {
        super(HttpStatus.CONFLICT, ErrorCode.EMAIL_CHANGE_CONFLICT.getCode(),
            ErrorCode.EMAIL_CHANGE_CONFLICT.getMessage(), msg);
    }
}
