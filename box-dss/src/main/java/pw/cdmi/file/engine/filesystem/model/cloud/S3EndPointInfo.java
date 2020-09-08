package pw.cdmi.file.engine.filesystem.model.cloud;

import pw.cdmi.file.engine.core.common.AutoToStringEntity;

public class S3EndPointInfo extends AutoToStringEntity
{
  private static final long serialVersionUID = 7542420375676835718L;
  private final String ip;
  private final int httpPort;
  private final int httpsPort;

  public S3EndPointInfo(String ip, String httpport, String httpsport)
  {
    this.ip = ip;
    this.httpPort = Integer.parseInt(httpport);
    this.httpsPort = Integer.parseInt(httpsport);
  }

  public String getIP()
  {
    return this.ip;
  }

  public int getHttpPort()
  {
    return this.httpPort;
  }

  public int getHttpsPort()
  {
    return this.httpsPort;
  }

  public int hashCode()
  {
    int result = 1;
    result = 31 * result + (this.ip == null ? 0 : this.ip.hashCode());
    result = 31 * result + this.httpPort;
    result = 31 * result + this.httpsPort;
    return result;
  }

  public boolean equals(Object obj)
  {
    if ((obj instanceof S3EndPointInfo))
    {
      if (this == obj)
      {
        return true;
      }
      if (getClass() != obj.getClass())
      {
        return false;
      }
      S3EndPointInfo other = (S3EndPointInfo)obj;
      if (this.ip == null)
      {
        if (other.ip != null)
        {
          return false;
        }
      }
      else if (!this.ip.equals(other.ip))
      {
        return false;
      }
      if (this.httpPort != other.httpPort)
      {
        return false;
      }
      if (this.httpsPort != other.httpsPort)
      {
        return false;
      }
      return true;
    }

    return false;
  }
}