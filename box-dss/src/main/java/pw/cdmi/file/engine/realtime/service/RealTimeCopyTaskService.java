/*
 * 版权声明(Copyright Notice)：
 *      Copyright(C) 2017-2017 聚数科技成都有限公司。保留所有权利。
 *      Copyright(C) 2017-2017 www.cdmi.pw Inc. All rights reserved. 
 * 
 *      警告：本内容仅限于聚数科技成都有限公司内部传阅，禁止外泄以及用于其他的商业目
 */
package pw.cdmi.file.engine.realtime.service;

import java.util.List;

import pw.cdmi.file.engine.object.domain.FileObject;
import pw.cdmi.file.engine.realtime.domain.RealTimeCopyTask;

public interface RealTimeCopyTaskService
{
    /**
     * 插入新任务
     * 
     * @param task
     */
    void addCopyTask(RealTimeCopyTask task);
    
    /**
     * 批量获取待执行任务
     * 
     */
    List<RealTimeCopyTask> queryWaitCopyTask();
    
    /**
     * 分配执行节点
     * 
     * @param task
     */
    void allocateAgent(RealTimeCopyTask task);
    
    /**
     * 获取本节点待执行的任务
     * 
     * @param exeAgent
     */
    List<RealTimeCopyTask> queryWaitCopyTaskByAgent(String exeAgent);
    
    /**
     * 删除任务
     * 
     * @param task
     * @return 
     */
    void deleteTask(RealTimeCopyTask task);
    
    /**
     * 更新任务状态
     * 
     * @param task
     */
    void updateTaskStatus(RealTimeCopyTask task);
    
    /**
     * 重置超时、失败的任务
     * 
     * @param exeAgent
     */
    void resetUnSuccessTask(String exeAgent);
    
    /**
     * 查询回调失败的任务
     * @param exeAgent 
     * 
     * @param
     */
    List<RealTimeCopyTask> queryCallBackFailedTask(String exeAgent);
    
    /**
     * 删除回调成功的任务
     * 
     * @param exeAgent
     */
    void deleteSucessHistoryTask(String exeAgent);
    
    /**
     * 获取其他节点的任务
     * 
     * @param exeAgent
     */
    void updateBrotherTaskStatus(String exeAgent);
    
    /**
     * 检查任务是否存在
     * 
     * @param taskId
     */
    boolean checkTaskExisit(String taskId);
    
    /**
     * 分片任务在回调时填写Md5值
     * 
     * @param fileObject
     */
    void updateMD5ByObjectId(FileObject fileObject);

    /**
     * 查找任务
     * @param taskId
     * @return
     */
    RealTimeCopyTask getTask(String taskId);
    /**
     * 查找重新計算MD5后等待回調的任務
     * @param exeAgent 
     * @param 
     * @return
     */
    List<RealTimeCopyTask> queryCallBackTask(String exeAgent);
    /**
     * 查询因源文件不见而失败的任务
     * 
     * @param exeAgent
     * @return 
     */
    List<RealTimeCopyTask> queryNotFoundTask(String exeAgent);
    /**
     * 查询重试次数大于10次的任务
     * 
     * @param exeAgent
     * @return 
     */
    List<RealTimeCopyTask> queryFailedManyTimeTasks(String exeAgent);
    
}
