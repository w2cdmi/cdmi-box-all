package com.huawei.sharedrive.uam.exception;

import org.springframework.http.HttpStatus;

public class LocalAuthException extends BaseRunException
{
    
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    
    public LocalAuthException()
    {
        super(HttpStatus.CONFLICT, ErrorCode.NOT_LOCAL_AUTH.getCode(), ErrorCode.NOT_LOCAL_AUTH.getMessage());
    }
    
}
