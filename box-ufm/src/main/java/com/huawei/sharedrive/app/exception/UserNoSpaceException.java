package com.huawei.sharedrive.app.exception;

import org.springframework.http.HttpStatus;

public class UserNoSpaceException extends BaseRunException
{
    private static final long serialVersionUID = -1201180972599198685L;
    
    public UserNoSpaceException()
    {
        super(HttpStatus.BAD_REQUEST, ErrorCode.BAD_REQUEST.getCode(), ErrorCode.BAD_REQUEST.getMessage());
    }
    
    public UserNoSpaceException(String excepMessage)
    {
        
        super(HttpStatus.BAD_REQUEST, ErrorCode.BAD_REQUEST.getCode(), excepMessage);
    }
}
