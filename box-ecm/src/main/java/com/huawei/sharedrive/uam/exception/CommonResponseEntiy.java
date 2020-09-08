package com.huawei.sharedrive.uam.exception;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"code", "requestID", "message"})
public class CommonResponseEntiy
{
    
    private String code = "OK";
    
    private String requestId;
    
    private String message;
    
    private Object object;
    
    public CommonResponseEntiy(String requestId)
    {
        this.requestId = requestId;
    }
    
    public CommonResponseEntiy(Object object, String requestId)
    {
        this(requestId);
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
    
    public String getRequestId()
    {
        return requestId;
    }
    
    public void setRequestId(String requestId)
    {
        this.requestId = requestId;
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
