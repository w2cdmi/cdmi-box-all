package pw.cdmi.box.uam.exception;

import org.springframework.http.HttpStatus;

public abstract class BaseRunTimeException extends RuntimeException
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
    
    public BaseRunTimeException()
    {
        super();
    }
    
    public BaseRunTimeException(Throwable ex, HttpStatus httpcode, String code, String msg)
    {
        
        super(code, ex);
        this.httpcode = httpcode;
        this.code = code;
        this.msg = msg;
    }
    
    public BaseRunTimeException(HttpStatus httpcode, String code, String msg)
    {
        
        super();
        this.httpcode = httpcode;
        this.code = code;
        this.msg = msg;
    }
    
    public BaseRunTimeException(Throwable ex)
    {
        super(ex);
    }
    
    public BaseRunTimeException(Throwable ex, HttpStatus httpcode)
    {
        super(ex);
        this.httpcode = httpcode;
    }
    
}
