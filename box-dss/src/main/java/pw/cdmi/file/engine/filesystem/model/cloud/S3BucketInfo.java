package pw.cdmi.file.engine.filesystem.model.cloud;

import pw.cdmi.file.engine.core.common.AutoToStringEntity;

public class S3BucketInfo extends AutoToStringEntity
{
  private static final long serialVersionUID = -8606420066318409033L;
  private String name;
  private S3Userinfo userInfo;

  public S3BucketInfo()
  {
  }

  public S3BucketInfo(String name, S3Userinfo userInfo)
  {
    this.name = name;
    this.userInfo = userInfo;
  }

  public String getName()
  {
    return this.name;
  }

  public void setName(String name)
  {
    this.name = name;
  }

  public S3Userinfo getUserInfo()
  {
    return this.userInfo;
  }

  public void setUserInfo(S3Userinfo userInfo)
  {
    this.userInfo = userInfo;
  }
}