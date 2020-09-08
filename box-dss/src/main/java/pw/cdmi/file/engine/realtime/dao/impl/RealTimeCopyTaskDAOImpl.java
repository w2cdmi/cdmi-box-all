/*
 * 版权声明(Copyright Notice)：
 *      Copyright(C) 2017-2017 聚数科技成都有限公司。保留所有权利。
 *      Copyright(C) 2017-2017 www.cdmi.pw Inc. All rights reserved. 
 * 
 *      警告：本内容仅限于聚数科技成都有限公司内部传阅，禁止外泄以及用于其他的商业目
 */
package pw.cdmi.file.engine.realtime.dao.impl;

import java.util.List;

import org.springframework.stereotype.Repository;

import pw.cdmi.core.exception.InnerException;
import pw.cdmi.file.engine.core.ibatis.IbatisSupportDAO;
import pw.cdmi.file.engine.realtime.dao.RealTimeCopyTaskDAO;
import pw.cdmi.file.engine.realtime.domain.RealTimeCopyTask;

@SuppressWarnings({"deprecation", "unchecked"})
@Repository
public class RealTimeCopyTaskDAOImpl extends IbatisSupportDAO<RealTimeCopyTask> implements
    RealTimeCopyTaskDAO
{
    
    @Override
    public void addCopyTask(RealTimeCopyTask task)
    {
        this.sqlMapClientTemplate.insert(warpSqlstatement(RealTimeCopyTask.class, "addCopyTask"), task);
    }
    
    @Override
    public List<RealTimeCopyTask> queryWaitCopyTask()
    {
        return this.sqlMapClientTemplate.queryForList(warpSqlstatement(RealTimeCopyTask.class,
            "queryWaitCopyTasks"));
    }
    
    @Override
    public List<RealTimeCopyTask> queryWaitCopyTaskByAgent(String exeAgent)
    {
        return this.sqlMapClientTemplate.queryForList(warpSqlstatement(RealTimeCopyTask.class,
            "queryWaitCopyTaskByAgent"),
            exeAgent);
    }
    
    @Override
    public void allocateAgent(RealTimeCopyTask task)
    {
        if (task == null)
        {
            return;
        }
        this.sqlMapClientTemplate.update(warpSqlstatement(RealTimeCopyTask.class, "allocateAgent"), task);
    }
    
    @Override
    public void updateTaskStatus(RealTimeCopyTask task)
    {
        this.sqlMapClientTemplate.update(warpSqlstatement(RealTimeCopyTask.class, "updateTaskStatus"),
            task);
    }
    
    @Override
    public List<RealTimeCopyTask> queryNotFoundTask(String exeAgent)
    {
        return this.sqlMapClientTemplate.queryForList(warpSqlstatement(RealTimeCopyTask.class, "queryNotFoundTask"),exeAgent);       
    }
    
    @Override
    protected void doDelete(RealTimeCopyTask obj)
    {
        throw new InnerException("unimplement method");
        
    }
    
    @Override
    protected void doInsert(RealTimeCopyTask obj)
    {
        throw new InnerException("unimplement method");
        
    }
    
    @Override
    protected RealTimeCopyTask doSelect(RealTimeCopyTask obj)
    {
        throw new InnerException("unimplement method");
    }
    
    @Override
    protected int doUpdate(RealTimeCopyTask obj)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public void resetUnSuccessTask(String exeAgent)
    {
        this.sqlMapClientTemplate.update(warpSqlstatement(RealTimeCopyTask.class, "resetUnSuccessTask"),exeAgent);
        
    }

    @Override
    public List<RealTimeCopyTask> queryCallBackFailedTask(String exeAgent)
    {
        return this.sqlMapClientTemplate.queryForList(warpSqlstatement(RealTimeCopyTask.class,
            "queryCallBackFailedTask"),exeAgent);
    }

    @Override
    public void deleteSucessHistoryTask(String exeAgent)
    {
        this.sqlMapClientTemplate.delete(warpSqlstatement(RealTimeCopyTask.class, "deleteSucessHistoryTask"),exeAgent);
        
    }

    @Override
    public void updateBrotherTaskStatus(String exeAgent)
    {
        this.sqlMapClientTemplate.update(warpSqlstatement(RealTimeCopyTask.class, "updateBrotherTaskStatus"),exeAgent);
        
    }

    @Override
    public RealTimeCopyTask find(String taskId)
    {
        return (RealTimeCopyTask) this.sqlMapClientTemplate.queryForObject(warpSqlstatement(RealTimeCopyTask.class,
            "find"),
            taskId);
    }

    @Override
    public void updateMD5ByObjectId(RealTimeCopyTask task)
    {
        this.sqlMapClientTemplate.update(warpSqlstatement(RealTimeCopyTask.class, "updateMD5ByObjectId"), task);
        
    }

    @Override
    public List<RealTimeCopyTask> queryCallBackTask(String exeAgent)
    {
        return this.sqlMapClientTemplate.queryForList(warpSqlstatement(RealTimeCopyTask.class,
            "queryCallBackTask"),exeAgent);
    }

    @Override
    public void deleteTask(RealTimeCopyTask task)
    {
         this.sqlMapClientTemplate.delete(warpSqlstatement(RealTimeCopyTask.class, "deleteTask"),task);   
        
    }

    @Override
    public RealTimeCopyTask queryByDestObjID(String objectID)
    {
         return (RealTimeCopyTask) this.sqlMapClientTemplate.queryForObject(warpSqlstatement(RealTimeCopyTask.class,
            "queryByDestObjID"),
            objectID);
    }

    @Override
    public List<RealTimeCopyTask> queryFailedManyTimeTasks(String exeAgent)
    {
        return this.sqlMapClientTemplate.queryForList(warpSqlstatement(RealTimeCopyTask.class,
            "queryFailedManyTimeTasks"),exeAgent);
    }
  
}
