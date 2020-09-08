package com.huawei.sharedrive.uam.exception;

import org.springframework.http.HttpStatus;

public class InvalidAppIdException extends BaseRunException
{
    
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    
    public InvalidAppIdException()
    {
        super(HttpStatus.BAD_REQUEST, ErrorCode.INVALID_APPID.getCode(), ErrorCode.INVALID_APPID.getMessage());
    }
    
}
