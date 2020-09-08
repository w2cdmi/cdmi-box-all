package com.huawei.sharedrive.isystem.mirror.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.huawei.sharedrive.isystem.authapp.service.AuthAppService;
import com.huawei.sharedrive.isystem.cluster.domain.DataCenter;
import com.huawei.sharedrive.isystem.cluster.service.DCService;
import com.huawei.sharedrive.isystem.exception.BusinessException;
import com.huawei.sharedrive.isystem.mirror.domain.CopyPolicy;
import com.huawei.sharedrive.isystem.mirror.domain.CopyPolicySiteInfo;
import com.huawei.sharedrive.isystem.mirror.domain.MirrorCommonStatic;
import com.huawei.sharedrive.isystem.mirror.domain.TimeConfig;
import com.huawei.sharedrive.isystem.mirror.service.CopyPolicyService;
import com.huawei.sharedrive.isystem.mirror.service.CopyTaskService;
import com.huawei.sharedrive.isystem.mirror.service.CopyTaskStatistic;
import com.huawei.sharedrive.isystem.mirror.service.MirrorConfigService;
import com.huawei.sharedrive.isystem.mirror.service.TimeConfigService;
import com.huawei.sharedrive.isystem.mirror.web.view.CopyPlolicyView;
import com.huawei.sharedrive.isystem.mirror.web.view.CopyPolicySiteInfoView;
import com.huawei.sharedrive.isystem.plugin.domain.DCTreeNode;
import com.huawei.sharedrive.isystem.system.dao.SystemConfigDAO;

import pw.cdmi.common.domain.SystemConfig;
import pw.cdmi.uam.domain.AuthApp;

@Service("mirrorQueryManager")
public class MirrorQueryManager
{
    
    private static final Logger LOGGER = LoggerFactory.getLogger(MirrorQueryManager.class);
    
    @Autowired
    private AuthAppService appService;
    
    @Autowired
    private CopyPolicyService copyPolicyService;
    
    
    @Autowired
    private CopyTaskService copyTaskService;
    
    @Autowired
    private DCService dcService;
    
    @Autowired
    private MirrorConfigService mirrorManagerService;
    
    @Autowired
    private SystemConfigDAO systemConfigDAO;
    
    @Autowired
    private TimeConfigService timeConfigService;
    
    
    /**
     * 获取App的复制策略
     * 
     * @param appId
     * @return
     */
    public List<CopyPolicy> getAppCopyPolicy(String appId)
    {
        if (StringUtils.isBlank(appId))
        {
            LOGGER.error("appId is null");
            throw new BusinessException("policy is null");
        }
        
        return copyPolicyService.getAppCopyPolicy(appId);
        
    }
    
    /**
     * 获取某一个用户的复制策略
     * 
     * @param appId
     * @param userId
     * @return
     */
    public List<CopyPolicy> getAppCopyPolicy(String appId, long userId)
    {
        return null;
    }
    
    public List<AuthApp> getAuthAppList()
    {
        return appService.getAuthAppList(null, null, null);
    }
    
    /**
     * 
     * @param policyId
     * @return
     */
    public CopyPolicy getCopyPolicy(int policyId)
    {
        return copyPolicyService.getCopyPolicy(policyId);
    }
    
    
    
    public CopyPlolicyView getCopyPolicyView(CopyPolicy copyPolicy)
    {
        List<DataCenter> dataCenters = dcService.listDataCenter();
        if (dataCenters == null)
        {
            return null;
        }
        int size = dataCenters.size();
        Map<Integer, String> dcMap = new HashMap<Integer, String>(size);
        for (DataCenter dcCenter : dataCenters)
        {
            dcMap.put(dcCenter.getId(), dcCenter.getName());
        }
        CopyPlolicyView cpView = null;
        if (null != copyPolicy)
        {
            cpView = coventCopyPlolicyView(copyPolicy, dcMap);
        }
        
        return cpView;
    }
    
    public List<CopyPolicy> getListCopyPolicy()
    {
        List<CopyPolicy> policies = copyPolicyService.listCopyPolicy();
        return policies;
    }
    
    
    /**
     * 获取系统对当前复制任务状态，允许任务允许、暂停任务、删除任务、
     * 
     * @return
     */
    public int getMirrorGlobalTaskState()
    {
        return mirrorManagerService.getMirrorGlobalTaskState();
    }
    
