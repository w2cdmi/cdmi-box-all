/**
 * 
 */
package pw.cdmi.file.engine.mirro.exception;

import pw.cdmi.core.exception.CustomException;

/**
 * @author w00186884
 * 
 */
public class CopyTaskException extends CustomException
{
    
    /**
     * 
     */
    private static final long serialVersionUID = -7242323752943683176L;

    public CopyTaskException()
    {
        super();
    }

    public CopyTaskException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace)
    {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public CopyTaskException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public CopyTaskException(String message)
    {
        super(message);
    }

    public CopyTaskException(Throwable cause)
    {
        super(cause);
    }
}
