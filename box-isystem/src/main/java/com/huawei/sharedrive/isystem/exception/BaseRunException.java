package com.huawei.sharedrive.isystem.exception;

import org.springframework.http.HttpStatus;

public abstract class BaseRunException extends RuntimeException
{
    private static final long serialVersionUID = -1273277342163427903L;
    
    private HttpStatus httpcode;
    
    private String code;
    
    private String msg;
    
    public BaseRunException()
    {
        super();
    }
    
    public BaseRunException(HttpStatus httpcode, String code, String msg)
    {
        
        super();
        this.httpcode = httpcode;
        this.code = code;
        this.msg = msg;
    }
    
    public BaseRunException(HttpStatus httpcode, String code, String msg, String excepMessage)
    {
        super(excepMessage);
        this.httpcode = httpcode;
        this.code = code;
        this.msg = msg;
        
    }
    
    public BaseRunException(HttpStatus httpcode, String code, String msg, String excepMessage, Throwable ex)
    {
        super(excepMessage, ex);
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
    
    public BaseRunException(Throwable ex, HttpStatus httpcode, String code, String msg)
    {
        
        super(code, ex);
        this.httpcode = httpcode;
        this.code = code;
        this.msg = msg;
    }
    
    public String getCode()
    {
        return code;
    }
    
    public HttpStatus getHttpcode()
    {
        return httpcode;
    }
    
    public String getMsg()
    {
        return msg;
    }
    
    public void setCode(String code)
    {
        this.code = code;
    }
    
    public void setHttpcode(HttpStatus httpcode)
    {
        this.httpcode = httpcode;
    }
    
    public void setMsg(String msg)
    {
        this.msg = msg;
    }
    
}
