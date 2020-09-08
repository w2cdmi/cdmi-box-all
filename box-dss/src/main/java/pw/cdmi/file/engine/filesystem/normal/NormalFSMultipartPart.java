package pw.cdmi.file.engine.filesystem.normal;

import pw.cdmi.file.engine.filesystem.FSMultipartPart;
import pw.cdmi.file.engine.filesystem.model.FSObject;

public class NormalFSMultipartPart<T extends FSObject> extends FSMultipartPart<T>
{
  private long timestamp;

  public NormalFSMultipartPart(T fsObject, int partNumber, long timestamp)
  {
    super(fsObject, partNumber);
    setTimestamp(timestamp);
  }

  public long getTimestamp()
  {
    return this.timestamp;
  }

  public void setTimestamp(long timestamp)
  {
    this.timestamp = timestamp;
  }

  public int hashCode()
  {
    return super.hashCode();
  }

  public boolean equals(Object obj)
  {
    if ((obj instanceof NormalFSMultipartPart))
    {
      return super.equals(obj);
    }

    return false;
  }
}