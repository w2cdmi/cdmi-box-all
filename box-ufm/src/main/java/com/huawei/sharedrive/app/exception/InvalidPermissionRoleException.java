package com.huawei.sharedrive.app.exception;

import org.springframework.http.HttpStatus;

public class InvalidPermissionRoleException extends BaseRunException
{
    private static final long serialVersionUID = 4401447521221659769L;
    
    public InvalidPermissionRoleException()
    {
        super(HttpStatus.BAD_REQUEST, ErrorCode.INVALID_RESOURCE_ROLE.getCode(),
            ErrorCode.INVALID_RESOURCE_ROLE.getMessage());
    }
    
    public InvalidPermissionRoleException(String excepMessage)
    {
        super(HttpStatus.BAD_REQUEST, ErrorCode.INVALID_RESOURCE_ROLE.getCode(),
            ErrorCode.INVALID_RESOURCE_ROLE.getMessage(), excepMessage);
    }
    
    public InvalidPermissionRoleException(Throwable e)
    {
        super(e, HttpStatus.BAD_REQUEST, ErrorCode.INVALID_RESOURCE_ROLE.getCode(),
            ErrorCode.INVALID_RESOURCE_ROLE.getMessage());
    }
}
