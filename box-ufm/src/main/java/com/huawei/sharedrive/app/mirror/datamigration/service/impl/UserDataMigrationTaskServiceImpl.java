package com.huawei.sharedrive.app.mirror.datamigration.service.impl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.huawei.sharedrive.app.mirror.datamigration.dao.UserDataMigrationTaskDAO;
import com.huawei.sharedrive.app.mirror.datamigration.domain.UserDataMigrationTask;
import com.huawei.sharedrive.app.mirror.datamigration.service.UserDataMigrationTaskService;

import pw.cdmi.box.domain.Limit;

@Service("userDataMigrationTaskService")
public class UserDataMigrationTaskServiceImpl implements UserDataMigrationTaskService
{

    @Autowired
    private UserDataMigrationTaskDAO userDataMigrationTaskDAO;
    
   
    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public UserDataMigrationTask createTask(long cloudUserId, int destRegionId, int destResourceGroupId)
    {

        UserDataMigrationTask task = new UserDataMigrationTask();
        
        task.setCloudUserId(cloudUserId);
        Date date =new Date();
        task.setCreatedAt(date);
        task.setModifiedAt(date);
        task.setStatus(UserDataMigrationTask.INIT_STATUS);
        task.setDestRegionId(destRegionId);
        task.setDestResourceGroupId(destResourceGroupId);
        userDataMigrationTaskDAO.insert(task);
        return task;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public int updateStatus(UserDataMigrationTask task)
    {        
        task.setModifiedAt(new Date());
        return userDataMigrationTaskDAO.updateStatus(task);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public int delete(UserDataMigrationTask task)
    {
        return userDataMigrationTaskDAO.delete(task);
    }

    

    @Override
    public UserDataMigrationTask getByUserId(long cloudUserId)
    {
        return userDataMigrationTaskDAO.getByUserId(cloudUserId);
    }

    

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public UserDataMigrationTask getOneTaskToExe(String exeAgent)
    {
        return userDataMigrationTaskDAO.getOneTaskToExe(exeAgent);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public int update(UserDataMigrationTask task)
    {
        return userDataMigrationTaskDAO.update(task);
    }

    @Override
    public int getNotCompletedTaskTotal()
    {

        return userDataMigrationTaskDAO.getNotCompletedTaskTotal();
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public UserDataMigrationTask updateProgressForEveryFile(long userId, long filesSize)
    {
        
        return userDataMigrationTaskDAO.updateProgressForEveryFile(userId, filesSize);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public UserDataMigrationTask updateProgress(long userId, long filesTotal, long filesSize)
    {
        
        return userDataMigrationTaskDAO.updateProgress(userId, filesTotal, filesSize);
    }

    @Override
    public UserDataMigrationTask getOneTaskByStatus(int status)
    {
        return userDataMigrationTaskDAO.getOneTaskByStatus(status);
    }

    @Override
    public List<UserDataMigrationTask> listTask(Limit limit)
    {
        
        return userDataMigrationTaskDAO.listTask(limit);
    }

    @Override
    public boolean checkUserDataMigrationIsRunning(long userId)
    {
        UserDataMigrationTask task = userDataMigrationTaskDAO.getByUserId(userId);
        
        //不存在任务，返回flase
        if(null == task)
        {
            return false;
        }
        
        if(UserDataMigrationTask.FAILED_STATUS != task.getStatus() && UserDataMigrationTask.COMPELETE_STATUS != task.getStatus())
        {
            //存在任务，但是没有完成的任务，返回true
            return true;
        }
        
        return false;
    }
    
}
