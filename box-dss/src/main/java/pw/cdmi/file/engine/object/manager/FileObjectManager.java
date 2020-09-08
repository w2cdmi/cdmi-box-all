/*
 * 版权声明(Copyright Notice)：
 *      Copyright(C) 2017-2017 聚数科技成都有限公司。保留所有权利。
 *      Copyright(C) 2017-2017 www.cdmi.pw Inc. All rights reserved. 
 * 
 *      警告：本内容仅限于聚数科技成都有限公司内部传阅，禁止外泄以及用于其他的商业目
 */ 
package pw.cdmi.file.engine.object.manager;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pw.cdmi.core.exception.InnerException;
import pw.cdmi.file.engine.core.exception.errorcode.ErrorCode;
import pw.cdmi.file.engine.filesystem.FileSystemFactory;
import pw.cdmi.file.engine.filesystem.exception.FSException;
import pw.cdmi.file.engine.filesystem.io.MD5DigestInputStream;
import pw.cdmi.file.engine.filesystem.manage.FSEndpointSelector;
import pw.cdmi.file.engine.filesystem.model.FSEndpoint;
import pw.cdmi.file.engine.filesystem.model.FSObject;
import pw.cdmi.file.engine.filesystem.model.FileSystem;
import pw.cdmi.file.engine.object.domain.FileObject;
import pw.cdmi.file.engine.object.domain.FileObjectStatus;
import pw.cdmi.file.engine.object.exception.BadRequestException;
import pw.cdmi.file.engine.object.exception.CallbackFailedException;
import pw.cdmi.file.engine.object.exception.ObjectAlreadyExistException;
import pw.cdmi.file.engine.object.exception.ObjectNotFoundException;
import pw.cdmi.file.engine.object.exception.PreconditionFailedException;
import pw.cdmi.file.engine.object.service.CallBackService;
import pw.cdmi.file.engine.object.service.FileObjectDeleteService;
import pw.cdmi.file.engine.object.service.FileObjectService;

/**
 * 文件对象管理器，主要用于实现文件系统和数据库事务隔离
 * 
 * @author s90006125
 * 
 */
@Service("fileObjectManager")
public class FileObjectManager extends AbstractFileObjectManager
{
    private static final Range COMPLETE_FILE = null;
    
    private static final Logger LOGGER = LoggerFactory.getLogger(FileObjectManager.class);
    
    @Autowired
    private CallBackService callBackService;
    
    @Autowired
    private FileObjectService fileObjectService;
    
    @Autowired
    private FileObjectDeleteService fileObjectDeleteService;
    
    /**
     * 创建文件对象
     * 
     * @param fileObject
     * @param inputStream
     * @return
     */
    public FileObject createFileObject(String objectID, String objectName, long objectLength, InputStream inputStream,
        String callBackKey)
    {
        try
        {
            if (fileObjectService.isExist(objectID))
            {
                LOGGER.warn("FileObject [ " + objectID + " ] Already Exist");
                throw new ObjectAlreadyExistException();
            }
            
            FileObject fileObject = new FileObject(objectID);
            fileObject.setObjectLength(objectLength);
            fileObject.setObjectName(objectName);

            FSEndpoint fsEndpoint = FSEndpointSelector.assignWriteAbleStorage(fileObject);
            FileSystem<FSObject> fileSystem = FileSystemFactory.getInstance(fsEndpoint);
            
            FSObject fsObject = fileSystem.transToFSObject(fsEndpoint, fileObject);
            MD5DigestInputStream mis = new MD5DigestInputStream(inputStream);
            fsObject = fileSystem.putObject(fsObject, mis);
            fileObject.setObjectLength(fsObject.getLength());
            fileObject.setStoragePath(fsObject.getPath());
            
            // 方案变更, 数据内容计算由sha1变更为MD5
            fileObject.setSha1(generalMD5(mis));
            fileObject.setStatus(FileObjectStatus.COMPLETED);
            fileObjectService.saveFileObject(fileObject);
            
            fileObject.setCallBackKey(callBackKey);
            callBackService.updateFileObject(fileObject);
            
            return fileObject;
        }
        catch (FSException e)
        {
            String message = "CreateFileObject [ " + objectID + " ] Failed.";
            LOGGER.warn(message, e);
            throw new InnerException(message, e);
        }
        catch (CallbackFailedException e)
        {
            String message = "CreateFileObject [ " + objectID + " ] Failed.";
            LOGGER.warn(message, e);
            // 如果非回调导致的异常，则需要回调APP，上报上传失败信息
            callBackService.abortUpload(objectID, callBackKey);
            throw e;
        }
    }
    
