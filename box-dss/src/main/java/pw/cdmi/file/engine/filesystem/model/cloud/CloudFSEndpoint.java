package pw.cdmi.file.engine.filesystem.model.cloud;

import pw.cdmi.file.engine.filesystem.model.FSAccessPathSelector;
import pw.cdmi.file.engine.filesystem.model.FSEndpoint;

public class CloudFSEndpoint extends FSEndpoint
{
  private static final long serialVersionUID = 7067105989915299105L;
  private transient FSAccessPathSelector fsAccessPathManager;

  public FSAccessPathSelector getFsAccessPathManager()
  {
    if (this.fsAccessPathManager == null)
    {
      this.fsAccessPathManager = new CloudFSAccessPathSelector(this);
    }
    return this.fsAccessPathManager;
  }
}