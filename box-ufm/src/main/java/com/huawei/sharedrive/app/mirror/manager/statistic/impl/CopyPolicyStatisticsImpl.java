package com.huawei.sharedrive.app.mirror.manager.statistic.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.huawei.sharedrive.app.account.domain.Account;
import com.huawei.sharedrive.app.account.service.AccountService;
import com.huawei.sharedrive.app.common.systemtask.domain.SystemTask;
import com.huawei.sharedrive.app.common.systemtask.domain.TaskKeyConstant;
import com.huawei.sharedrive.app.common.systemtask.service.SystemTaskService;
import com.huawei.sharedrive.app.mirror.domain.CopyPolicy;
import com.huawei.sharedrive.app.mirror.domain.MirrorCommonStatic;
import com.huawei.sharedrive.app.mirror.manager.statistic.CopyPolicyStatistics;
import com.huawei.sharedrive.app.mirror.manager.statistic.StatisticTaskInfo;
import com.huawei.sharedrive.app.mirror.service.MirrorSystemConfigService;
import com.huawei.sharedrive.app.user.domain.User;
import com.huawei.sharedrive.app.user.service.UserService;

import pw.cdmi.core.utils.DateUtils;
import pw.cdmi.core.utils.MethodLogAble;
import pw.cdmi.core.utils.RandomGUID;

/**
 * 执行统计
 * 
 * @author c00287749
 * 
 */
@Service("copyPolicyStatistics")
public class CopyPolicyStatisticsImpl implements CopyPolicyStatistics
{
    
    @Autowired
    private MirrorSystemConfigService mirrorSystemConfigService;
    
    @Autowired
    private SystemTaskService systemTaskService;
    
    @Autowired
    private AccountService accountService;
    
    @Autowired
    private UserService userService;
    
    private static final Logger LOGGER = LoggerFactory.getLogger(CopyPolicyStatisticsImpl.class);
    
    @Override
    public void clearTimeOutSystemTaskForCopyPolicy()
    {
        List<SystemTask> lstTask = systemTaskService.listSystemTaskByTaskKey(TaskKeyConstant.MIRROR_POLICY_STATISTIC_TASK);
        if (null == lstTask || lstTask.isEmpty())
        {
            return;
        }
        
        int timeOut = mirrorSystemConfigService.getCopyStatisticExeTimeout();
        for (SystemTask task : lstTask)
        {
            // 超时时间超过180分钟，就是删除
            if (task.getExeUpdateTime() != null
                && task.getExeUpdateTime().before(DateUtils.getDateBeforeMinute(new Date(), timeOut)))
            {
                LOGGER.info("The copyStatistic task time out ," + task.getTaskInfo() + ",updatetime :"
                    + task.getExeUpdateTime());
                systemTaskService.deleteTask(task.getTaskId());
            }
        }
    }
    
    /**
     * 创建任务
     * 
     * @param policy
     */
    @MethodLogAble
    @Override
    public void createSystemTaskForCopyPolicy(CopyPolicy policy)
    {
        
        if (null == policy)
        {
            LOGGER.error("policy is null");
            return;
        }
        
        // 检查控制是否
        if (!isMirrorPolicyStatisticEnable())
        {
            LOGGER.error("MirrorPolicyStatisticEnable is false,not enable statistic.");
            return;
        }
        
        if (policy.getState() == MirrorCommonStatic.POLICY_STATE_PAUSE)
        {
            // 如果暂停任务，就删除任务
            deleteSystemTaskForCopyPolicy(policy.getId());
        }
        
        // 检查统计类型是否允许
        if (!isAllowStatistic(policy))
        {
            LOGGER.error("copyType not allow statistic,policy copy type:" + policy.getCopyType());
            return;
        }
        List<SystemTask> lst = systemTaskService.listSystemTaskByParentTaskId(String.valueOf(policy.getId()));
        if (null != lst && !lst.isEmpty())
        {
            LOGGER.info("There is existing statistic task" + lst.size());
            return;
        }
        
        SystemTask task = new SystemTask();
        task.setTaskId(new RandomGUID().getValueAfterMD5());
        task.setCreateTime(new Date());
        task.setTaskKey(TaskKeyConstant.MIRROR_POLICY_STATISTIC_TASK);
        task.setState(SystemTask.TASK_STATE_BEGIN);
        // 设置策略ID为父ID,编译查询
        task.setpTaskId(policy.getId() + "");
        StatisticTaskInfo info = new StatisticTaskInfo();
        info.setAppId(policy.getAppId());
        info.setPolicyId(policy.getId());
        
        task.setTaskInfo(StatisticTaskInfo.toJsonStr(info));
        
        systemTaskService.createSingleTask(task);
        
    }
    
