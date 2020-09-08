package com.huawei.sharedrive.app.mirror.dao.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.huawei.sharedrive.app.mirror.dao.CopyTaskDAO;
import com.huawei.sharedrive.app.mirror.domain.CopyTask;
import com.huawei.sharedrive.app.mirror.domain.CopyType;
import com.huawei.sharedrive.app.mirror.domain.MirrorCommonStatic;

import pw.cdmi.box.dao.impl.AbstractDAOImpl;
import pw.cdmi.box.domain.Limit;
import pw.cdmi.core.utils.JsonUtils;

@Component
public class CopyTaskDAOImpl extends AbstractDAOImpl implements CopyTaskDAO
{
    
    private static final Logger LOGGER = LoggerFactory.getLogger(CopyTaskDAOImpl.class);
    
    @SuppressWarnings("deprecation")
    @Override
    public void create(CopyTask t)
    {
        sqlMapClientTemplate.insert("CopyTask.insert", t);
    }
    
    @SuppressWarnings("deprecation")
    @Override
    public void delete(String id)
    {
        sqlMapClientTemplate.delete("CopyTask.delete", id);
    }
    
    @SuppressWarnings("deprecation")
    @Override
    public CopyTask get(String id)
    {
        
        return (CopyTask) sqlMapClientTemplate.queryForObject("CopyTask.get", id);
    }
    
    @SuppressWarnings("deprecation")
    @Override
    public void update(CopyTask t)
    {
        sqlMapClientTemplate.update("CopyTask.update", t);
        
    }
    
    @SuppressWarnings({"deprecation", "unchecked"})
    @Override
    public List<CopyTask> getBystatusAndExeType(int exeType, int state, boolean isPop, Limit limit)
    {
        Map<String, Object> map = new HashMap<String, Object>(2);
        
        CopyTask task = new CopyTask();
        task.setExeType(exeType);
        task.setState(state);
        task.setPop(isPop);
        task.setExeStartAt(null);
        
        map.put("filter", task);
        map.put("limit", limit);
        
        return sqlMapClientTemplate.queryForList("CopyTask.getBystatusAndExeType", map);
        
    }
    
    @SuppressWarnings("deprecation")
    @Override
    public void createCopyTask(List<CopyTask> lstTask)
    {
        if (null == lstTask || lstTask.isEmpty())
        {
            return;
        }
        
        for (CopyTask t : lstTask)
        {
            sqlMapClientTemplate.insert("CopyTask.insert", t);
        }
        
    }
    
    @SuppressWarnings("deprecation")
    @Override
    public void updateForPop(List<CopyTask> lstTask, boolean isPop)
    {
        if (null == lstTask || lstTask.isEmpty())
        {
            return;
        }
        
        Date date = null;
        for (CopyTask task : lstTask)
        {
            try
            {
                LOGGER.info(JsonUtils.toJson(task));
            }
            catch (Exception e)
            {
                LOGGER.info(e.getMessage(),e);
            }
            date = new Date();
            task.setPop(isPop);
            task.setModifiedAt(date);
            sqlMapClientTemplate.update("CopyTask.update", task);
        }
        
    }
    
    @SuppressWarnings("deprecation")
    @Override
    public void activateTimeTask(String curTime)
    {
        // 考虑执行的开始时间和结束时间是否是顺序还是倒序
        CopyTask task = new CopyTask();
        task.setState(MirrorCommonStatic.TASK_STATE_WAITTING);
        task.setExeType(MirrorCommonStatic.EXE_TYPE_TIME);
        Map<String, Object> map = new HashMap<String, Object>(2);
        map.put("filter", task);
        map.put("curTime", curTime);
        
        // 当执行时间的结束时间大于开始时间时，是顺序
        sqlMapClientTemplate.update("CopyTask.updateTimeTaskStateForSerial", map);
        
        // 当执行时间的结束时间小于开始时间时，是倒序
        sqlMapClientTemplate.update("CopyTask.updateTimeTaskStateForReverse1", map);
        
        sqlMapClientTemplate.update("CopyTask.updateTimeTaskStateForReverse2", map);
        
    }

