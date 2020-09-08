package com.huawei.sharedrive.app.exception;

import org.springframework.http.HttpStatus;

public class ExceedUserMaxVersionNumException extends BaseRunException
{
    private static final long serialVersionUID = -5254133500262392293L;

    public ExceedUserMaxVersionNumException()
    {
        super(HttpStatus.PRECONDITION_FAILED, ErrorCode.EXCEED_MAX_VERSION_NUM.getCode(), ErrorCode.EXCEED_MAX_VERSION_NUM.getMessage());
    }
    
    public ExceedUserMaxVersionNumException(String excepMessage)
    {
        super(HttpStatus.PRECONDITION_FAILED, ErrorCode.EXCEED_MAX_VERSION_NUM.getCode(), ErrorCode.EXCEED_MAX_VERSION_NUM.getMessage(),
            excepMessage);
    }
}