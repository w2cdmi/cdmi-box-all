package com.huawei.sharedrive.uam.exception;

import org.springframework.http.HttpStatus;

public class NoSuchItemException extends BaseRunException
{
    
    private static final long serialVersionUID = -1662211958750936293L;
    
    public NoSuchItemException()
    {
        super(HttpStatus.NOT_FOUND, ErrorCode.NO_SUCH_ITEM.getCode(), ErrorCode.NO_SUCH_ITEM.getMessage());
    }
    
    public NoSuchItemException(String msg)
    {
        super(HttpStatus.NOT_FOUND, ErrorCode.NO_SUCH_ITEM.getCode(),
            ErrorCode.NO_SUCH_ITEM.getMessage(), msg);
    }
    
}
