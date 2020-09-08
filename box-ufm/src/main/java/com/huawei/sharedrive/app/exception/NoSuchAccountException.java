package com.huawei.sharedrive.app.exception;

import org.springframework.http.HttpStatus;

public class NoSuchAccountException extends BaseRunException
{
    
    private static final long serialVersionUID = -838855225585187355L;
    
    public NoSuchAccountException()
    {
        super(HttpStatus.NOT_FOUND, ErrorCode.NO_SUCH_ACCOUNT.getCode(),
            ErrorCode.NO_SUCH_ACCOUNT.getMessage());
    }
    
    public NoSuchAccountException(String excepMessage)
    {
        super(HttpStatus.NOT_FOUND, ErrorCode.NO_SUCH_ACCOUNT.getCode(),
            ErrorCode.NO_SUCH_ACCOUNT.getMessage(), excepMessage);
    }
}
