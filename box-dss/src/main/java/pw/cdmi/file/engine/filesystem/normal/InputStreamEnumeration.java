package pw.cdmi.file.engine.filesystem.normal;

import pw.cdmi.file.engine.filesystem.FSMultipartPart;
import pw.cdmi.file.engine.filesystem.exception.FSException;
import pw.cdmi.file.engine.filesystem.exception.RuntimeFSException;
import pw.cdmi.file.engine.filesystem.model.NormalFSObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Iterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InputStreamEnumeration
  implements Enumeration<InputStream>
{
  private static final Logger LOGGER = LoggerFactory.getLogger(InputStreamEnumeration.class);
  private Iterator<FSMultipartPart<NormalFSObject>> iterator;
  private long startOffset;
  private boolean isFirst = true;

  private InputStream preIs = null;

  private NormalFileSystem normalFileSystem = null;

  public InputStreamEnumeration(NormalFileSystem normalFileSystem, Iterator<FSMultipartPart<NormalFSObject>> iterator, long startOffset)
  {
    this.normalFileSystem = normalFileSystem;
    this.iterator = iterator;
    this.startOffset = startOffset;
  }

  public boolean hasMoreElements()
  {
    return this.iterator.hasNext();
  }

  public InputStream nextElement()
  {
    try
    {
      if (this.preIs != null)
      {
        this.preIs.close();
      }
    }
    catch (IOException e)
    {
      LOGGER.warn("pre input stream close falied.", e);
    }

    long seek = 0L;
    if (this.isFirst)
    {
      seek = this.startOffset;
      this.isFirst = false;
    }

    NormalFSObject fsObject = (NormalFSObject)((FSMultipartPart)this.iterator.next()).getFSObject();
    try
    {
      this.preIs = this.normalFileSystem.getNormalObject(fsObject, seek, fsObject.getLength()).getInputStream();
    }
    catch (FSException e)
    {
      throw new RuntimeFSException(e);
    }
    return this.preIs;
  }
}