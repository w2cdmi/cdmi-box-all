/*
 * 版权声明(Copyright Notice)：
 *      Copyright(C) 2017-2017 聚数科技成都有限公司。保留所有权利。
 *      Copyright(C) 2017-2017 www.cdmi.pw Inc. All rights reserved. 
 * 
 *      警告：本内容仅限于聚数科技成都有限公司内部传阅，禁止外泄以及用于其他的商业目
 */ 
package pw.cdmi.file.engine.object.manager;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import pw.cdmi.common.file.FileUtils;
import pw.cdmi.common.plugins.picture.PictureHandlerException;
import pw.cdmi.common.plugins.picture.PictureUtils;
import pw.cdmi.common.plugins.picture.RotateInfo;
import pw.cdmi.common.plugins.picture.Thumbnail;
import pw.cdmi.core.exception.InnerException;
import pw.cdmi.file.engine.filesystem.FileSystemFactory;
import pw.cdmi.file.engine.filesystem.exception.FSException;
import pw.cdmi.file.engine.filesystem.exception.FileAlreadyExistException;
import pw.cdmi.file.engine.filesystem.manage.FSEndpointSelector;
import pw.cdmi.file.engine.filesystem.model.FSEndpoint;
import pw.cdmi.file.engine.filesystem.model.FSObject;
import pw.cdmi.file.engine.filesystem.model.FileSystem;
import pw.cdmi.file.engine.object.domain.FileObject;
import pw.cdmi.file.engine.object.domain.FileObjectAttachment;
import pw.cdmi.file.engine.object.domain.ThumbnailFileObject;
import pw.cdmi.file.engine.object.service.FileObjectAttachmentService;

import javax.annotation.PostConstruct;
import java.io.*;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

/**
 * 
 * @author s90006125
 * 
 */
@Service("fileObjectAttachmentManager")
public class FileObjectAttachmentManager extends AbstractFileObjectManager
{
    private static final Logger LOGGER = LoggerFactory.getLogger(FileObjectAttachmentManager.class);
    
    private static final String SPLIT = "_";
    
    
    @Autowired
    private FileObjectAttachmentService fileObjectAttachmentService;
    
    @Autowired
    private FileObjectManager fileObjectManager;
    
    private static final String DEFAULT_IMG_NAME = "default-img-icon.png";
    
    @Value("${rotate.supported}")
    private boolean rotateSupported;
    
    @Value("${degree.orientation.1}")
    private int degreeOne;
    
    @Value("${degree.orientation.2}")
    private int degreeTwo;
    
    @Value("${degree.orientation.3}")
    private int degreeThree;
    
    @Value("${degree.orientation.4}")
    private int degreeFour;
    
    @Value("${degree.orientation.5}")
    private int degreeFive;
    
    @Value("${degree.orientation.6}")
    private int degreeSix;
    
    @Value("${degree.orientation.7}")
    private int degreeSeven;
    
    @Value("${degree.orientation.8}")
    private int degreeEight;
    
    @Value("${thumbnail.height.max}")
    private int maxHeight = 2048;
    
    @Value("${thumbnail.width.max}")
    private int maxWidth = 2048;
    
    private int minHeight = 1;
    
    private int minWidth = 1;
    
    private Map<Integer, RotateInfo> rotateInfos = new HashMap<Integer, RotateInfo>(8);
    
    @PostConstruct
    public void init()
    {
        rotateInfos.put(1, new RotateInfo(degreeOne));
        rotateInfos.put(2, new RotateInfo(degreeTwo));
        rotateInfos.put(3, new RotateInfo(degreeThree));
        rotateInfos.put(4, new RotateInfo(degreeFour));
        rotateInfos.put(5, new RotateInfo(degreeFive));
        rotateInfos.put(6, new RotateInfo(degreeSix));
        rotateInfos.put(7, new RotateInfo(degreeSeven));
        rotateInfos.put(8, new RotateInfo(degreeEight));
    }
    
    public boolean validateWidthAndHeigth(ThumbnailFileObject attachement)
    {
        if(null == attachement)
        {
            LOGGER.warn("attachement is null");
            return false;
        }
        
        if(attachement.getResetWidth() < minWidth || attachement.getResetWidth() > maxWidth
            || attachement.getResetHeight() < minHeight || attachement.getResetHeight() > maxHeight)
        {
            LOGGER.warn("width or height is warnning. width [ {} ], height [ {} ]", attachement.getResetWidth(), attachement.getResetHeight());
            return false;
        }
        
        return true;
    }
    
    /**
     * 删除一个文件的所有附件
     * 
     * @param fileObject
     */
    public void deleteFileObjectAttachment(FileObject fileObject)
    {
        List<FileObjectAttachment> attachments = fileObjectAttachmentService.getFileObjectAttachments(fileObject);
        if (null == attachments || attachments.isEmpty())
        {
            return;
        }
        fileObjectAttachmentService.deleteFileObjectAttachment(fileObject);
        
        for (FileObjectAttachment attachment : attachments)
        {
            tryDeleteFileObjectAttachment(attachment);
        }
    }
    
