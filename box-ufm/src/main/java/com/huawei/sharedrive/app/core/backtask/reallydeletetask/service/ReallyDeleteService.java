package com.huawei.sharedrive.app.core.backtask.reallydeletetask.service;

import java.util.Date;
import java.util.List;

import com.huawei.sharedrive.app.common.systemtask.domain.SystemTask;
import com.huawei.sharedrive.app.common.systemtask.domain.UserDBInfo;
import com.huawei.sharedrive.app.exception.BaseRunException;
import com.huawei.sharedrive.app.files.domain.INode;
import com.huawei.sharedrive.app.files.domain.ObjectReference;

import pw.cdmi.box.domain.Limit;

public interface ReallyDeleteService
{
    /**
     * 
     * @return
     */
    SystemTask getFileDistributeScanTask();
    
    /**
     * 
     * @return
     */
    SystemTask getObjectDistributeScanTask();
    
    /**
     * 
     * @return
     */
    List<SystemTask> getScanTableTask(String pTask);
    
    /**
     * 删除当前所有文件扫描相关的任务
     */
    void deleteAllFileScanTableTask();
    
    List<UserDBInfo> listAllUserdbInfo();
    
    /**
     * 
     * @param task
     * @throws BaseRunException
     */
    void updateExeAgent(SystemTask task) throws BaseRunException;
    
    /**
     * 
     * @param lstTask
     */
    void createSystemTask(List<SystemTask> lstTask);
    
    /**
     * 
     * @param userdbNumber
     * @param tableNumber
     * @param lastModified
     * @return
     */
    int reallyDeleteFolderNode(int userdbNumber, int tableNumber, Date lastModified);
    
    /**
     * 
     * @param userdbNumber
     * @param tableNumber
     * @param lastModified
     * @param limit
     * @return
     */
    List<INode> lstDeleteNode(int userdbNumber, int tableNumber, Date lastModified, Limit limit);
    
    /**
     * 列举对象引用
     * 
     * @param userdbNumber
     * @param tableNumber
     * @param lastModified
     * @param limit
     * @return
     */
    List<ObjectReference> lstDeleteObject(int userdbNumber, int tableNumber, Limit limit);
    
    /**
     * 获取一个等待执行的任务
     * 
     * @param task
     * @param exeAgent
     * @return
     */
    SystemTask getOneWaitingExeTaskByTaskKey(String pTaskID, String taskKey, String exeAgent);
    
    int deleteINode(INode node);
    
    void deleteAllObjectScanTableTask();
    
    int deleteObjectReferenceCheckRef(ObjectReference objRef);
    
    int updateTaskState(SystemTask task);
    
    /**
     * 删除对象SHA1
     * 
     * @param objRef
     * @return
     */
    int deleteObjectFingerprintIndex(ObjectReference objRef);
    
}
