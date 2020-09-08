package com.huawei.sharedrive.app.exception;

import org.springframework.http.HttpStatus;

public class ExceedEnterpriseAvailabelSpaceException extends BaseRunException
{
    private static final long serialVersionUID = 1L;
    
    public ExceedEnterpriseAvailabelSpaceException()
    {
        super(HttpStatus.PRECONDITION_FAILED, ErrorCode.EXCEED_ENTERPRISE_AVAILABLE_SPACE.getCode(), ErrorCode.EXCEED_ENTERPRISE_AVAILABLE_SPACE.getMessage());
    }
    
    public ExceedEnterpriseAvailabelSpaceException(String excepMessage)
    {
        super(HttpStatus.PRECONDITION_FAILED, ErrorCode.EXCEED_ENTERPRISE_AVAILABLE_SPACE.getCode(), ErrorCode.EXCEED_ENTERPRISE_AVAILABLE_SPACE.getMessage(),
            excepMessage);
    }
    
}
