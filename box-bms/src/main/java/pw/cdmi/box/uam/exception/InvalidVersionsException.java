package pw.cdmi.box.uam.exception;

import org.springframework.http.HttpStatus;

public class InvalidVersionsException extends BaseRunException
{
    private static final long serialVersionUID = 3187889527609485428L;
    
    public InvalidVersionsException()
    {
        super(HttpStatus.BAD_REQUEST, ErrorCode.INVALID_VERSIONS.getCode(),
            ErrorCode.INVALID_VERSIONS.getMessage());
    }
    
    public InvalidVersionsException(String excepMessage)
    {
        super(HttpStatus.BAD_REQUEST, ErrorCode.INVALID_VERSIONS.getCode(),
            ErrorCode.INVALID_VERSIONS.getMessage(), excepMessage);
    }
}
