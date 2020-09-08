package pw.cdmi.file.engine.filesystem.extend;

import pw.cdmi.file.engine.filesystem.FSMultipartObject;
import pw.cdmi.file.engine.filesystem.FSMultipartPart;
import pw.cdmi.file.engine.filesystem.exception.FSException;
import pw.cdmi.file.engine.filesystem.model.FSObject;

import java.io.InputStream;
import java.util.SortedSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class BaseFileSystemHock<T extends FSObject>
  implements FileSystemHock<T>
{
  private static final Logger LOGGER = LoggerFactory.getLogger(BaseFileSystemHock.class);

  public void beforePutObject(FileSystemContext context, T fsObject, InputStream inputStream)
    throws FSException
  {
    if (LOGGER.isDebugEnabled())
    {
      LOGGER.debug("beforePutObject do nothing.");
    }
  }

  public void afterPutObject(FileSystemContext context, T fsObject, InputStream inputStream, T returnObject)
    throws FSException
  {
    if (LOGGER.isDebugEnabled())
    {
      LOGGER.debug("afterPutObject do nothing.");
    }
  }

  public void beforeGetObject(FileSystemContext context, T fsObject)
    throws FSException
  {
    if (LOGGER.isDebugEnabled())
    {
      LOGGER.debug("beforeGetObject do nothing.");
    }
  }

  public void afterGetObject(FileSystemContext context, T fsObject, T returnObject)
    throws FSException
  {
    if (LOGGER.isDebugEnabled())
    {
      LOGGER.debug("afterGetObject do nothing.");
    }
  }

  public void beforeGetObject(FileSystemContext context, T fsObject, Long start, Long end)
    throws FSException
  {
    if (LOGGER.isDebugEnabled())
    {
      LOGGER.debug("beforeGetObject do nothing.");
    }
  }

  public void afterGetObject(FileSystemContext context, T fsObject, Long start, Long end, T returnObject)
    throws FSException
  {
    if (LOGGER.isDebugEnabled())
    {
      LOGGER.debug("afterGetObject do nothing.");
    }
  }

  public void beforeDeleteObject(FileSystemContext context, T fsObject)
    throws FSException
  {
    if (LOGGER.isDebugEnabled())
    {
      LOGGER.debug("beforeDeleteObject do nothing.");
    }
  }

  public void afterDeleteObject(FileSystemContext context, T fsObject, boolean result)
    throws FSException
  {
    if (LOGGER.isDebugEnabled())
    {
      LOGGER.debug("afterDeleteObject do nothing.");
    }
  }

  public void beforeCopyObject(FileSystemContext context, T srcFsObject, T destFsObject)
    throws FSException
  {
    if (LOGGER.isDebugEnabled())
    {
      LOGGER.debug("beforeCopyObject do nothing.");
    }
  }

  public void afterCopyObject(FileSystemContext context, T srcFsObject, T destFsObject, T returnObject)
    throws FSException
  {
    if (LOGGER.isDebugEnabled())
    {
      LOGGER.debug("afterCopyObject do nothing.");
    }
  }

  public void beforeMultipartStartUpload(FileSystemContext context, T srcFsObject)
    throws FSException
  {
    if (LOGGER.isDebugEnabled())
    {
      LOGGER.debug("beforeMultipartStartUpload do nothing.");
    }
  }

  public void afterMultipartStartUpload(FileSystemContext context, T srcFsObject, FSMultipartObject result)
    throws FSException
  {
    if (LOGGER.isDebugEnabled())
    {
      LOGGER.debug("afterMultipartStartUpload do nothing.");
    }
  }

  public void beforeMultipartUploadPart(FileSystemContext context, T fsObject, String uploadID, int partNumber, InputStream inputStream)
    throws FSException
  {
    if (LOGGER.isDebugEnabled())
    {
      LOGGER.debug("beforeMultipartUploadPart do nothing.");
    }
  }

  public void afterMultipartUploadPart(FileSystemContext context, T fsObject, MultipartParam multiParam, InputStream inputStream, FSMultipartPart<T> result)
    throws FSException
  {
    if (LOGGER.isDebugEnabled())
    {
      LOGGER.debug("afterMultipartUploadPart do nothing.");
    }
  }

  public void beforeMultipartCompleteUpload(FileSystemContext context, T fsObject, String uploadID)
    throws FSException
  {
    if (LOGGER.isDebugEnabled())
    {
      LOGGER.debug("beforeMultipartCompleteUpload do nothing.");
    }
  }

  public void afterMultipartCompleteUpload(FileSystemContext context, T fsObject, String uploadID, FSMultipartObject result)
    throws FSException
  {
    if (LOGGER.isDebugEnabled())
    {
      LOGGER.debug("afterMultipartCompleteUpload do nothing.");
    }
  }

  public void beforeMultipartAbortUpload(FileSystemContext context, T fsObject, String uploadID)
    throws FSException
  {
    if (LOGGER.isDebugEnabled())
    {
      LOGGER.debug("beforeMultipartAbortUpload do nothing.");
    }
  }

  public void afterMultipartAbortUpload(FileSystemContext context, T fsObject, String uploadID, boolean result)
    throws FSException
  {
    if (LOGGER.isDebugEnabled())
    {
      LOGGER.debug("afterMultipartAbortUpload do nothing.");
    }
  }

  public void beforeMultipartListParts(FileSystemContext context, T fsObject, String uploadID)
    throws FSException
  {
    if (LOGGER.isDebugEnabled())
    {
      LOGGER.debug("beforeMultipartListParts do nothing.");
    }
  }

  public void afterMultipartListParts(FileSystemContext context, T fsObject, String uploadID, SortedSet<FSMultipartPart<T>> result)
    throws FSException
  {
    if (LOGGER.isDebugEnabled())
    {
      LOGGER.debug("afterMultipartListParts do nothing.");
    }
  }
}