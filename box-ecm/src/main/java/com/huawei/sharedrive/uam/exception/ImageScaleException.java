package com.huawei.sharedrive.uam.exception;

import org.springframework.http.HttpStatus;

public class ImageScaleException extends BaseRunException
{
    
    private static final long serialVersionUID = -7025256617754421196L;
    
    public ImageScaleException()
    {
        super(HttpStatus.CONFLICT, ErrorCode.FILES_CONFLICT.getCode(), ErrorCode.FILES_CONFLICT.getMessage());
    }
    
    public ImageScaleException(Throwable e)
    {
        super(e, HttpStatus.CONFLICT, ErrorCode.FILES_CONFLICT.getCode(),
            ErrorCode.FILES_CONFLICT.getMessage());
    }
    
    public ImageScaleException(String excepMessage)
    {
        super(HttpStatus.CONFLICT, ErrorCode.FILES_CONFLICT.getCode(), ErrorCode.FILES_CONFLICT.getMessage(),
            excepMessage);
    }
    
    public ImageScaleException(String excepMessage, String code)
    {
        super(HttpStatus.FORBIDDEN, code, ErrorCode.INVALID_SCALE.getMessage(), excepMessage);
    }
}
