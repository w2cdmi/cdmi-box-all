package pw.cdmi.file.engine.filesystem.nas;

import pw.cdmi.file.engine.filesystem.model.FSDefinition;
import pw.cdmi.file.engine.filesystem.normal.NormalFileSystemManager;

import org.springframework.stereotype.Service;

@Service("nasFileSystemManager")
public class NASFileSystemManager extends NormalFileSystemManager
{
  public FSDefinition getDefinition()
  {
    return FSDefinition.NAS_FileSystem;
  }
}