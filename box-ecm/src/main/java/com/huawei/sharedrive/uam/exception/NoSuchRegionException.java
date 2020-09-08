package com.huawei.sharedrive.uam.exception;

import org.springframework.http.HttpStatus;

public class NoSuchRegionException extends BaseRunException
{
    private static final long serialVersionUID = 3187889527609485428L;
    
    public NoSuchRegionException()
    {
        super(HttpStatus.NOT_FOUND, ErrorCode.NO_SUCH_REGION.getCode(), ErrorCode.NO_SUCH_REGION.getMessage());
    }
    
    public NoSuchRegionException(String excepMessage)
    {
        super(HttpStatus.NOT_FOUND, ErrorCode.NO_SUCH_REGION.getCode(),
            ErrorCode.NO_SUCH_REGION.getMessage(), excepMessage);
    }
}
