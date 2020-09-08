package com.huawei.sharedrive.app.exception;

import org.springframework.http.HttpStatus;

public class ExistACLConflictException extends BaseRunException
{
    private static final long serialVersionUID = -8599882166626927071L;
    
    public ExistACLConflictException()
    {
        super(HttpStatus.CONFLICT, ErrorCode.ACL_CONFLICT.getCode(), ErrorCode.ACL_CONFLICT.getMessage());
    }
    
    public ExistACLConflictException(String excepMessage)
    {
        super(HttpStatus.CONFLICT, ErrorCode.ACL_CONFLICT.getCode(), ErrorCode.ACL_CONFLICT.getMessage(),
            excepMessage);
    }
    
    public ExistACLConflictException(Throwable e)
    {
        super(e, HttpStatus.CONFLICT, ErrorCode.ACL_CONFLICT.getCode(), ErrorCode.ACL_CONFLICT.getMessage());
    }
}