    @Override
    public void deleteSystemTaskForCopyPolicy(int policyId)
    {
        // 列举策略统计任务
        List<SystemTask> lstTask = systemTaskService.listSystemTaskByParentTaskId(String.valueOf(policyId));
        
        if (null == lstTask || lstTask.isEmpty())
        {
            return;
        }
        
        for (SystemTask task : lstTask)
        {
            systemTaskService.deleteTask(task.getTaskId());
        }
    }
    
    @Override
    public boolean isMirrorPolicyStatisticEnable()
    {
        return mirrorSystemConfigService.isSystemMirrorPolicyStatisticEnable();
    }
    
    /**
     * 获取一个任务
     * 
     * @return
     */
    @MethodLogAble
    @Override
    public SystemTask getOneWaitingStatisticTask(String pTaskId)
    {
        SystemTask task = systemTaskService.getOneWaitingExecuteTask(pTaskId,
            TaskKeyConstant.MIRROR_POLICY_STATISTIC_TASK);
        
        if (null == task)
        {
            LOGGER.info("getOneWaitingExecuteTask is null,taskKey:"
                + TaskKeyConstant.MIRROR_POLICY_STATISTIC_TASK);
            
            return null;
        }
        
        StatisticTaskInfo info = StatisticTaskInfo.toObject(task.getTaskInfo());
        if (null == info)
        {
            LOGGER.info("StatisticTaskInfo.toObject is null");
            return null;
        }
        else if (StatisticTaskInfo.INIT_DEFAULT == info.getAccountId()
            || StatisticTaskInfo.INIT_DEFAULT == info.getUserId())
        {
            
            LOGGER.info("accountId or userId is init value,to set next user.");
            // 任务开始的时候肯定会出现这一步
            setNextStatisticUser(task);
            return null;
        }
        return task;
        
    }
    
    @Override
    public boolean isAllowStatistic(CopyPolicy policy)
    {
        return mirrorSystemConfigService.checkSystemMirrorPolicyStatisticType(policy.getCopyType());
    }
    
    /**
     * 
     * @param task
     * @return
     */
    @Override
    public SystemTask setNextStatisticUser(SystemTask task)
    {
        StatisticTaskInfo info = StatisticTaskInfo.toObject(task.getTaskInfo());
        Map<Long, Long> map = getNextUser(info.getAppId(), info.getAccountId(), info.getUserId());
        if (null != map)
        {
            for (Map.Entry<Long, Long> entry : map.entrySet())
            {
                info.setAccountId(entry.getKey());
                info.setUserId(entry.getValue());
                break;
            }
        }
        else
        {
            LOGGER.info("the policy statistic task  finish,task info:" + task.getTaskInfo());
            
            /**
             * 如果是newtask为空，表示该policy 的统计完成，需要删除policy统计任务。getpTask就是存放的policy id
             */
            deleteSystemTaskForCopyPolicy(Integer.parseInt(task.getpTaskId()));
            return null;
        }
        String infoStr = StatisticTaskInfo.toJsonStr(info);
        LOGGER.info(infoStr);
        task.setTaskInfo(infoStr);
        task.setExeUpdateTime(new Date());
        task.setState(SystemTask.TASK_STATE_BEGIN);
        task.setExeAgent(null);
        
        systemTaskService.updateTask(task);
        
        return task;
    }
    
    @Override
    public void updateStatisticTaskExeTime(SystemTask task)
    {
        systemTaskService.updateExecuteState(task.getState(), task.getExeUpdateTime(), task.getTaskId());
    }
    
    @MethodLogAble
    private Map<Long, Long> getNextUser(String appId, long accountId, long userId)
    {
        
        long newAccountId = accountId;
        // 判断accoutid是否有效
        if (StatisticTaskInfo.INIT_DEFAULT == accountId)
        {
            Account account = accountService.getOneAccountOrderByACS(appId, accountId);
            if (null == account)
            {
                LOGGER.info("appId " + appId + "not existing account: " + accountId);
                // 表明该App下面无account
                return null;
            }
            
            newAccountId = account.getId();
            
        }
        
        // 首先查询当前accoutid是否还有user;
        User user = userService.getOneUserOrderByACS(accountId, userId);
        LOGGER.info("========================accountId: " + accountId + "user ID: " + userId);
        if (null == user)
        {
            // 重新找Account
            Account account = accountService.getOneAccountOrderByACS(appId, accountId);
            if (null == account)
            {
                // 表明该App下面无account
                LOGGER.info("appId " + appId + ",not existing account: " + accountId);
                return null;
            }
            
            // 返回下一个
            return getNextUser(appId, account.getId(), StatisticTaskInfo.INIT_DEFAULT);
        }
        LOGGER.info("========================newAccountId: " + newAccountId + "user ID: " + user.getId());
        Map<Long, Long> map = new HashMap<Long, Long>(1);
        
        map.put(newAccountId, user.getId());
        
        return map;
    }
    
}
