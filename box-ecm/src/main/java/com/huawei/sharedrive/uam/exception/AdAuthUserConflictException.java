package com.huawei.sharedrive.uam.exception;

import org.springframework.http.HttpStatus;

public class AdAuthUserConflictException extends BaseRunException
{
    
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    
    public AdAuthUserConflictException()
    {
        super(HttpStatus.CONFLICT, ErrorCode.ADAUTH_USER_CONFLICT.getCode(),
            ErrorCode.ADAUTH_USER_CONFLICT.getMessage());
    }
    
}
