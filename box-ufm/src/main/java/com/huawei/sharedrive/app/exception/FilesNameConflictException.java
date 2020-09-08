package com.huawei.sharedrive.app.exception;

import org.springframework.http.HttpStatus;

public class FilesNameConflictException extends BaseRunException
{
    
    private static final long serialVersionUID = -7025256617754421196L;
    
    public FilesNameConflictException()
    {
        super(HttpStatus.CONFLICT, ErrorCode.FILES_CONFLICT.getCode(), ErrorCode.FILES_CONFLICT.getMessage());
    }
    
    public FilesNameConflictException(Throwable e)
    {
        super(e, HttpStatus.CONFLICT, ErrorCode.FILES_CONFLICT.getCode(),
            ErrorCode.FILES_CONFLICT.getMessage());
    }
    
    public FilesNameConflictException(String excepMessage)
    {
        super(HttpStatus.CONFLICT, ErrorCode.FILES_CONFLICT.getCode(), ErrorCode.FILES_CONFLICT.getMessage(),
            excepMessage);
    }
}
