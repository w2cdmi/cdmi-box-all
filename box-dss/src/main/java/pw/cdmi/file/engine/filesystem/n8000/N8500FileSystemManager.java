package pw.cdmi.file.engine.filesystem.n8000;

import java.io.IOException;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import pw.cdmi.core.log.Level;
import pw.cdmi.core.utils.MethodLogAble;
import pw.cdmi.file.engine.filesystem.exception.FSException;
import pw.cdmi.file.engine.filesystem.exception.FileSystemIOException;
import pw.cdmi.file.engine.filesystem.exception.WrongFileSystemArgsException;
import pw.cdmi.file.engine.filesystem.model.FSAccessPath;
import pw.cdmi.file.engine.filesystem.model.FSDefinition;
import pw.cdmi.file.engine.filesystem.model.FSEndpoint;
import pw.cdmi.file.engine.filesystem.model.NormalFile;
import pw.cdmi.file.engine.filesystem.normal.NormalFileSystemManager;

@Service("n8500FileSystemManager")
public class N8500FileSystemManager extends NormalFileSystemManager
{
  private static final Logger LOGGER = LoggerFactory.getLogger(N8500FileSystemManager.class);
  private static final String PATH_SPLIT = ";";
  private static final int N8000_ENDPOINT_MIN_SPLIT = 2;

  public FSDefinition getDefinition()
  {
    return FSDefinition.N8500_FileSystem;
  }

  @MethodLogAble(Level.INFO)
  public FSEndpoint createFSEndpoint(FSEndpoint endpoint)
    throws FSException
  {
    return createNewFSEndpoint(endpoint);
  }

  public FSEndpoint updateFSEndpoint(FSEndpoint endpoint)
    throws FSException
  {
    endpoint.getAccessPaths().clear();

    if (StringUtils.isNotBlank(endpoint.getEndpoint()))
    {
      return createNewFSEndpoint(endpoint);
    }

    return endpoint;
  }

  private boolean isApproved(FSEndpoint endpoint)
  {
    if (endpoint.getEndpoint().split(":").length < N8000_ENDPOINT_MIN_SPLIT)
    {
      return false;
    }

    if ((endpoint.getMaxUtilization() == null) || (endpoint.getRetrieval() == null))
    {
      return false;
    }

    return true;
  }

  private String[] parsePath(FSEndpoint endpoint)
  {
    String[] temp = endpoint.getEndpoint().split(":");
    endpoint.setEndpoint(StringUtils.trimToEmpty(temp[0]));
    return StringUtils.trimToEmpty(temp[1]).split(";");
  }

  private FSEndpoint createNewFSEndpoint(FSEndpoint endpoint) throws FSException
  {
    LOGGER.info("create n8500 fileSystem with endpoint [ {} ]", endpoint.getEndpoint());
    if (!isApproved(endpoint))
    {
      String message = "Endpoint Info [" + endpoint + "] is not approved";
      if (LOGGER.isWarnEnabled())
      {
        LOGGER.warn(message);
        throw new WrongFileSystemArgsException(message, new String[] { endpoint.logFormat() });
      }
    }

    endpoint.setFsType(getDefinition().getType());

    String[] paths = parsePath(endpoint);
    NormalFile folder = null;
    String realPath = null;
    for (String path : paths)
    {
      realPath = StringUtils.trimToEmpty(path);

      if (!StringUtils.isBlank(realPath))
      {
        folder = new NormalFile(realPath);
        if ((!folder.exists()) || (folder.isFile()))
        {
          String message = "Path [" + realPath + "] is not exist";
          if (LOGGER.isWarnEnabled())
          {
            LOGGER.warn(message);
            throw new WrongFileSystemArgsException(message, new String[] { endpoint.logFormat() });
          }
        }

        endpoint.addAccessPaths(createAccessPath(endpoint, folder));
      }
    }
    return endpoint;
  }

  private FSAccessPath createAccessPath(FSEndpoint endpoint, NormalFile folder) throws FSException
  {
    try
    {
      StringBuilder accessPath = new StringBuilder(folder.getCanonicalPath());
      if (!accessPath.toString().endsWith(NormalFile.separator))
      {
        accessPath.append(NormalFile.separator);
      }

      FSAccessPath fsAccessPath = new FSAccessPath(accessPath.toString());

      createCheckReadFile(fsAccessPath);

      return fsAccessPath;
    }
    catch (IOException e)
    {
      LOGGER.warn("Create FSEndpoint Failed.", e);
      throw new FileSystemIOException(endpoint.logFormat(), e);
    }
  }
}