    public int getSystemConfig()
    {
        SystemConfig systemConfig = systemConfigDAO.get(MirrorCommonStatic.MIRROR_GLOBAL_TASK_STATE);
        int temp = 0;
        try
        {
            temp = Integer.parseInt(systemConfig.getValue());
        }
        catch (NumberFormatException e)
        {
            LOGGER.error("The string:" + systemConfig.getValue() + " to int err");
            throw e;
        }
        if (temp != MirrorCommonStatic.TASK_STATE_WAITTING
            && temp != MirrorCommonStatic.TASK_STATE_SYSTEM_PAUSE)
        {
            temp = MirrorCommonStatic.TASK_STATE_WAITTING;
        }
        return temp;
    }
    
    public List<DCTreeNode> getTreeNode(Integer id)
    {
        return copyPolicyService.getTreeNode(id);
    }
    public int countAllTimeConfig()
    {
        return timeConfigService.countAll();
    }
    /**
     * 获取系统全局开关，只有开启了，策略才有效果
     * 
     * @return
     */
    public boolean isMirrorGlobalEnable()
    {
        return mirrorManagerService.isMirrorGlobalEnable();
    }
    
    /**
     * 获取策略配置
     * 
     * @return
     */
    public List<CopyPolicy> listCopyPolicy()
    {
        return copyPolicyService.listCopyPolicy();
    }
    

    
    public List<CopyPlolicyView> listCopyPolicyForView()
    {
        List<DataCenter> dataCenters = dcService.listDataCenter();
        if (dataCenters == null)
        {
            return new ArrayList<CopyPlolicyView>(0);
        }
        int size = dataCenters.size();
        Map<Integer, String> dcMap = new HashMap<Integer, String>(size);
        for (DataCenter dcCenter : dataCenters)
        {
            dcMap.put(dcCenter.getId(), dcCenter.getName());
        }
        List<CopyPolicy> policies = copyPolicyService.listCopyPolicy();
        List<CopyPlolicyView> cpViews = new ArrayList<CopyPlolicyView>(10);
        CopyPlolicyView cpView = null;
        if (null != policies)
        {
            for (CopyPolicy copyPolicy : policies)
            {
                
                cpView = coventCopyPlolicyView(copyPolicy, dcMap);
                cpViews.add(cpView);
            }
        }
        
        return cpViews;
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
   
    
   
    
    private CopyPlolicyView coventCopyPlolicyView(CopyPolicy copyPolicy, Map<Integer, String> dcMap)
    {
        CopyPlolicyView cpView = new CopyPlolicyView(copyPolicy);
        List<CopyPolicySiteInfoView> cpsiteViews = null;
        CopyPolicySiteInfoView cpsiteView = null;
        if (copyPolicy.getLstCopyPolicyDataSiteInfo() != null)
        {
            cpsiteViews = new ArrayList<CopyPolicySiteInfoView>(copyPolicy.getLstCopyPolicyDataSiteInfo()
                .size());
            for (CopyPolicySiteInfo copyPolicySiteInfo : copyPolicy.getLstCopyPolicyDataSiteInfo())
            {
                cpsiteView = new CopyPolicySiteInfoView(copyPolicySiteInfo,
                    dcMap.get(copyPolicySiteInfo.getSrcResourceGroupId()),
                    dcMap.get(copyPolicySiteInfo.getDestResourceGroupId()));
                cpsiteViews.add(cpsiteView);
            }
            
            cpView.setSiteViews(cpsiteViews);
        }
        
        return cpView;
    }
    
    public TimeConfig getTimeConfig(String uuid)
    {
        return timeConfigService.getTimeConfig(uuid);
    }
    public List<TimeConfig> getListTimeConfig()
    {
        List<TimeConfig> timeconfigs = timeConfigService.listTimeConfig();
        return timeconfigs;
    }
    /**
     * 获取系统全局开关，只有开启了，策略才有效果
     * 
     * @return
     */
    public boolean isTimeConfigGlobalEnable()
    {
        return mirrorManagerService.isTimeConfigGlobalEnable();
    }
}
