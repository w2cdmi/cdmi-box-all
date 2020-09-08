package pw.cdmi.box.disk.httpclient.exception;

public class ClientException extends RuntimeException
{
    private static final long serialVersionUID = -1273277342163495270L;
    
    private int statusCode;
    
    private String code;
    
    public ClientException(int statusCode)
    {
        super();
        this.statusCode = statusCode;
    }
    
    public ClientException(int statusCode, Throwable cause)
    {
        super(cause);
        this.statusCode = statusCode;
    }
    
    public ClientException(int statusCode, String message)
    {
        super(message);
        this.statusCode = statusCode;
    }
    
    public ClientException(String code, String message)
    {
        super(message);
        this.code = code;
    }
    
    public ClientException(int statusCode, String code, String message)
    {
        super(message);
        this.statusCode = statusCode;
        this.code = code;
    }
    
    public ClientException(String message)
    {
        super(message);
    }
    
    public ClientException(Throwable t)
    {
        super(t);
    }
    
    public int getStatusCode()
    {
        return statusCode;
    }
    
    public void setStatusCode(int statusCode)
    {
        this.statusCode = statusCode;
    }
    
    public String getCode()
    {
        return code;
    }
    
    public void setCode(String code)
    {
        this.code = code;
    }
}
