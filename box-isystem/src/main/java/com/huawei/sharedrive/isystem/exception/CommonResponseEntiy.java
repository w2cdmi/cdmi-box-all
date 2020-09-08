package com.huawei.sharedrive.isystem.exception;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"code", "requestID", "message"})
public class CommonResponseEntiy
{
    
    private String code = "OK";
    
    private String requestID;
    
    private String message;
    
    private Object object;
    
    public CommonResponseEntiy(String requestID)
    {
        this.requestID = requestID;
    }
    
    public CommonResponseEntiy(Object object, String requestID)
    {
        this(requestID);
        this.object = object;
    }
    
    public String getCode()
    {
        return code;
    }
    
    public void setCode(String code)
    {
        this.code = code;
    }
    
    public String getRequestID()
    {
        return requestID;
    }
    
    public void setRequestID(String requestID)
    {
        this.requestID = requestID;
    }
    
    public String getMessage()
    {
        return message;
    }
    
    public void setMessage(String message)
    {
        this.message = message;
    }
    
    public Object getObject()
    {
        return object;
    }
    
    public void setObject(Object object)
    {
        this.object = object;
    }
    
}
