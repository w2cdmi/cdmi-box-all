package com.huawei.sharedrive.app.core.backtask.reallydeletetask.service.impl;

import com.huawei.sharedrive.app.common.systemtask.dao.SystemTaskDAO;
import com.huawei.sharedrive.app.common.systemtask.dao.UserDBInfoDAO;
import com.huawei.sharedrive.app.common.systemtask.domain.SystemTask;
import com.huawei.sharedrive.app.common.systemtask.domain.TaskKeyConstant;
import com.huawei.sharedrive.app.common.systemtask.domain.UserDBInfo;
import com.huawei.sharedrive.app.core.backtask.reallydeletetask.service.ReallyDeleteService;
import com.huawei.sharedrive.app.exception.BaseRunException;
import com.huawei.sharedrive.app.files.dao.INodeDAO;
import com.huawei.sharedrive.app.files.dao.ObjectFingerprintIndexDAO;
import com.huawei.sharedrive.app.files.dao.ObjectReferenceDAO;
import com.huawei.sharedrive.app.files.domain.INode;
import com.huawei.sharedrive.app.files.domain.ObjectFingerprintIndex;
import com.huawei.sharedrive.app.files.domain.ObjectReference;
import com.huawei.sharedrive.app.plugins.preview.manager.FilePreviewManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import pw.cdmi.box.domain.Limit;

import java.util.Date;
import java.util.List;

@Service("reallyDeleteService")
public class ReallyDeleteServiceImpl implements ReallyDeleteService
{
    
    @Autowired
    private SystemTaskDAO systemTaskDAO;
    
    @Autowired
    private UserDBInfoDAO userDBInfoDAO;
    
    @Autowired
    private INodeDAO iNodeDAO;
    
    @Autowired
    private ObjectReferenceDAO objectReferenceDAO;
    
    @Autowired
    private ObjectFingerprintIndexDAO objectFingerprintIndexDAO;
    
    @Autowired
    private FilePreviewManager filePreviewManager;
    
    @Override
    public SystemTask getFileDistributeScanTask()
    {
        List<SystemTask> lstTask = systemTaskDAO.listSystemTaskByTaskKey(TaskKeyConstant.DISTRIBUTE_FILE_SCAN_TASK);
        if (null != lstTask && !lstTask.isEmpty())
        {
            return lstTask.get(0);
        }
        return null;
    }
    
    @Override
    public List<SystemTask> getScanTableTask(String pTask)
    {
        
        return systemTaskDAO.listSystemTaskByPTaskID(pTask);
    }
    
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteAllFileScanTableTask()
    {
        systemTaskDAO.deleteTaskByTaskKey(TaskKeyConstant.DISTRIBUTE_FILE_SCAN_TASK);
        systemTaskDAO.deleteTaskByTaskKey(TaskKeyConstant.FILE_SCAN_TABLE_TASK);
        
    }
    
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteAllObjectScanTableTask()
    {
        systemTaskDAO.deleteTaskByTaskKey(TaskKeyConstant.DISTRIBUTE_OBJECT_SCAN_TASK);
        systemTaskDAO.deleteTaskByTaskKey(TaskKeyConstant.OBJECT_SCAN_TABLE_TASK);
        
    }
    
    @Override
    public List<UserDBInfo> listAllUserdbInfo()
    {
        return userDBInfoDAO.listAllUserdbInfo();
    }
    
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void createSystemTask(List<SystemTask> lstTask)
    {
        if (null == lstTask || lstTask.isEmpty())
        {
            return;
        }
        
        for (SystemTask task : lstTask)
        {
            systemTaskDAO.createTask(task);
        }
        
    }
    
    @Override
    public void updateExeAgent(SystemTask task) throws BaseRunException
    {
        systemTaskDAO.updateExecuteAgent(task);
    }
    
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public int reallyDeleteFolderNode(int userdbNumber, int tableNumber, Date lastModified)
    {
        
        return iNodeDAO.reallyDeleteFolderNode(userdbNumber, tableNumber, lastModified);
    }
    
    @Override
    public List<INode> lstDeleteNode(int userdbNumber, int tableNumber, Date lastModified, Limit limit)
    {
        return iNodeDAO.lstDeleteNode(userdbNumber, tableNumber, lastModified, limit);
    }
    
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public int deleteINode(INode node)
    {
        return iNodeDAO.delete(node);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public int deleteObjectReferenceCheckRef(ObjectReference objRef) {
        filePreviewManager.deleteByObjectId(objRef.getId());
        return objectReferenceDAO.deleteCheckRef(objRef);
    }
    
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public SystemTask getOneWaitingExeTaskByTaskKey(String pTaskId, String taskKey, String exeAgent)
    {
        SystemTask task = new SystemTask();
        task.setpTaskId(pTaskId);
        task.setTaskKey(taskKey);
        SystemTask newTask = systemTaskDAO.getOneWaitingExeTaskByTaskKey(task);
        if (null == newTask)
        {
            return null;
        }
        newTask.setExeAgent(exeAgent);
        if (1 == systemTaskDAO.updateExecuteAgent(newTask))
        {
            return newTask;
        }
        return null;
    }
    
    @Override
    public SystemTask getObjectDistributeScanTask()
    {
        List<SystemTask> lstTask = systemTaskDAO.listSystemTaskByTaskKey(TaskKeyConstant.DISTRIBUTE_OBJECT_SCAN_TASK);
        if (null != lstTask && !lstTask.isEmpty())
        {
            return lstTask.get(0);
        }
        return null;
    }
    
    @Override
    public List<ObjectReference> lstDeleteObject(int userdbNumber, int tableNumber, Limit limit)
    {
        
        return objectReferenceDAO.lstNeedDeleteObjects(userdbNumber, tableNumber, limit);
    }
    
    @Override
    public int updateTaskState(SystemTask task)
    {
        
        return systemTaskDAO.updateTaskState(task);
    }
    
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public int deleteObjectFingerprintIndex(ObjectReference objRef)
    {
        ObjectFingerprintIndex objFpIndex = new ObjectFingerprintIndex();
        objFpIndex.setSha1(objRef.getSha1());
        objFpIndex.setId(objRef.getId());
        return objectFingerprintIndexDAO.delete(objFpIndex);
    }
    
}
