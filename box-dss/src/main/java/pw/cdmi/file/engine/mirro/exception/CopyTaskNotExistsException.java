package pw.cdmi.file.engine.mirro.exception;

import pw.cdmi.core.exception.CustomException;

public class CopyTaskNotExistsException extends CustomException
{

    /**
     * 
     */
    private static final long serialVersionUID = -8289676457465968718L;

    public CopyTaskNotExistsException()
    {
        super();
    }

    public CopyTaskNotExistsException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace)
    {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public CopyTaskNotExistsException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public CopyTaskNotExistsException(String message)
    {
        super(message);
    }

    public CopyTaskNotExistsException(Throwable cause)
    {
        super(cause);
    }
    
}
