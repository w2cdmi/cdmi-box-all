package pw.cdmi.file.engine.mirro.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import pw.cdmi.core.exception.InnerException;
import pw.cdmi.file.engine.core.ibatis.IbatisSupportDAO;
import pw.cdmi.file.engine.mirro.dao.CopyTaskDAO;
import pw.cdmi.file.engine.mirro.domain.CopyTask;
import pw.cdmi.file.engine.mirro.domain.CopyTaskRunInfo;
import pw.cdmi.file.engine.mirro.domain.CopyTaskStatus;
import pw.cdmi.file.engine.mirro.util.CopyTaskTool;

@SuppressWarnings({"unchecked", "deprecation"})
@Repository
public class CopyTaskDAOImpl extends IbatisSupportDAO<CopyTask> implements CopyTaskDAO
{
    
    private static final Logger LOGGER = LoggerFactory.getLogger(CopyTaskDAOImpl.class);
    
    public List<CopyTask> queryWaitCopyTask(String exeAgent)
    {
        return this.sqlMapClientTemplate.queryForList(warpSqlstatement(CopyTask.class, "queryWaitCopyTasks"), exeAgent);
    }
    
    public void batchInsertOrReplace(List<CopyTask> copyTaskList)
    {
        this.sqlMapClientTemplate.insert(warpSqlstatement(CopyTask.class, "batchInsertOrReplace"), copyTaskList);
    }
    
    public void updateTaskStatus(CopyTask copyTask)
    {
        
        if (LOGGER.isDebugEnabled())
        {
            LOGGER.debug(CopyTaskTool.LOG_TAG + ",updateTaskStatus:" + copyTask.toString());
        }
        this.sqlMapClientTemplate.update(warpSqlstatement(CopyTask.class, "updateTaskStatus"), copyTask);
    }
    
    public List<CopyTask> queryWaitCallbackTasks(String exeAgent)
    {
        return this.sqlMapClientTemplate.queryForList(warpSqlstatement(CopyTask.class, "queryWaitCallbackTasks"), exeAgent);
    }
    
    @Override
    public void lockTaskStatus(List<CopyTask> copyTaskList)
    {
        if (CollectionUtils.isEmpty(copyTaskList))
        {
            return;
        }
        for (CopyTask task : copyTaskList)
        {
            if (LOGGER.isDebugEnabled())
            {
                LOGGER.debug(CopyTaskTool.LOG_TAG + ",lockTaskStatus:" + task.toString());
            }
            this.sqlMapClientTemplate.update(warpSqlstatement(CopyTask.class, "lockTaskStatus"), task);
        }
    }
    
    @Override
    public void unLockTaskStatus(CopyTask copyTask)
    {
        if (LOGGER.isDebugEnabled())
        {
            LOGGER.debug(CopyTaskTool.LOG_TAG + ",unLockTaskStatus:" + copyTask.toString());
        }
        this.sqlMapClientTemplate.update(warpSqlstatement(CopyTask.class, "unLockTaskStatus"), copyTask);
    }
    
    @Override
    public void updateCrashTaskStatus(String exeAgent)
    {
        if (LOGGER.isDebugEnabled())
        {
            LOGGER.debug(CopyTaskTool.LOG_TAG + ",updateCrashTaskStatus:" + exeAgent);
        }
        this.sqlMapClientTemplate.update(warpSqlstatement(CopyTask.class, "updateCrashTaskStatus"), exeAgent);
    }
    
    @Override
    public void updateMD5ByObjectId(CopyTask task)
    {
        if (LOGGER.isDebugEnabled())
        {
            LOGGER.debug(CopyTaskTool.LOG_TAG + ",updateMD5ByObjectId:" + task.toString());
        }
        this.sqlMapClientTemplate.update(warpSqlstatement(CopyTask.class, "updateMD5ByObjectId"), task);
    }
    
    @Override
    public void updateBrotherTaskStatus(String exeAgent)
    {
        if (LOGGER.isDebugEnabled())
        {
            LOGGER.debug(CopyTaskTool.LOG_TAG + ",updateBrotherTaskStatus:" + exeAgent);
        }
        this.sqlMapClientTemplate.update(warpSqlstatement(CopyTask.class, "updateBrotherTaskStatus"), exeAgent);
    }
    
    @Override
    public void deleteSucessHistoryTask(String exeAgent)
    {
        if (LOGGER.isDebugEnabled())
        {
            LOGGER.debug(CopyTaskTool.LOG_TAG + ",deleteSucessHistoryTask:" + exeAgent);
        }
        this.sqlMapClientTemplate.delete(warpSqlstatement(CopyTask.class, "deleteSucessHistoryTask"), exeAgent);
    }
    
    @Override
    public void pushCopyUnSuccessTask(String exeAgent)
    {
        if (LOGGER.isDebugEnabled())
        {
            LOGGER.debug(CopyTaskTool.LOG_TAG + ",pushCopyUnSuccessTask:" + exeAgent);
        }
        this.sqlMapClientTemplate.update(warpSqlstatement(CopyTask.class, "pushCopyUnSuccessTask"), exeAgent);
    }
    
