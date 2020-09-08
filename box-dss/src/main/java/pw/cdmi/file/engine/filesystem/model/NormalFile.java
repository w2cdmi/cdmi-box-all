package pw.cdmi.file.engine.filesystem.model;

import java.io.File;

public class NormalFile extends File
{
  private static final long serialVersionUID = -8900945659887088403L;

  public NormalFile(File file)
  {
    super(file.getPath());
  }

  public NormalFile(String pathname)
  {
    super(pathname);
  }

  public NormalFile(String parent, String child)
  {
    super(parent, child);
  }

  public int hashCode()
  {
    return super.hashCode();
  }

  public boolean equals(Object obj)
  {
    if ((obj instanceof NormalFile))
    {
      return super.equals(obj);
    }
    return false;
  }

  public NormalFile[] listFiles()
  {
    File[] temp = super.listFiles();
    if (temp == null)
    {
      return new NormalFile[0];
    }

    NormalFile[] files = new NormalFile[temp.length];
    for (int i = 0; i < temp.length; i++)
    {
      files[i] = new NormalFile(temp[i]);
    }

    return files;
  }

  public boolean delete()
  {
    if (isDirectory())
    {
      if (!deleteSubData(this))
      {
        return false;
      }

    }

    return super.delete();
  }

  private boolean deleteSubData(NormalFile dir)
  {
    boolean result = true;

    NormalFile[] files = dir.listFiles();

    if ((files == null) || (files.length <= 0))
    {
      return true;
    }
    for (NormalFile f : files)
    {
      result = (result) && (f.delete());
    }

    return result;
  }
}