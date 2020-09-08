package com.huawei.sharedrive.uam.exception;

import org.springframework.http.HttpStatus;

public class LoginAuthFailedException extends BaseRunNoStackException
{
    
    private static final long serialVersionUID = 7820264192222815663L;
    
    public LoginAuthFailedException()
    {
        super(HttpStatus.UNAUTHORIZED, "unauthorized", ErrorCode.LOGINUNAUTHORIZED.toString());
    }
    
    public LoginAuthFailedException(String msg)
    {
        super(HttpStatus.UNAUTHORIZED, "unauthorized", ErrorCode.LOGINUNAUTHORIZED.toString(), msg);
    }
    
    public LoginAuthFailedException(String type, String msg)
    {
        super(HttpStatus.UNAUTHORIZED, type, ErrorCode.LOGINUNAUTHORIZED.toString(), msg);
    }
}
