package pw.cdmi.file.engine.filesystem.cache;

import java.io.InputStream;
import java.io.Serializable;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;

public class CacheFSObject
  implements Serializable
{
  private static final long serialVersionUID = -5062704034412685338L;
  private static final String CACHE_PREFIX = "object_cache_";
  private String cacheKey;
  private long length;
  private transient InputStream inputStream;

  public CacheFSObject(String cacheKey)
  {
    this.cacheKey = cacheKey;
  }

  public String getCacheKey()
  {
    return "object_cache_" + this.cacheKey;
  }

  public void setCacheKey(String cacheKey)
  {
    this.cacheKey = cacheKey;
  }

  public long getLength()
  {
    return this.length;
  }

  public void setLength(long length)
  {
    this.length = length;
  }

  public InputStream getInputStream()
  {
    return this.inputStream;
  }

  public void setInputStream(InputStream inputStream)
  {
    this.inputStream = inputStream;
  }

  public String toString()
  {
    return ReflectionToStringBuilder.toString(this);
  }
}