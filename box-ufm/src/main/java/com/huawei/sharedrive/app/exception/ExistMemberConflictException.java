package com.huawei.sharedrive.app.exception;

import org.springframework.http.HttpStatus;

public class ExistMemberConflictException extends BaseRunException
{
    private static final long serialVersionUID = -6421172310466967003L;
    
    public ExistMemberConflictException()
    {
        super(HttpStatus.CONFLICT, ErrorCode.EXIST_MEMBER_CONFLICT.getCode(),
            ErrorCode.EXIST_MEMBER_CONFLICT.getMessage());
    }
    
    public ExistMemberConflictException(String excepMessage)
    {
        super(HttpStatus.CONFLICT, ErrorCode.EXIST_MEMBER_CONFLICT.getCode(),
            ErrorCode.EXIST_MEMBER_CONFLICT.getMessage(), excepMessage);
    }
    
    public ExistMemberConflictException(Throwable e)
    {
        super(e, HttpStatus.CONFLICT, ErrorCode.EXIST_MEMBER_CONFLICT.getCode(),
            ErrorCode.EXIST_MEMBER_CONFLICT.getMessage());
    }
}
