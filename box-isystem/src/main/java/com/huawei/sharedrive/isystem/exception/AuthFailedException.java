package com.huawei.sharedrive.isystem.exception;

import org.springframework.http.HttpStatus;

public class AuthFailedException extends BaseRunException
{
    private static final long serialVersionUID = -6708777582060907876L;
    
    public AuthFailedException()
    {
        super(HttpStatus.UNAUTHORIZED, ErrorCode.TOKENUNAUTHORIZED.getCode(),
            ErrorCode.TOKENUNAUTHORIZED.getMessage());
    }
    
    public AuthFailedException(Throwable e)
    {
        
        super(e, HttpStatus.UNAUTHORIZED, ErrorCode.TOKENUNAUTHORIZED.getCode(),
            ErrorCode.TOKENUNAUTHORIZED.getMessage());
        
    }
    
}
