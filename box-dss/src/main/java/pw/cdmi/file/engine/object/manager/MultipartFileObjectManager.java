/*
 * 版权声明(Copyright Notice)：
 *      Copyright(C) 2017-2017 聚数科技成都有限公司。保留所有权利。
 *      Copyright(C) 2017-2017 www.cdmi.pw Inc. All rights reserved. 
 * 
 *      警告：本内容仅限于聚数科技成都有限公司内部传阅，禁止外泄以及用于其他的商业目
 */ 
package pw.cdmi.file.engine.object.manager;

import java.io.InputStream;
import java.util.SortedSet;
import java.util.TreeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pw.cdmi.core.exception.InnerException;
import pw.cdmi.file.engine.core.exception.errorcode.ErrorCode;
import pw.cdmi.file.engine.filesystem.FSMultipartObject;
import pw.cdmi.file.engine.filesystem.FSMultipartPart;
import pw.cdmi.file.engine.filesystem.FileSystemFactory;
import pw.cdmi.file.engine.filesystem.exception.FSException;
import pw.cdmi.file.engine.filesystem.exception.FileNotFoundException;
import pw.cdmi.file.engine.filesystem.manage.FSEndpointSelector;
import pw.cdmi.file.engine.filesystem.model.FSEndpoint;
import pw.cdmi.file.engine.filesystem.model.FSObject;
import pw.cdmi.file.engine.filesystem.model.FileSystem;
import pw.cdmi.file.engine.object.domain.FileObject;
import pw.cdmi.file.engine.object.domain.FileObjectStatus;
import pw.cdmi.file.engine.object.domain.MultipartFileObject;
import pw.cdmi.file.engine.object.domain.MultipartPart;
import pw.cdmi.file.engine.object.exception.BadRequestException;
import pw.cdmi.file.engine.object.exception.CallbackFailedException;
import pw.cdmi.file.engine.object.exception.ObjectAlreadyExistException;
import pw.cdmi.file.engine.object.exception.ObjectNotFoundException;
import pw.cdmi.file.engine.object.exception.PreconditionFailedException;
import pw.cdmi.file.engine.object.service.CallBackService;
import pw.cdmi.file.engine.object.service.FileObjectService;
import pw.cdmi.file.engine.object.service.MultipartFileObjectService;

/**
 * 分片文件对象管理器，主要用于实现文件系统和数据库事务隔离
 * 
 * @author s90006125
 * 
 */
@SuppressWarnings("PMD.LooseCoupling")
@Service("multipartFileObjectManager")
public class MultipartFileObjectManager extends AbstractFileObjectManager
{
    private static final Logger LOGGER = LoggerFactory.getLogger(MultipartFileObjectManager.class);
    
    @Autowired
    private CallBackService callBackService;
    
    @Autowired
    private MultipartFileObjectService multipartFileObjectService;
    
    @Autowired
    private MultipartFileObjectMergeTask multipartFileObjectMergeTask;
    
    @Autowired
    private FileObjectService fileObjectService;
    
    /**
     * 取消分片上传
     * 
     * @param fileObject
     * @return
     */
    public boolean abortMultipartUpload(String objectID)
    {
        MultipartFileObject fileObject = getUploadingMultipartFileObject(objectID);
        
        multipartFileObjectService.abortMultipartUpload(fileObject);
        
        callBackService.abortUpload(objectID, fileObject.getCallBackKey());
        
        FileSystem<FSObject> fileSystem = FileSystemFactory.getInstance(fileObject.getStoragePath());
        try
        {
            fileSystem.multipartAbortUpload(fileSystem.transToFSObject(fileObject.getStoragePath()),
                fileObject.getUploadID());
        }
        catch (FSException e)
        {
            String message = "Multipart Abort Failed [ " + objectID + " ]";
            LOGGER.warn(message, e);
            throw new InnerException(message, e);
        }
        
        return true;
    }
    
