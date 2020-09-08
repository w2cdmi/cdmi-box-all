/*
 * 版权声明(Copyright Notice)：
 *      Copyright(C) 2017-2017 聚数科技成都有限公司。保留所有权利。
 *      Copyright(C) 2017-2017 www.cdmi.pw Inc. All rights reserved. 
 * 
 *      警告：本内容仅限于聚数科技成都有限公司内部传阅，禁止外泄以及用于其他的商业目
 */
package pw.cdmi.file.engine.realtime.manager;

import java.io.InputStream;

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
import pw.cdmi.file.engine.object.service.FileObjectService;
import pw.cdmi.file.engine.realtime.printer.RealTimePrinter;
import pw.cdmi.file.engine.realtime.util.RealTimeCopyTaskTool;

@Service("realTimeCopyFileObjectManager")
public class RealTimeCopyFileObjectManager
{
    
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
            RealTimePrinter.warn(RealTimeCopyTaskTool.LOG_TAG+"FileObject [ " + objectID + " ] Already Exist");
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
            RealTimePrinter.warn(RealTimeCopyTaskTool.LOG_TAG+message, e);
            
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
