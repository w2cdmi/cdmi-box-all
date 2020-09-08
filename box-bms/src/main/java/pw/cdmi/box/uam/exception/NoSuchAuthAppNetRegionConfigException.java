package pw.cdmi.box.uam.exception;

import org.springframework.http.HttpStatus;

public class NoSuchAuthAppNetRegionConfigException extends BaseRunException
{
    private static final long serialVersionUID = 1826643366776562778L;
    
    public NoSuchAuthAppNetRegionConfigException()
    {
        super(HttpStatus.NOT_FOUND, ErrorCode.NO_SUCH_ANNOUNCEMENT.getCode(),
            ErrorCode.NO_SUCH_ANNOUNCEMENT.getMessage());
    }
    
    public NoSuchAuthAppNetRegionConfigException(Throwable e)
    {
        super(e, HttpStatus.NOT_FOUND, ErrorCode.NO_SUCH_ANNOUNCEMENT.getCode(),
            ErrorCode.NO_SUCH_ANNOUNCEMENT.getMessage());
    }
    
    public NoSuchAuthAppNetRegionConfigException(String excepMessage)
    {
        super(HttpStatus.NOT_FOUND, ErrorCode.NO_SUCH_ANNOUNCEMENT.getCode(),
            ErrorCode.NO_SUCH_ANNOUNCEMENT.getMessage(), excepMessage);
    }
    
}
