package pw.cdmi.file.engine.filesystem.io;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import org.apache.commons.codec.binary.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MyDigestInputStream extends FilterInputStream
{
  private static final Logger LOGGER = LoggerFactory.getLogger(MyDigestInputStream.class);
  private long length;
  private MessageDigest md;

  public MyDigestInputStream(InputStream in)
  {
    super(in);
    try
    {
      this.md = MessageDigest.getInstance("SHA-1");
    }
    catch (NoSuchAlgorithmException e)
    {
      LOGGER.warn("NoSuchAlgorithm", e);
    }
  }

  public MyDigestInputStream(InputStream in, String algorithm)
  {
    super(in);
    try
    {
      this.md = MessageDigest.getInstance(algorithm);
    }
    catch (NoSuchAlgorithmException e)
    {
      LOGGER.warn("NoSuchAlgorithm", e);
    }
  }

  public String getHexString()
  {
    return new String(Hex.encodeHex(this.md.digest()));
  }

  public long getLength()
  {
    return this.length;
  }

  public int read() throws IOException
  {
    int ch = this.in.read();
    if (ch != -1)
    {
      this.length += 1L;
      this.md.update((byte)ch);
    }
    return ch;
  }

  public int read(byte[] b) throws IOException
  {
    return read(b, 0, b.length);
  }

  public int read(byte[] b, int off, int len) throws IOException
  {
    int result = this.in.read(b, off, len);
    if (result != -1)
    {
      this.length += result;
      this.md.update(b, off, result);
    }
    return result;
  }
}