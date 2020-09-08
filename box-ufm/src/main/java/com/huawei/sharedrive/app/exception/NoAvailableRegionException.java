package com.huawei.sharedrive.app.exception;

import org.springframework.http.HttpStatus;

/**
 * 无可用的存储区域
 * @author c00287749
 *
 */
public class NoAvailableRegionException extends BaseRunException
{
    private static final long serialVersionUID = 7840037089773634072L;
    
    public NoAvailableRegionException()
    {
        super(HttpStatus.NOT_FOUND, ErrorCode.NO_AVAILABLE_REGION.getCode(), ErrorCode.NO_AVAILABLE_REGION.getMessage());
    }
    
    public NoAvailableRegionException(String excepMessage)
    {
        super(HttpStatus.NOT_FOUND, ErrorCode.NO_AVAILABLE_REGION.getCode(), ErrorCode.NO_AVAILABLE_REGION.getMessage(),
            excepMessage);
    }
    
}
