package pw.cdmi.file.engine.filesystem.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pw.cdmi.common.file.FileUtils;
import pw.cdmi.file.engine.filesystem.exception.NoAvailableFileSystemException;
import pw.cdmi.file.engine.filesystem.exception.UnrecognizedFileException;

import java.util.regex.Pattern;

public class NormalFSObject extends FSObject
  implements Cloneable
{
  private static final long serialVersionUID = 1L;
  private static final Logger LOGGER = LoggerFactory.getLogger(NormalFSObject.class);
  private NormalFile normalFile;
  private String folder;

  public NormalFSObject(String path)
    throws UnrecognizedFileException
  {
    super(path);
  }

  public NormalFSObject(FSEndpoint fsEndpoint, String folder, String fileName)
  {
    this(fsEndpoint, fsEndpoint.getFsAccessPathManager().select(fileName), folder, fileName);
  }

  public NormalFSObject(FSEndpoint fsEndpoint, FSAccessPath accessPath, String folder, String fileName)
  {
    super(fsEndpoint);
    reset(fsEndpoint, accessPath, folder, fileName);
  }

  protected void reset(FSEndpoint fsEndpoint, FSAccessPath accessPath, String folder, String fileName)
  {
    super.reset(fsEndpoint);
    this.folder = folder;
    this.fsAccessPath = accessPath;
    this.normalFile = new NormalFile(getFSAccessPath().getPath() + folder + fileName);
  }

  public NormalFile getNormalFile()
  {
    return this.normalFile;
  }

  public long getFileLength()
  {
    return this.normalFile.length();
  }

  protected void initByPath(String path)
    throws UnrecognizedFileException
  {
    try
    {
      String[] temp = path.substring(1, path.length() - 1).split(Pattern.quote("]["));

      this.fsDefinition = FSDefinition.findFSDefinition(temp[0]);

      this.fsEndpoint = getFSEndpoint(this.fsDefinition, temp[1]);
      if (this.fsEndpoint == null)
      {
        throw new NoAvailableFileSystemException("not exist");
      }

      this.folder = temp[3];
      this.fsAccessPath = getFSEndpoint().getFsAccessPathManager().select(temp[4]);
      if (this.fsAccessPath == null)
      {
        LOGGER.warn("Cann't not find available fsAccessPath");

        this.fsAccessPath = new FSAccessPath(temp[2]);
      }
      this.normalFile = new NormalFile(this.fsAccessPath.getPath() + this.folder + temp[4]);
    }
    catch (RuntimeException e)
    {
      String message = "Init FSObject By Path Failed. [ " + path + " ]";
      LOGGER.warn(message, e);
      throw new UnrecognizedFileException(message, e);
    }
    catch (Exception e)
    {
      String message = "Init FSObject By Path Failed. [ " + path + " ]";
      LOGGER.warn(message, e);
      throw new UnrecognizedFileException(message, e);
    }
  }

  public void setObjectKey(String objectKey)
  {
    if (this.normalFile != null)
    {
      this.normalFile = new NormalFile(getNormalFile().getParent() + NormalFile.separator + 
        objectKey);
    }
    else
    {
      super.setObjectKey(objectKey);
    }
  }

  public String getPath()
  {
    StringBuilder sb = new StringBuilder("[").append(getFSDefinition().getType())
      .append("][")
      .append(getFSEndpoint().getId())
      .append("][")
      .append(this.fsAccessPath.getPath())
      .append("][")
      .append(getFolder())
      .append("][")
      .append(this.normalFile.getName())
      .append("]");
    return sb.toString();
  }

  public String getFolder()
  {
    return this.folder;
  }

  public String getObjectKey()
  {
    return getNormalFile().getName();
  }

  public Object clone()
    throws CloneNotSupportedException
  {
    NormalFSObject target = (NormalFSObject)super.clone();
    target.reset(getFSEndpoint(), getFSAccessPath(), 
      getFolder(), getObjectKey());
    return target;
  }

  public String logFormat()
  {
    StringBuilder sb = new StringBuilder(NormalFSObject.class.getCanonicalName()).append("[")
      .append("length=")
      .append(getLength())
      .append(", ")
      .append("fsDefinition=")
      .append(this.fsDefinition)
      .append(", ")
      .append("fsEndpoint=")
      .append(this.fsEndpoint == null ? "null" : this.fsEndpoint.logFormat())
      .append(", ")
      .append("fsAccessPath=")
      .append(this.fsAccessPath == null ? "null" : this.fsAccessPath.logFormat())
      .append(", ")
      .append("folder=")
      .append(getFolder())
      .append(", ")
      .append("fileAbsolutePath=")
      .append(FileUtils.getCanonicalPathWithOutException(this.normalFile))
      .append(", ")
      .append("]");

    return sb.toString();
  }
}