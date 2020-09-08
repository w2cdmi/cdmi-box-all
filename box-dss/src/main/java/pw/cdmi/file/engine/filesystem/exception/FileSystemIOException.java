package pw.cdmi.file.engine.filesystem.exception;

public class FileSystemIOException extends FSException
{
  private static final long serialVersionUID = -8306353966684858761L;

  public FileSystemIOException(String message)
  {
    super(message);
  }

  public FileSystemIOException(Throwable cause)
  {
    super(cause);
  }

  public FileSystemIOException(String message, Throwable cause)
  {
    super(message, cause);
  }
}