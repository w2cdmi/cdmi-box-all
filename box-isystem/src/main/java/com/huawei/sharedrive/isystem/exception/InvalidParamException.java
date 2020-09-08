package com.huawei.sharedrive.isystem.exception;

import org.springframework.http.HttpStatus;

public class InvalidParamException extends BaseRunException
{
    private static final long serialVersionUID = -7030998098122795455L;
    
    public InvalidParamException()
    {
        super(HttpStatus.BAD_REQUEST, ErrorCode.INVALID_PARAMTER.getCode(),
            ErrorCode.INVALID_PARAMTER.getMessage());
    }
    
    public InvalidParamException(String excepMessage)
    {
        super(HttpStatus.BAD_REQUEST, ErrorCode.INVALID_PARAMTER.getCode(),
            ErrorCode.INVALID_PARAMTER.getMessage(), excepMessage);
    }
    
    public InvalidParamException(String excepMessage, Throwable e)
    {
        super(HttpStatus.BAD_REQUEST, ErrorCode.INVALID_PARAMTER.getCode(),
            ErrorCode.INVALID_PARAMTER.getMessage(), excepMessage, e);
    }
    
}
