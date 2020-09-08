package com.huawei.sharedrive.app.exception;

import org.springframework.http.HttpStatus;

public class SecurityMatixException extends BaseRunException
{
    private static final long serialVersionUID = 4499219344277563337L;
    
    public SecurityMatixException()
    {
        super(HttpStatus.FORBIDDEN, ErrorCode.SECURITY_MATRIX_FORBIDDEN.getCode(),
            ErrorCode.SECURITY_MATRIX_FORBIDDEN.getMessage());
    }
    
    public SecurityMatixException(String excepMessage)
    {
        super(HttpStatus.FORBIDDEN, ErrorCode.SECURITY_MATRIX_FORBIDDEN.getCode(),
            ErrorCode.SECURITY_MATRIX_FORBIDDEN.getMessage(), excepMessage);
    }
    
    public SecurityMatixException(Throwable e)
    {
        super(e, HttpStatus.FORBIDDEN, ErrorCode.SECURITY_MATRIX_FORBIDDEN.getCode(),
            ErrorCode.SECURITY_MATRIX_FORBIDDEN.getMessage());
    }
    
}
