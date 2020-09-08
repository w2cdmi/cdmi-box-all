package pw.cdmi.file.engine.filesystem.n8000;

import pw.cdmi.file.engine.filesystem.FileSystemManager;
import pw.cdmi.file.engine.filesystem.normal.NormalFileSystem;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service("n8500FileSystem")
public class N8500FileSystem extends NormalFileSystem
{

  @Autowired
  @Qualifier("n8500FileSystemManager")
  private FileSystemManager fileSystemManager;

  public FileSystemManager getFSManager()
  {
    return this.fileSystemManager;
  }
}