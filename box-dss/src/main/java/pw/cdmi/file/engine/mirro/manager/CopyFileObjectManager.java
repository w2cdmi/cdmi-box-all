/**
 * 
 */
package pw.cdmi.file.engine.mirro.manager;

import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pw.cdmi.core.exception.InnerException;
import pw.cdmi.file.engine.filesystem.FileSystemFactory;
import pw.cdmi.file.engine.filesystem.exception.FSException;
import pw.cdmi.file.engine.filesystem.io.MD5DigestInputStream;
import pw.cdmi.file.engine.filesystem.manage.FSEndpointSelector;
import pw.cdmi.file.engine.filesystem.model.FSEndpoint;
import pw.cdmi.file.engine.filesystem.model.FSObject;
import pw.cdmi.file.engine.filesystem.model.FileSystem;
import pw.cdmi.file.engine.object.domain.FileObject;
import pw.cdmi.file.engine.object.domain.FileObjectStatus;
import pw.cdmi.file.engine.object.exception.ObjectAlreadyExistException;
import pw.cdmi.file.engine.object.manager.FileObjectManager;
import pw.cdmi.file.engine.object.service.FileObjectService;

/**
 * @author w00186884
 * 
 */
@Service("copyFileObjectManager")
public class CopyFileObjectManager extends FileObjectManager
{
    private static final Logger LOGGER = LoggerFactory.getLogger(CopyFileObjectManager.class);
    
    @Autowired
    private FileObjectService fileObjectService;
    
    /**
     * 创建文件对象
     * 
     * @param fileObject
     * @param inputStream
     * @return
     */
    public FileObject createFileObject(String objectID, long objectLength, InputStream inputStream)
    {
        if (fileObjectService.isExist(objectID))
        {
            LOGGER.warn("FileObject [ " + objectID + " ] Already Exist");
            throw new ObjectAlreadyExistException();
        }
        
        FileObject fileObject = new FileObject(objectID);
        fileObject.setObjectLength(objectLength);
        try
        {
            FSEndpoint fsEndpoint = FSEndpointSelector.assignWriteAbleStorage(fileObject);
            FileSystem<FSObject> fileSystem = FileSystemFactory.getInstance(fsEndpoint);
            
            FSObject fsObject = fileSystem.transToFSObject(fsEndpoint, fileObject);
            MD5DigestInputStream mis = new MD5DigestInputStream(inputStream);
            fsObject = fileSystem.putObject(fsObject, mis);
            fileObject.setObjectLength(fsObject.getLength());
            fileObject.setStoragePath(fsObject.getPath());
            
            // 方案变更, 数据内容计算由sha1变更为MD5
            fileObject.setSha1(this.generalMD5(mis));
            fileObject.setStatus(FileObjectStatus.COMPLETED);
            fileObjectService.saveFileObject(fileObject);
            
            return fileObject;
        }
        catch (FSException e)
        {
            String message = "CreateFileObject [ " + objectID + " ] Failed.";
            LOGGER.warn(message, e);
            
            throw new InnerException(message, e);
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
