package com.huawei.sharedrive.app.exception;

import org.springframework.http.HttpStatus;

public class ExceedUserAvailabelSpaceException extends BaseRunException
{
    private static final long serialVersionUID = 1L;
    
    public ExceedUserAvailabelSpaceException()
    {
        super(HttpStatus.PRECONDITION_FAILED, ErrorCode.EXCEED_USER_AVAILABLE_SPACE.getCode(), ErrorCode.EXCEED_USER_AVAILABLE_SPACE.getMessage());
    }
    
    public ExceedUserAvailabelSpaceException(String excepMessage)
    {
        super(HttpStatus.PRECONDITION_FAILED, ErrorCode.EXCEED_USER_AVAILABLE_SPACE.getCode(), ErrorCode.EXCEED_USER_AVAILABLE_SPACE.getMessage(),
            excepMessage);
    }
    
}
