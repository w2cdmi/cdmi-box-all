package com.huawei.sharedrive.uam.exception;

import org.springframework.http.HttpStatus;

public class ExistTagConflictException extends BaseRunException
{
    private static final long serialVersionUID = 5608620607174703615L;
    
    public ExistTagConflictException()
    {
        super(HttpStatus.CONFLICT, ErrorCode.EXIST_TAG_CONFLICT.getCode(),
            ErrorCode.EXIST_TAG_CONFLICT.getMessage());
    }
}
