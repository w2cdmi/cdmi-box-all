package pw.cdmi.box.uam.exception;

public class RestException extends Exception
{
    private static final long serialVersionUID = 2908651329962096546L;
    
    private String code;
    
    private String message;
    
    private String requestID;
    
    private String type;
    
    public String getCode()
    {
        return code;
    }
    
    public String getMessage()
    {
        return message;
    }
    
    public String getRequestID()
    {
        return requestID;
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
    
    public void setRequestID(String requestID)
    {
        this.requestID = requestID;
    }
    
    public void setType(String type)
    {
        this.type = type;
    }
    
}
