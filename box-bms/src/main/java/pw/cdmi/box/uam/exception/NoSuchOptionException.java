package pw.cdmi.box.uam.exception;

import org.springframework.http.HttpStatus;

public class NoSuchOptionException extends BaseRunException
{
    
    private static final long serialVersionUID = -7193477005151354217L;
    
    public NoSuchOptionException()
    {
        super(HttpStatus.NOT_FOUND, ErrorCode.NO_SUCH_OPTION.getCode(), ErrorCode.NO_SUCH_OPTION.getMessage());
    }
    
    public NoSuchOptionException(String excepMessage)
    {
        super(HttpStatus.NOT_FOUND, ErrorCode.NO_SUCH_OPTION.getCode(),
            ErrorCode.NO_SUCH_OPTION.getMessage(), excepMessage);
    }
    
    public NoSuchOptionException(Throwable e)
    {
        super(e, HttpStatus.NOT_FOUND, ErrorCode.NO_SUCH_OPTION.getCode(),
            ErrorCode.NO_SUCH_OPTION.getMessage());
    }
    
}
