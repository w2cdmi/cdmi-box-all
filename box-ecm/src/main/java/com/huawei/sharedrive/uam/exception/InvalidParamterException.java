package com.huawei.sharedrive.uam.exception;

import org.springframework.http.HttpStatus;

public class InvalidParamterException extends BaseRunException
{
    private static final long serialVersionUID = 3187889527609485428L;
    
    public InvalidParamterException()
    {
        super(HttpStatus.BAD_REQUEST, ErrorCode.INVALID_PARAMTER.getCode(),
            ErrorCode.INVALID_PARAMTER.getMessage());
    }
    
    public InvalidParamterException(String excepMessage)
    {
        super(HttpStatus.BAD_REQUEST, ErrorCode.INVALID_PARAMTER.getCode(),
            ErrorCode.INVALID_PARAMTER.getMessage(), excepMessage);
    }
    
    public InvalidParamterException(String msg, String excepMessage)
    {
        super(HttpStatus.BAD_REQUEST, ErrorCode.INVALID_PARAMTER.getCode(), msg, excepMessage);
    }
    
    public InvalidParamterException(Throwable e)
    {
        super(e, HttpStatus.BAD_REQUEST, ErrorCode.INVALID_PARAMTER.getCode(),
            ErrorCode.INVALID_PARAMTER.getMessage());
    }
    
}
