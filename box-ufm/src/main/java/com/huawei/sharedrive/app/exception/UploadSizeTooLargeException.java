package com.huawei.sharedrive.app.exception;

import org.springframework.http.HttpStatus;

public class UploadSizeTooLargeException extends BaseRunException
{
    
    /**
     * 
     */
    private static final long serialVersionUID = -5760980673106668213L;
    
    public UploadSizeTooLargeException()
    {
        super(HttpStatus.BAD_REQUEST, ErrorCode.UPLOAD_SIZE_TOO_LARGE.getCode(),
            ErrorCode.UPLOAD_SIZE_TOO_LARGE.getMessage());
    }
}
