package com.huawei.sharedrive.app.exception;

import org.springframework.http.HttpStatus;

public class NoSuchGroupException extends BaseRunException
{
    private static final long serialVersionUID = -5766903180102012235L;

    public NoSuchGroupException()
    {
        super(HttpStatus.NOT_FOUND, ErrorCode.NO_SUCH_GROUP.getCode(), ErrorCode.NO_SUCH_GROUP.getMessage());
    }
    
    public NoSuchGroupException(String excepMessage)
    {
        super(HttpStatus.NOT_FOUND, ErrorCode.NO_SUCH_GROUP.getCode(), ErrorCode.NO_SUCH_GROUP.getMessage(),
            excepMessage);
    }
}
