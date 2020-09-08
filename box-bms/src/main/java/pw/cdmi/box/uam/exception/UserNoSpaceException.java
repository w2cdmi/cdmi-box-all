package pw.cdmi.box.uam.exception;

import org.springframework.http.HttpStatus;

public class UserNoSpaceException extends BaseRunException
{
    
    private static final long serialVersionUID = 7130752567111315147L;
    
    public UserNoSpaceException()
    {
        super(HttpStatus.BAD_REQUEST, "bad_request", ErrorCode.BAD_REQUEST.toString());
    }
}
