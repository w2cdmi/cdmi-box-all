package pw.cdmi.file.engine.filesystem.cache.exception;

import pw.cdmi.core.exception.CustomException;

public class CacheFSException extends CustomException
{
  private static final long serialVersionUID = -8523433742714264023L;

  public CacheFSException(String message)
  {
    super(message);
  }

  public CacheFSException(Throwable cause)
  {
    super(cause);
  }

  public CacheFSException(String message, Throwable cause)
  {
    super(message, cause);
  }
}