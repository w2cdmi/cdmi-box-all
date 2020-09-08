package com.huawei.sharedrive.uam.exception;

import org.springframework.http.HttpStatus;

public class ImageInvalidException extends BaseRunException
{
    
    private static final long serialVersionUID = -7025256617754421196L;
    
    public ImageInvalidException()
    {
        super(HttpStatus.CONFLICT, ErrorCode.FILES_CONFLICT.getCode(), ErrorCode.FILES_CONFLICT.getMessage());
    }
    
    public ImageInvalidException(Throwable e)
    {
        super(e, HttpStatus.CONFLICT, ErrorCode.FILES_CONFLICT.getCode(),
            ErrorCode.FILES_CONFLICT.getMessage());
    }
    
    public ImageInvalidException(String excepMessage)
    {
        super(HttpStatus.CONFLICT, ErrorCode.FILES_CONFLICT.getCode(), ErrorCode.FILES_CONFLICT.getMessage(),
            excepMessage);
    }
    
    public ImageInvalidException(String excepMessage, String code)
    {
        super(HttpStatus.FORBIDDEN, code, ErrorCode.INVALID_IMAGE.getMessage(), excepMessage);
    }
}
