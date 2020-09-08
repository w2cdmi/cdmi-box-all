package com.huawei.sharedrive.app.exception;

import org.springframework.http.HttpStatus;

public class FileScanningException extends BaseRunException
{
    
    private static final long serialVersionUID = -2890714980772867133L;
    
    public FileScanningException()
    {
        super(HttpStatus.PRECONDITION_FAILED, ErrorCode.FILE_SCANNING.getCode(),
            ErrorCode.FILE_SCANNING.getMessage());
    }
    
    public FileScanningException(String excepMessage)
    {
        super(HttpStatus.PRECONDITION_FAILED, ErrorCode.FILE_SCANNING.getCode(),
            ErrorCode.FILE_SCANNING.getMessage(), excepMessage);
    }
    
}
