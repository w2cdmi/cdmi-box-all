package com.huawei.sharedrive.uam.exception;

import org.springframework.http.HttpStatus;

public abstract class BaseRunException extends RuntimeException
{
    
    private static final long serialVersionUID = -1273277342163427903L;
    
    private HttpStatus httpcode;
    
    private String code;
    
    private String msg;
    
    public HttpStatus getHttpcode()
    {
        return httpcode;
    }
    
    public void setHttpcode(HttpStatus httpcode)
    {
        this.httpcode = httpcode;
    }
    
    public String getCode()
    {
        return code;
    }
    
    public void setCode(String code)
    {
        this.code = code;
    }
    
    public String getMsg()
    {
        return msg;
    }
    
    public void setMsg(String msg)
    {
        this.msg = msg;
    }
    
    public BaseRunException()
    {
        super();
    }
    
    public BaseRunException(HttpStatus httpcode, String code, String msg, String excepMessage)
    {
        super(excepMessage);
        this.httpcode = httpcode;
        this.code = code;
        this.msg = msg;
    }
    
    public BaseRunException(Throwable ex, HttpStatus httpcode, String code, String msg)
    {
        
        super(code, ex);
        this.httpcode = httpcode;
        this.code = code;
        this.msg = msg;
    }
    
    public BaseRunException(HttpStatus httpcode, String code, String msg)
    {
        
        super();
        this.httpcode = httpcode;
        this.code = code;
        this.msg = msg;
    }
    
    public BaseRunException(Throwable ex)
    {
        super(ex);
    }
    
    public BaseRunException(Throwable ex, HttpStatus httpcode)
    {
        super(ex);
        this.httpcode = httpcode;
    }
    
}
