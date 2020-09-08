package pw.cdmi.file.engine.filesystem.io;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

public class CountInputStream extends BufferedInputStream
{
  private long totalReadSize = 0L;

  public CountInputStream(InputStream in)
  {
    super(in);
  }

  public synchronized int read()
    throws IOException
  {
    int ret = super.read();

    if (ret >= 0)
    {
      this.totalReadSize += 1L;
    }
    return ret;
  }

  public synchronized int read(byte[] b, int off, int len)
    throws IOException
  {
    int ret = super.read(b, off, len);
    if (ret >= 0)
    {
      this.totalReadSize += ret;
    }
    return ret;
  }

  public synchronized long getTotalReadSize()
  {
    return this.totalReadSize;
  }
}