package com.huawei.sharedrive.uam.exception;

import org.springframework.http.HttpStatus;

public class ExceedMaxEnterpriseNumException extends BaseRunException
{
    private static final long serialVersionUID = 8710444229160134512L;
    
    public ExceedMaxEnterpriseNumException()
    {
        super(HttpStatus.PRECONDITION_FAILED, ErrorCode.EXCEED_MAX_ENTERPRISE_NUM.getCode(),
            ErrorCode.EXCEED_MAX_ENTERPRISE_NUM.getMessage());
    }
    
    public ExceedMaxEnterpriseNumException(String excepMessage)
    {
        super(HttpStatus.PRECONDITION_FAILED, ErrorCode.EXCEED_MAX_ENTERPRISE_NUM.getCode(),
            ErrorCode.EXCEED_MAX_ENTERPRISE_NUM.getMessage(), excepMessage);
    }
}
