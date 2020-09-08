package com.huawei.sharedrive.app.exception;

import org.springframework.http.HttpStatus;

public class ScannedForbiddenException extends BaseRunException
{
    
    private static final long serialVersionUID = -1927601392039703199L;
    
    public ScannedForbiddenException()
    {
        super(HttpStatus.FORBIDDEN, ErrorCode.SCANNED_FORBIDDEN.getCode(),
            ErrorCode.SCANNED_FORBIDDEN.getMessage());
    }
    
    public ScannedForbiddenException(String excepMessage)
    {
        super(HttpStatus.FORBIDDEN, ErrorCode.SCANNED_FORBIDDEN.getCode(),
            ErrorCode.SCANNED_FORBIDDEN.getMessage(), excepMessage);
    }
    
}
