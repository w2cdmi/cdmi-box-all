package com.huawei.sharedrive.app.exception;

import org.springframework.http.HttpStatus;

public class NoSuchLinkException extends BaseRunException
{
    private static final long serialVersionUID = 3709497485424428329L;
    
    public NoSuchLinkException()
    {
        super(HttpStatus.NOT_FOUND, ErrorCode.NO_SUCH_LINK.getCode(), ErrorCode.NO_SUCH_LINK.getMessage());
    }
    
    public NoSuchLinkException(String excepMessage)
    {
        super(HttpStatus.NOT_FOUND, ErrorCode.NO_SUCH_LINK.getCode(), ErrorCode.NO_SUCH_LINK.getMessage(),
            excepMessage);
    }
    
    public NoSuchLinkException(String excepMessage, Throwable cause)
    {
        super(HttpStatus.NOT_FOUND, ErrorCode.NO_SUCH_LINK.getCode(), ErrorCode.NO_SUCH_LINK.getMessage(),
            excepMessage, cause);
    }
    
}
