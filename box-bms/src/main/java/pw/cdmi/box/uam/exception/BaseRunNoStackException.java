package pw.cdmi.box.uam.exception;

import org.springframework.http.HttpStatus;

public abstract class BaseRunNoStackException extends RuntimeException
{
    private static final long serialVersionUID = 331980180741978226L;
    
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
    
    public BaseRunNoStackException()
    {
        super();
    }
    
    public BaseRunNoStackException(HttpStatus httpcode, String code, String msg, String excepMessage)
    {
        super(excepMessage);
        this.httpcode = httpcode;
        this.code = code;
        this.msg = msg;
    }
    
    public BaseRunNoStackException(Throwable ex, HttpStatus httpcode, String code, String msg)
    {
        
        super(code, ex);
        this.httpcode = httpcode;
        this.code = code;
        this.msg = msg;
    }
    
    public BaseRunNoStackException(HttpStatus httpcode, String code, String msg)
    {
        
        super();
        this.httpcode = httpcode;
        this.code = code;
        this.msg = msg;
    }
    
    public BaseRunNoStackException(Throwable ex)
    {
        super(ex);
    }
    
    public BaseRunNoStackException(Throwable ex, HttpStatus httpcode)
    {
        super(ex);
        this.httpcode = httpcode;
    }
    
}
