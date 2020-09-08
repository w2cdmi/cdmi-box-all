package com.huawei.sharedrive.uam.httpclient.rest.response;

import java.io.Serializable;

public class INodeResponse implements Serializable
{
    private static final long serialVersionUID = 5083596932899148442L;
    
    private String code;
    
    private String message;
    
    private String requestId;
    
    private String id;
    
    private byte type;
    
    private String name;
    
    public String getId()
    {
        return id;
    }
    
    public void setId(String id)
    {
        this.id = id;
    }
    
    public byte getType()
    {
        return type;
    }
    
    public void setType(byte type)
    {
        this.type = type;
    }
    
    public String getName()
    {
        return name;
    }
    
    public void setName(String name)
    {
        this.name = name;
    }
    
    public String getCode()
    {
        return code;
    }
    
    public void setCode(String code)
    {
        this.code = code;
    }
    
    public String getMessage()
    {
        return message;
    }
    
    public void setMessage(String message)
    {
        this.message = message;
    }
    
    public String getRequestId()
    {
        return requestId;
    }
    
    public void setRequestId(String requestId)
    {
        this.requestId = requestId;
    }
}
