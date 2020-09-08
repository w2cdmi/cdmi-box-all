/*
 * 版权声明(Copyright Notice)：
 *      Copyright(C) 2017-2017 聚数科技成都有限公司。保留所有权利。
 *      Copyright(C) 2017-2017 www.cdmi.pw Inc. All rights reserved. 
 * 
 *      警告：本内容仅限于聚数科技成都有限公司内部传阅，禁止外泄以及用于其他的商业目
 */
package pw.cdmi.file.engine.realtime.dao;

import java.util.List;

import pw.cdmi.file.engine.realtime.domain.RealTimeCopyTask;

public interface RealTimeCopyTaskDAO
{
    void addCopyTask(RealTimeCopyTask task);

    List<RealTimeCopyTask> queryWaitCopyTask();
    
    void allocateAgent(RealTimeCopyTask task);

    List<RealTimeCopyTask> queryWaitCopyTaskByAgent(String exeAgent);
    
    List<RealTimeCopyTask> queryNotFoundTask(String exeAgent);

    void updateTaskStatus(RealTimeCopyTask task);

    void resetUnSuccessTask(String exeAgent);

    List<RealTimeCopyTask> queryCallBackFailedTask(String exeAgent);

    void deleteSucessHistoryTask(String exeAgent);
    
    void updateBrotherTaskStatus(String exeAgent);

    RealTimeCopyTask find(String taskId);

    void updateMD5ByObjectId(RealTimeCopyTask task);

    List<RealTimeCopyTask> queryCallBackTask(String exeAgent);

    void deleteTask(RealTimeCopyTask task);

    RealTimeCopyTask queryByDestObjID(String objectID);

    List<RealTimeCopyTask> queryFailedManyTimeTasks(String exeAgent);

}
