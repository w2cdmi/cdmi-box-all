package pw.cdmi.file.engine.filesystem.model;

import pw.cdmi.common.log.LogFormat;
import pw.cdmi.file.engine.core.common.CloneableEntity;
import pw.cdmi.file.engine.filesystem.exception.FSException;
import pw.cdmi.file.engine.filesystem.exception.UnrecognizedFileException;
import pw.cdmi.file.engine.filesystem.manage.cache.FSEndpointCache;

import java.io.InputStream;
import java.io.Serializable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class FSObject extends CloneableEntity
  implements Serializable, LogFormat, Cloneable
{
  private static final Logger LOGGER = LoggerFactory.getLogger(FSObject.class);
  private static final long serialVersionUID = 1L;
  private String objectKey;
  private String name;
  private long length = -1L;
  protected FSDefinition fsDefinition;
  protected FSEndpoint fsEndpoint;
  protected FSAccessPath fsAccessPath;
  private transient InputStream inputStream;
  protected static final String PATHINF_START = "[";
  protected static final String PATHINF_END = "]";
  protected static final String PATHINFO_SPLIT = "][";
  protected static final String PATH_VERSION_INFO_HWY = "V2_HWY";

  public FSObject(FSEndpoint endpoint)
  {
    reset(endpoint);
  }

  protected void reset(FSEndpoint endpoint)
  {
    this.fsEndpoint = endpoint;
    this.fsDefinition = FSDefinition.findFSDefinition(this.fsEndpoint.getFsType());
  }

  public FSObject(String path) throws UnrecognizedFileException
  {
    initByPath(path);
  }

  public FSObject(String path, long length) throws UnrecognizedFileException
  {
    this(path);
    this.length = length;
  }

  public String getObjectKey()
  {
    return this.objectKey;
  }

  public void setObjectKey(String objectKey)
  {
    this.objectKey = objectKey;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public long getLength()
  {
    return this.length;
  }

  public void setLength(long length)
  {
    this.length = length;
  }

  public String getPath()
  {
    StringBuilder sb = new StringBuilder("[").append(getFSDefinition().getType())
      .append("][")
      .append(getVersionTag())
      .append("][")
      .append(getFSEndpoint().getId())
      .append("][")
      .append(this.fsAccessPath.getPath())
      .append("][")
      .append(getObjectKey())
      .append("]");

    return sb.toString();
  }

  public void setPath(String path) throws UnrecognizedFileException
  {
    initByPath(path);
  }

  protected abstract void initByPath(String paramString)
    throws UnrecognizedFileException;

  protected String getVersionTag()
  {
    return "V2_HWY";
  }

  public InputStream getInputStream()
  {
    return this.inputStream;
  }

  public void setInputStream(InputStream inputStream)
  {
    this.inputStream = inputStream;
  }

  public FSDefinition getFSDefinition()
  {
    return this.fsDefinition;
  }

  public FSEndpoint getFSEndpoint()
  {
    return this.fsEndpoint;
  }

  public FSAccessPath getFSAccessPath()
  {
    return this.fsAccessPath;
  }

  public Object clone()
    throws CloneNotSupportedException
  {
    return super.clone();
  }

  public String logFormat()
  {
    StringBuilder sb = new StringBuilder(FSObject.class.getCanonicalName()).append("[")
      .append("objectKey=")
      .append(this.objectKey)
      .append(", ")
      .append("length=")
      .append(this.length)
      .append(", ")
      .append("fsDefinition=")
      .append(this.fsDefinition)
      .append(", ")
      .append("fsEndpoint=")
      .append(this.fsEndpoint == null ? "null" : this.fsEndpoint.getId())
      .append(", ")
      .append("fsAccessPath=")
      .append(this.fsAccessPath == null ? "null" : this.fsAccessPath.logFormat())
      .append(", ")
      .append("]");

    return sb.toString();
  }

  public String toString()
  {
    return logFormat();
  }

  protected FSEndpoint getFSEndpoint(FSDefinition fsDefinition, String endpointId)
    throws FSException
  {
    FSEndpoint fsEndpoint = FSEndpointCache.getFSEndpoint(fsDefinition.getType(), endpointId);
    if (fsEndpoint == null)
    {
      LOGGER.warn("Cann't not find available fsEndpoint");
    }

    return fsEndpoint;
  }
}