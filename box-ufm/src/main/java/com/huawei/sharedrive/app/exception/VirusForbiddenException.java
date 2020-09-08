package com.huawei.sharedrive.app.exception;

import org.springframework.http.HttpStatus;

public class VirusForbiddenException extends BaseRunException
{
    
    private static final long serialVersionUID = -1927601392039703199L;
    
    public VirusForbiddenException()
    {
        super(HttpStatus.FORBIDDEN, ErrorCode.VIRUS_FORBIDDEN.getCode(),
            ErrorCode.VIRUS_FORBIDDEN.getMessage());
    }
    
    public VirusForbiddenException(String excepMessage)
    {
        super(HttpStatus.FORBIDDEN, ErrorCode.VIRUS_FORBIDDEN.getCode(),
            ErrorCode.VIRUS_FORBIDDEN.getMessage(), excepMessage);
    }
    
}
