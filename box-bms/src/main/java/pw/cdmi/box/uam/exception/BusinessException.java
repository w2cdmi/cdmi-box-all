package pw.cdmi.box.uam.exception;

public class BusinessException extends RuntimeException
{
    private static final long serialVersionUID = -7738340645030834742L;
    
    private int code;
    
    private String message;
    
    public BusinessException(BusinessErrorCode code, String message)
    {
        this(message);
        this.code = code.getCode();
        this.message = message;
    }
    
    public BusinessException(int code, String message)
    {
        this(message);
        this.code = code;
        this.message = message;
    }
    
    public BusinessException()
    {
        super();
        this.code = BusinessErrorCode.INTERNAL_SERVER_ERROR.getCode();
    }
    
    public BusinessException(String message)
    {
        super(message);
        this.code = BusinessErrorCode.INTERNAL_SERVER_ERROR.getCode();
    }
    
    public BusinessException(Throwable cause)
    {
        super(cause);
        this.code = BusinessErrorCode.INTERNAL_SERVER_ERROR.getCode();
    }
    
    public int getCode()
    {
        return code;
    }
    
    public void setCode(int code)
    {
        this.code = code;
    }
    
    @Override
    public String getMessage()
    {
        return message;
    }
    
    public void setMessage(String message)
    {
        this.message = message;
    }
}
