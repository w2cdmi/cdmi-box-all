package pw.cdmi.file.engine.filesystem.qingcloud;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import pw.cdmi.file.engine.filesystem.FileSystemManager;
import pw.cdmi.file.engine.filesystem.aws.S3FileSystem;

@Service("qcloudFileSystem")
public class QCloudFileSystem extends S3FileSystem
{
  @Autowired
  @Qualifier("qcloudFileSystemManager")
  private FileSystemManager fileSystemManager;

  public FileSystemManager getFSManager()
  {
    return this.fileSystemManager;
  }
}