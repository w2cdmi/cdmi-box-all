package pw.cdmi.box.uam.exception;

import org.springframework.http.HttpStatus;

public class MethodNotAllowedException extends BaseRunException
{
    
    private static final long serialVersionUID = -7547729367933395721L;
    
    public MethodNotAllowedException()
    {
        super(HttpStatus.METHOD_NOT_ALLOWED, ErrorCode.METHOD_NOT_ALLOWED.getCode(),
            ErrorCode.METHOD_NOT_ALLOWED.getMessage());
    }
    
    public MethodNotAllowedException(String excepMessage)
    {
        super(HttpStatus.METHOD_NOT_ALLOWED, ErrorCode.METHOD_NOT_ALLOWED.getCode(),
            ErrorCode.METHOD_NOT_ALLOWED.getMessage(), excepMessage);
    }
    
    public MethodNotAllowedException(Throwable e)
    {
        super(e, HttpStatus.METHOD_NOT_ALLOWED, ErrorCode.METHOD_NOT_ALLOWED.getCode(),
            ErrorCode.METHOD_NOT_ALLOWED.getMessage());
    }
    
}
