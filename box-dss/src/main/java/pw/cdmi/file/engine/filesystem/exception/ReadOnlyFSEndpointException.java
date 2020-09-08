package pw.cdmi.file.engine.filesystem.exception;

public class ReadOnlyFSEndpointException extends FSException
{
  private static final long serialVersionUID = 2241837556864341601L;

  public ReadOnlyFSEndpointException(String message)
  {
    super(message);
  }
}