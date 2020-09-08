package pw.cdmi.box.uam.exception;

import org.springframework.http.HttpStatus;

public class NoFoundRquestException extends BaseRunException
{
    
    private static final long serialVersionUID = -1025954708195033303L;
    
    public NoFoundRquestException()
    {
        super(HttpStatus.BAD_REQUEST, "bad_request", ErrorCode.BAD_REQUEST.toString());
    }
    
}
