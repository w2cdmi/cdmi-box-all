package pw.cdmi.file.engine.filesystem.model.cloud;

import pw.cdmi.common.log.LogFormat;
import pw.cdmi.file.engine.core.common.AutoToStringEntity;
import pw.cdmi.file.engine.filesystem.model.FSEndpoint;

public class S3Userinfo extends AutoToStringEntity
  implements LogFormat
{
  private static final long serialVersionUID = 8653427860526038260L;
  private String ak;
  private String sk;
  private S3EndPointInfo endpointInfo;

  public S3Userinfo()
  {
  }

  public S3Userinfo(String ak, String sk)
  {
    this.ak = ak;
    this.sk = sk;
  }

  public String getAk()
  {
    return this.ak;
  }

  public void setAk(String ak)
  {
    this.ak = ak;
  }

  public String getSk()
  {
    return this.sk;
  }

  public void setSk(String sk)
  {
    this.sk = sk;
  }

  public S3EndPointInfo getEndpointInfo()
  {
    return this.endpointInfo;
  }

  public void setEndpointInfo(S3EndPointInfo endpointInfo)
  {
    this.endpointInfo = endpointInfo;
  }

  public String logFormat()
  {
    StringBuilder sb = new StringBuilder(FSEndpoint.class.getCanonicalName()).append("[")
      .append("sk=")
      .append(this.sk)
      .append(", ")
      .append("endpointInfo=")
      .append(this.endpointInfo)
      .append("]");
    return sb.toString();
  }
}