package com.huawei.sharedrive.isystem.mirror.manager;

import java.security.InvalidParameterException;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.huawei.sharedrive.isystem.cluster.domain.DataCenter;
import com.huawei.sharedrive.isystem.cluster.service.DCService;
import com.huawei.sharedrive.isystem.exception.BusinessException;
import com.huawei.sharedrive.isystem.mirror.domain.CopyPolicy;
import com.huawei.sharedrive.isystem.mirror.domain.CopyPolicySiteInfo;
import com.huawei.sharedrive.isystem.mirror.domain.CopyPolicyUserConfig;
import com.huawei.sharedrive.isystem.mirror.domain.MirrorCommonStatic;
import com.huawei.sharedrive.isystem.mirror.domain.TimeConfig;
import com.huawei.sharedrive.isystem.mirror.service.CopyPolicyService;
import com.huawei.sharedrive.isystem.mirror.service.CopyTaskService;
import com.huawei.sharedrive.isystem.mirror.service.CopyTaskStatistic;
import com.huawei.sharedrive.isystem.mirror.service.MirrorConfigService;
import com.huawei.sharedrive.isystem.mirror.service.TimeConfigService;
import com.huawei.sharedrive.isystem.system.dao.SystemConfigDAO;

import pw.cdmi.common.config.service.ConfigManager;
import pw.cdmi.common.domain.SystemConfig;

@Service("mirrorManager")
public class MirrorManager
{
    
    private static final Logger LOGGER = LoggerFactory.getLogger(MirrorManager.class);
    
    private static final String MIRROR_GLOBAL_CONFIG_CHANGE = "mirror_global_config_change";
    
    public final static String COPY_POLICY_CHANGE_KEY = "miror_copy_policy_change";
    
    //异地复制时间控制时间配置改变
    public final static String TIME_CONFIG_CHANGE = "miror_time_config_change";
    
    //异地复制时间控制开关状态改变
    public final static String TIME_CONFIG_SWITCH_CHANGE = "miror_time_switch_config_change";
    
    
    

    
    
    
    
    @Autowired
    private ConfigManager configManager;
    
    @Autowired
    private CopyPolicyService copyPolicyService;
    
    
    @Autowired
    private CopyTaskService copyTaskService;
    
    @Autowired
    private TimeConfigService timeConfigService;
    
    @Autowired
    private DCService dcService;
    
    @Autowired
    private MirrorConfigService mirrorManagerService;
    
    @Autowired
    private SystemConfigDAO systemConfigDAO;
    /**
     * 创建复制策略
     * 
     * @param policy
     */
    public boolean createCopyPolicy(CopyPolicy policy)
    {
        if (null == policy)
        {
            LOGGER.error("policy is null");
            throw new BusinessException("policy is null");
        }
        Date date = new Date();
        policy.setCreatedAt(date);
        policy.setModifiedAt(date);
        if (policy.getType() == MirrorCommonStatic.POLICY_APP_USER)
        {
            CopyPolicyUserConfig userConfig = new CopyPolicyUserConfig(policy.getState());
            policy.setUserConfig(userConfig);
        }
        policy.setState(MirrorCommonStatic.POLICY_STATE_COMMON);
        if (!checkCopyPlolicy(policy))
        {
            return false;
        }
        copyPolicyService.createCopyPolicy(policy);
        sendMirrorConfigChangeNotify(COPY_POLICY_CHANGE_KEY, Integer.toString(policy.getId()));
        return true;
    }
    
    /**
     * 删除复制策略
     * 
     * @param policy
     */
    public void deleteCopyPolicy(CopyPolicy policy)
    {
        if (null == policy)
        {
            LOGGER.error("policy is null");
            throw new BusinessException("policy is null");
        }
        
        copyPolicyService.deleteCopyPolicy(policy);
        sendMirrorConfigChangeNotify(COPY_POLICY_CHANGE_KEY, Integer.toString(policy.getId()));
    }
    
