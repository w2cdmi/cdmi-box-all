package pw.cdmi.file.engine.filesystem.exception;

public class NoEnoughSpaceException extends FSException
{
  private static final long serialVersionUID = 2644168353155332425L;

  public NoEnoughSpaceException(String message)
  {
    super(message);
  }
}