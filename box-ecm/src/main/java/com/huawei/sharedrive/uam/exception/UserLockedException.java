package com.huawei.sharedrive.uam.exception;

import org.springframework.http.HttpStatus;

public class UserLockedException extends BaseRunException
{
    private static final long serialVersionUID = -2147093566523637264L;
    
    public UserLockedException()
    {
        super();
    }
    
    public UserLockedException(String errMsg)
    {
        super(HttpStatus.FORBIDDEN, ErrorCode.USERLOCKED.getCode(), ErrorCode.USERLOCKED.getMessage(), errMsg);
    }
}
