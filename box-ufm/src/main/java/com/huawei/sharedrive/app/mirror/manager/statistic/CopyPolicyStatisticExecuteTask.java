package com.huawei.sharedrive.app.mirror.manager.statistic;

import java.util.Date;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.huawei.sharedrive.app.common.systemtask.domain.SystemTask;
import com.huawei.sharedrive.app.dataserver.domain.ResourceGroup;
import com.huawei.sharedrive.app.dataserver.service.DCManager;
import com.huawei.sharedrive.app.files.domain.INode;
import com.huawei.sharedrive.app.mirror.domain.CopyPolicy;
import com.huawei.sharedrive.app.mirror.domain.CopyPolicySiteInfo;
import com.huawei.sharedrive.app.mirror.domain.MirrorCommonStatic;
import com.huawei.sharedrive.app.mirror.domain.ObjectMirrorShip;
import com.huawei.sharedrive.app.mirror.domain.UserMirrorStatisticInfo;
import com.huawei.sharedrive.app.mirror.service.CopyPolicyService;
import com.huawei.sharedrive.app.mirror.service.MirrorStatisticService;
import com.huawei.sharedrive.app.mirror.service.UserMirrorStatisticInfoService;

import pw.cdmi.box.domain.Limit;
import pw.cdmi.common.job.JobExecuteContext;
import pw.cdmi.common.job.JobExecuteRecord;
import pw.cdmi.common.job.daemon.DaemonJobTask;
import pw.cdmi.core.utils.JsonUtils;
import pw.cdmi.core.utils.MethodLogAble;
import pw.cdmi.core.utils.RandomGUID;

@Service("copyPolicyStatisticExecuteTask")
public class CopyPolicyStatisticExecuteTask extends DaemonJobTask<Object>
{
    
    @Autowired
    private CopyPolicyService copyPolicyService;
    
    @Autowired
    private MirrorStatisticService mirrorStatisticService;
    
    @Autowired
    private UserMirrorStatisticInfoService userMirrorStatisticInfoService;
    
    @Autowired
    private DCManager dcManager;
    
    @Autowired
    private CopyPolicyStatistics copyPolicyStatistics;
    
    private final static int LENGTH_DEFAULT = 10000;
    
    private static final Logger LOGGER = LoggerFactory.getLogger(CopyPolicyStatisticExecuteTask.class);
    
    public static String getJobName()
    {
        return "copyPolicyStatisticExecuteTask";
    }
    
    @Override
    public boolean available(Object arg0)
    {
        if (null == arg0)
        {
            return false;
        }
        return true;
    }
    
    @MethodLogAble
    @Override
    public void doTask(JobExecuteContext arg0, JobExecuteRecord arg1, Object arg2)
    {
        
        SystemTask task = (SystemTask) arg2;
        if (null == task)
        {
            LOGGER.error("task is null");
            return;
        }
        
        // 检查控制是否
        if (!copyPolicyStatistics.isMirrorPolicyStatisticEnable())
        {
            LOGGER.error("MirrorPolicyStatisticEnable is false,not enable statistic.");
            return;
        }
        
        StatisticTaskInfo info = StatisticTaskInfo.toObject(task.getTaskInfo());
        if (null == info)
        {
            LOGGER.error("The info is null,not execute statistic.");
            return;
        }
        
        CopyPolicy policy = copyPolicyService.getCopyPolicy(info.getPolicyId());
        if (null == policy)
        {
            LOGGER.error("The policy is null,not execute statistic.");
            return;
        }
        
        if (MirrorCommonStatic.POLICY_STATE_COMMON != policy.getState())
        {
            LOGGER.error("policy state not common ,not execute statistic ,state:" + policy.getState());
            return;
        }
        
        // 检查统计类型是否允许
        if (!copyPolicyStatistics.isAllowStatistic(policy))
        {
            LOGGER.error("copyType not allow statistic,policy copy type:" + policy.getCopyType());
            return;
        }
        
        // 执行统计任务
        executeStatistic(info, policy, task);
        
        // 统计下一个用户
        copyPolicyStatistics.setNextStatisticUser(task);
        
    }
    
    @MethodLogAble
    @Override
    public SystemTask takeData()
    {
        if (!checkCurrentTime())
        {
            LOGGER.error("checkTime failed,paramter" + this.getParameter() + ",date:" + new Date());
            return null;
        }
        
        return copyPolicyStatistics.getOneWaitingStatisticTask(null);
    }
    
    @SuppressWarnings("deprecation")
    private boolean checkCurrentTime()
    {
        try
        {
            String timeConfig = this.getParameter();
            if (StringUtils.isBlank(timeConfig))
            {
                return true;
            }
            int beginTime;
            int endTime;
            String[] times = timeConfig.split("-");
            if (2 == times.length)
            {
                beginTime = Integer.parseInt(times[0]);
                endTime = Integer.parseInt(times[1]);
                Date date = new Date();
                if (beginTime <= endTime && beginTime <= date.getHours() && date.getHours() <= endTime)
                {
                    return true;
                }
                if (beginTime >= endTime && (beginTime <= date.getHours() || date.getHours() <= endTime))
                {
                    return true;
                }
                return false;
            }
        }
        catch (RuntimeException e)
        {
            return false;
        }
        catch (Exception e)
        {
            return false;
        }
        return true;
    }
    
