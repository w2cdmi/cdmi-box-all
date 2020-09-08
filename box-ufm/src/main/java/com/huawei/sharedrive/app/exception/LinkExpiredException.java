package com.huawei.sharedrive.app.exception;

import org.springframework.http.HttpStatus;

public class LinkExpiredException extends BaseRunException
{
    private static final long serialVersionUID = 5090094219615181013L;
    
    public LinkExpiredException()
    {
        super(HttpStatus.NOT_FOUND, ErrorCode.LINK_EXPIRED.getCode(), ErrorCode.LINK_EXPIRED.getMessage());
    }
    
    public LinkExpiredException(String excepMessage)
    {
        super(HttpStatus.NOT_FOUND, ErrorCode.LINK_EXPIRED.getCode(), ErrorCode.LINK_EXPIRED.getMessage(),
            excepMessage);
    }
}
