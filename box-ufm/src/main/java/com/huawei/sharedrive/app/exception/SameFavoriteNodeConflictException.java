package com.huawei.sharedrive.app.exception;

import org.springframework.http.HttpStatus;

@SuppressWarnings("serial")
public class SameFavoriteNodeConflictException extends BaseRunException
{
    public SameFavoriteNodeConflictException()
    {
        super(HttpStatus.CONFLICT, ErrorCode.FAVORITE_CONFLICT.getCode(), ErrorCode.FAVORITE_CONFLICT.getMessage());
    }
    
    public SameFavoriteNodeConflictException(String excepMessage)
    {
        super(HttpStatus.CONFLICT, ErrorCode.FAVORITE_CONFLICT.getCode(), ErrorCode.FAVORITE_CONFLICT.getMessage(), excepMessage);
    }
    
    public SameFavoriteNodeConflictException(Throwable e)
    {
        super(e, HttpStatus.CONFLICT, ErrorCode.FAVORITE_CONFLICT.getCode(),
            ErrorCode.FAVORITE_CONFLICT.getMessage());
    }
    
}
