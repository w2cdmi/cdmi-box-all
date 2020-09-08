package com.huawei.sharedrive.app.mirror.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.huawei.sharedrive.app.mirror.domain.MirrorCommonStatic;
import com.huawei.sharedrive.app.mirror.service.MirrorSystemConfigService;
import com.huawei.sharedrive.app.system.dao.SystemConfigDAO;

import pw.cdmi.box.domain.Limit;
import pw.cdmi.common.domain.SystemConfig;

@Service("mirrorSystemConfigService")
public class MirrorSystemConfigServiceImpl implements MirrorSystemConfigService
{
    @Autowired
    private SystemConfigDAO systemConfigDAO;
    
    // 任務發送超時時間
    private static final int TASK_SEND_TIMEOUT_MINUTE = 60;
    
    // 任務執行超時時間
    private static final int TASK_EXE_TIMEOUT_MINUTE = 60 * 24;
    
    // 统计任务执行超时
    private static final int STATISTIC_EXE_TIME_OUT_MINUTE = 180;
    
    private static final Logger LOGGER = LoggerFactory.getLogger(MirrorSystemConfigServiceImpl.class);
    
    @Override
    public boolean checkSystemMirrorPolicyStatisticType(int copyType)
    {
        // 支持的复制类型，按","分割，默认统计只支持容灾场景,就1。
        Limit limit = new Limit();
        limit.setOffset(0L);
        limit.setLength(1);
        List<SystemConfig> values = systemConfigDAO.getByPrefix(limit,
            MirrorCommonStatic.MIRROR_GLOBAL_POLICY_STATISTIC_TYPE);
        if (null == values || values.isEmpty())
        {
            return false;
        }
        
        String str = values.get(0).getValue();
        if (null == str)
        {
            return false;
        }
        
        try
        {
            String[] typeStr = str.split(",");
            for (String t : typeStr)
            {
                
                if (Integer.parseInt(t) == copyType)
                {
                    return true;
                }
                
            }
        }
        catch (Exception e)
        {
            LOGGER.warn(e.getMessage(), e);
            return false;
        }
        
        return false;
    }
    
    @Override
    public int getCopyStatisticExeTimeout()
    {
        
        Limit limit = new Limit();
        limit.setOffset(0L);
        limit.setLength(1);
        List<SystemConfig> values = systemConfigDAO.getByPrefix(limit,
            MirrorCommonStatic.MIRROR_GLOBAL_POLICY_STATISTIC_EXE_TIMEOUT_MINUTE);
        if (null == values || values.isEmpty())
        {
            return STATISTIC_EXE_TIME_OUT_MINUTE;
        }
        return Integer.parseInt(values.get(0).getValue());
    }
    
    @Override
    public int getCopyTaskExeTimeout()
    {
        Limit limit = new Limit();
        limit.setOffset(0L);
        limit.setLength(1);
        List<SystemConfig> values = systemConfigDAO.getByPrefix(limit,
            MirrorCommonStatic.MIRROR_GLOBAL_TASK_EXE_TIMEOUT_MINUTE);
        if (null == values || values.isEmpty())
        {
            return TASK_EXE_TIMEOUT_MINUTE;
        }
        return Integer.parseInt(values.get(0).getValue());
    }
    
    @Override
    public int getCopyTaskSendTimeout()
    {
        Limit limit = new Limit();
        limit.setOffset(0L);
        limit.setLength(1);
        List<SystemConfig> values = systemConfigDAO.getByPrefix(limit,
            MirrorCommonStatic.MIRROR_GLOBAL_TASK_SEND_TIMEOUT_MINUTE);
        if (null == values || values.isEmpty())
        {
            return TASK_SEND_TIMEOUT_MINUTE;
        }
        return Integer.parseInt(values.get(0).getValue());
    }
    
    @Override
    public String getErrorCodeForNeedDeleteFailedTask()
    {
        
        Limit limit = new Limit();
        limit.setOffset(0L);
        limit.setLength(1);
        List<SystemConfig> values = systemConfigDAO.getByPrefix(limit,
            MirrorCommonStatic.MIRROR_GLOBAL_TASK_DELETE_CODE);
        if (null == values || values.isEmpty())
        {
            return null;
        }
        return values.get(0).getValue();
    }
    
