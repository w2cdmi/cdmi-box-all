package pw.cdmi.file.engine.filesystem.model;

import java.util.Random;

public class NormalFSAccessPathSelector extends FSAccessPathSelector
{
  private Random random;

  public NormalFSAccessPathSelector(NormalFSEndpoint fsEndpoint)
  {
    super(fsEndpoint);
  }

  public FSAccessPath select(String tag)
  {
    if ((this.fsEndpoint.getAccessPaths() != null) && (this.fsEndpoint.getAccessPaths().size() > 1))
    {
      return (FSAccessPath)this.fsEndpoint.getAccessPaths().get(this.random.nextInt(this.fsEndpoint.getAccessPaths()
        .size()));
    }

    return super.select(tag);
  }

  protected void init()
  {
    this.random = new Random(System.currentTimeMillis());
  }
}