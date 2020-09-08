package pw.cdmi.file.engine.filesystem.io;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

public class LimitedSizeInputStream extends BufferedInputStream
{
  private long limitSize;

  public LimitedSizeInputStream(InputStream in, long limitSize)
  {
    super(in);
    this.limitSize = limitSize;
  }

  public synchronized int read()
    throws IOException
  {
    if (this.limitSize <= 0L)
    {
      return -1;
    }

    int b = super.read();

    if (b < 0)
    {
      this.limitSize = 0L;
    }
    else
    {
      this.limitSize -= 1L;
    }

    return b;
  }

  public synchronized int read(byte[] b, int off, int len)
    throws IOException
  {
    if (this.limitSize <= 0L)
    {
      return -1;
    }

    if (this.limitSize < len)
    {
      len = Long.valueOf(this.limitSize).intValue();
    }

    int ret = super.read(b, off, len);
    if (ret < 0)
    {
      this.limitSize = 0L;
    }
    else
    {
      this.limitSize -= ret;
    }

    return ret;
  }
}