package com.huawei.sharedrive.app.exception;

import org.springframework.http.HttpStatus;

public class ExceedUserMaxNodeNumException extends BaseRunException
{
    private static final long serialVersionUID = 5499884556194277680L;

    public ExceedUserMaxNodeNumException()
    {
        super(HttpStatus.PRECONDITION_FAILED, ErrorCode.EXCEED_MAX_NODE_NUM.getCode(), ErrorCode.EXCEED_MAX_NODE_NUM.getMessage());
    }
    
    public ExceedUserMaxNodeNumException(String excepMessage)
    {
        super(HttpStatus.PRECONDITION_FAILED, ErrorCode.EXCEED_MAX_NODE_NUM.getCode(), ErrorCode.EXCEED_MAX_NODE_NUM.getMessage(),
            excepMessage);
    }
}
