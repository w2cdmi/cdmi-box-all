package pw.cdmi.file.engine.filesystem.manage;

import pw.cdmi.file.engine.filesystem.model.FSEndpoint;

public class RefreshFuture
{
  private FSEndpoint beforeObj;
  private FSEndpoint afterObj;

  public RefreshFuture(FSEndpoint beforeObj)
  {
    this.beforeObj = beforeObj;
    this.afterObj = null;
  }

  public FSEndpoint getBeforeObj()
  {
    return this.beforeObj;
  }

  public void setBeforeObj(FSEndpoint beforeObj)
  {
    this.beforeObj = beforeObj;
  }

  public FSEndpoint getAfterObj()
  {
    return this.afterObj;
  }

  public void setAfterObj(FSEndpoint afterObj)
  {
    this.afterObj = afterObj;
  }
}