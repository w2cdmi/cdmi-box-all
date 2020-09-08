/*
 * 版权声明(Copyright Notice)：
 *      Copyright(C) 2017-2017 聚数科技成都有限公司。保留所有权利。
 *      Copyright(C) 2017-2017 www.cdmi.pw Inc. All rights reserved. 
 * 
 *      警告：本内容仅限于聚数科技成都有限公司内部传阅，禁止外泄以及用于其他的商业目
 */ 
package pw.cdmi.file.engine.object.service.impl;

import java.util.Calendar;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import pw.cdmi.file.engine.object.dao.MultipartFileObjectDao;
import pw.cdmi.file.engine.object.domain.FileObjectStatus;
import pw.cdmi.file.engine.object.domain.MultipartFileObject;
import pw.cdmi.file.engine.object.domain.MultipartPart;
import pw.cdmi.file.engine.object.service.FileObjectService;
import pw.cdmi.file.engine.object.service.MultipartFileObjectService;

/**
 * 分片文件服务类
 * 
 * @author s90006125
 * 
 */
@Service("multipartFileObjectService")
public class MultipartFileObjectServiceImpl implements MultipartFileObjectService
{
    @Autowired
    private MultipartFileObjectDao multipartFileObjectDao;
    
    @Autowired
    private FileObjectService fileObjectService;
    
    @Value("${multipartfileobject.need.merge}")
    private boolean needMerge = true;
    
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor=Exception.class)
    @Override
    public void initMultipartUpload(MultipartFileObject fileObject)
    {
        fileObjectService.saveFileObject(fileObject);
        multipartFileObjectDao.create(fileObject);
    }
    
    @Override
    public MultipartFileObject getMultipartUpload(String objectID)
    {
        return multipartFileObjectDao.get(new MultipartFileObject(objectID));
    }
    
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor=Exception.class)
    @Override
    public MultipartPart createMultipartPart(MultipartFileObject fileObject, MultipartPart part)
    {
        throw new UnsupportedOperationException();
    }
    
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor=Exception.class)
    @Override
    public boolean abortMultipartUpload(MultipartFileObject fileObject)
    {
        fileObjectService.deleteFileObject(fileObject);
        multipartFileObjectDao.delete(fileObject);
        return true;
    }
    
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor=Exception.class)
    @Override
    public boolean clearMultipartUpload(MultipartFileObject fileObject)
    {
        fileObject.setStatus(FileObjectStatus.COMPLETED);
        fileObjectService.updateFileObject(fileObject);
        multipartFileObjectDao.delete(fileObject);
        return true;
    }
    
    @Override
    public MultipartFileObject listMultipartParts(MultipartFileObject fileObject)
    {
        throw new UnsupportedOperationException();
    }
    
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor=Exception.class)
    @Override
    public MultipartFileObject completeMultipartUpload(MultipartFileObject fileObject)
    {
        if(needMerge)
        {
            // 需要合并
            fileObject.setStatus(FileObjectStatus.COMMITTING);
            multipartFileObjectDao.update(fileObject);
        }
        else
        {
            // 不需要合并
            fileObject.setStatus(FileObjectStatus.COMPLETED);
            multipartFileObjectDao.delete(fileObject);
        }
        fileObjectService.updateFileObject(fileObject);
        return fileObject;
    }

    @Override
    public MultipartFileObject selectCommittingMultipartFileObject( int timeout, int maxMergeTimes)
    {
        return multipartFileObjectDao.selectCommittingMultipartFileObject(getMergeTimeout(timeout).getTime(), maxMergeTimes);
    }

    @Override
    public List<MultipartFileObject> selectMultipartForUploadId(String fileObjectId, String uploadId)
    {
        return multipartFileObjectDao.selectMultipartForUploadId(fileObjectId, uploadId);
    }
    
    @Override
    public List<MultipartFileObject> selectWaitClearMultipartFileObject(int reserveTime, int maxClearTimes, int limit)
    {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MINUTE, 0 - reserveTime);
        
        return multipartFileObjectDao.selectWaitClearMultipartFileObject(cal.getTime(), maxClearTimes, limit);
    }
    
    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor=Exception.class)
    public void updateMultipartUpload(MultipartFileObject fileObject)
    {
        multipartFileObjectDao.update(fileObject);
    }

    /**
     * 独立事务，不受嵌套事务影响，执行后就提交
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor=Exception.class)
    public void deleteInvalidData(int timeout, int maxMergeTimes, int maxClearTimes)
    {
        multipartFileObjectDao.deleteInvalidData(getMergeTimeout(timeout).getTime(), maxMergeTimes, maxClearTimes);
    }
    
    private Calendar getMergeTimeout(int timeout)
    {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.HOUR_OF_DAY, 0 - timeout);
        return cal;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor=Exception.class)
    public void updateLastModifyTime(MultipartFileObject fileObject)
    {
        multipartFileObjectDao.updateLastModifyTime(fileObject);
    }
}