    /**
     * 删除文件对象
     * 
     * @param fileObject
     */
    public boolean deleteFileObject(String objectID)
    {
        FileObject fileObject = fileObjectService.getFileObject(objectID);
        if (null == fileObject)
        {
            LOGGER.warn("fileObject [ " + objectID + " ] not exists.");
            return false;
        }
        
        if (FileObjectStatus.UPLOADING == fileObject.getStatus())
        {
            String message = "Object [" + fileObject.getObjectID() + "] Not Completed";
            LOGGER.warn(message);
            throw new PreconditionFailedException(ErrorCode.PreconditionFailedException.getCode(), message);
        }
        
        fileObjectDeleteService.createFileObjectDeleteTask(fileObject);
        
        return true;
    }
    
    /**
     * 通过文件对象下载文件
     * 
     * @param fileObject
     * @return
     */
    public DownFileObjectContent downloadFileObject(FileObject fileObject)
    {
        return downloadFileObject(fileObject, COMPLETE_FILE);
    }
    
    /**
     * 通过文件对象下载文件
     * 
     * @param fileObject
     * @return
     */
    public DownFileObjectContent downloadFileObject(FileObject fileObject, Range range)
    {
        if (FileObjectStatus.UPLOADING == fileObject.getStatus())
        {
            String message = "Object [" + fileObject.getObjectID() + "] uploaded yet completed";
            LOGGER.warn(message);
            throw new PreconditionFailedException(ErrorCode.PreconditionFailedException.getCode(), message);
        }
        
        // 修改WEB无法下载0KB文件的问题
        if (0L == fileObject.getObjectLength())
        {
            checkRangeForEmptyFile(range);
            return new DownFileObjectContent(fileObject, 0L);
        }
        
        try
        {
            FSObject fsObject = null;
            
            if (null == range)
            {
                fsObject = getFSObject(fileObject.getStoragePath());
            }
            else
            {
                Range realRange = parseAndCheckRange(range, fileObject);
                fsObject = getFSObject(fileObject.getStoragePath(), realRange.getStart(), realRange.getEnd());
            }
            if(null == fsObject)
            {
                String message = "FileObject [ " + fileObject.getObjectID() + " ] Not Found";
                LOGGER.warn(message);
                throw new ObjectNotFoundException(ErrorCode.ObjectNotFoundException.getCode(), message);
            }
            
            fileObject.setInputStream(fsObject.getInputStream());
            return new DownFileObjectContent(fileObject, fsObject.getLength());
        }
        catch (FSException e)
        {
            String message = "Get Object [ " + fileObject.getStoragePath() + " ] Failed.";
            LOGGER.warn(message, e);
            throw new InnerException(message, e);
        }
    }
    
    private Range parseAndCheckRange(Range range, FileObject fileObject)
    {
        long start = 0L;
        long end = fileObject.getObjectLength() - 1L;
        
        if (null != range.getStart() && null != range.getEnd())
        {
            // 表示头500个字节：bytes=0-499
            start = range.getStart();
            end = range.getEnd();
            if (end >= fileObject.getObjectLength())
            {
                end = fileObject.getObjectLength() - 1L;
            }
        }
        else if (null == range.getStart() && null != range.getEnd())
        {
            // 类似:表示最后500个字节：bytes=-500
            start = fileObject.getObjectLength() - range.getEnd();
            if (start <= 0)
            {
                start = 0L;
            }
        }
        else if (null != range.getStart() && null == range.getEnd())
        {
            // 表示500字节以后的范围：bytes=500-
            start = range.getStart();
        }
        
        checkLength(fileObject, range, start, end);
        
        return new Range(start, end);
    }
    
