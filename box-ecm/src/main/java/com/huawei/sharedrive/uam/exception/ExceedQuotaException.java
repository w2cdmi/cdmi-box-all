package com.huawei.sharedrive.uam.exception;

import org.springframework.http.HttpStatus;


public class ExceedQuotaException extends BaseRunException
{
    private static final long serialVersionUID = 1L;
    
    public ExceedQuotaException()
    {
        super(HttpStatus.PRECONDITION_FAILED, ErrorCode.EXCEED_QUOTA.getCode(),
            ErrorCode.EXCEED_QUOTA.getMessage());
    }
    
    public ExceedQuotaException(String msg)
    {
        super(HttpStatus.PRECONDITION_FAILED, ErrorCode.EXCEED_QUOTA.getCode(),
            ErrorCode.EXCEED_QUOTA.getMessage(), msg);
    }
}
