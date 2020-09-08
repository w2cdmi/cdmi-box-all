package pw.cdmi.file.engine.filesystem.normal;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pw.cdmi.core.utils.HashTool;
import pw.cdmi.file.engine.core.config.SystemConfigContainer;
import pw.cdmi.file.engine.filesystem.model.NormalFile;
import pw.cdmi.file.engine.object.domain.FileObject;

public class PathGenerator
{
  private static final Logger LOGGER = LoggerFactory.getLogger(PathGenerator.class);

  private int subfolerNumber = 128;

  private String subFolderPrefix = "folder_";
  private static final String PATTERN_DAY = "yyyyMMdd";

  public PathGenerator()
  {
    this.subfolerNumber = SystemConfigContainer.getInteger("fs.normal.subfolder.number", Integer.valueOf(3000)).intValue();
    this.subFolderPrefix = SystemConfigContainer.getString("fs.normal.subfolder.prefix", 
      "folder_");
  }

  public String generate(FileObject fileObject)
  {
    StringBuilder path = new StringBuilder("");

    path.append(this.subFolderPrefix)
      .append(getFirstDirName())
      .append(NormalFile.separator)
      .append(HashTool.apply(path.toString() + fileObject.getObjectID()) % this.subfolerNumber)
      .append(NormalFile.separator);

    if (LOGGER.isDebugEnabled())
    {
      LOGGER.debug("New Save Path is: " + path.toString());
    }
    return path.toString();
  }

  private String getFirstDirName()
  {
    SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
    return format.format(new Date());
  }
}