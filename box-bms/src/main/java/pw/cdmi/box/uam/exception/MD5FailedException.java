package pw.cdmi.box.uam.exception;

import org.springframework.http.HttpStatus;

public class MD5FailedException extends BaseRunTimeException
{
    
    private static final long serialVersionUID = -8385462547381801475L;
    
    public MD5FailedException()
    {
        super(HttpStatus.INTERNAL_SERVER_ERROR, ErrorCode.MD5_ERROR.getCode(), ErrorCode.MD5_ERROR.getMessage());
    }
    
    public MD5FailedException(Throwable e)
    {
        super(e, HttpStatus.INTERNAL_SERVER_ERROR, ErrorCode.MD5_ERROR.getCode(),
            ErrorCode.MD5_ERROR.getMessage());
    }
    
    
    public MD5FailedException(String message, Throwable e)
    {
        super(e, HttpStatus.INTERNAL_SERVER_ERROR, ErrorCode.MD5_ERROR.getCode(),
            message);
    }
}
