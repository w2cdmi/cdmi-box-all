package pw.cdmi.box.uam.exception;

import org.springframework.http.HttpStatus;

public class NoSuchUserException extends BaseRunException
{
    private static final long serialVersionUID = 7840037089773634072L;
    
    public NoSuchUserException()
    {
        super(HttpStatus.NOT_FOUND, ErrorCode.NO_SUCH_USER.getCode(), ErrorCode.NO_SUCH_USER.getMessage());
    }
    
    public NoSuchUserException(String msg)
    {
        super(HttpStatus.NOT_FOUND, ErrorCode.NO_SUCH_USER.getCode(), ErrorCode.NO_SUCH_USER.getMessage(),
            msg);
    }
    
}
