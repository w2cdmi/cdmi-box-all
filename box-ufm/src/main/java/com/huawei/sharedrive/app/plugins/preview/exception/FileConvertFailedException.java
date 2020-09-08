package com.huawei.sharedrive.app.plugins.preview.exception;

import org.springframework.http.HttpStatus;

import com.huawei.sharedrive.app.exception.BaseRunException;
import com.huawei.sharedrive.app.exception.ErrorCode;

public class FileConvertFailedException extends BaseRunException
{
    
    /**
     * 
     */
    private static final long serialVersionUID = 8761036540286613908L;
    
    public FileConvertFailedException()
    {
        super(HttpStatus.PRECONDITION_FAILED, ErrorCode.FILE_CONVERT_FAILED.getCode(),
            ErrorCode.FILE_CONVERT_FAILED.getMessage());
    }
    
    public FileConvertFailedException(String excepMessage)
    {
        super(HttpStatus.PRECONDITION_FAILED, ErrorCode.FILE_CONVERT_FAILED.getCode(),
            ErrorCode.FILE_CONVERT_FAILED.getMessage(), excepMessage);
    }
}
