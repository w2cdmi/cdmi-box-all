package com.huawei.sharedrive.app.mirror.datamigration.dao;

import java.util.List;

import com.huawei.sharedrive.app.mirror.datamigration.domain.UserDataMigrationTask;

import pw.cdmi.box.domain.Limit;

public interface UserDataMigrationTaskDAO 
{
    /**
     * 添加任务
     * @param task
     */
    void insert(UserDataMigrationTask task);
    
    /**
     * 更新任务
     * @param task
     * @return
     */
    int  update(UserDataMigrationTask task);
    
    /**
     * 更新任务状态
     * @param task
     * @return
     */
    int  updateStatus(UserDataMigrationTask task);
    
    /**
     * 删除任务
     * @param task
     * @return
     */
    int delete(UserDataMigrationTask task);
    

    
    /**
     * 通过用户ID获取任务
     * @param userId
     * @return
     */
    UserDataMigrationTask getByUserId(long  userId);
    
    
    UserDataMigrationTask getOneTaskToExe(String exeAgent);
    

   /**
    * 获取未哇完成任务的总数
    * @return
    */
    int getNotCompletedTaskTotal();
    
    /**
     * 根据状态只获取一个任务
     * @param status
     * @return
     */
    UserDataMigrationTask getOneTaskByStatus(int status);
    
    /**
     * 为每一个对象更新哇才能状态
     * @param userId
     * @param filesSize
     * @return
     */
    UserDataMigrationTask  updateProgressForEveryFile(long userId,long filesSize);
    
    /**
     * 更新进度
     * @param userId
     * @param filesTotal
     * @param filesSize
     * @return
     */
    UserDataMigrationTask  updateProgress(long userId,long filesTotal,long filesSize);
    
    /**
     * 
     * @param limit
     * @return
     */
    List<UserDataMigrationTask> listTask(Limit limit);
}
