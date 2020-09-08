package pw.cdmi.file.engine.filesystem.exception;

import pw.cdmi.core.exception.CustomException;

public class FSException extends CustomException
{
  private static final long serialVersionUID = -8523433742714264023L;

  public FSException(String message)
  {
    super(message);
  }

  public FSException(Throwable cause)
  {
    super(cause);
  }

  public FSException(String message, Throwable cause)
  {
    super(message, cause);
  }
}