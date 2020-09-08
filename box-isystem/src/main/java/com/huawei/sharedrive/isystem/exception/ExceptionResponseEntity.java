package com.huawei.sharedrive.isystem.exception;

import org.springframework.web.bind.MissingServletRequestParameterException;

public class ExceptionResponseEntity extends CommonResponseEntiy
{
    
    public ExceptionResponseEntity(String requestID)
    {
        super(requestID);
        this.setCode("Error");
    }
    
    public ExceptionResponseEntity(String requestID, BaseRunException exception)
    {
        this(requestID);
        this.setCode(exception.getCode());
        this.setMessage(exception.getMsg());
    }
    
    public ExceptionResponseEntity(String requestID, MissingServletRequestParameterException exception)
    {
        this(requestID);
        this.setCode(ErrorCode.MissingParameter.getCode());
        this.setMessage(exception.getMessage() + "name:" + exception.getParameterName() + "type:"
            + exception.getParameterType());
    }
    
}
