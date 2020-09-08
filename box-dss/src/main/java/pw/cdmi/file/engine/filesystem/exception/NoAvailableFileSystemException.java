package pw.cdmi.file.engine.filesystem.exception;

public class NoAvailableFileSystemException extends FSException
{
  private static final long serialVersionUID = -8155432459975826731L;

  public NoAvailableFileSystemException(String message)
  {
    super(message);
  }
}