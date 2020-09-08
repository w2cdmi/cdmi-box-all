package pw.cdmi.box.uam.exception;

import org.springframework.http.HttpStatus;

public class SecurityMartixException extends BaseRunException
{
    
    private static final long serialVersionUID = -1216559425084843515L;
    
    public SecurityMartixException()
    {
        super(HttpStatus.FORBIDDEN, ErrorCode.SecurityMartix.getCode(), ErrorCode.SecurityMartix.getMessage());
    }
    
    public SecurityMartixException(Throwable e)
    {
        
        super(e, HttpStatus.FORBIDDEN, ErrorCode.SecurityMartix.getCode(),
            ErrorCode.SecurityMartix.getMessage());
        
    }
    
}
