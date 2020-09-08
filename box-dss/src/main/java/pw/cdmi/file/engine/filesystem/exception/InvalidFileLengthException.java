package pw.cdmi.file.engine.filesystem.exception;

public class InvalidFileLengthException extends FSException
{
  private static final long serialVersionUID = -3775359832049552635L;

  public InvalidFileLengthException(String message)
  {
    super(message);
  }
}