package com.huawei.sharedrive.app.exception;

import org.springframework.http.HttpStatus;

public class InvalidParamException extends BaseRunException
{
    private static final long serialVersionUID = -7030998098122795455L;
    
    public InvalidParamException()
    {
        super(HttpStatus.BAD_REQUEST, ErrorCode.INVALID_PARAMTER.getCode(),
            ErrorCode.INVALID_PARAMTER.getMessage());
    }
    
    public InvalidParamException(Throwable cause)
    {
        super(HttpStatus.BAD_REQUEST, ErrorCode.INVALID_PARAMTER.getCode(),
            ErrorCode.INVALID_PARAMTER.getMessage(), cause);
    }
    
    public InvalidParamException(String excepMessage)
    {
        super(HttpStatus.BAD_REQUEST, ErrorCode.INVALID_PARAMTER.getCode(),
            ErrorCode.INVALID_PARAMTER.getMessage(), excepMessage);
    }
    
    public InvalidParamException(String excepMessage, Throwable cause)
    {
        super(HttpStatus.BAD_REQUEST, ErrorCode.INVALID_PARAMTER.getCode(),
            ErrorCode.INVALID_PARAMTER.getMessage(), excepMessage, cause);
    }
    
}
