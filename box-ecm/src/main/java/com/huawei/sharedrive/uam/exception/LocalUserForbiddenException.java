package com.huawei.sharedrive.uam.exception;

import org.springframework.http.HttpStatus;

public class LocalUserForbiddenException extends BaseRunException
{
    private static final long serialVersionUID = 3187889527609485428L;
    
    public LocalUserForbiddenException()
    {
        super(HttpStatus.FORBIDDEN, ErrorCode.LOCALUSER_FORBIDDEN.getCode(),
            ErrorCode.LOCALUSER_FORBIDDEN.getMessage());
    }
    
    public LocalUserForbiddenException(String excepMessage)
    {
        super(HttpStatus.FORBIDDEN, ErrorCode.LOCALUSER_FORBIDDEN.getCode(),
            ErrorCode.LOCALUSER_FORBIDDEN.getMessage(), excepMessage);
    }
}
