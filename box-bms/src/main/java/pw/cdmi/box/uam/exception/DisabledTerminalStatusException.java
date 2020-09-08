package pw.cdmi.box.uam.exception;

import org.springframework.http.HttpStatus;

public class DisabledTerminalStatusException extends BaseRunException
{
    private static final long serialVersionUID = -2147093566523637264L;
    
    public DisabledTerminalStatusException()
    {
        super(HttpStatus.UNAUTHORIZED, ErrorCode.CLIENTUNAUTHORIZED.getCode(),
            ErrorCode.CLIENTUNAUTHORIZED.getMessage());
    }
    
    public DisabledTerminalStatusException(String excepMessage)
    {
        super(HttpStatus.UNAUTHORIZED, ErrorCode.CLIENTUNAUTHORIZED.getCode(),
            ErrorCode.CLIENTUNAUTHORIZED.getMessage(), excepMessage);
    }
}
