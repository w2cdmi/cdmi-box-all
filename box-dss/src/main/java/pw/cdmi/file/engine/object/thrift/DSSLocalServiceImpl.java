package pw.cdmi.file.engine.object.thrift;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.thrift.TException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.huawei.sharedrive.thrift.dc.local.DSSLocalService;
import com.huawei.sharedrive.thrift.dc.local.TLocalServiceException;

import pw.cdmi.common.log.LoggerUtil;
import pw.cdmi.core.exception.InnerException;
import pw.cdmi.file.engine.filesystem.FileSystemFactory;
import pw.cdmi.file.engine.filesystem.exception.FSException;
import pw.cdmi.file.engine.filesystem.io.MD5DigestInputStream;
import pw.cdmi.file.engine.filesystem.manage.FSEndpointSelector;
import pw.cdmi.file.engine.filesystem.manage.cache.FSEndpointCache;
import pw.cdmi.file.engine.filesystem.model.FSEndpoint;
import pw.cdmi.file.engine.filesystem.model.FSObject;
import pw.cdmi.file.engine.filesystem.model.FileSystem;
import pw.cdmi.file.engine.object.domain.FileObject;
import pw.cdmi.file.engine.object.domain.FileObjectStatus;
import pw.cdmi.file.engine.object.manager.DownFileObjectContent;
import pw.cdmi.file.engine.object.manager.FileObjectManager;
import pw.cdmi.file.engine.object.service.FileObjectService;

public class DSSLocalServiceImpl implements DSSLocalService.Iface
{
    private static final Logger LOGGER = LoggerFactory.getLogger(DSSLocalServiceImpl.class);
    
    @Resource
    private FileObjectManager fileObjectManager;
    
    @Resource
    private FileObjectService fileObjectService;
    
    private String optSuccess = "200";
    
    private String optFail = "500";
    
    @Override
    public String downloadFileObject(String objectId, String localPath) throws TException
    {
        LoggerUtil.regiestThreadLocalLog();
        String status = this.getStatus();
        if (DssLocalStatusEnum.OFFLINE.getValue().equals(status))
        {
            throw new TLocalServiceException("", "DSS service is :" + status);
        }
        
        if (StringUtils.isBlank(objectId) || StringUtils.isBlank(localPath))
        {
            throw new IllegalArgumentException("objectId or localPath is blank");
        }
        
        File file = new File(localPath);
        
        // 该方法对objectId是否存在，或是文件状态在上传中做校验
        DownFileObjectContent downFileObjectContent = fileObjectManager.downloadFileObject(objectId);
        if(!downloadFile(downFileObjectContent, file))
        {
            return optFail;
        }
        return optSuccess;
    }
    
    @Override
    public String getStatus() throws TException
    {
        LoggerUtil.regiestThreadLocalLog();
        String status = null;
        try{
            List<FSEndpoint> allWriteAble = FSEndpointCache.getAllWriteAbleEndpoints();
            status = DssLocalStatusEnum.NORMAL.getValue();
            boolean isAllWriteAble = true;
            boolean isAllEnable = true;
            if (CollectionUtils.isEmpty(allWriteAble))
            {
                allWriteAble = FSEndpointCache.getAllEnableEndpoints();
                isAllWriteAble = false;
            }
            
            if (CollectionUtils.isEmpty(allWriteAble))
            {
                isAllEnable = false;
            }
            
            if(!isAllWriteAble && !isAllEnable){
                status = DssLocalStatusEnum.OFFLINE.getValue();
            }
        }catch(Exception ex){
            LOGGER.error("Failed to getStatus: " + ex.getMessage());
        }
        return status;
    }
    
