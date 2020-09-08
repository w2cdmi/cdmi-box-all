package com.huawei.sharedrive.app.plugins.preview.exception;

import org.springframework.http.HttpStatus;

import com.huawei.sharedrive.app.exception.BaseRunException;
import com.huawei.sharedrive.app.exception.ErrorCode;

public class FileConvertNotSupportException extends BaseRunException
{

    /**
     * 
     */
    private static final long serialVersionUID = 7739188194407840358L;
    
    public FileConvertNotSupportException()
    {
        super(HttpStatus.PRECONDITION_FAILED, ErrorCode.FILE_CONVERT_NOT_SUPPORT.getCode(),
            ErrorCode.FILE_CONVERT_NOT_SUPPORT.getMessage());
    }
    
    
    public FileConvertNotSupportException(String excepMessage)
    {
        super(HttpStatus.PRECONDITION_FAILED, ErrorCode.FILE_CONVERT_NOT_SUPPORT.getCode(),
            ErrorCode.FILE_CONVERT_NOT_SUPPORT.getMessage(), excepMessage);
    }
    
    public FileConvertNotSupportException(String excepMessage, Exception e)
    {
        super(HttpStatus.PRECONDITION_FAILED, ErrorCode.FILE_CONVERT_NOT_SUPPORT.getCode(),
            ErrorCode.FILE_CONVERT_NOT_SUPPORT.getMessage(), excepMessage, e);
    }
}