    private void tryDeleteFileObjectAttachment(FileObjectAttachment attachment)
    {
        try
        {
            FileSystem<FSObject> fileSystem = FileSystemFactory.getInstance(attachment.getStoragePath());
            FSObject srcFsObject = fileSystem.transToFSObject(attachment.getStoragePath());
            fileSystem.deleteObject(srcFsObject);
        }
        catch (RuntimeException e)
        {
            LOGGER.error("Delete Attachment [ " + attachment + " ] Failed.");
        }
        catch (Exception e)
        {
            LOGGER.error("Delete Attachment [ " + attachment + " ] Failed.");
        }
    }
    
    /**
     * 下载文件的附件
     * 
     * @param attachment
     * @return
     * @throws TimeoutException
     * @throws ExecutionException
     * @throws InterruptedException
     * @throws FSException
     * @throws IOException
     */
    public DownFileObjectContent getAttachment(FileObjectAttachment attachment) throws FSException,
        InterruptedException, ExecutionException, IOException, TimeoutException
    {
        // 查找以前是否已经生成过该文件附件
        FileObjectAttachment attachmentInDB = fileObjectAttachmentService.getFileObjectAttachment(attachment);
        
        if (null != attachmentInDB)
        {
            handleExistsAttachment(attachment, attachmentInDB);
        }
        else
        {
            handleNoneExistsAttach(attachment);
        }
        
        return new DownFileObjectContent(attachment, attachment.getObjectLength());
    }
    
    /**
     * 获取默认的缩略图
     * 
     * @param attachment
     * @return
     */
    public DownFileObjectContent getDefaultThumbnail(ThumbnailFileObject attachment)
    {
        InputStream is = null;
        try
        {
            is = getDefaultThumbnail();
            
            Thumbnail thumbnail = new Thumbnail(attachment.getResetWidth(), attachment.getResetHeight());
            thumbnail = PictureUtils.compress(thumbnail, is, null);
            
            attachment.setObjectLength(thumbnail.getSize());
            attachment.setInputStream(thumbnail.getInputStream());
            attachment.setData(thumbnail.getData());
            
            return new DownFileObjectContent(attachment, attachment.getObjectLength());
        }
        catch (Exception e)
        {
            String message = "Get Default Thumbnail Failed.";
            LOGGER.warn(message, e);
            throw new InnerException(message,e);
        }
        finally
        {
            IOUtils.closeQuietly(is);
        }
    }
    
    private InputStream getDefaultThumbnail() throws FileNotFoundException
    {
        URL url = FileObjectAttachmentManager.class.getResource("/");
        File defaultFile = new File(url.getPath() + DEFAULT_IMG_NAME);
        if (LOGGER.isDebugEnabled())
        {
            LOGGER.debug("defaultFile is : {}" + FileUtils.getCanonicalPathWithOutException(defaultFile));
        }
        return new FileInputStream(defaultFile);
    }
    
    /**
     * 生成并存储文件缩略图
     * 
     * @param attachment 附件信息
     * @param originalFile 原始文件
     * @return
     * @throws IOException
     * @throws FSException
     */
    private FileObjectAttachment createAndStorageThumbnail(ThumbnailFileObject attachment,
        RotateInfo rotateInfo, FileObject originalFile) throws IOException, FSException
    {
        // 获取压缩图片
        Thumbnail thumbnail = new Thumbnail(attachment.getResetWidth(), attachment.getResetHeight());
        
        try
        {
            thumbnail = PictureUtils.compress(thumbnail, originalFile.getInputStream(), rotateInfo);
        }
        catch (PictureHandlerException e)
        {
            String message = "compress picture failed.";
            LOGGER.warn(message, e);
            throw new InnerException(message, e);
        }
        
        attachment.setObjectLength(thumbnail.getSize());
        
        attachment.setInputStream(thumbnail.getInputStream());
        
        attachment.setData(thumbnail.getData());
        
        saveOnStorage(originalFile, attachment);
        
        return attachment;
    }
    
    /**
     * 已经存在附件时的处理
     * 
     * @param attachment
     * @param attachmentInDB
     */
    private void handleExistsAttachment(FileObjectAttachment attachment, FileObjectAttachment attachmentInDB)
    {
        // 如果已经存在压缩文件，就直接读取
        DownFileObjectContent downFileObjectContent = fileObjectManager.downloadFileObject(attachmentInDB);
        attachment.setObjectLength(downFileObjectContent.getContentLength());
        attachment.setInputStream(downFileObjectContent.getFileObject().getInputStream());
    }
    
