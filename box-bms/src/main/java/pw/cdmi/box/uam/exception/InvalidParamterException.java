package pw.cdmi.box.uam.exception;

import org.springframework.http.HttpStatus;

public class InvalidParamterException extends BaseRunException
{
    private static final long serialVersionUID = 3187889527609485428L;
    
    public InvalidParamterException()
    {
        super(HttpStatus.BAD_REQUEST, ErrorCode.INVALID_PARAMTER.getCode(),
            ErrorCode.INVALID_PARAMTER.getMessage());
    }
    
    public InvalidParamterException(String excepMessage)
    {
        super(HttpStatus.BAD_REQUEST, ErrorCode.INVALID_PARAMTER.getCode(),
            ErrorCode.INVALID_PARAMTER.getMessage(), excepMessage);
    }
}
