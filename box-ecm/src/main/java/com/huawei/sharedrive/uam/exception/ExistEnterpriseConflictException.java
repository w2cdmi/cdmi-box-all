package com.huawei.sharedrive.uam.exception;

import org.springframework.http.HttpStatus;

public class ExistEnterpriseConflictException extends BaseRunException
{
    
    private static final long serialVersionUID = -3002892640656677750L;
    
    public ExistEnterpriseConflictException()
    {
        super(HttpStatus.CONFLICT, ErrorCode.EXIST_ENTERPRISE_CONFLICT.getCode(),
            ErrorCode.EXIST_ENTERPRISE_CONFLICT.getMessage());
    }
    
}
