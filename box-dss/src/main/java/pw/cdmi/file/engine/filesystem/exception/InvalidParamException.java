package pw.cdmi.file.engine.filesystem.exception;

import pw.cdmi.core.exception.CustomException;

public class InvalidParamException extends CustomException
{
  private static final long serialVersionUID = 1255832433123585021L;

  public InvalidParamException(String message)
  {
    super(message);
  }

  public InvalidParamException(String message, Throwable cause)
  {
    super(message, cause);
  }
}