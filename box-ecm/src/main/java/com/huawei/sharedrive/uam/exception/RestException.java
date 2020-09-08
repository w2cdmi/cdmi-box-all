package com.huawei.sharedrive.uam.exception;

public class RestException extends Exception
{
    private static final long serialVersionUID = 2908651329962096546L;
    
    private String code;
    
    private String message;
    
    private String requestId;
    
    private String type;
    
    public String getCode()
    {
        return code;
    }
    
    public String getMessage()
    {
        return message;
    }
    
    public String getRequestId()
    {
        return requestId;
    }
    
    public String getType()
    {
        return type;
    }
    
    public void setCode(String code)
    {
        this.code = code;
    }
    
    public void setMessage(String message)
    {
        this.message = message;
    }
    
    public void setRequestId(String requestId)
    {
        this.requestId = requestId;
    }
    
    public void setType(String type)
    {
        this.type = type;
    }
    
}
