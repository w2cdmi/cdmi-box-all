package pw.cdmi.file.engine.filesystem.model;


public abstract class FSAccessPathSelector
{
  protected FSEndpoint fsEndpoint;

  protected FSAccessPathSelector(FSEndpoint fsEndpoint)
  {
    this.fsEndpoint = fsEndpoint;
    if (this.fsEndpoint.getAccessPaths().size() > 1)
    {
      init();
    }
  }

  protected abstract void init();

  public FSAccessPath select(String tag)
  {
    if ((this.fsEndpoint.getAccessPaths() == null) || (this.fsEndpoint.getAccessPaths().isEmpty()))
    {
      return null;
    }
    return (FSAccessPath)this.fsEndpoint.getAccessPaths().get(0);
  }
}