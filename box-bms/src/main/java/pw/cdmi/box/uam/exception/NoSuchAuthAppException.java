package pw.cdmi.box.uam.exception;

import org.springframework.http.HttpStatus;

public class NoSuchAuthAppException extends BaseRunException
{
    private static final long serialVersionUID = 1826643366776562778L;
    
    public NoSuchAuthAppException()
    {
        super(HttpStatus.NOT_FOUND, ErrorCode.NO_SUCH_ANNOUNCEMENT.getCode(),
            ErrorCode.NO_SUCH_ANNOUNCEMENT.getMessage());
    }
    
    public NoSuchAuthAppException(Throwable e)
    {
        super(e, HttpStatus.NOT_FOUND, ErrorCode.NO_SUCH_ANNOUNCEMENT.getCode(),
            ErrorCode.NO_SUCH_ANNOUNCEMENT.getMessage());
    }
    
    public NoSuchAuthAppException(String excepMessage)
    {
        super(HttpStatus.NOT_FOUND, ErrorCode.NO_SUCH_ANNOUNCEMENT.getCode(),
            ErrorCode.NO_SUCH_ANNOUNCEMENT.getMessage(), excepMessage);
    }
    
}
