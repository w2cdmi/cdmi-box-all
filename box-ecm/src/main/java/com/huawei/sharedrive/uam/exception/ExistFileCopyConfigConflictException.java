package com.huawei.sharedrive.uam.exception;

import org.springframework.http.HttpStatus;

public class ExistFileCopyConfigConflictException extends BaseRunException
{
    
    private static final long serialVersionUID = -2328352607182037103L;
    
    public ExistFileCopyConfigConflictException()
    {
        super(HttpStatus.CONFLICT, ErrorCode.EXIST_FILECOPY_CONFLICT.getCode(),
            ErrorCode.EXIST_FILECOPY_CONFLICT.getMessage());
    }
    
    public ExistFileCopyConfigConflictException(String excepMessage)
    {
        super(HttpStatus.CONFLICT, ErrorCode.EXIST_FILECOPY_CONFLICT.getCode(),
            ErrorCode.EXIST_FILECOPY_CONFLICT.getMessage(), excepMessage);
    }
}
