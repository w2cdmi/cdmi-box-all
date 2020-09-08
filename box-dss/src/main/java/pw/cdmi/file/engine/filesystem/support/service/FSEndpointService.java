package pw.cdmi.file.engine.filesystem.support.service;

import java.util.List;

import pw.cdmi.file.engine.filesystem.model.FSEndpoint;
import pw.cdmi.file.engine.filesystem.model.FSEndpointStatus;

public abstract interface FSEndpointService
{
  public abstract FSEndpoint addFSEndpoint(FSEndpoint paramFSEndpoint);

  public abstract void updateFSEndpointStatus(FSEndpoint paramFSEndpoint, FSEndpointStatus paramFSEndpointStatus);

  public abstract void updateBaseInfo(FSEndpoint paramFSEndpoint);

  public abstract void updateFSEndpointsForCurrentDevice(FSEndpoint paramFSEndpoint);

  public abstract void updateFSEndpoint(FSEndpoint paramFSEndpoint);

  public abstract void deleteFSEndpoint(FSEndpoint paramFSEndpoint);

  public abstract FSEndpoint getFSEndpoint(String paramString);

  public abstract List<FSEndpoint> getAllFSEndpointsForCurrentDevice();

  public abstract void updateFSEndpointExtAttribute(FSEndpoint paramFSEndpoint);
}