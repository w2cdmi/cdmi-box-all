package com.huawei.sharedrive.app.exception;

import org.springframework.http.HttpStatus;

public class UnmatchedUploadUrlException extends BaseRunException
{
    private static final long serialVersionUID = 1826643366776562778L;
    
    public UnmatchedUploadUrlException()
    {
        super(HttpStatus.BAD_REQUEST, ErrorCode.UNMATCHED_UPLOADURL.getCode(), ErrorCode.UNMATCHED_UPLOADURL.getMessage());
    }
    
    public UnmatchedUploadUrlException(String excepMessage)
    {
        super(HttpStatus.BAD_REQUEST, ErrorCode.UNMATCHED_UPLOADURL.getCode(), ErrorCode.UNMATCHED_UPLOADURL.getMessage(),
            excepMessage);
    }
    
    public UnmatchedUploadUrlException(Throwable e)
    {
        super(e, HttpStatus.BAD_REQUEST, ErrorCode.UNMATCHED_UPLOADURL.getCode(), ErrorCode.UNMATCHED_UPLOADURL.getMessage());
    }
    
}
