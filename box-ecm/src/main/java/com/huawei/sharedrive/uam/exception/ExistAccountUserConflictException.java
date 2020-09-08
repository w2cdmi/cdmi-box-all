package com.huawei.sharedrive.uam.exception;

import org.springframework.http.HttpStatus;

public class ExistAccountUserConflictException extends BaseRunException
{

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    
    public ExistAccountUserConflictException()
    {
        super(HttpStatus.NOT_FOUND, ErrorCode.EXIST_ACCOUNT_USER_CONFLICT.getCode(),
            ErrorCode.EXIST_ACCOUNT_USER_CONFLICT.getMessage());
    }
    
}
