package pw.cdmi.file.engine.core.web;

import org.springframework.http.HttpStatus;

public class BaseRunException extends RuntimeException
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
    
    public BaseRunException(Throwable ex, HttpStatus httpcode, String code, String msg)
    {
        
        super(code, ex);
        this.httpcode = httpcode;
        this.code = code;
        this.msg = msg;
        this.printStackTrace();
    }
    
    public BaseRunException(HttpStatus httpcode, String code, String msg)
    {
        
        super();
        this.httpcode = httpcode;
        this.code = code;
        this.msg = msg;
        this.printStackTrace();
    }
    
    public BaseRunException(Throwable ex)
    {
        super(ex);
        this.printStackTrace();
    }
    
    public BaseRunException(Throwable ex, HttpStatus httpcode)
    {
        super(ex);
        this.httpcode = httpcode;
        this.printStackTrace();
    }
    
}
