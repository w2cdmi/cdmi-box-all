package com.huawei.sharedrive.app.openapi.domain.task;

import java.util.List;

/**
 * 获取任务执行结果的响应对象
 * 
 * @author l90003768
 *
 */
public class ResponseGetTask
{
    
    private List<ConflictNode> conflictNodes;

    private String status;

    private String taskId;

    public List<ConflictNode> getConflictNodes()
    {
        return conflictNodes;
    }

    public String getStatus()
    {
        return status;
    }

    public String getTaskId()
    {
        return taskId;
    }

    public void setConflictNodes(List<ConflictNode> conflictNodes)
    {
        this.conflictNodes = conflictNodes;
    }
    
    public void setStatus(String status)
    {
        this.status = status;
    }
    
    public void setTaskId(String taskId)
    {
        this.taskId = taskId;
    }
    
}
