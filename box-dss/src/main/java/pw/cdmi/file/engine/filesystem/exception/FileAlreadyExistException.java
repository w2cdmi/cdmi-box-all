package pw.cdmi.file.engine.filesystem.exception;

import pw.cdmi.file.engine.filesystem.model.FSObject;

public class FileAlreadyExistException extends FSException
{
  private static final long serialVersionUID = -8370110365064321208L;

  public FileAlreadyExistException(String message)
  {
    super(message);
  }

  public FileAlreadyExistException(FSObject fsObject)
  {
    super("FSObject : [ " + fsObject.logFormat() + " ] is Already Exist.");
  }
}