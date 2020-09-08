package pw.cdmi.file.engine.filesystem.model;

public class NormalFSEndpoint extends FSEndpoint
{
  private static final long serialVersionUID = 7067105989915299105L;
  private transient FSAccessPathSelector fsAccessPathManager;

  public FSAccessPathSelector getFsAccessPathManager()
  {
    if (this.fsAccessPathManager == null)
    {
      this.fsAccessPathManager = new NormalFSAccessPathSelector(this);
    }
    return this.fsAccessPathManager;
  }
}