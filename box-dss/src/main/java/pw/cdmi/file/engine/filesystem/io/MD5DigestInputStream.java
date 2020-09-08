package pw.cdmi.file.engine.filesystem.io;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pw.cdmi.file.engine.core.spring.ext.BeanHolder;

public class MD5DigestInputStream extends FilterInputStream
{
  private static final Logger LOGGER = LoggerFactory.getLogger(MD5DigestInputStream.class);

  private static final int SAMPLING_LENGTH_SMALLER_FILE = Integer.parseInt(BeanHolder.getMessage("sampling.length.for.smaller.file", 
    "256"));

  private static final int SAMPLING_LENGTH_BIGGER_FILE = Integer.parseInt(BeanHolder.getMessage("sampling.length.for.bigger.file", 
    "262144"));

  private byte[] smallFileBuffer = new byte[SAMPLING_LENGTH_SMALLER_FILE];

  private byte[] bigFileBuffer = new byte[SAMPLING_LENGTH_BIGGER_FILE];
  private long length;
  private int destPos;
  private MessageDigest digester;
  private MessageDigest samplingDigester;
  private String digest = null;

  public MD5DigestInputStream(InputStream in)
  {
    super(in);
    try
    {
      this.digester = MessageDigest.getInstance("MD5");
      this.samplingDigester = MessageDigest.getInstance("MD5");
    }
    catch (NoSuchAlgorithmException e)
    {
      LOGGER.error("NoSuchAlgorithm", e);
    }
  }

  public MD5DigestInputStream(InputStream in, String algorithm) throws NoSuchAlgorithmException
  {
    super(in);
    this.digester = MessageDigest.getInstance(algorithm);
    this.samplingDigester = MessageDigest.getInstance(algorithm);
  }

  public int read() throws IOException
  {
    int ch = this.in.read();
    if (ch != -1)
    {
      this.length += 1L;
      this.digester.update((byte)ch);
    }
    return ch;
  }

  public int read(byte[] b) throws IOException
  {
    return read(b, 0, b.length);
  }

  public int read(byte[] b, int off, int len)
    throws IOException
  {
    if ((this.length < SAMPLING_LENGTH_SMALLER_FILE) && (this.length + len > SAMPLING_LENGTH_SMALLER_FILE))
    {
      len = (int)(SAMPLING_LENGTH_SMALLER_FILE - this.length);
    }
    else if ((this.length < SAMPLING_LENGTH_BIGGER_FILE) && (this.length + len > SAMPLING_LENGTH_BIGGER_FILE))
    {
      len = (int)(SAMPLING_LENGTH_BIGGER_FILE - this.length);
    }

    int result = this.in.read(b, off, len);
    if (result != -1)
    {
      this.length += result;
      this.digester.update(b, off, result);

      if (this.length <= SAMPLING_LENGTH_SMALLER_FILE)
      {
        System.arraycopy(b, 0, this.smallFileBuffer, this.destPos, result);
        System.arraycopy(b, 0, this.bigFileBuffer, this.destPos, result);
      }
      else if (this.length <= SAMPLING_LENGTH_BIGGER_FILE)
      {
        System.arraycopy(b, 0, this.bigFileBuffer, this.destPos, result);
      }
      else
      {
        getSamplingMD5();
      }
      this.destPos += result;
    }
    else
    {
      getSamplingMD5();
    }
    return result;
  }

  public long getLength()
  {
    return this.length;
  }

  public String getMd5()
  {
    return new String(Hex.encodeHex(this.digester.digest()));
  }

  public String getSamplingMD5()
  {
    if (StringUtils.isNotBlank(this.digest))
    {
      return this.digest;
    }

    if (this.length > SAMPLING_LENGTH_BIGGER_FILE)
    {
      this.digest = new String(Hex.encodeHex(this.samplingDigester.digest(this.bigFileBuffer)));
    }
    else if (this.length > SAMPLING_LENGTH_SMALLER_FILE)
    {
      this.digest = new String(Hex.encodeHex(this.samplingDigester.digest(this.smallFileBuffer)));
    }

    this.smallFileBuffer = null;
    this.bigFileBuffer = null;

    return this.digest;
  }
}