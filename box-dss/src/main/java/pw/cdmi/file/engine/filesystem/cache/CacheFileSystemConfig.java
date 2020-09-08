package pw.cdmi.file.engine.filesystem.cache;

import java.io.Serializable;

public class CacheFileSystemConfig
  implements Serializable
{
  private static final long serialVersionUID = -3116517593736370261L;
  private int rangeStart;
  private int rangeEnd;

  public int getRangeStart()
  {
    return this.rangeStart;
  }

  public void setRangeStart(int rangeStart)
  {
    this.rangeStart = rangeStart;
  }

  public int getRangeEnd()
  {
    return this.rangeEnd;
  }

  public void setRangeEnd(int rangeEnd)
  {
    this.rangeEnd = rangeEnd;
  }
}