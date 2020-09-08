package pw.cdmi.file.engine.filesystem.exception;

import java.util.Arrays;

public class WrongFileSystemArgsException extends FSException
{
  private static final long serialVersionUID = -5784724860021197389L;

  public WrongFileSystemArgsException(String message)
  {
    super(message);
  }

  public WrongFileSystemArgsException(String message, String[] args)
  {
    this(message + " : [ " + (args == null ? "" : Arrays.toString(args)) + " ]");
  }
}