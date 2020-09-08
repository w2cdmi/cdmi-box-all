package pw.cdmi.file.engine.filesystem.exception;

public class FileRenameFailedException extends FSException
{
  private static final long serialVersionUID = 5553814540911359857L;

  public FileRenameFailedException(String message)
  {
    super(message);
  }

  public FileRenameFailedException(String message, Throwable cause)
  {
    super(message, cause);
  }
}