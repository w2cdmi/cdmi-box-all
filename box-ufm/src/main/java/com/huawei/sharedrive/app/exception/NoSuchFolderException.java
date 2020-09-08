package com.huawei.sharedrive.app.exception;

import org.springframework.http.HttpStatus;

public class NoSuchFolderException extends BaseRunException
{
    private static final long serialVersionUID = 1826643366776562778L;
    
    public NoSuchFolderException()
    {
        super(HttpStatus.NOT_FOUND, ErrorCode.NO_SUCH_FOLDER.getCode(),
            ErrorCode.NO_SUCH_FOLDER.getMessage());
    }
    
    public NoSuchFolderException(Throwable e)
    {
        super(e, HttpStatus.NOT_FOUND, ErrorCode.NO_SUCH_FOLDER.getCode(),
            ErrorCode.NO_SUCH_FOLDER.getMessage());
    }
    
    public NoSuchFolderException(String excepMessage)
    {
        super(HttpStatus.NOT_FOUND, ErrorCode.NO_SUCH_FOLDER.getCode(),
            ErrorCode.NO_SUCH_FOLDER.getMessage(),excepMessage);
    }
    
}
