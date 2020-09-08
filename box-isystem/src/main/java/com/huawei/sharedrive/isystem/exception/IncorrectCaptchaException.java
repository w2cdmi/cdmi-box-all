package com.huawei.sharedrive.isystem.exception;

import org.apache.shiro.authc.AuthenticationException;

/**
 * 校验码异常
 * @author d00199602
 *
 */
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