    @Override
    public void pushCallbackUnSuccessTask(String exeAgent)
    {
        if (LOGGER.isDebugEnabled())
        {
            LOGGER.debug(CopyTaskTool.LOG_TAG + ",pushCallbackUnSuccessTask:" + exeAgent);
        }
        this.sqlMapClientTemplate.update(warpSqlstatement(CopyTask.class, "pushCallbackUnSuccessTask"), exeAgent);
    }
    
    @Override
    public void resetAllCopyFailedTask(String exeAgent)
    {
        if (LOGGER.isDebugEnabled())
        {
            LOGGER.debug(CopyTaskTool.LOG_TAG + ",resetAllCopyFailedTask:" + exeAgent);
        }
        this.sqlMapClientTemplate.update(warpSqlstatement(CopyTask.class, "resetAllCopyFailedTask"), exeAgent);
    }
    
    @Override
    public void restoreCrashTaskStatus(String exeAgent)
    {
        if (LOGGER.isDebugEnabled())
        {
            LOGGER.debug(CopyTaskTool.LOG_TAG + ",restoreCrashTaskStatus:" + exeAgent);
        }
        this.sqlMapClientTemplate.update(warpSqlstatement(CopyTask.class, "restoreCrashTaskStatus"), exeAgent);
    }
    
    @Override
    public void deleteCallback700Task(CopyTask task)
    {
        if (LOGGER.isDebugEnabled())
        {
            LOGGER.debug(CopyTaskTool.LOG_TAG + ",deleteCallback700Task:" + task.toString());
        }
        this.sqlMapClientTemplate.delete(warpSqlstatement(CopyTask.class, "deleteCallback700Task"), task);
    }
    
    @Override
    public List<CopyTask> queryCallBack700Task(String exeAgent)
    {
        return this.sqlMapClientTemplate.queryForList(warpSqlstatement(CopyTask.class, "queryCallBack700Task"), exeAgent);
    }
    
    public void update(CopyTask obj, Map<String, Object> map)
    {
        throw new InnerException("unimplement method");
    }
    
    protected void doDelete(CopyTask obj)
    {
        throw new InnerException("unimplement method");
    }
    
    protected void doInsert(CopyTask obj)
    {
        throw new InnerException("unimplement method");
    }
    
    protected CopyTask doSelect(CopyTask obj)
    {
        throw new InnerException("unimplement method");
    }
    
    protected int doUpdate(CopyTask obj)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<CopyTask> listAllCopyTask()
    {
        return this.sqlMapClientTemplate.queryForList(warpSqlstatement(CopyTask.class, "getAllCopyTask"));
    }

    @Override
    public void pauseOrGoTask(int state)
    {
        Map<String, Integer> map = new HashMap<String, Integer>(2);
        if(state == CopyTaskStatus.PAUSE.getCode())
        {
            map.put("oldState", CopyTaskStatus.INPUT.getCode());
            map.put("newState", CopyTaskStatus.PAUSE.getCode());
            sqlMapClientTemplate.update(warpSqlstatement(CopyTask.class, "pauseOrGoTask") ,map);
        }
        else if(state == CopyTaskStatus.INPUT.getCode())
        {
            map.put("oldState", CopyTaskStatus.PAUSE.getCode());
            map.put("newState", CopyTaskStatus.INPUT.getCode());
            sqlMapClientTemplate.update(warpSqlstatement(CopyTask.class, "pauseOrGoTask") ,map);
        }
    }
    
    @Override
    public List<CopyTask> listExeAgengtIsNullCopyTask()
    {
        return this.sqlMapClientTemplate.queryForList(warpSqlstatement(CopyTask.class, "getExeAgentIsNullCopyTask"));
    }
    
    @Override
    public void updateExeAgent(CopyTask copytask)
    {
        this.sqlMapClientTemplate.update(warpSqlstatement(CopyTask.class, "updateTaskExeAgent"),copytask);        
    }
    
    @Override
    public void batchInsertOrReplaceWithExeAgent(List<CopyTask> copyTaskList)
    {
        this.sqlMapClientTemplate.insert(warpSqlstatement(CopyTask.class, "batchInsertOrReplaceWithExeAgent"), copyTaskList);
    }
    
    @Override
    public void setExeAgentForCopyTask(String siteName, int limit)
    {
        Map map = new HashMap(2);
        map.put("exeAgent", siteName);
        map.put("size", limit);
        this.sqlMapClientTemplate.insert(warpSqlstatement(CopyTask.class, "setTaskExeAgent"), map);
    }
    
    @Override
    public List<CopyTaskRunInfo> getTaskRunInfo()
    {
        return this.sqlMapClientTemplate.queryForList(warpSqlstatement(CopyTask.class, "getTaskRunInfo"));
    }
    
    
}
