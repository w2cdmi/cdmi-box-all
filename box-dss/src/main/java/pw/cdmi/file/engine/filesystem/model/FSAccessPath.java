package pw.cdmi.file.engine.filesystem.model;

import pw.cdmi.common.log.LogFormat;
import pw.cdmi.file.engine.core.common.CloneableEntity;
import pw.cdmi.file.engine.core.ibatis.Namingspace;

import java.io.Serializable;

@Namingspace("fsEndpoint")
public class FSAccessPath extends CloneableEntity
  implements Serializable, LogFormat
{
  private static final long serialVersionUID = -2126341586897588473L;
  private String id;
  private String endpointID;
  private boolean writeAble = true;

  private boolean noSpace = false;

  private boolean available = true;
  private String path;
  private String device;

  public FSAccessPath()
  {
  }

  public FSAccessPath(String path)
  {
    this.path = path;
  }

  public FSAccessPath(String endpointID, String path)
  {
    this.endpointID = endpointID;
    this.path = path;
  }

  public String getId()
  {
    return this.id;
  }

  public void setId(String id)
  {
    this.id = id;
  }

  public boolean isWriteAble()
  {
    return this.writeAble;
  }

  public void setWriteAble(boolean writeAble)
  {
    this.writeAble = writeAble;
  }

  public boolean isNoSpace()
  {
    return this.noSpace;
  }

  public void setNoSpace(boolean noSpace)
  {
    this.noSpace = noSpace;
  }

  public boolean isAvailable()
  {
    return this.available;
  }

  public void setAvailable(boolean available)
  {
    this.available = available;
  }

  public String getEndpointID()
  {
    return this.endpointID;
  }

  public void setEndpointID(String endpointID)
  {
    this.endpointID = endpointID;
  }

  public String getPath()
  {
    return this.path;
  }

  public void setPath(String path)
  {
    this.path = path;
  }

  public String getDevice()
  {
    return this.device;
  }

  public void setDevice(String device)
  {
    this.device = device;
  }

  public String logFormat()
  {
    return toString();
  }
}