    /**
     * 通过文件ID下载文件
     * 
     * @param fileObject
     * @return
     */
    public DownFileObjectContent downloadFileObject(String objectID)
    {
        return downloadFileObject(objectID, COMPLETE_FILE);
    }
    
    /**
     * 通过文件ID下载文件
     * 
     * @param fileObject
     * @return
     */
    public DownFileObjectContent downloadFileObject(String objectID, Range range)
    {
        FileObject fileObject = fileObjectService.getFileObject(objectID);
        if (null == fileObject)
        {
            LOGGER.warn("Object [ " + objectID + " ] Not Found.");
            throw new ObjectNotFoundException();
        }
        
        if ((range != null) && range.getStart() != null && range.getEnd() == null) {
            if(range.getStart() > fileObject.getObjectLength()-1){
            	return new DownFileObjectContent(fileObject, 0L);
            }else{
            	range.setEnd(Long.valueOf(fileObject.getObjectLength() - 1L));
            }
          }
        
        return downloadFileObject(fileObject, range);
    }
    
    /**
     * 更加objectId，重新计算对象的标签
     * @param objectId
     * @return
     */
    public FileObject reCalcObjectMark(String objectId)
    {
        FileObject fileObject = fileObjectService.getFileObject(objectId);
        if (null == fileObject)
        {
            LOGGER.warn("Object [ " + objectId + " ] Not Found.");
            throw new ObjectNotFoundException();
        }
        
        MD5DigestInputStream inputStream = null;
        try
        {
            FSObject fsObject = getFSObject(fileObject.getStoragePath());
            
            inputStream = new MD5DigestInputStream(fsObject.getInputStream());
            
            byte[] buffer = new byte[1024 * 8];
            int len = -1;
            do
            {
                len = inputStream.read(buffer);
            } while (len != -1);
            
            fileObject.setSha1(generalMD5(inputStream));
            
            return fileObject;
        }
        catch (FSException e)
        {
            String message = "Get Object [ " + fileObject.getStoragePath() + " ] Failed.";
            LOGGER.warn(message, e);
            throw new InnerException(message, e);
        }
        catch (IOException e)
        {
            String message = "read inputStream failed. [ " + fileObject.getStoragePath() + " ] Failed.";
            LOGGER.warn(message, e);
            throw new InnerException(message, e);
        }
        finally
        {
            IOUtils.closeQuietly(inputStream);
        }
    }
    
    /**
     * 检查长度
     * 
     * @param fileObject
     * @param range
     * @param start
     * @param end
     */
    private void checkLength(FileObject fileObject, Range range, long start, long end)
    {
        if (start < 0 || start >= fileObject.getObjectLength() || end < 0
            || end >= fileObject.getObjectLength() || start > end)
        {
            String message = "Required parameter 'Range' is incorrect. Range: " + range.toString()
                + ", FileObject: " + fileObject.logFormat();
            LOGGER.warn(message);
            throw new BadRequestException(message);
        }
    }
    
    /**
     * 文件大小为0KB时校验Range参数
     * 
     * @param range
     */
    private void checkRangeForEmptyFile(Range range)
    {
        // WEB下载0KB文件Range为null, 客户端下载OKB文件Range为[0,0]
        if (range != null)
        {
            if (range.getStart() != 0 || range.getEnd() != 0)
            {
                String message = "'Range' is incorrect. Range: " + range.toString() + ", file length : 0";
                LOGGER.warn(message);
                throw new BadRequestException(message);
            }
        }
    }
    
    /**
     * 获取文件MD5值及文件片段MD5值, 格式"MD5:xxx;BlockMD5:yyy"
     * 
     * @param inputStream
     * @return
     */
    private String generalMD5(MD5DigestInputStream inputStream)
    {
        StringBuffer buffer = new StringBuffer("MD5:");
        buffer.append(inputStream.getMd5()).append(";BlockMD5:").append(inputStream.getSamplingMD5());
        return buffer.toString();
    }
}
