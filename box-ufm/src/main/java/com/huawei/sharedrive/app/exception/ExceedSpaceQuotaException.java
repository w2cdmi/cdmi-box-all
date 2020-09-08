package com.huawei.sharedrive.app.exception;

import org.springframework.http.HttpStatus;

public class ExceedSpaceQuotaException extends BaseRunException
{
    private static final long serialVersionUID = 1826643366776562778L;
    
    public ExceedSpaceQuotaException()
    {
        super(HttpStatus.PRECONDITION_FAILED, ErrorCode.EXCEED_SPACE_QUOTA.getCode(),
            ErrorCode.EXCEED_SPACE_QUOTA.getMessage());
    }
    
    public ExceedSpaceQuotaException(String excepMessage)
    {
        super(HttpStatus.PRECONDITION_FAILED, ErrorCode.EXCEED_SPACE_QUOTA.getCode(),
            ErrorCode.EXCEED_SPACE_QUOTA.getMessage(), excepMessage);
    }
    
    public ExceedSpaceQuotaException(Throwable e)
    {
        super(e, HttpStatus.PRECONDITION_FAILED, ErrorCode.EXCEED_SPACE_QUOTA.getCode(),
            ErrorCode.EXCEED_SPACE_QUOTA.getMessage());
    }
    
}
