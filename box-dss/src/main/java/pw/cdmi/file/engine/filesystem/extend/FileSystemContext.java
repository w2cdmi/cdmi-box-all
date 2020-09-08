package pw.cdmi.file.engine.filesystem.extend;

import java.util.HashMap;
import java.util.Map;

import pw.cdmi.file.engine.filesystem.model.FileSystem;

public final class FileSystemContext
{
  private static final ThreadLocal<FileSystemContext> THREAD_LOCAL = new ThreadLocal<FileSystemContext>();

  private Map<String, Object> contexts = new HashMap<String, Object>(1);
  private FileSystem fileSystem;

  public static FileSystemContext getCurrent()
  {
    FileSystemContext context = (FileSystemContext)THREAD_LOCAL.get();
    if (context == null)
    {
      context = new FileSystemContext();
    }

    return context;
  }

  public static FileSystemContext getCurrent(FileSystem fileSystem)
  {
    FileSystemContext context = (FileSystemContext)THREAD_LOCAL.get();
    if (context == null)
    {
      context = new FileSystemContext();
      context.setFileSystem(fileSystem);
    }

    return context;
  }

  public void addContext(String key, Object value)
  {
    this.contexts.put(key, value);
  }

  public Object getContext(String key)
  {
    return this.contexts.get(key);
  }

  public FileSystem getFileSystem()
  {
    return this.fileSystem;
  }

  public void setFileSystem(FileSystem fileSystem)
  {
    this.fileSystem = fileSystem;
  }
}