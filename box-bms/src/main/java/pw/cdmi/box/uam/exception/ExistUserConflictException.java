package pw.cdmi.box.uam.exception;

import org.springframework.http.HttpStatus;

public class ExistUserConflictException extends BaseRunException
{
    private static final long serialVersionUID = 5608620607174703615L;
    
    public ExistUserConflictException()
    {
        super(HttpStatus.CONFLICT, ErrorCode.EXIST_USER_CONFLICT.getCode(),
            ErrorCode.EXIST_USER_CONFLICT.getMessage());
    }
}