    /**
     * 修改复制策略
     * 
     * @param policy
     */
    public boolean modifyCopyPolicy(CopyPolicy policy)
    {
        if (null == policy)
        {
            LOGGER.error("policy is null");
            throw new BusinessException("policy is null");
        }
        policy.setModifiedAt(new Date());
        if (policy.getType() == MirrorCommonStatic.POLICY_APP_USER)
        {
            CopyPolicyUserConfig userConfig = new CopyPolicyUserConfig(policy.getState(), policy.getId());
            policy.setUserConfig(userConfig);
        }
        if (!checkCopyPlolicy(policy))
        {
            return false;
        }
        CopyPolicy temp = copyPolicyService.getCopyPolicy(policy.getId());
        if (null == temp)
        {
            throw new InvalidParameterException("update plicy db get is null");
        }
        policy.setState(temp.getState());
        policy.setCopyType(temp.getCopyType());
        copyPolicyService.modifyCopyPolicy(policy);
        sendMirrorConfigChangeNotify(COPY_POLICY_CHANGE_KEY, Integer.toString(policy.getId()));
        return true;
    }
    
    /**
     * 修改复制策略的执行时间
     * 
     * @param policy
     */
    public void modifyCopyPolicyExeTime(CopyPolicy policy)
    {
        if (null == policy)
        {
            LOGGER.error("policy is null");
            throw new BusinessException("policy is null");
        }
        
        copyPolicyService.modifyCopyPolicyExeTime(policy);
        sendMirrorConfigChangeNotify(COPY_POLICY_CHANGE_KEY, Integer.toString(policy.getId()));
    }
    
    /**
     * 修改复制策略的站点信息
     * 
     * @param policy
     * @param lstCopyPolicyDataSiteInfo
     */
    public void modifyCopyPolicySiteInfo(CopyPolicy policy, List<CopyPolicySiteInfo> lstCopyPolicyDataSiteInfo)
    {
        if (null == policy || null == lstCopyPolicyDataSiteInfo)
        {
            LOGGER.error("policy or  lstCopyPolicyDataSiteInfo  null");
            throw new BusinessException("policy or  lstCopyPolicyDataSiteInfo  null");
        }
        
        copyPolicyService.modifyCopyPolicySiteInfo(policy, lstCopyPolicyDataSiteInfo);
        sendMirrorConfigChangeNotify(COPY_POLICY_CHANGE_KEY, Integer.toString(policy.getId()));
        
    }
    
    /**
     * 修改复制策略状态
     * 
     * @param policy
     */
    public void modifyCopyPolicyState(CopyPolicy policy)
    {
        if (null == policy)
        {
            LOGGER.error("policy is null");
            throw new BusinessException("policy is null");
        }
        
        copyPolicyService.modifyCopyPolicyState(policy);
        sendMirrorConfigChangeNotify(COPY_POLICY_CHANGE_KEY, Integer.toString(policy.getId()));
    }
    
    
    
    /**
     * 设置系统全局开关，只有开启了，策略才有效果
     * 
     * @param flag
     */
    public void setMirrorGlobalEnable(boolean flag)
    {
        mirrorManagerService.setMirrorGlobalEnable(flag);
        sendMirrorConfigChangeNotify(MIRROR_GLOBAL_CONFIG_CHANGE, MirrorCommonStatic.MIRROR_GLOBAL_ENABLE);
    }
    /**
     * 所有暂停任务
     * 
     * @param state
     */
    public void setMirrorGlobalTaskState(int state)
    {
        mirrorManagerService.setMirrorGlobalTaskState(state);
        sendMirrorConfigChangeNotify(MIRROR_GLOBAL_CONFIG_CHANGE, MirrorCommonStatic.MIRROR_GLOBAL_TASK_STATE);
    }
    
    public CopyTaskStatistic statisticCurrentTaskInfo()
    {
        return copyTaskService.statisticCurrentTaskInfo();
    }
    
    public CopyTaskStatistic statisticCurrentTaskInfo(CopyPolicy copyPolicy)
    {
        if (null != copyPolicy)
        {
            return copyTaskService.statisticCurrentTaskInfo(copyPolicy);
        }
        return new CopyTaskStatistic();
    }
    
