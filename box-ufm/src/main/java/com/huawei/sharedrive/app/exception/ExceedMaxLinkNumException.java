package com.huawei.sharedrive.app.exception;

import org.springframework.http.HttpStatus;

public class ExceedMaxLinkNumException extends BaseRunException
{
    private static final long serialVersionUID = 8710444229160134512L;

    public ExceedMaxLinkNumException()
    {
        super(HttpStatus.PRECONDITION_FAILED, ErrorCode.EXCEED_MAX_LINK_NUM.getCode(), ErrorCode.EXCEED_MAX_LINK_NUM.getMessage());
    }
    
    public ExceedMaxLinkNumException(String excepMessage)
    {
        super(HttpStatus.PRECONDITION_FAILED, ErrorCode.EXCEED_MAX_LINK_NUM.getCode(), ErrorCode.EXCEED_MAX_LINK_NUM.getMessage(),
            excepMessage);
    }
}
