package pw.cdmi.file.engine.filesystem;

import pw.cdmi.file.engine.filesystem.exception.FSException;
import pw.cdmi.file.engine.filesystem.model.FSDefinition;
import pw.cdmi.file.engine.filesystem.model.FSEndpoint;

public abstract interface FileSystemManager
{
  public static final String ENDPOINT_SPLIT = ":";

  public abstract FSDefinition getDefinition();

  public abstract FSEndpoint createFSEndpoint(FSEndpoint paramFSEndpoint)
    throws FSException;

  public abstract FSEndpoint updateFSEndpoint(FSEndpoint paramFSEndpoint)
    throws FSException;

  public abstract FSEndpoint refreshFSEndpoint(FSEndpoint paramFSEndpoint)
    throws FSException;
}