package com.huawei.sharedrive.uam.rest.test.util;

/**
 * REST 调用业务异常
 * 
 * @author l90003768
 *
 */
public class RestException extends Exception
{
    /**
     * 序列化编号
     */
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
