/* 
 * 版权声明(Copyright Notice)：
 *      Copyright(C) 2017-2017 聚数科技成都有限公司。保留所有权利。
 *      Copyright(C) 2017-2017 www.cdmi.pw Inc. All rights reserved. 
 * 
 *      警告：本内容仅限于聚数科技成都有限公司内部传阅，禁止外泄以及用于其他的商业目
 */ 
package pw.cdmi.file.engine.object.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import pw.cdmi.file.engine.object.dao.FileObjectDao;
import pw.cdmi.file.engine.object.domain.FileObject;
import pw.cdmi.file.engine.object.service.FileObjectService;

/**
 * 
 * @author s90006125
 * 
 */
@Service("fileObjectService")
public class FileObjectServiceImpl implements FileObjectService
{
    @Autowired
    private FileObjectDao fileObjectDao;
    
    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor=Exception.class)
    public void saveFileObject(FileObject fileObject)
    {
        fileObjectDao.create(fileObject);
    }
    
    @Override
    public FileObject getFileObject(String objectID)
    {
        return fileObjectDao.get(new FileObject(objectID));
    }
    
    @Override
    public void updateFileObject(FileObject fileObject)
    {
        fileObjectDao.update(fileObject);
    }
    
    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor=Exception.class)
    public boolean deleteFileObject(FileObject fileObject)
    {
        fileObjectDao.delete(fileObject);
        return true;
    }
    
    @Override
    public boolean isExist(String objectID)
    {
        return null != getFileObject(objectID);
    }
}
