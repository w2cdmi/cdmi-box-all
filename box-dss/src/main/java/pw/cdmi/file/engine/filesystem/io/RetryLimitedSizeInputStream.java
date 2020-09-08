package pw.cdmi.file.engine.filesystem.io;

import pw.cdmi.file.engine.filesystem.exception.FileNotFoundException;
import pw.cdmi.file.engine.filesystem.model.FSObject;
import pw.cdmi.file.engine.filesystem.model.FileSystem;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RetryLimitedSizeInputStream<T extends FSObject> extends BufferedInputStream
{
  private static final Logger LOGGER = LoggerFactory.getLogger(RetryLimitedSizeInputStream.class);
  private long limitSize;
  private long start;
  private long end;
  private T fsObject;
  private FileSystem<T> fileSystem;
  private InputStream retryIs;
  private boolean supportRetry = true;

  public RetryLimitedSizeInputStream(InputStream in, long start, long end, T fsObject, FileSystem<T> fileSystem)
  {
    super(in);
    this.limitSize = (end - start + 1L);
    this.start = start;
    this.end = end;
    this.fsObject = fsObject;
    this.fileSystem = fileSystem;
  }

  public synchronized int read()
    throws IOException
  {
    if (this.limitSize <= 0L)
    {
      return -1;
    }

    int b = doRead();

    if (b < 0)
    {
      this.limitSize = 0L;
    }
    else
    {
      this.limitSize -= 1L;
      this.start += 1L;
    }

    return b;
  }

  private int doRead() throws IOException
  {
    if (this.retryIs != null)
    {
      return this.retryIs.read();
    }

    int b = 0;
    try
    {
      b = super.read();
    }
    catch (Exception e)
    {
      if (this.supportRetry)
      {
        initRetryInputStream();
        b = this.retryIs.read();
      }
      else
      {
        LOGGER.warn("not support retry.");
        throw new IOException(e);
      }
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

    int ret = doRead(b, off, len);

    if (ret < 0)
    {
      this.limitSize = 0L;
    }
    else
    {
      this.limitSize -= ret;
      this.start += ret;
    }

    return ret;
  }

  private int doRead(byte[] b, int off, int len) throws IOException
  {
    if (this.retryIs != null)
    {
      return this.retryIs.read(b, off, len);
    }

    int ret = 0;
    try
    {
      ret = super.read(b, off, len);
    }
    catch (Exception e)
    {
      if (this.supportRetry)
      {
        initRetryInputStream();
        ret = this.retryIs.read(b, off, len);
      }
      else
      {
        LOGGER.warn("not support retry.");
        throw new IOException(e);
      }
    }

    return ret;
  }

  public void close()
    throws IOException
  {
    try
    {
      super.close();
    }
    catch (Exception e)
    {
      if ((e.getCause() instanceof FileNotFoundException))
      {
        LOGGER.error("file not found.", e);
        return;
      }
      throw e;
    }
    finally
    {
      IOUtils.closeQuietly(this.retryIs); } IOUtils.closeQuietly(this.retryIs);
  }

  private void initRetryInputStream()
    throws IOException
  {
    if (this.retryIs != null)
    {
      return;
    }

    try
    {
      FSObject obj = this.fileSystem.doGetObject(this.fsObject, Long.valueOf(this.start), Long.valueOf(this.end));
      this.retryIs = obj.getInputStream();
      if ((this.retryIs instanceof RetryLimitedSizeInputStream))
      {
        ((RetryLimitedSizeInputStream)this.retryIs).setSupportRetry(false);
      }
    }
    catch (Exception e)
    {
      throw new IOException(e);
    }
  }

  public void setSupportRetry(boolean supportRetry)
  {
    this.supportRetry = supportRetry;
  }
}