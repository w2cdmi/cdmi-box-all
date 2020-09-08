package pw.cdmi.file.engine.filesystem.model.cloud;

import java.util.Random;

import pw.cdmi.file.engine.filesystem.model.FSAccessPath;
import pw.cdmi.file.engine.filesystem.model.FSAccessPathSelector;
import pw.cdmi.file.engine.filesystem.model.FSEndpoint;

public class CloudFSAccessPathSelector extends FSAccessPathSelector
{
  private Random random;

  public CloudFSAccessPathSelector(FSEndpoint fsEndpoint)
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