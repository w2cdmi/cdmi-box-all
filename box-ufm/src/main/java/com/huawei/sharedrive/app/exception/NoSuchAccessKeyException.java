package com.huawei.sharedrive.app.exception;

import org.springframework.http.HttpStatus;

public class NoSuchAccessKeyException extends BaseRunException
{
    private static final long serialVersionUID = -5766903180102012235L;

    public NoSuchAccessKeyException()
    {
        super(HttpStatus.NOT_FOUND, ErrorCode.NO_SUCH_ACCESSKEY.getCode(), ErrorCode.NO_SUCH_ACCESSKEY.getMessage());
    }
    
    public NoSuchAccessKeyException(String excepMessage)
    {
        super(HttpStatus.NOT_FOUND, ErrorCode.NO_SUCH_ACCESSKEY.getCode(), ErrorCode.NO_SUCH_ACCESSKEY.getMessage(),
            excepMessage);
    }
}
