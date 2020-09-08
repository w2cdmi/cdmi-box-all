package pw.cdmi.box.uam.exception;

import org.apache.shiro.authc.AuthenticationException;

public class IncorrectCaptchaException extends AuthenticationException
{
    private static final long serialVersionUID = -8385462547381801475L;
    
    public IncorrectCaptchaException()
    {
        super();
    }
    
    public IncorrectCaptchaException(String message, Throwable cause)
    {
        super(message, cause);
    }
    
    public IncorrectCaptchaException(String message)
    {
        super(message);
    }
    
    public IncorrectCaptchaException(Throwable cause)
    {
        super(cause);
    }
}
