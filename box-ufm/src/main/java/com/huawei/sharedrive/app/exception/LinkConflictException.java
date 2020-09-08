package com.huawei.sharedrive.app.exception;

import org.springframework.http.HttpStatus;

public class LinkConflictException extends BaseRunException
{
    private static final long serialVersionUID = -204678251463991865L;
    
    public LinkConflictException()
    {
        super(HttpStatus.CONFLICT, ErrorCode.LINK_CONFLICT.getCode(), ErrorCode.LINK_CONFLICT.getMessage());
    }
    
    public LinkConflictException(Throwable e)
    {
        super(e, HttpStatus.CONFLICT, ErrorCode.LINK_CONFLICT.getCode(), ErrorCode.LINK_CONFLICT.getMessage());
    }
    
    public LinkConflictException(String excepMessage)
    {
        super(HttpStatus.CONFLICT, ErrorCode.LINK_CONFLICT.getCode(), ErrorCode.LINK_CONFLICT.getMessage(),
            excepMessage);
    }
}
