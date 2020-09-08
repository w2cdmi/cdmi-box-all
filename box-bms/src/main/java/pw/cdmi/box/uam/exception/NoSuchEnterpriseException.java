package pw.cdmi.box.uam.exception;

import org.springframework.http.HttpStatus;

public class NoSuchEnterpriseException extends BaseRunException
{
    
    private static final long serialVersionUID = -6476908649717712995L;
    
    public NoSuchEnterpriseException()
    {
        super(HttpStatus.NOT_FOUND, ErrorCode.NO_SUCH_ENTERPRISE.getCode(),
            ErrorCode.NO_SUCH_ENTERPRISE.getMessage());
    }
    
    public NoSuchEnterpriseException(String msg)
    {
        super(HttpStatus.NOT_FOUND, ErrorCode.NO_SUCH_ENTERPRISE.getCode(),
            ErrorCode.NO_SUCH_ENTERPRISE.getMessage(), msg);
    }
}
