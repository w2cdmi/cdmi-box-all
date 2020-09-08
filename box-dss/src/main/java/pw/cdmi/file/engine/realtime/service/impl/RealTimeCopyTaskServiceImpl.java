/*
 * 版权声明(Copyright Notice)：
 *      Copyright(C) 2017-2017 聚数科技成都有限公司。保留所有权利。
 *      Copyright(C) 2017-2017 www.cdmi.pw Inc. All rights reserved. 
 * 
 *      警告：本内容仅限于聚数科技成都有限公司内部传阅，禁止外泄以及用于其他的商业目
 */
package pw.cdmi.file.engine.realtime.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pw.cdmi.file.engine.object.domain.FileObject;
import pw.cdmi.file.engine.realtime.dao.RealTimeCopyTaskDAO;
import pw.cdmi.file.engine.realtime.domain.RealTimeCopyTask;
import pw.cdmi.file.engine.realtime.service.RealTimeCopyTaskCallbacker;
import pw.cdmi.file.engine.realtime.service.RealTimeCopyTaskService;
import pw.cdmi.file.engine.realtime.util.RealTimeCopyTaskTool;

@Service("realTimeCopyTaskService")
public class RealTimeCopyTaskServiceImpl implements RealTimeCopyTaskService
{
    
    @Autowired
    private RealTimeCopyTaskDAO realTimeCopyTaskDAO;
    
    @Autowired
    private RealTimeCopyTaskCallbacker realTimeCopyTaskCallbacker;
    
    @Override
    public void addCopyTask(RealTimeCopyTask task)
    {
        realTimeCopyTaskDAO.addCopyTask(task);
    }
    
    @Override
    public List<RealTimeCopyTask> queryWaitCopyTask()
    {
        return realTimeCopyTaskDAO.queryWaitCopyTask();
    }
    
    @Override
    public void allocateAgent(RealTimeCopyTask task)
    {
        realTimeCopyTaskDAO.allocateAgent(task);
    }
    
    @Override
    public List<RealTimeCopyTask> queryWaitCopyTaskByAgent(String exeAgent)
    {
        return realTimeCopyTaskDAO.queryWaitCopyTaskByAgent(exeAgent);
    }
    
    @Override
    public List<RealTimeCopyTask> queryNotFoundTask(String exeAgent)
    {
        return realTimeCopyTaskDAO.queryNotFoundTask(exeAgent);
    }
    
    @Override
    public void updateTaskStatus(RealTimeCopyTask task)
    {
        realTimeCopyTaskDAO.updateTaskStatus(task);
        
    }
    
    @Override
    public void resetUnSuccessTask(String exeAgent)
    {
        realTimeCopyTaskDAO.resetUnSuccessTask(exeAgent);
        
    }
    
    @Override
    public List<RealTimeCopyTask> queryCallBackFailedTask(String exeAgent)
    {
        return realTimeCopyTaskDAO.queryCallBackFailedTask(exeAgent);
    }
    
    @Override
    public void deleteSucessHistoryTask(String exeAgent)
    {
        realTimeCopyTaskDAO.deleteSucessHistoryTask(exeAgent);
        
    }
    
    @Override
    public void updateBrotherTaskStatus(String exeAgent)
    {
        realTimeCopyTaskDAO.updateBrotherTaskStatus(exeAgent);
        
    }
    
    @Override
    public boolean checkTaskExisit(String taskId)
    {
        boolean flag = false;
        RealTimeCopyTask obj = realTimeCopyTaskDAO.find(taskId);
        if (obj != null)
        {
            flag = true;
        }
        return flag;
    }
    
    @Override
    public void updateMD5ByObjectId(FileObject fileObject)
    {
        RealTimeCopyTask task = realTimeCopyTaskDAO.queryByDestObjID(fileObject.getObjectID());
        task.setMd5(RealTimeCopyTaskTool.getMD5(fileObject.getSha1()));
        task.setBlockMD5(RealTimeCopyTaskTool.getBlockMD5(fileObject.getSha1()));
        realTimeCopyTaskDAO.updateMD5ByObjectId(task);
        realTimeCopyTaskCallbacker.taskCallback(task);
        
        
    }
    
    @Override
    public RealTimeCopyTask getTask(String taskId)
    {
        return realTimeCopyTaskDAO.find(taskId);
    }

    @Override
    public List<RealTimeCopyTask> queryCallBackTask(String exeAgent)
    {
        return realTimeCopyTaskDAO.queryCallBackTask(exeAgent);
    }

    @Override
    public void deleteTask(RealTimeCopyTask task)
    {
        realTimeCopyTaskDAO.deleteTask(task);      
    }

    @Override
    public List<RealTimeCopyTask> queryFailedManyTimeTasks(String exeAgent)
    {
        return realTimeCopyTaskDAO.queryFailedManyTimeTasks(exeAgent);
    }
    
}