    @SuppressWarnings("deprecation")
    @Override
    public long recoveryNoExeTaskForPopState(Date outTime)
    {
        CopyTask task = new CopyTask();
        task.setState(MirrorCommonStatic.TASK_STATE_WAITTING);
        task.setPop(true);
        task.setModifiedAt(outTime);
        Map<String, Object> map = new HashMap<String, Object>(2);
        map.put("pop", false);
        map.put("filter", task);
        
        return sqlMapClientTemplate.update("CopyTask.recoveryNoExeTaskForPopState", map);
    }

    @SuppressWarnings({"deprecation", "unchecked"})
    @Override
    public Map<Long ,Long> statisticCopyTask(int state)
    {

        //為了實現簡單簡單，使用MAP 傳值，key 對應 number,value為SIZE
        return  sqlMapClientTemplate.queryForMap("CopyTask.statisticByState", Integer.valueOf(state), "key","value");
    }

    /**
     * 主要两种情况
     * 1:执行时间是 开始时间小于结束执行，例如1点到6点执行
     * 2:执行数据是 开始时间大于结束之际，例如22点6点执行
     */
    @SuppressWarnings("deprecation")
    @Override
    public void deactivatOverdueTimeTask(String curTime)
    {
        // 考虑执行的开始时间和结束时间是否是顺序还是倒序
        CopyTask task = new CopyTask();
        task.setState(MirrorCommonStatic.TASK_STATE_WAITTING);
        task.setExeType(MirrorCommonStatic.EXE_TYPE_TIME);
        task.setPop(false);
        Map<String, Object> map = new HashMap<String, Object>(2);
        map.put("filter", task);
        map.put("curTime", curTime);
        map.put("state", Integer.valueOf(MirrorCommonStatic.TASK_STATE_NOACTIVATE));
        
        // 当执行时间的结束时间大于开始时间时，是顺序
        sqlMapClientTemplate.update("CopyTask.deactivatOverdueTimeTaskForSerial1", map);

        sqlMapClientTemplate.update("CopyTask.deactivatOverdueTimeTaskForSerial2", map);
        
        // 当执行时间的结束时间小于开始时间时，是倒序
        sqlMapClientTemplate.update("CopyTask.deactivatOverdueTimeTaskForReverse", map);
        
    }

    @SuppressWarnings("deprecation")
    @Override
    public CopyTask getTaskBySrcObjectAndResourceGroupID(CopyTask task)
    {
        return (CopyTask) sqlMapClientTemplate.queryForObject("CopyTask.getTaskBySrcObjectAndResourceGroupID", task);
    }

    @SuppressWarnings("deprecation")
    @Override
    public long recoveryExeTimeOutTask(Date outTime)
    {
        CopyTask task = new CopyTask();
        task.setState(MirrorCommonStatic.TASK_STATE_WAITTING);
        task.setPop(false);
        task.setModifiedAt(outTime);
        Map<String, Object> map = new HashMap<String, Object>(2);
        map.put("state", MirrorCommonStatic.TASK_STATE_EXEING);
        map.put("filter", task);
        
        return sqlMapClientTemplate.update("CopyTask.recoveryExeTimeOutTask", map);
    }

    @SuppressWarnings("deprecation")
    @Override
    public long recoveryFailedTaskForByExeType(int exeType)
    {
        CopyTask task = new CopyTask();
        if(MirrorCommonStatic.EXE_TYPE_NOW == exeType)
        {
            task.setState(MirrorCommonStatic.TASK_STATE_WAITTING);
        }
        else
        {
            task.setState(MirrorCommonStatic.EXE_TYPE_TIME);
        }
        
        task.setExeResult(0);
        task.setPop(false);
        Map<String, Object> map = new HashMap<String, Object>(2);
        map.put("state", MirrorCommonStatic.TASK_STATE_FAILED);
        map.put("exeType", exeType);
        map.put("filter", task);
        
        return sqlMapClientTemplate.update("CopyTask.recoveryFailedTask", map);
    }