    /**
     * 完成分片上传
     * 
     * @param fileObject
     * @return
     */
    public MultipartFileObject completeMultipartUpload(String objectID, TreeSet<MultipartPart> parts)
    {
        MultipartFileObject fileObject = listMultipartParts(objectID);
        
        if (!isEquals(parts, fileObject.getParts()))
        {
            String message = "Required parameter 'partList' is not incorrect.";
            LOGGER.warn(message);
            throw new BadRequestException(ErrorCode.BadRequestException.getCode(), message);
        }
        
        FileSystem<FSObject> fileSystem = FileSystemFactory.getInstance(fileObject.getStoragePath());
        try
        {
            FSObject fsObject = fileSystem.transToFSObject(fileObject.getStoragePath());
            FSMultipartObject fsMultipartObject = fileSystem.multipartCompleteUpload(fsObject,
                fileObject.getUploadID());
            fileObject.setObjectLength(fsMultipartObject.getLength());
        }
        catch (FSException e)
        {
            String message = "Commit Multipart Failed [ " + objectID + " ]";
            LOGGER.warn(message, e);
            throw new InnerException(message, e);
        }
        
        try
        {
            updateObjectStatusToCommit(fileObject);
        }
        catch(CallbackFailedException e)
        {
            String message = "CreateFileObject [ " + objectID + " ] Failed.";
            LOGGER.warn(message, e);
            callBackService.abortUpload(objectID, fileObject.getCallBackKey());
            throw e;
        }
        
        return fileObject;
    }
    
    /**
     * 更新文件状态为提交后状态
     * 
     * @param fileObject
     */
    private void updateObjectStatusToCommit(MultipartFileObject fileObject)
    {
        multipartFileObjectService.completeMultipartUpload(fileObject);
        
        callBackService.updateFileObject(fileObject);
        
        // 通知集群
        try
        {
            multipartFileObjectMergeTask.activeJob();
        }
        catch (Exception e)
        {
            LOGGER.warn("active job failed.", e);
        }
    }
    
    /**
     * 创建文件对象分片
     * 
     * @param objectID
     * @param callBackKey
     * @param partID
     * @param inputStream
     * @return
     * @throws FSException 
     */
    public MultipartPart createMultipartPart(String objectID, long objLength, String callBackKey, int partID,
        InputStream inputStream) throws FSException
    {
        MultipartFileObject fileObject = null;
        try
        {
            fileObject = getUploadingMultipartFileObject(objectID);
        }
        catch (ObjectNotFoundException e)
        {
            fileObject = initMultipartUpload(objectID, callBackKey);
        }
        
        try
        {
            FileSystem<FSObject> fileSystem = FileSystemFactory.getInstance(fileObject.getStoragePath());
            FSObject fsObject = fileSystem.transToFSObject(fileObject.getStoragePath());
            
            // add by shenqing 2016-5-23 begin
            fsObject.setLength(objLength);
            // add by shenqing 2016-5-23 end
            
            fileSystem.multipartUploadPart(fsObject, fileObject.getUploadID(), partID, inputStream);
        }
        catch (FSException e)
        {
            String message = "Create Multipart Part For [ " + objectID + " ] Failed.";
            LOGGER.warn(message, e);
            throw new InnerException(message, e);
        }
        multipartFileObjectService.updateLastModifyTime(fileObject);
        return new MultipartPart(partID);
    }
    
    /**
     * 获取正在上传的分片对象，如果分片已经执行了提交命令 或者对象不存在，则抛出异常 <br>
     * 该方法不会返回null，返回的object一定是还未合并的分片对象
     * 
     * @param objectID
     * @return
     */
    private MultipartFileObject getUploadingMultipartFileObject(String objectID)
    {
    	if(objectID.indexOf("_")>-1){
    		objectID=objectID.split("_")[1];
    	}
        MultipartFileObject fileObject = multipartFileObjectService.getMultipartUpload(objectID);
        FileObject obj = null;
        if (null == fileObject)
        {
            obj = fileObjectService.getFileObject(objectID);
            if (null == obj)
            {
                String message = "MultipartFileObject [ " + objectID + " ] Not Found";
                LOGGER.warn(message);
                throw new ObjectNotFoundException(ErrorCode.ObjectNotFoundException.getCode(), message);
            }
        }
        
        if (null != obj || FileObjectStatus.UPLOADING != fileObject.getStatus())
        {
            String message = "Multipart Object [ " + objectID + " ] has been submitted";
            LOGGER.warn(message);
            throw new PreconditionFailedException(ErrorCode.PreconditionFailedException.getCode(), message);
        }
        
        return fileObject;
    }
    
