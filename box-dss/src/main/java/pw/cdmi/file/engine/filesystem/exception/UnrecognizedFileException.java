package pw.cdmi.file.engine.filesystem.exception;

public class UnrecognizedFileException extends FSException
{
  private static final long serialVersionUID = 7144618353303787435L;

  public UnrecognizedFileException(String message)
  {
    super(message);
  }

  public UnrecognizedFileException(String message, Throwable cause) {
    super(message, cause);
  }
}