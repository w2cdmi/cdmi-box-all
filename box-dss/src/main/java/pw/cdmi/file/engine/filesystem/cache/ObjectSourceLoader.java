package pw.cdmi.file.engine.filesystem.cache;

import pw.cdmi.core.exception.InnerException;
import pw.cdmi.file.engine.filesystem.exception.FSException;
import pw.cdmi.file.engine.filesystem.model.FSObject;
import pw.cdmi.file.engine.filesystem.model.FileSystem;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ObjectSourceLoader<T extends FSObject>
{
  private static final Logger LOGGER = LoggerFactory.getLogger(ObjectSourceLoader.class);
  private FileSystem<T> fileSystem;
  private T fsObject;

  public ObjectSourceLoader(FileSystem<T> fileSystem, T fsObject)
  {
    this.fileSystem = fileSystem;
    this.fsObject = fsObject;
  }

  public CacheFSObject load()
  {
    FSObject fsObject = null;
    try
    {
      fsObject = this.fileSystem.doGetObject(this.fsObject, null, null);
    }
    catch (FSException e)
    {
      String message = "Get Object [ " + this.fsObject + " ] Failed.";
      LOGGER.warn(message, e);
      throw new InnerException(message, e);
    }

    if (fsObject == null)
    {
      LOGGER.warn("load object from storage failed. not exist");
      return null;
    }

    CacheFSObject cacheFSObject = new CacheFSObject(fsObject.getObjectKey());
    cacheFSObject.setInputStream(fsObject.getInputStream());
    cacheFSObject.setLength(fsObject.getLength());

    return cacheFSObject;
  }

  public FileSystem<T> getFileSystem()
  {
    return this.fileSystem;
  }

  public FSObject getFSObject()
  {
    return this.fsObject;
  }
}