    @Override
    public boolean isSystemMirrorEnable()
    {
        Limit limit = new Limit();
        limit.setOffset(0L);
        limit.setLength(1);
        List<SystemConfig> values = systemConfigDAO.getByPrefix(limit,
            MirrorCommonStatic.MIRROR_GLOBAL_ENABLE);
        if (null == values || values.isEmpty())
        {
            return false;
        }
        try
        {
            LOGGER.info("getSystemMirrorEnable:" + values.get(0).getId() + ',' + values.get(0).getValue());
            return Boolean.parseBoolean(values.get(0).getValue());
            
        }
        catch (Exception e)
        {
            LOGGER.warn("parse mirrorEnable fail", e.getMessage());
            return false;
        }
    }
    
    @Override
    public boolean isTimeconfigEnable()
    {
        Limit limit = new Limit();
        limit.setOffset(0L);
        limit.setLength(1);
        List<SystemConfig> values = systemConfigDAO.getByPrefix(limit,
            MirrorCommonStatic.TIMECONFIG_ENABLE);
        if (null == values || values.isEmpty())
        {
            return false;
        }
        try
        {
            LOGGER.info("getSystemTimeConfigEnable:" + values.get(0).getId() + ',' + values.get(0).getValue());
            return Boolean.parseBoolean(values.get(0).getValue());
            
        }
        catch (Exception e)
        {
            LOGGER.warn("parse timeEnable fail", e.getMessage());
            return false;
        }
    }
    
    @Override
    public boolean isSystemMirrorEnableTimer()
    {
        Limit limit = new Limit();
        limit.setOffset(0L);
        limit.setLength(1);
        List<SystemConfig> values = systemConfigDAO.getByPrefix(limit,
            MirrorCommonStatic.MIRROR_GLOBAL_ENABLE_TIMER);
        if (null == values || values.isEmpty())
        {
            return false;
        }
        try
        {
            LOGGER.info("getSystemMirrorEnableTimer:" + values.get(0).getId() + ','
                + values.get(0).getValue());
            return Boolean.parseBoolean(values.get(0).getValue());
            
        }
        catch (Exception e)
        {
            LOGGER.warn("parse mirrorEnableTimer fail", e.getMessage());
            return false;
        }
    }
    
    @Override
    public boolean isSystemMirrorPolicyStatisticEnable()
    {
        
        Limit limit = new Limit();
        limit.setOffset(0L);
        limit.setLength(1);
        List<SystemConfig> values = systemConfigDAO.getByPrefix(limit,
            MirrorCommonStatic.MIRROR_GLOBAL_POLICY_STATISTIC_ENABLE);
        if (null == values || values.isEmpty())
        {
            return false;
        }
        return Boolean.parseBoolean(values.get(0).getValue());
    }
    
    @Override
    public int getSystemMirrorTaskState()
    {
        Limit limit = new Limit();
        limit.setOffset(0L);
        limit.setLength(1);
        List<SystemConfig> values = systemConfigDAO.getByPrefix(limit,
            MirrorCommonStatic.MIRROR_GLOBAL_TASK_STATE);
        if (null == values || values.isEmpty())
        {
            return MirrorCommonStatic.TASK_STATE_WAITTING;
        }
        return Integer.parseInt(values.get(0).getValue());
    }
    
    @Override
    public boolean isSystemNearAccessEnable()
    {
        Limit limit = new Limit();
        limit.setOffset(0L);
        limit.setLength(1);
        List<SystemConfig> values = systemConfigDAO.getByPrefix(limit,
            MirrorCommonStatic.SYSTEM_NEAR_ACCESS_ENABLE);
        if (null == values || values.isEmpty())
        {
            return false;
        }
        return Boolean.parseBoolean(values.get(0).getValue());
    }
    
    @Override
    public Map<String, Boolean> lstNearAccessEnable()
    {
        List<SystemConfig> lst = systemConfigDAO.getByPrefix(null,
            MirrorCommonStatic.SYSTEM_NEAR_ACCESS_APP_ENABLE_PREFIX);
        
        if (null == lst || lst.isEmpty())
        {
            return null;
        }
        Map<String, Boolean> map = new HashMap<String, Boolean>(10);
        
        for (SystemConfig config : lst)
        {
            map.put(config.getAppId(), Boolean.parseBoolean(config.getValue()));
        }
        
        return map;
    }
    
}