    @SuppressWarnings("deprecation")
    @Override
    public long deleteTaskByErrorCode(int errCode)
    {

        CopyTask task = new CopyTask();
        task.setState(MirrorCommonStatic.TASK_STATE_FAILED);
        task.setExeResult(errCode);
        return sqlMapClientTemplate.update("CopyTask.deleteTaskByErrorCode", task);
    }

    @SuppressWarnings({"deprecation", "unchecked"})
    @Override
    public List<CopyTask> lstTaskByErrorCode(int errCode)
    {
        CopyTask task = new CopyTask();
        task.setState(MirrorCommonStatic.TASK_STATE_FAILED);
        task.setExeResult(errCode);
        return sqlMapClientTemplate.queryForList("CopyTask.lstTaskByErrorCode", task);
    }
    
    @SuppressWarnings({"unchecked", "deprecation"})
    @Override
    public List<CopyTask> getCopyTaskBySrcObjectId(String srcObjectId)
    {
        return sqlMapClientTemplate.queryForList("CopyTask.getBySrcObjectID", srcObjectId);
    }

    @SuppressWarnings("deprecation")
    @Override
    public long deleteTaskForDataMigration(long userId)
    {
        CopyTask task = new CopyTask();
        task.setSrcOwnedBy(userId);
        task.setCopyType(CopyType.COPY_TYPE_USER_DATA_MIGRATION.getCopyType());
        return sqlMapClientTemplate.delete("CopyTask.deleteTaskForDataMigration", task);
    }

    @SuppressWarnings("deprecation")
    @Override
    public long getNotCompleteTaskForDataMigration(long userId)
    {
        CopyTask task = new CopyTask();
        task.setSrcOwnedBy(userId);
        task.setCopyType(CopyType.COPY_TYPE_USER_DATA_MIGRATION.getCopyType());
        return (Long) sqlMapClientTemplate.queryForObject("CopyTask.getNotCompleteTaskForDataMigration", task);
    }

    @SuppressWarnings("deprecation")
    @Override
    public long clearNotExeTaskForDataMigration(long userId)
    {
        CopyTask task = new CopyTask();
        task.setSrcOwnedBy(userId);
        task.setCopyType(CopyType.COPY_TYPE_USER_DATA_MIGRATION.getCopyType());
        task.setPop(false);
        task.setState(MirrorCommonStatic.TASK_STATE_WAITTING);
        return sqlMapClientTemplate.delete("CopyTask.clearNotExeTaskForDataMigration", task);
    }

    @SuppressWarnings("deprecation")
    @Override
    public long cleanMirrorCopyTaskBySrcOwnedby(long userId)
    {
        
        return sqlMapClientTemplate.delete("CopyTask.cleanMirrorCopyTaskBySrcOwnedby", Long.valueOf(userId));
    }

    @SuppressWarnings("deprecation")
    @Override
    public long updateTaskStateSameWithSystemConfigState(int newState, int oldState)
    {
        Map<String, Object> map = new HashMap<String, Object>(2);
        map.put("oldstate", oldState);
        map.put("newstate", newState);
        return sqlMapClientTemplate.update("CopyTask.updateTaskStateSameWithSystemConfigState", map);
    }

    @SuppressWarnings({"deprecation", "unchecked"})
    @Override
    public List<Integer> selectAllPolicyId()
    {
        return sqlMapClientTemplate.queryForList("CopyTask.selectAllPolicyId");
    }

    @SuppressWarnings("deprecation")
    @Override
    public void deleteCopyTaskByPolicy(Integer policyId)
    {
        sqlMapClientTemplate.delete("CopyTask.deleteTaskByPolicy",policyId);
    }
    
}
