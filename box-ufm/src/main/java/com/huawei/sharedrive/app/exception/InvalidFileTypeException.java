package com.huawei.sharedrive.app.exception;

import org.springframework.http.HttpStatus;

public class InvalidFileTypeException extends BaseRunException
{
    private static final long serialVersionUID = -7030998098122795455L;
    
    public InvalidFileTypeException()
    {
        super(HttpStatus.BAD_REQUEST, ErrorCode.INVALID_FILE_TYPE.getCode(),
            ErrorCode.INVALID_FILE_TYPE.getMessage());
    }
    
    public InvalidFileTypeException(String excepMessage)
    {
        super(HttpStatus.BAD_REQUEST, ErrorCode.INVALID_FILE_TYPE.getCode(),
            ErrorCode.INVALID_FILE_TYPE.getMessage(), excepMessage);
    }
    
}
