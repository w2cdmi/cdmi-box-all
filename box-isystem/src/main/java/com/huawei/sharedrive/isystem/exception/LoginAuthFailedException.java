package com.huawei.sharedrive.isystem.exception;

import org.springframework.http.HttpStatus;

public class LoginAuthFailedException extends BaseRunException
{
    
    private static final long serialVersionUID = 7820264192222815663L;
    
    public LoginAuthFailedException()
    {
        super(HttpStatus.UNAUTHORIZED, "unauthorized", ErrorCode.LOGINUNAUTHORIZED.toString());
    }
}