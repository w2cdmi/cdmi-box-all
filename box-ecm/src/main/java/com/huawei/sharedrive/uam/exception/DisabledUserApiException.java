package com.huawei.sharedrive.uam.exception;

import org.springframework.http.HttpStatus;

public class DisabledUserApiException extends BaseRunException
{
    private static final long serialVersionUID = 5608620607174703615L;
    
    public DisabledUserApiException()
    {
        super(HttpStatus.FORBIDDEN, ErrorCode.USER_DISABLED.getCode(), ErrorCode.USER_DISABLED.getMessage());
    }
}
