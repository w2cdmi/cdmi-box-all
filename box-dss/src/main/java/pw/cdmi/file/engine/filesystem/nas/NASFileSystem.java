package pw.cdmi.file.engine.filesystem.nas;

import pw.cdmi.file.engine.filesystem.FileSystemManager;
import pw.cdmi.file.engine.filesystem.normal.NormalFileSystem;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service("nasFileSystem")
public class NASFileSystem extends NormalFileSystem
{

  @Autowired
  @Qualifier("nasFileSystemManager")
  private FileSystemManager fileSystemManager;

  public FileSystemManager getFSManager()
  {
    return this.fileSystemManager;
  }
}