    @Override
    public String uploadFileObject(String objectId, String localPath) throws TException
    {
        LoggerUtil.regiestThreadLocalLog();
        InputStream inputStream = null;
        try
        {
            if (StringUtils.isBlank(objectId) || StringUtils.isBlank(localPath))
            {
                throw new IllegalArgumentException("objectId or localPath is blank");
            }
            
            File file = new File(localPath);
            if (!file.isFile())
            {
                throw new IllegalArgumentException("file not found :" + localPath);
            }
            this.uploadFile(objectId, file);
        }
        catch (RuntimeException ex)
        {
            LOGGER.error("Failed to uploadFileObject: " + ex.getMessage());
            LOGGER.debug(ex.getClass().getName(), ex);
            throw new InnerException("Failed to uploadFileObject: " + ex.getMessage(), ex);
            //throw new TLocalServiceException("Failed to uploadFileObject:", " " + ex.getMessage());
        }
        catch (IOException e)
        {
            String message = "read inputStream failed Failed.";
            LOGGER.warn(message, e);
            throw new InnerException(message, e);
        }
        finally
        {
            IOUtils.closeQuietly(inputStream);
        }
        return optSuccess;
    }
    
    private void uploadFile(String objectID, File file) throws IOException, FSException
    {
        InputStream inputStream = new FileInputStream(file);
        try
        {
            if (fileObjectService.isExist(objectID))
            {
                throw new IllegalArgumentException("FileObject [ " + objectID + " ] Already Exist");
            }
            
            FileObject fileObject = new FileObject(objectID);
            fileObject.setObjectLength(file.length());
            FSEndpoint fsEndpoint = FSEndpointSelector.assignWriteAbleStorage(fileObject);
            FileSystem<FSObject> fileSystem = FileSystemFactory.getInstance(fsEndpoint);
            
            FSObject fsObject = fileSystem.transToFSObject(fsEndpoint, fileObject);
            MD5DigestInputStream mis = new MD5DigestInputStream(inputStream);
            fsObject = fileSystem.putObject(fsObject, mis);
            fileObject.setObjectLength(fsObject.getLength());
            fileObject.setStoragePath(fsObject.getPath());
            
            fileObject.setSha1(generalMD5(mis));
            fileObject.setStatus(FileObjectStatus.COMPLETED);
            fileObjectService.saveFileObject(fileObject);
        }
        finally
        {
            IOUtils.closeQuietly(inputStream);
        }
        
    }
    
    private boolean downloadFile(DownFileObjectContent downFileObjectContent, File file)  
    {
        InputStream is = null;
        OutputStream out = null;
        boolean isBadFile = false;
        boolean isDownload = true;
        try
        {
            out = new FileOutputStream(file);
            if (0L == downFileObjectContent.getContentLength())
            {
                return isDownload;
            }
            
            if (null != downFileObjectContent.getFileObject().getData()
                && downFileObjectContent.getFileObject().getData().length != 0)
            {
                out.write(downFileObjectContent.getFileObject().getData());
                out.flush();
            }
            else
            {
                is = downFileObjectContent.getFileObject().getInputStream();
                byte[] b = new byte[1024 * 64];
                int length = is.read(b);
                while (length > 0)
                {
                    out.write(b, 0, length);
                    out.flush();
                    length = is.read(b);
                }
            }
            
        }catch (IOException ex)
        {
            LOGGER.error("Failed to downloadFile: " + ex.getMessage());
            if (LOGGER.isDebugEnabled())
            {
                LOGGER.debug(ex.getClass().getName(), ex);
            }
            isBadFile = true;
            isDownload = false;
            throw new InnerException("Failed to downloadFile: " + ex.getMessage(), ex);
        }
        finally
        {
            IOUtils.closeQuietly(out);
            IOUtils.closeQuietly(is);
            if(isBadFile && file.exists())
            {
                deleteBadFile(file);
                isDownload = false;
            }
        }
        
        return isDownload;
    }
    
    /**
     * 删除垃圾文件
     * 
     * @param inputStream
     * @return
     */
    private boolean deleteBadFile(File file){
        return file.delete();
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
