package com.huawei.sharedrive.isystem.exception;

import org.springframework.http.HttpStatus;

public class NoSuchItemsException extends BaseRunException
{
    
    private static final long serialVersionUID = 2440763696066909667L;
    
    public NoSuchItemsException()
    {
        super(HttpStatus.NOT_FOUND, "unauthorized", ErrorCode.TOKENUNAUTHORIZED.toString());
    }
}
