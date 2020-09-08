package com.huawei.sharedrive.isystem.mirror.dao.impl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.huawei.sharedrive.isystem.mirror.dao.CopyTaskDAO;
import com.huawei.sharedrive.isystem.mirror.domain.CopyPolicy;
import com.huawei.sharedrive.isystem.mirror.domain.MirrorCommonStatic;

import pw.cdmi.box.dao.impl.AbstractDAOImpl;

@Component
public class CopyTaskDAOImpl extends AbstractDAOImpl implements CopyTaskDAO
{
    
    @SuppressWarnings({"deprecation", "unchecked"})
    @Override
    public Map<Long, Long> statisticCopyTask(int state)
    {
        
        // 為了實現簡單簡單，使用MAP 傳值，key 對應 number,value為SIZE
        return sqlMapClientTemplate.queryForMap("CopyTask.statisticByState",
            Integer.valueOf(state),
            "key",
            "value");
    }
    
    @SuppressWarnings({"deprecation", "unchecked"})
    @Override
    public Map<Long, Long> statisticCopyTask(int state, CopyPolicy copyPolicy)
    {
        
        // 為了實現簡單簡單，使用MAP 傳值，key 對應 number,value為SIZE
        Map<String, Integer> map = new HashMap<String, Integer>(2);
        map.put("state", Integer.valueOf(state));
        map.put("id", Integer.valueOf(copyPolicy.getId()));
        return sqlMapClientTemplate.queryForMap("CopyTask.statisticByStateAndPolicyId", map, "key", "value");
    }
    
    @SuppressWarnings("deprecation")
    @Override
    public long pauseOrGoTask(int state)
    {
        
        long temp = 0;
        if (state == MirrorCommonStatic.TASK_STATE_SYSTEM_PAUSE)
        {
            temp = sqlMapClientTemplate.update("CopyTask.pauseOrGoTaskForNowType", state);
            temp = temp + sqlMapClientTemplate.update("CopyTask.pauseOrGoTaskForTimeType", state);
            
        }
        else if (state == MirrorCommonStatic.TASK_STATE_WAITTING)
        {
            // timer task to state -1
            
            temp = sqlMapClientTemplate.update("CopyTask.pauseOrGoTaskForTimeType",
                MirrorCommonStatic.TASK_STATE_NOACTIVATE);
            // now task to stat 0;
            
            temp = temp
                + sqlMapClientTemplate.update("CopyTask.pauseOrGoTaskForNowType",
                    MirrorCommonStatic.TASK_STATE_WAITTING);
        }
        return temp;
    }

    @SuppressWarnings("deprecation")
    @Override
    public void deleteCopyTaskByPolicy(Integer policyId)
    {
        sqlMapClientTemplate.delete("CopyTask.deleteTaskByPolicy",policyId);
    }
    
}
