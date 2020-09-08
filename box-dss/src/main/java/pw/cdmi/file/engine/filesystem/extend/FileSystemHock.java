package pw.cdmi.file.engine.filesystem.extend;

import pw.cdmi.file.engine.filesystem.FSMultipartObject;
import pw.cdmi.file.engine.filesystem.FSMultipartPart;
import pw.cdmi.file.engine.filesystem.exception.FSException;
import pw.cdmi.file.engine.filesystem.model.FSObject;

import java.io.InputStream;
import java.util.SortedSet;

public abstract interface FileSystemHock<T extends FSObject>
{
  public abstract void afterCopyObject(FileSystemContext paramFileSystemContext, T paramT1, T paramT2, T paramT3)
    throws FSException;

  public abstract void afterDeleteObject(FileSystemContext paramFileSystemContext, T paramT, boolean paramBoolean)
    throws FSException;

  public abstract void afterGetObject(FileSystemContext paramFileSystemContext, T paramT1, Long paramLong1, Long paramLong2, T paramT2)
    throws FSException;

  public abstract void afterGetObject(FileSystemContext paramFileSystemContext, T paramT1, T paramT2)
    throws FSException;

  public abstract void afterMultipartAbortUpload(FileSystemContext paramFileSystemContext, T paramT, String paramString, boolean paramBoolean)
    throws FSException;

  public abstract void afterMultipartCompleteUpload(FileSystemContext paramFileSystemContext, T paramT, String paramString, FSMultipartObject paramFSMultipartObject)
    throws FSException;

  public abstract void afterMultipartListParts(FileSystemContext paramFileSystemContext, T paramT, String paramString, SortedSet<FSMultipartPart<T>> paramSortedSet)
    throws FSException;

  public abstract void afterMultipartStartUpload(FileSystemContext paramFileSystemContext, T paramT, FSMultipartObject paramFSMultipartObject)
    throws FSException;

  public abstract void afterMultipartUploadPart(FileSystemContext paramFileSystemContext, T paramT, MultipartParam paramMultipartParam, InputStream paramInputStream, FSMultipartPart<T> paramFSMultipartPart)
    throws FSException;

  public abstract void afterPutObject(FileSystemContext paramFileSystemContext, T paramT1, InputStream paramInputStream, T paramT2)
    throws FSException;

  public abstract void beforeCopyObject(FileSystemContext paramFileSystemContext, T paramT1, T paramT2)
    throws FSException;

  public abstract void beforeDeleteObject(FileSystemContext paramFileSystemContext, T paramT)
    throws FSException;

  public abstract void beforeGetObject(FileSystemContext paramFileSystemContext, T paramT)
    throws FSException;

  public abstract void beforeGetObject(FileSystemContext paramFileSystemContext, T paramT, Long paramLong1, Long paramLong2)
    throws FSException;

  public abstract void beforeMultipartAbortUpload(FileSystemContext paramFileSystemContext, T paramT, String paramString)
    throws FSException;

  public abstract void beforeMultipartCompleteUpload(FileSystemContext paramFileSystemContext, T paramT, String paramString)
    throws FSException;

  public abstract void beforeMultipartListParts(FileSystemContext paramFileSystemContext, T paramT, String paramString)
    throws FSException;

  public abstract void beforeMultipartStartUpload(FileSystemContext paramFileSystemContext, T paramT)
    throws FSException;

  public abstract void beforeMultipartUploadPart(FileSystemContext paramFileSystemContext, T paramT, String paramString, int paramInt, InputStream paramInputStream)
    throws FSException;

  public abstract void beforePutObject(FileSystemContext paramFileSystemContext, T paramT, InputStream paramInputStream)
    throws FSException;
}