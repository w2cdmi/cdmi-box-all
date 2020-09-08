package pw.cdmi.file.engine.filesystem.exception;

public class FileNotFoundException extends FSException
{
  private static final long serialVersionUID = 5553814540911359857L;

  public FileNotFoundException(String message)
  {
    super(message);
  }

  public FileNotFoundException(String message, Throwable cause) {
    super(message, cause);
  }
}