    /**
     * 查看統計
     * 
     * @param info
     * @param policy
     */
    private void executeStatistic(StatisticTaskInfo info, CopyPolicy policy, SystemTask task)
    {
        // 如果設置為用戶級，但是用戶配置表中沒有改用戶的複製策略，則不統計
        if (policy.getType() == MirrorCommonStatic.POLICY_APP_USER
            && !copyPolicyService.checkUserCopyPolicy(policy.getId(), info.getUserId()))
        {
            LOGGER.error("policy is user level,but not contains this user.the policy:" + policy.getId()
                + ",appId:" + policy.getAppId() + ",userId:" + info.getUserId());
            return;
        }
        
        long mirrorFileNumber = 0L;
        long notMirrorFileNumber = 0L;
        UserMirrorStatisticInfo statisticInfo = new UserMirrorStatisticInfo();
        statisticInfo.setAppId(info.getAppId());
        statisticInfo.setAccountId(info.getAccountId());
        statisticInfo.setUserId(info.getUserId());
        statisticInfo.setPolicyId(policy.getId());
        statisticInfo.setStatistcDate(new Date());
        statisticInfo.setId(new RandomGUID().getValueAfterMD5());
        
        // 獲取出策略的站點關係
        List<CopyPolicySiteInfo> lstSiteInfo = policy.getLstCopyPolicyDataSiteInfo();
        
        // 列舉用戶文件
        Limit limit = new Limit();
        limit.setOffset(0L);
        limit.setLength(LENGTH_DEFAULT);
        
        List<INode> iNodes = null;
        boolean bFound = false;
        Date date = null;
        while (true)
        {
            iNodes = mirrorStatisticService.lstFileAndVersionNode(info.getUserId(), limit);
            if (CollectionUtils.isEmpty(iNodes))
            {
                break;
            }
            
            for (INode node : iNodes)
            {
                bFound = isExistingMirror(lstSiteInfo, node);
                
                // 发现统计任务
                if (bFound)
                {
                    mirrorFileNumber++;
                }
                else
                {
                    notMirrorFileNumber++;
                    LOGGER.info("UserId:" + info.getUserId() + "the node not miror,objectId:"
                        + node.getObjectId());
                }
                
            }
            
            date = new Date();
            task.setExeUpdateTime(date);
            copyPolicyStatistics.updateStatisticTaskExeTime(task);
            
            if (iNodes.size() < LENGTH_DEFAULT)
            {
                break;
            }
            limit.setOffset(limit.getOffset() + iNodes.size());
        }
        
        statisticInfo.setMirrorFileNumber(mirrorFileNumber);
        statisticInfo.setNotMirrorFileNumber(notMirrorFileNumber);
        
        LOGGER.info(JsonUtils.toJson(statisticInfo));
        userMirrorStatisticInfoService.create(statisticInfo);
        
    }
    
    /**
     * 是否存在镜像
     * 
     * @param lstSiteInfo
     * @param iNode
     * @return
     */
    private boolean isExistingMirror(List<CopyPolicySiteInfo> lstSiteInfo, INode iNode)
    {
        ResourceGroup group = dcManager.getResourceGroup(iNode.getResourceGroupId());
        if (null == group)
        {
            return false;
        }
        
        List<ObjectMirrorShip> ships = null;
        ResourceGroup mirrorGroup = null;
        
        ships = mirrorStatisticService.listObjectMirrorShip(iNode.getObjectId());
        
        if (null == ships || ships.isEmpty())
        {
            LOGGER.info("node not miror,objectId:" + iNode.getObjectId());
            return false;
        }
        
        for (CopyPolicySiteInfo siteInfo : lstSiteInfo)
        {
            // 存储区域不相同则查询下一个
            if (siteInfo.getSrcRegionId() != group.getRegionId())
            {
                continue;
            }
            
            // 如果制定了资源组的则需要比较是否日志
            if (siteInfo.getSrcResourceGroupId() != CopyPolicySiteInfo.DEFAULT_VALUE
                && siteInfo.getSrcResourceGroupId() != group.getId())
            {
                continue;
            }
            
            for (ObjectMirrorShip ship : ships)
            {
                mirrorGroup = dcManager.getResourceGroup(ship.getResourceGroupId());
                
                if (null != mirrorGroup && siteInfo.getDestRegionId() == mirrorGroup.getRegionId())
                {
                    return siteInfo.getDestResourceGroupId() == CopyPolicySiteInfo.DEFAULT_VALUE
                        || siteInfo.getDestResourceGroupId() == ship.getResourceGroupId();
                    
                }
            }
            
        }
        
        return false;
        
    }
    
}
