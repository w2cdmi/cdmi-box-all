package com.huawei.sharedrive.isystem.mirror.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.huawei.sharedrive.isystem.exception.BusinessException;
import com.huawei.sharedrive.isystem.mirror.domain.MirrorCommonStatic;
import com.huawei.sharedrive.isystem.mirror.service.MirrorConfigService;
import com.huawei.sharedrive.isystem.system.dao.SystemConfigDAO;

import pw.cdmi.common.domain.SystemConfig;

@Service("mirrorManagerService")
public class MirrorManagerServiceImpl implements MirrorConfigService
{
    
    @Autowired
    private SystemConfigDAO systemConfigDAO;
    
    private static final Logger LOGGER = LoggerFactory.getLogger(MirrorManagerServiceImpl.class);
    
    @Override
    public boolean isMirrorGlobalEnable()
    {
        SystemConfig value = systemConfigDAO.get(MirrorCommonStatic.MIRROR_GLOBAL_ENABLE);
        if (null == value)
        {
            return false;
        }
        LOGGER.info("getSystemMirrorEnable:" + value.getId() + ',' + value.getValue());
        return Boolean.parseBoolean(value.getValue());
    }
    
    @Override
    public void setMirrorGlobalEnable(boolean flag)
    {
        SystemConfig value = systemConfigDAO.get(MirrorCommonStatic.MIRROR_GLOBAL_ENABLE);
        if (null == value)
        {
            throw new BusinessException("not found system config:" + MirrorCommonStatic.MIRROR_GLOBAL_ENABLE);
        }
        value.setValue(String.valueOf(flag));
        systemConfigDAO.update(value);
    }
    
    @Override
    public int getMirrorGlobalTaskState()
    {
        SystemConfig value = systemConfigDAO.get(MirrorCommonStatic.MIRROR_GLOBAL_TASK_STATE);
        if (null == value)
        {
            return MirrorCommonStatic.TASK_STATE_WAITTING;
        }
        try
        {
            LOGGER.info("MIRROR_GLOBAL_TASK_STATE:" + value.getId() + ',' + value.getValue());
            return Integer.parseInt(value.getValue());
        }
        catch (Exception e)
        {
            LOGGER.warn("parse task state fail", e.getMessage());
            return MirrorCommonStatic.TASK_STATE_WAITTING;
        }
    }
    
    @Override
    public void setMirrorGlobalTaskState(int state)
    {
        SystemConfig value = systemConfigDAO.get(MirrorCommonStatic.MIRROR_GLOBAL_TASK_STATE);
        if (null == value)
        {
            throw new BusinessException("not found system config:"
                + MirrorCommonStatic.MIRROR_GLOBAL_TASK_STATE);
        }
        value.setValue(Integer.toString(state));
        systemConfigDAO.update(value);
    }
    
    @Override
    public boolean isTimeConfigGlobalEnable()
    {
        SystemConfig value = systemConfigDAO.get(MirrorCommonStatic.TIMECONFIG_ENABLE);
        if (null == value)
        {
            return false;
        }
        LOGGER.info("getSystemMirrorEnable:" + value.getId() + ',' + value.getValue());
        return Boolean.parseBoolean(value.getValue());
    }
    
    @Override
    public void setTimeConfigGlobalEnable(boolean flag)
    {
        SystemConfig value = systemConfigDAO.get(MirrorCommonStatic.TIMECONFIG_ENABLE);
        if (null == value)
        {
            throw new BusinessException("not found system config:" + MirrorCommonStatic.TIMECONFIG_ENABLE);
        }
        value.setValue(String.valueOf(flag));
        systemConfigDAO.update(value);
    }
    
    @Override
    public void setMirrorGlobalEnableByTimer(boolean flag)
    {
        SystemConfig value = systemConfigDAO.get(MirrorCommonStatic.MIRROR_GLOBAL_ENABLE_TIMER);
        if (null == value)
        {
            throw new BusinessException("not found system config:"
                + MirrorCommonStatic.MIRROR_GLOBAL_ENABLE_TIMER);
        }
        value.setValue(String.valueOf(flag));
        systemConfigDAO.update(value);
    }
    
}
