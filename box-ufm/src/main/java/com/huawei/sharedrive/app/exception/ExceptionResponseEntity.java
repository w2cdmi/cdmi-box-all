package com.huawei.sharedrive.app.exception;

import org.springframework.web.bind.MissingServletRequestParameterException;

public class ExceptionResponseEntity extends CommonResponseEntity
{
    private static final String ERROR = "error";
    private String type;
    
    public ExceptionResponseEntity(String requestID)
    {
        super(requestID);
        this.setType(ERROR);
    }
    
    public ExceptionResponseEntity(String requestID, BaseRunException exception)
    {
        this(requestID);
        this.setCode(exception.getCode());
        this.setMessage(exception.getMsg());
        this.setType(ERROR);
    }
    
    public ExceptionResponseEntity(String requestID, MissingServletRequestParameterException exception)
    {
        this(requestID);
        this.setCode(ErrorCode.MISSING_PARAMETER.getCode());
        this.setType(ERROR);
        this.setMessage(exception.getMessage() + "name:" + exception.getParameterName() + "type:"
            + exception.getParameterType());
    }

    public String getType()
    {
        return type;
    }

    public void setType(String type)
    {
        this.type = type;
    }
    
}
