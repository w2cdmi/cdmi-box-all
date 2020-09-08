package com.huawei.sharedrive.app.exception;

import org.springframework.http.HttpStatus;

public class LinkNotEffectiveException extends BaseRunException
{
    private static final long serialVersionUID = 7823255844316770860L;
    
    public LinkNotEffectiveException()
    {
        super(HttpStatus.NOT_FOUND, ErrorCode.LINK_NOT_EFFECTIVE.getCode(),
            ErrorCode.LINK_NOT_EFFECTIVE.getMessage());
    }
    
    public LinkNotEffectiveException(String excepMessage)
    {
        super(HttpStatus.NOT_FOUND, ErrorCode.LINK_NOT_EFFECTIVE.getCode(),
            ErrorCode.LINK_NOT_EFFECTIVE.getMessage(), excepMessage);
    }
    
}
