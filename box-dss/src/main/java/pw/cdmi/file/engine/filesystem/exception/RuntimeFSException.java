package pw.cdmi.file.engine.filesystem.exception;

import pw.cdmi.core.exception.CustomException;

public class RuntimeFSException extends CustomException
{
  private static final long serialVersionUID = -8523433742714264023L;

  public RuntimeFSException(String message)
  {
    super(message);
  }

  public RuntimeFSException(Throwable cause)
  {
    super(cause);
  }

  public RuntimeFSException(String message, Throwable cause)
  {
    super(message, cause);
  }
}