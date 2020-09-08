package pw.cdmi.box.uam.exception;

import org.springframework.http.HttpStatus;

public class NoSuchAuthServerException extends BaseRunException
{
    private static final long serialVersionUID = -2403991900101600349L;
    
    public NoSuchAuthServerException()
    {
        super(HttpStatus.NOT_FOUND, ErrorCode.NO_SUCH_AUTHSERVER.getCode(),
            ErrorCode.NO_SUCH_AUTHSERVER.getMessage());
    }
    
    public NoSuchAuthServerException(String excepMessage)
    {
        super(HttpStatus.NOT_FOUND, ErrorCode.NO_SUCH_AUTHSERVER.getCode(),
            ErrorCode.NO_SUCH_AUTHSERVER.getMessage(), excepMessage);
    }
}
