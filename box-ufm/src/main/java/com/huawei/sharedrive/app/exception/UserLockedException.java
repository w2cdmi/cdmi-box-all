package com.huawei.sharedrive.app.exception;

import org.springframework.http.HttpStatus;

/**
 * 终端禁用
 * 
 * @author h90005572
 * 
 */
public class UserLockedException extends BaseRunException
{
    private static final long serialVersionUID = -2147093566523637264L;
    
    public UserLockedException()
    {
        super(HttpStatus.FORBIDDEN, ErrorCode.USERLOCKED.getCode(), ErrorCode.USERLOCKED.getMessage());
    }
    
    public UserLockedException(String excepMessage)
    {
        super(HttpStatus.FORBIDDEN, ErrorCode.USERLOCKED.getCode(), ErrorCode.USERLOCKED.getMessage(),excepMessage);
    }
    
}
