package com.huawei.sharedrive.app.plugins.preview.exception;

import org.springframework.http.HttpStatus;

import com.huawei.sharedrive.app.exception.BaseRunException;
import com.huawei.sharedrive.app.exception.ErrorCode;

public class FileInConvertException extends BaseRunException
{
    
    /**
     * 
     */
    private static final long serialVersionUID = 4672030644988203588L;
    
    public FileInConvertException()
    {
        super(HttpStatus.PRECONDITION_FAILED, ErrorCode.FILE_CONVERTING.getCode(),
            ErrorCode.FILE_CONVERTING.getMessage());
    }
    
    public FileInConvertException(String excepMessage)
    {
        super(HttpStatus.PRECONDITION_FAILED, ErrorCode.FILE_CONVERTING.getCode(),
            ErrorCode.FILE_CONVERTING.getMessage(), excepMessage);
    }
}
