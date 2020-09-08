package com.huawei.sharedrive.uam.exception;

import org.springframework.http.HttpStatus;

public class ExistSecurityRoleConflictException extends BaseRunException
{
    
    private static final long serialVersionUID = 1965940030381077499L;
    
    public ExistSecurityRoleConflictException()
    {
        super(HttpStatus.CONFLICT, ErrorCode.EXIST_SECURITY_ROLE_CONFLICT.getCode(),
            ErrorCode.EXIST_SECURITY_ROLE_CONFLICT.getMessage());
    }
    
}