    /**
     * 初始化分片上传
     * 
     * @param objectID
     * @param callBackKey
     * @return
     * @throws FSException
     */
    public MultipartFileObject initMultipartUpload(String objectID, String callBackKey) throws FSException
    {
        LOGGER.info("Start Init Multipart Upload [ " + objectID + ", " + callBackKey + " ]");
        
        // 如果已经存在，不能初始化
        if (null != multipartFileObjectService.getMultipartUpload(objectID)
            || null != fileObjectService.getFileObject(objectID))
        {
            LOGGER.warn("FileObject [ {} ] Already Exist.", objectID);
            throw new ObjectAlreadyExistException();
        }
        
        MultipartFileObject fileObject = new MultipartFileObject(objectID, callBackKey);
        
        FSEndpoint fsEndpoint = FSEndpointSelector.assignWriteAbleStorage(fileObject);
        FileSystem<FSObject> fileSystem = FileSystemFactory.getInstance(fsEndpoint);
        FSObject fsObject = fileSystem.transToFSObject(fsEndpoint, fileObject);
        FSMultipartObject multipartObject = fileSystem.multipartStartUpload(fsObject);
        
        fileObject.setUploadID(multipartObject.getMObjUploadID());
        fileObject.setPartSize(multipartObject.getPartSize());
        fileObject.setStoragePath(fsObject.getPath());
        fileObject.setStatus(FileObjectStatus.UPLOADING);
        LOGGER.warn("fileObject etag value {}", fileObject.logFormat(), fileObject.getEtag()); 
        
        try
        {
            multipartFileObjectService.initMultipartUpload(fileObject);
        }
        catch(Exception e)
        {
            LOGGER.warn("initMultipartUpload failed. {}", fileObject.logFormat(), e); 
            fileSystem.multipartAbortUpload(fsObject, fileObject.getUploadID());
            throw new InnerException("init multipartUpload : " + fileObject.getObjectID() + " failed.", e);
        }
        
        return fileObject;
    }
    
    /**
     * 列举分片文件分片列表，只有未提交状态的分片，可与进行列举
     * 
     * @param fileObject
     * @return
     */
    public MultipartFileObject listMultipartParts(String objectID)
    {
        MultipartFileObject fileObject = getUploadingMultipartFileObject(objectID);
        
        FileSystem<FSObject> fileSystem = FileSystemFactory.getInstance(fileObject.getStoragePath());
        
        FSObject fsObject = null;
        try
        {
            fsObject = fileSystem.transToFSObject(fileObject.getStoragePath());
            SortedSet<FSMultipartPart<FSObject>> parts = fileSystem.multipartListParts(fsObject,
                fileObject.getUploadID());
            fileObject.getParts().clear();
            
            MultipartPart multipartPart = null;
            for (FSMultipartPart<FSObject> part : parts)
            {
                multipartPart = new MultipartPart(part.getPartNumber(), part.getFSObject().getLength(),part.getETag(), part.getPartCRC());
                fileObject.getParts().add(multipartPart);
            }
            
            return fileObject;
        }
        catch (FileNotFoundException e)
        {
            if (null == fsObject || !existOnFileSystem(fsObject, fileSystem))
            {
                String message = "MultipartFileObject [ " + objectID + " ] Not Found";
                LOGGER.warn(message);
                throw new ObjectNotFoundException(ErrorCode.ObjectNotFoundException.getCode(), message, e);
            }
            
            // 如果列举的时候，发现底层存储文件已经存在了，则表示该文件已经提交过，则再次更新数据库状态，合并文件，并抛出异常
           
                updateObjectStatusToCommit(fileObject);
            
            
            String message = "Multipart Object [ " + objectID + " ] has been submitted";
            LOGGER.warn(message);
            throw new PreconditionFailedException(ErrorCode.PreconditionFailedException.getCode(), message, e);
        }
        catch (FSException e)
        {
            String message = "List Multipart Failed [ " + objectID + " ]";
            LOGGER.warn(message, e);
            throw new InnerException(message, e);
        }
    }
    
    private boolean existOnFileSystem(FSObject fsObject, FileSystem<FSObject> fileSystem)
    {
        if (null == fsObject || null == fileSystem)
        {
            LOGGER.warn("fsObject is null, or fileSystem is null.");
            return false;
        }
        
        try
        {
            return fileSystem.checkObjectExist(fsObject);
        }
        catch (FSException e)
        {
            LOGGER.warn("check failed.", e);
            return false;
        }
    }
    
    /**
     * 判断两个TreeSet的内容是否相同<br>
     * 调用该方法前，必须保证两个参数都不为空
     * 
     * @param parts1
     * @param parts2
     * @return
     */
    private boolean isEquals(TreeSet<MultipartPart> parts1, TreeSet<MultipartPart> parts2)
    {
        if (parts1.size() == parts2.size())
        {
            if (parts1.isEmpty() && parts2.isEmpty())
            {
                return true;
            }
            
            // 第一个和最后一个都相同
            if (parts1.first().getPartId() == parts2.first().getPartId()
                && parts1.last().getPartId() == parts2.last().getPartId())
            {
                return true;
            }
        }
        
        return false;
    }
}
