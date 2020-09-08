package pw.cdmi.file.engine.filesystem.manage;

import java.util.List;

import pw.cdmi.file.engine.filesystem.model.FSEndpoint;

public abstract class SelectAlgorithm
{
  abstract FSEndpoint select(List<FSEndpoint> paramList, Object paramObject);
}