package com.huawei.sharedrive.isystem.exception;

import org.springframework.http.HttpStatus;

public class NoSuchUserException extends BaseRunException
{
    
    private static final long serialVersionUID = 7820264192222815663L;
    
    public NoSuchUserException()
    {
        super(HttpStatus.NOT_FOUND, ErrorCode.NOSUCHUSER.getCode(), ErrorCode.NOSUCHUSER.toString());
    }
}
