package pw.cdmi.file.engine.filesystem.css;

import pw.cdmi.file.engine.filesystem.model.FSDefinition;
import pw.cdmi.file.engine.filesystem.normal.NormalFileSystemManager;

import org.springframework.stereotype.Service;

@Service("cssFileSystemManager")
public class CSSFileSystemManager extends NormalFileSystemManager
{
  public FSDefinition getDefinition()
  {
    return FSDefinition.CSS_FileSystem;
  }
}