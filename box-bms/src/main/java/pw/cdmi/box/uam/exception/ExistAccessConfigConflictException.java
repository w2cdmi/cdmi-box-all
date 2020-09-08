package pw.cdmi.box.uam.exception;

import org.springframework.http.HttpStatus;

public class ExistAccessConfigConflictException extends BaseRunException
{
    
    private static final long serialVersionUID = -3002892640656677750L;
    
    public ExistAccessConfigConflictException()
    {
        super(HttpStatus.CONFLICT, ErrorCode.EXIST_ACCESSCONFIG_CONFLICT.getCode(),
            ErrorCode.EXIST_ACCESSCONFIG_CONFLICT.getMessage());
    }
    
    public ExistAccessConfigConflictException(Throwable e)
    {
        
        super(e, HttpStatus.CONFLICT, ErrorCode.EXIST_ACCESSCONFIG_CONFLICT.getCode(),
            ErrorCode.EXIST_ACCESSCONFIG_CONFLICT.getMessage());
        
    }
}
