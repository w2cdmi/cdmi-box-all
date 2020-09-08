package pw.cdmi.file.engine.filesystem.exception;

public class UnknownFSException extends RuntimeFSException
{
  private static final long serialVersionUID = 2844163066247676561L;

  public UnknownFSException(String message)
  {
    super(message);
  }

  public UnknownFSException(Throwable cause)
  {
    super(cause);
  }

  public UnknownFSException(String message, Throwable cause)
  {
    super(message, cause);
  }
}