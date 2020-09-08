package com.huawei.sharedrive.uam.exception;

import org.springframework.web.bind.MissingServletRequestParameterException;

public class ExceptionResponseEntity extends CommonResponseEntiy
{
    private String type;
    
    private static final String ERROR = "error";
    
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
    
    public ExceptionResponseEntity(String requestID, BaseRunNoStackException exception)
    {
        this(requestID);
        this.setCode(exception.getCode());
        this.setMessage(exception.getMsg());
        this.setType(ERROR);
    }
    
    public ExceptionResponseEntity(String requestID, MissingServletRequestParameterException exception)
    {
        this(requestID);
        this.setType(ERROR);
        this.setCode(ErrorCode.MissingParameter.getCode());
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