    /**
     * 不存在附件时的处理
     * 
     * @param attachment
     * @throws TimeoutException
     * @throws IOException
     * @throws ExecutionException
     * @throws InterruptedException
     * @throws FSException
     */
    private void handleNoneExistsAttach(FileObjectAttachment attachment) throws FSException,
        InterruptedException, ExecutionException, IOException, TimeoutException
    {
        // 获取原始文件
        DownFileObjectContent downFileObjectContent = fileObjectManager.downloadFileObject(attachment.getObjectID());
        
        if (attachment instanceof ThumbnailFileObject)
        {
            RotateInfo rotateInfo = parseRotateInfo(attachment);
            
            attachment = handleThumb(attachment, rotateInfo, downFileObjectContent.getFileObject());
        }
        try
        {
            fileObjectAttachmentService.saveFileObjectAttachment(attachment);
        }
        catch (Exception e)
        {
            // 保存失败不报错
            LOGGER.warn("Save Attachment Failed [ " + attachment + " ] ", e);
        }
    }
    
    /**
     * 解析图片的方向信息<br>
     * 1 top left side 不用转<br>
     * 2 top right side <br>
     * 3 bottom right side 左转180<br>
     * 4 bottom left side<br>
     * 5 left side top<br>
     * 6 rigth side top 右转90<br>
     * 7 right side bottom<br>
     * 8 left side bottom 左转90<br>
     * 
     * @param attachment
     * @return
     */
    private RotateInfo parseRotateInfo(FileObjectAttachment attachment)
    {
        if (!rotateSupported)
        {
            return null;
        }
        InputStream is = null;
        try
        {
            DownFileObjectContent downFileObjectContent = fileObjectManager.downloadFileObject(attachment.getObjectID());
            is = downFileObjectContent.getFileObject().getInputStream();
            int orientation = PictureUtils.parseOrientation(is);
            
            LOGGER.info("orientation for [ " + attachment.logFormat() + " ] is [ " + orientation + " ] ");
            
            return rotateInfos.get(orientation);
        }
        catch (Exception e)
        {
            LOGGER.warn("parse rotate info failed.", e);
            return null;
        }
        finally
        {
            IOUtils.closeQuietly(is);
        }
    }
    
    /**
     * 缩略图处理
     * 
     * @param attachment
     * @param originalFile
     * @return
     */
    private FileObjectAttachment handleThumb(FileObjectAttachment attachment, RotateInfo rotateInfo,
        FileObject originalFile)
    {
        // 获取缩略图
        
        if (!(attachment instanceof ThumbnailFileObject))
        {
            String message = "not ThumbnailFileObject type.";
            LOGGER.warn(message);
            throw new InnerException(message);
        }
        
        try
        {
            attachment = createAndStorageThumbnail((ThumbnailFileObject) attachment, rotateInfo, originalFile);
        }
        catch (IOException e1)
        {
            String message = "IOException CreateAndStorageThumbnail Failed for: " + attachment.logFormat();
            LOGGER.warn(message, e1);
            throw new InnerException(message,e1);
        }
        catch (FSException e)
        {
            String message = "FSException CreateAndStorageThumbnail Failed for: " + attachment.logFormat();
            LOGGER.warn(message, e);
            throw new InnerException(message,e);
        }
        finally
        {
            IOUtils.closeQuietly(originalFile.getInputStream());
        }
        return attachment;
    }
    
    
    /**
     * 将转换后的文件保存到远程存储
     * 
     * @param originalFile 原始文件
     * @param attachment 附件
     * @throws IOException
     * @throws FSException
     */
    private void saveOnStorage(FileObject originalFile, FileObjectAttachment attachment) throws IOException,
        FSException
    {
        FileSystem<FSObject> fileSystem = FileSystemFactory.getInstance(originalFile.getStoragePath());
        FSObject fsObject = fileSystem.transToFSObject(originalFile.getStoragePath());
        
        FileObject fileObject = new FileObject(fsObject.getObjectKey() + SPLIT + attachment.getAttachment());
        fileObject.setObjectLength(attachment.getObjectLength());
        
        FSEndpoint fsEndpoint = FSEndpointSelector.assignWriteAbleStorage(fileObject);
        fileSystem = FileSystemFactory.getInstance(fsEndpoint);
        fsObject = fileSystem.transToFSObject(fsEndpoint, fileObject);
        
        // 设置一个新的文件存储的key
//        setNewFSObjectKey(fsObject, attachment);
        
        fsObject.setLength(attachment.getObjectLength());
        
        try
        {
            fsObject = fileSystem.putObject(fsObject, attachment.getInputStream());
            attachment.setStoragePath(fsObject.getPath());
            if (attachment.getInputStream() instanceof BufferedInputStream
                && attachment.getInputStream().markSupported())
            {
                attachment.getInputStream().reset();
            }
        }
        catch (FileAlreadyExistException e)
        {
            LOGGER.warn("ThumbnailFileObject [ " + fsObject.logFormat() + " ] Is Already Exist");
        }
        attachment.setStoragePath(fsObject.getPath());
    }
}
