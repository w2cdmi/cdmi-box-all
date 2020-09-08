package com.huawei.sharedrive.app.mirror.datamigration.service;

import java.util.List;

import com.huawei.sharedrive.app.mirror.datamigration.domain.UserDataMigrationTask;

import pw.cdmi.box.domain.Limit;

public interface UserDataMigrationTaskService
{
    
    /**
     * 创建任务
     * @param cloudUserId
     * @param destRegionId
     * @param destResourceGroupId
     */
    UserDataMigrationTask createTask(long cloudUserId,int destRegionId,int destResourceGroupId);
    
    /**
     * 更新任务
     * @param task
     * @return
     */
    int  updateStatus(UserDataMigrationTask task);
    
    
    /**
     * 更新任务
     * @param task
     * @return
     */
    int  update(UserDataMigrationTask task);
    
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
    UserDataMigrationTask getByUserId(long  cloudUserId);
    
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
     * 获取一个迁移任务去执行
     * @param exeAgent
     * @return
     */
    UserDataMigrationTask getOneTaskToExe(String exeAgent);
    
    
    /**
     * 获取当前未完成的数据迁移总数
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
     * 列舉任務
     * @param limit
     * @return
     */
    List<UserDataMigrationTask> listTask(Limit limit);
    
    /**
     * 检查用户是否有数据迁移操作
     * @param userId
     * @return
     */
    boolean checkUserDataMigrationIsRunning(long userId);
    
}