    public void updateTask(String state)
    {
        SystemConfig systemConfig = new SystemConfig("-1", MirrorCommonStatic.MIRROR_GLOBAL_TASK_STATE, state);
        copyTaskService.pauseOrGoTask(Integer.parseInt(state));
        systemConfigDAO.update(systemConfig);
        sendMirrorConfigChangeNotify(MIRROR_GLOBAL_CONFIG_CHANGE, MirrorCommonStatic.MIRROR_GLOBAL_TASK_STATE);
    }
    
  
    private boolean checkCopyPlolicy(CopyPolicy copyPolicy)
    {
        if (null == copyPolicy)
        {
            return false;
        }
        if (null == copyPolicy.getLstCopyPolicyDataSiteInfo())
        {
            return false;
        }
        List<CopyPolicySiteInfo> list = copyPolicy.getLstCopyPolicyDataSiteInfo();
        if (list.isEmpty())
        {
            return false;
        }
        Set<CopyPolicySiteInfo> set = new HashSet<CopyPolicySiteInfo>(list.size());
        set.addAll(list);
        if (list.size() > set.size())
        {
            return false;
        }
        return checkListCopyPolicySiteInfo(list);
    }
    
    private boolean checkCopyPolicySiteInfo(CopyPolicySiteInfo copyPolicySiteInfo)
    {
        List<DataCenter> destdces = dcService.listDataCenterRe(copyPolicySiteInfo.getDestRegionId());
        if (null == destdces)
        {
            throw new InvalidParameterException("copyPolicySiteInfo.getDestRegionId() db not exist "
                + copyPolicySiteInfo.getDestRegionId());
        }
        List<DataCenter> srcdces = dcService.listDataCenterRe(copyPolicySiteInfo.getSrcRegionId());
        if (null == srcdces)
        {
            throw new InvalidParameterException("copyPolicySiteInfo.getDestRegionId() db not exist "
                + copyPolicySiteInfo.getSrcRegionId());
        }
        boolean tempDest = false;
        for (DataCenter dataCenter : destdces)
        {
            if (dataCenter.getId() == copyPolicySiteInfo.getDestResourceGroupId())
            {
                tempDest = true;
                break;
            }
        }
        boolean srcDest = false;
        for (DataCenter dataCenter : srcdces)
        {
            if (dataCenter.getId() == copyPolicySiteInfo.getSrcResourceGroupId())
            {
                srcDest = true;
                break;
            }
        }
        
        return tempDest && srcDest;
    }
    
    private boolean checkListCopyPolicySiteInfo(List<CopyPolicySiteInfo> list)
    {
        for (CopyPolicySiteInfo copyPolicySiteInfo : list)
        {
            if (!checkCopyPolicySiteInfo(copyPolicySiteInfo))
            {
                return false;
            }
        }
        return true;
    }
    
    /**
     * 发送配置策略改变的消息
     */
    private void sendMirrorConfigChangeNotify(String key, String value)
    {
        configManager.setConfig(key, value);
    }
    
    /**
     * 创建时间设置
     * 
     * @param timeconfig
     */
    public boolean createTimeConfig(TimeConfig timeconfig)
    {
        if (null == timeconfig)
        {
            LOGGER.error("timeconfig is null");
            throw new BusinessException("timeconfig is null");
        }
        Date date = new Date();
        UUID id = UUID.randomUUID();
        String uuid=id.toString();
        timeconfig.setUuid(uuid);
        timeconfig.setCreatedAt(date);
        timeConfigService.createTimeConfig(timeconfig);
        sendMirrorConfigChangeNotify(MIRROR_GLOBAL_CONFIG_CHANGE,TIME_CONFIG_CHANGE);
        return true;
    }
    
    /**
     * 删除时间设置
     * 
     * @param timeconfig
     */
    public void deleteTimeConfig(TimeConfig timeconfig)
    {
        if (null == timeconfig)
        {
            LOGGER.error("timeconfig is null");
            throw new BusinessException("timeconfig is null");
        }
        
        timeConfigService.deleteTimeConfig(timeconfig);
        sendMirrorConfigChangeNotify(MIRROR_GLOBAL_CONFIG_CHANGE, TIME_CONFIG_CHANGE);
    }
    
    /**
     * 设置系统全局开关，只有开启了，策略才有效果
     * 
     * @param flag
     */
    public void setTimeConfigGlobalEnable(boolean flag)
    {
        mirrorManagerService.setTimeConfigGlobalEnable(flag);
        sendMirrorConfigChangeNotify(MIRROR_GLOBAL_CONFIG_CHANGE, TIME_CONFIG_SWITCH_CHANGE);
       
    }
    
   
    
}
