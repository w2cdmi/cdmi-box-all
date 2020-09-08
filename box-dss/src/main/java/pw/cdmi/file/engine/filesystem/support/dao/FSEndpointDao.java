package pw.cdmi.file.engine.filesystem.support.dao;

import pw.cdmi.core.db.dao.BaseDAO;
import pw.cdmi.file.engine.filesystem.model.FSAccessPath;
import pw.cdmi.file.engine.filesystem.model.FSEndpoint;

import java.util.List;

public abstract interface FSEndpointDao extends BaseDAO<FSEndpoint>
{
  public abstract List<FSEndpoint> queryAllForDevice(String paramString);

  public abstract void updateBaseInfo(FSEndpoint paramFSEndpoint);

  public abstract void updateDeviceInfo(String paramString, FSEndpoint paramFSEndpoint);

  public abstract void updateStatus(FSEndpoint paramFSEndpoint);

  public abstract void addAccessPath(String paramString, List<FSAccessPath> paramList);

  public abstract void updateExtAttribute(FSEndpoint paramFSEndpoint);
}