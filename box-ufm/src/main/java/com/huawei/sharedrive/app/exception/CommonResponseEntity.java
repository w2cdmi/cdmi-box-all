package com.huawei.sharedrive.app.exception;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"code", "requestID", "message"})
public class CommonResponseEntity
{
    
    private String code = "OK";
    
    private String message;
    
    private Object object;
    
    private String requestId;
    
    public CommonResponseEntity(Object object, String requestID)
    {
        this(requestID);
        this.object = object;
    }
    
    public CommonResponseEntity(String requestId)
    {
        this.requestId = requestId;
    }
    
    public String getCode()
    {
        return code;
    }
    
    public String getMessage()
    {
        return message;
    }
    
    public Object getObject()
    {
        return object;
    }
    
    public String getRequestId()
    {
        return requestId;
    }
    
    public void setCode(String code)
    {
        this.code = code;
    }
    
    public void setMessage(String message)
    {
        this.message = message;
    }
    
    public void setObject(Object object)
    {
        this.object = object;
    }
    
    public void setRequestId(String requestId)
    {
        this.requestId = requestId;
    }
    
}
