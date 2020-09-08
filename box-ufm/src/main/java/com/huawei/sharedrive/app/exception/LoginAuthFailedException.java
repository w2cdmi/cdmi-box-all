package com.huawei.sharedrive.app.exception;

import org.springframework.http.HttpStatus;

public class LoginAuthFailedException extends BaseRunException
{
    private static final long serialVersionUID = 1325907017686953441L;
    
    public LoginAuthFailedException()
    {
        super(HttpStatus.UNAUTHORIZED, ErrorCode.LOGINUNAUTHORIZED.getCode(),
            ErrorCode.LOGINUNAUTHORIZED.getMessage());
    }
    
    public LoginAuthFailedException(String excepMessage)
    {
        super(HttpStatus.UNAUTHORIZED, ErrorCode.LOGINUNAUTHORIZED.getCode(),
            ErrorCode.LOGINUNAUTHORIZED.getMessage(),excepMessage);
    }
}
