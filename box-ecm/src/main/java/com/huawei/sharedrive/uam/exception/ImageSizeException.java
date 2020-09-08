package com.huawei.sharedrive.uam.exception;

import org.springframework.http.HttpStatus;

public class ImageSizeException extends BaseRunException
{
    
    private static final long serialVersionUID = -7025256617754421196L;
    
    public ImageSizeException()
    {
        super(HttpStatus.CONFLICT, ErrorCode.FILES_CONFLICT.getCode(), ErrorCode.FILES_CONFLICT.getMessage());
    }
    
    public ImageSizeException(Throwable e)
    {
        super(e, HttpStatus.CONFLICT, ErrorCode.FILES_CONFLICT.getCode(),
            ErrorCode.FILES_CONFLICT.getMessage());
    }
    
    public ImageSizeException(String excepMessage)
    {
        super(HttpStatus.CONFLICT, ErrorCode.FILES_CONFLICT.getCode(), ErrorCode.FILES_CONFLICT.getMessage(),
            excepMessage);
    }
    
    public ImageSizeException(String excepMessage, String code)
    {
        super(HttpStatus.FORBIDDEN, code, ErrorCode.INVALID_SIZE.getMessage(), excepMessage);
    }
}
