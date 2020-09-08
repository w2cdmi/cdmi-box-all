/*
 * 版权声明(Copyright Notice)：
 *      Copyright(C) 2017-2017 聚数科技成都有限公司。保留所有权利。
 *      Copyright(C) 2017-2017 www.cdmi.pw Inc. All rights reserved. 
 * 
 *      警告：本内容仅限于聚数科技成都有限公司内部传阅，禁止外泄以及用于其他的商业目
 */ 
package pw.cdmi.file.engine.object.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import pw.cdmi.file.engine.object.dao.FileObjectAttachmentDao;
import pw.cdmi.file.engine.object.domain.FileObject;
import pw.cdmi.file.engine.object.domain.FileObjectAttachment;
import pw.cdmi.file.engine.object.service.FileObjectAttachmentService;

/**
 * 
 * @author s90006125
 * 
 */
@Service("fileObjectAttachmentService")
public class FileObjectAttachmentServiceImp implements FileObjectAttachmentService
{
    @Autowired
    private FileObjectAttachmentDao fileObjectAttachmentDao;
    
    @Override
    public void saveFileObjectAttachment(FileObjectAttachment attachment)
    {
        fileObjectAttachmentDao.create(attachment);
    }
    
    @Override
    public FileObjectAttachment getFileObjectAttachment(FileObjectAttachment attachment)
    {
        return fileObjectAttachmentDao.get(attachment);
    }
    
    @Override
    public List<FileObjectAttachment> getFileObjectAttachments(FileObject fileObject)
    {
        return fileObjectAttachmentDao.queryAll(fileObject);
    }
    
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteFileObjectAttachment(FileObject fileObject)
    {
        fileObjectAttachmentDao.deleteByFileObject(fileObject);
    }
}
