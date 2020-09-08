/*
 * 版权声明(Copyright Notice)：
 *      Copyright(C) 2017-2017 聚数科技成都有限公司。保留所有权利。
 *      Copyright(C) 2017-2017 www.cdmi.pw Inc. All rights reserved. 
 * 
 *      警告：本内容仅限于聚数科技成都有限公司内部传阅，禁止外泄以及用于其他的商业目
 */ 
package pw.cdmi.file.engine.object.service.impl;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import pw.cdmi.core.log.Level;
import pw.cdmi.core.utils.MethodLogAble;
import pw.cdmi.file.engine.object.dao.FileObjectDeleteLogDao;
import pw.cdmi.file.engine.object.dao.FileObjectDeleteTaskDao;
import pw.cdmi.file.engine.object.domain.FileObject;
import pw.cdmi.file.engine.object.domain.FileObjectDeleteLog;
import pw.cdmi.file.engine.object.domain.FileObjectDeleteTask;
import pw.cdmi.file.engine.object.domain.FileObjectStatus;
import pw.cdmi.file.engine.object.domain.MultipartFileObject;
import pw.cdmi.file.engine.object.service.FileObjectDeleteService;
import pw.cdmi.file.engine.object.service.FileObjectService;
import pw.cdmi.file.engine.object.service.MultipartFileObjectService;

/**
 * 
 * @author s90006125
 *
 */
@Service("fileObjectDeleteService")
public class FileObjectDeleteServiceImpl implements FileObjectDeleteService
{
    private static final Logger LOGGER = LoggerFactory.getLogger(FileObjectDeleteServiceImpl.class);
    
    @Autowired
    private FileObjectService fileObjectService;
    
    @Autowired
    private FileObjectDeleteTaskDao fileObjectDeleteTaskDao;
    
    @Autowired
    private FileObjectDeleteLogDao fileObjectDeleteLogDao;
    
    @Autowired
    private MultipartFileObjectService multipartFileObjectService;
    
    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor=Exception.class)
    @MethodLogAble(Level.INFO)
    public void createFileObjectDeleteTask(FileObject fileObject)
    {
        FileObjectDeleteTask fileObjectTask = new FileObjectDeleteTask(fileObject);
        fileObjectTask.setStatus(FileObjectStatus.WAITDELETE);
        fileObjectTask.setDeleteAt(new Date());
        fileObjectTask.setDeleteTimes(0);
        fileObjectTask.setModified(null);
        
        fileObjectDeleteTaskDao.create(fileObjectTask);
        
        MultipartFileObject multipartFileObject = multipartFileObjectService.getMultipartUpload(fileObject.getObjectID());
        if(null != multipartFileObject)
        {
            // 如果分片已提交，但是还未合并完成，则只需该方法进行删除
            LOGGER.info("delete multipartFileobject and fileobject {} .", fileObject.logFormat());
            multipartFileObjectService.abortMultipartUpload(multipartFileObject);
        }
        else
        {
            LOGGER.info("delete fileobject {} .", fileObject.logFormat());
            fileObjectService.deleteFileObject(fileObject);
        }
    }
    
    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor=Exception.class)
    public int updateFileObjectDeleteTask(FileObjectDeleteTask fileObjectDeleteTask)
    {
        return fileObjectDeleteTaskDao.update(fileObjectDeleteTask);
    }
    
    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor=Exception.class)
    public boolean markFileObjectDeleteTask(FileObjectDeleteTask fileObjectDeleteTask)
    {
        if(fileObjectDeleteTaskDao.markFileObjectDeleteTask(fileObjectDeleteTask) == 1)
        {
            return true;
        }
        
        return false;
    }
    
    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor=Exception.class)
    public void completeFileObjectDeleteTask(FileObjectDeleteTask fileObjectDeleteTask)
    {
        fileObjectDeleteTaskDao.delete(fileObjectDeleteTask);
        fileObjectDeleteLogDao.create(new FileObjectDeleteLog(fileObjectDeleteTask));
    }
    
    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor=Exception.class)
    public void resumeFailedFileObjectDeleteTask(int dbIndex, int tableIndex, int timeout, int retryTimes)
    {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MINUTE, 0 - timeout);
        
        Map<String, Object> parameter = new HashMap<String, Object>(4);
        parameter.put("dbIndex", dbIndex);
        parameter.put("tableIndex", tableIndex);
        parameter.put("timeout", cal.getTime());
        parameter.put("retryTimes", retryTimes);
        
        fileObjectDeleteTaskDao.resumeFailedFileObjectDeleteTask(parameter);
    }
    
    @Override
    public List<FileObjectDeleteTask> listFileObjectDeleteTask(int dbIndex, int tableIndex, int retryTimes,
        int reserveTime, int limit)
    {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_YEAR, 0 - reserveTime);
        
        Map<String, Object> parameter = new HashMap<String, Object>(5);
        parameter.put("dbIndex", dbIndex);
        parameter.put("tableIndex", tableIndex);
        parameter.put("retryTimes", retryTimes);
        parameter.put("reserveTime", cal.getTime());
        parameter.put("limit", limit);
        
        return fileObjectDeleteTaskDao.listFileObjectDeleteTask(parameter);
    }
}
