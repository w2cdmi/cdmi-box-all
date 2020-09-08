package com.huawei.sharedrive.uam.exception;

import org.springframework.http.HttpStatus;

public class NoSuchAnnouncementException extends BaseRunException
{
    private static final long serialVersionUID = 1826643366776562778L;
    
    public NoSuchAnnouncementException()
    {
        super(HttpStatus.NOT_FOUND, ErrorCode.NO_SUCH_ANNOUNCEMENT.getCode(),
            ErrorCode.NO_SUCH_ANNOUNCEMENT.getMessage());
    }
    
    public NoSuchAnnouncementException(Throwable e)
    {
        super(e, HttpStatus.NOT_FOUND, ErrorCode.NO_SUCH_ANNOUNCEMENT.getCode(),
            ErrorCode.NO_SUCH_ANNOUNCEMENT.getMessage());
    }
    
    public NoSuchAnnouncementException(String excepMessage)
    {
        super(HttpStatus.NOT_FOUND, ErrorCode.NO_SUCH_ANNOUNCEMENT.getCode(),
            ErrorCode.NO_SUCH_ANNOUNCEMENT.getMessage(), excepMessage);
    }
    
}
