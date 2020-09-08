package pw.cdmi.file.engine.filesystem.css;

import pw.cdmi.file.engine.filesystem.FileSystemManager;
import pw.cdmi.file.engine.filesystem.normal.NormalFileSystem;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service("cssFileSystem")
public class CSSFileSystem extends NormalFileSystem
{

  @Autowired
  @Qualifier("cssFileSystemManager")
  private FileSystemManager fileSystemManager;

  public FileSystemManager getFSManager()
  {
    return this.fileSystemManager;
  }
}