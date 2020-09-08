package com.huawei.sharedrive.app.mirror.manager;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.PostConstruct;

import org.apache.commons.lang.StringUtils;
import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.SerializationUtils;

import com.huawei.sharedrive.app.core.job.ThreadPool;
import com.huawei.sharedrive.app.dataserver.service.DCManager;
import com.huawei.sharedrive.app.mirror.domain.CopyTask;
import com.huawei.sharedrive.app.mirror.domain.DcCopyTaskStatus;
import com.huawei.sharedrive.app.mirror.domain.DcTaskNumConfig;
import com.huawei.sharedrive.app.mirror.domain.JobParament;
import com.huawei.sharedrive.app.mirror.domain.MirrorCommonStatic;
import com.huawei.sharedrive.app.mirror.service.CopyTaskService;
import com.huawei.sharedrive.app.system.service.SystemConfigService;

import pw.cdmi.box.domain.Limit;
import pw.cdmi.common.config.service.ConfigListener;
import pw.cdmi.common.domain.SystemConfig;
import pw.cdmi.common.job.JobExecuteContext;
import pw.cdmi.common.job.JobExecuteRecord;
import pw.cdmi.common.job.daemon.DaemonJobTask;
import pw.cdmi.common.log.LoggerUtil;
import pw.cdmi.core.utils.EnvironmentUtils;
import pw.cdmi.core.utils.JsonUtils;
import pw.cdmi.core.utils.MethodLogAble;
import pw.cdmi.core.zk.ZookeeperServer;

/**
 * 定期获取任务线程，发送任务，定期清理异常任务，定制执行定时任务状态
 * 
 * @author c00287749
 * 
 */
@Service("copyTaskRouter")
public class CopyTaskRouter extends DaemonJobTask<Object> implements ConfigListener
{
    
    private final static int DEFAULT_LENGTH = 5000;
    
    // 任务数
    private final static int TASK_NUMBER = 10;
    
    private static final String CONFIG_PATH = "/config_copyTaskMonitor";
    
    public static final String CONFIG_ZOOKEEPER_KEY_DC_TASKNUM_CHANGE = "config.zookeeper.key.dc.tasknum.change";
    
    @Autowired
    private CopyTaskService copyTaskService;
    
    @Autowired
    private CopyConfigLocalCache copyConfigLocalCache;
    
    @Autowired
    private DCManager dcManager;
    
    @Autowired
    private ZookeeperServer zookeeperServer;
    
    @Autowired
    private CopyTaskMonitor copyTaskMonitor;
    
    @Autowired
    private SystemConfigService systemConfigService;
    
    private CuratorFramework zkClient;
    
    private static final String EXEAGENT = EnvironmentUtils.getHostName();
    
    private static final Logger LOGGER = LoggerFactory.getLogger(CopyTaskRouter.class);
    
    private Map<Integer, DcCopyTaskStatus> map = new HashMap<Integer, DcCopyTaskStatus>(3);
    
    private Map<Integer, Boolean> updateDateFlag = new HashMap<Integer, Boolean>(3);
    
    /**
     * 配置每套dc最大的任务量
     */
    private Map<Integer, Long> dcMaxTaskNumMap = new HashMap<Integer, Long>(3);
    
    private boolean updateFlag = false;
    
    // 默认值
    @Value("${mirror.dc.max.copytask}")
    private int dcMaxTaskNum;
    
    @PostConstruct
    public void init()
    {
        zkClient = zookeeperServer.getClient();
        updateDcMaxTaskNumMap();
    }
    
    private void updateDcMaxTaskNumMap()
    {
        SystemConfig systemConfig = systemConfigService.getConfig("mirror.dc.max.copytask");
        if (systemConfig == null)
        {
            LOGGER.info("mirror.dc.max.copytask is not seted ,use default value:" + dcMaxTaskNum);
            return;
        }
        @SuppressWarnings("unchecked")
        List<DcTaskNumConfig> lstDcTaskNumConfig = (List<DcTaskNumConfig>) JsonUtils.stringToList(systemConfig.getValue(),
            List.class,
            DcTaskNumConfig.class);
        if (lstDcTaskNumConfig != null)
        {
            for (DcTaskNumConfig dcTaskNumConfig : lstDcTaskNumConfig)
            {
                dcMaxTaskNumMap.put(dcTaskNumConfig.getDcId(), dcTaskNumConfig.getMaxTaskNum());
            }
        }
    }
    
    /**
     * 执行任务，让DSS发送任务
     */
    
    @MethodLogAble
    @SuppressWarnings("unchecked")
    @Override
    public void doTask(JobExecuteContext arg0, JobExecuteRecord arg1, Object object)
    {
        if (null == object)
        {
            LOGGER.info("Object is null");
            return;
        }
        List<CopyTask> lstTask = (List<CopyTask>) object;
        
        if (lstTask.isEmpty())
        {
            LOGGER.info("lstTask  isEmpty");
            return;
        }
        
        // TODO
        // 分任务
        int size = lstTask.size();
        
        // 小于十个任务
        if (size < TASK_NUMBER)
        {
            // 执行任务
            ThreadPool.execute(new SendCopyTask(lstTask));
            return;
        }
        
        // 大于10个, 分十个任务发送
        int length = size / TASK_NUMBER;
        List<CopyTask> subLstTask = null;
        SendCopyTask tempTask = null;
        for (int i = 0; i < TASK_NUMBER; i++)
        {
            if (i < (TASK_NUMBER - 1))
            {
                // 按偏移量去任务
                subLstTask = lstTask.subList(i * length, (i + 1) * length);
            }
            else
            {
                // 取最后的
                subLstTask = lstTask.subList(i * length, size);
            }
            if (null == subLstTask || subLstTask.isEmpty())
            {
                break;
            }
            // 执行任务
            tempTask = new SendCopyTask(subLstTask);
            ThreadPool.execute(tempTask);
        }
    }
    
    private void getZkDate()
    {
        map.clear();
        updateDateFlag.clear();
        List<String> lstNode = null;
        Stat stat = null;
        byte[] date = null;
        DcCopyTaskStatus dcCopyTaskStatus = null;
        try
        {
            stat = zkClient.checkExists().forPath(CONFIG_PATH);
            if (null == stat)
            {
                LOGGER.error("root node " + CONFIG_PATH + " not exist");
                return;
            }
            lstNode = zkClient.getChildren().forPath(CONFIG_PATH);
            if (null != lstNode)
            {
                for (String node : lstNode)
                {
                    date = zkClient.getData().forPath(CONFIG_PATH + "/" + node);
                    if (null == date)
                    {
                        LOGGER.warn("zkClient get null date from node :" + node);
                        continue;
                    }
                    dcCopyTaskStatus = (DcCopyTaskStatus) SerializationUtils.deserialize(date);
                    if (null != dcCopyTaskStatus)
                    {
                        map.put(dcCopyTaskStatus.getResourceGroup(), dcCopyTaskStatus);
                        updateDateFlag.put(dcCopyTaskStatus.getResourceGroup(), false);
                        LOGGER.info("zkClient get date ,node is " + node + " totaltask is: "
                            + dcCopyTaskStatus.getTotalTask() + " runngingtask is:"
                            + dcCopyTaskStatus.getRuningTask() + " waiting task is:"
                            + dcCopyTaskStatus.getInput());
                    }
                }
            }
        }
        catch (Exception e)
        {
            LOGGER.error("get zk data error or children error", e);
        }
    }
    
    private List<CopyTask> filterCopyTask(List<CopyTask> lstCopyTask)
    {
        if (null == lstCopyTask)
        {
            return null;
        }
        
        if (updateFlag)
        {
            LOGGER.info("update updateDcMaxTaskNumMap");
            updateDcMaxTaskNumMap();
            updateFlag = false;
        }
        
        List<CopyTask> rstCopyTasks = new ArrayList<CopyTask>(3);
        DcCopyTaskStatus dcCopyTaskStatus = null;
        Long maxNum = null;
        for (CopyTask copyTask : lstCopyTask)
        {
            dcCopyTaskStatus = map.get(copyTask.getDestResourceGroupId());
            // 如果未设置值，则使用默认值
            maxNum = dcMaxTaskNumMap.get(copyTask.getDestResourceGroupId());
            if (null == maxNum)
            {
                dcMaxTaskNumMap.put(copyTask.getDestResourceGroupId(), (long) dcMaxTaskNum);
            }
            if (dcCopyTaskStatus != null)
            {
                //
                if (dcCopyTaskStatus.getInput() >= dcMaxTaskNumMap.get(copyTask.getDestResourceGroupId()))
                {
                    // 将该任务放回数据库
                    LOGGER.debug("dc input task num is:" + dcCopyTaskStatus.getInput()
                        + " large than dcMaxTaskNum:" + dcMaxTaskNum + " write this task to db");
                    copyTask.setPop(false);
                    copyTask.setModifiedAt(new Date());
                    copyTaskService.updateCopyTask(copyTask);
                }
                else
                {
                    rstCopyTasks.add(copyTask);
                    LOGGER.debug("dc input task num is:" + dcCopyTaskStatus.getInput()
                        + " little than dcMaxTaskNum:" + dcMaxTaskNum + " send it");
                    dcCopyTaskStatus.setInput(dcCopyTaskStatus.getInput() + 1);
                    
                    // 如果有数据改变，则做好标记，将数据写回到zk中
                    if (updateDateFlag.containsKey(copyTask.getDestResourceGroupId()))
                    {
                        updateDateFlag.put(copyTask.getDestResourceGroupId(), true);
                    }
                    
                    // 将数据协会zk
                    
                }
            }
            else
            {
                LOGGER.info("dc dcCopyTaskStatus is null, do not add task");
            }
        }
        return rstCopyTasks;
    }
    
    /**
     * 更新zookeeper中的数据，使dc端的任务限制有效
     */
    private void updateZkDate()
    {
        for (Entry<Integer, Boolean> entry : updateDateFlag.entrySet())
        {
            if (entry.getValue())
            {
                copyTaskMonitor.saveToZk(map.get(entry.getKey()), entry.getKey());
            }
        }
    }
    
    @SuppressWarnings("deprecation")
    private boolean checkCurrentTime(JobParament jobParament)
    {
        try
        {
            // 参数配置，只允许晚上进行扫描,时间格式只记录是 凌晨1-6
            if (jobParament == null)
            {
                LOGGER.info("jobParament is null");
                return true;
            }
            String timeConfig = jobParament.getExecuteTime();
            if (StringUtils.isBlank(timeConfig))
            {
                LOGGER.info("timeConfig is null or blank ,just return true");
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
                else if (beginTime >= endTime && (beginTime <= date.getHours() || date.getHours() <= endTime))
                {
                    return true;
                }
                return false;
            }
        }
        catch (RuntimeException e)
        {
            LOGGER.warn(e.getMessage(), e);
            return false;
        }
        catch (Exception e)
        {
            LOGGER.warn(e.getMessage(), e);
            return false;
        }
        
        return true;
    }
    
    private boolean checkAgent(JobParament jobParament)
    {
        if (jobParament == null)
        {
            LOGGER.info("jobParament is null");
            return true;
        }
        String allowAgent = jobParament.getExecuteAgent();
        if (StringUtils.isBlank(allowAgent))
        {
            LOGGER.info("allowAgent is null or blank ,just return true");
            return true;
        }
        if (allowAgent.indexOf(EXEAGENT) < 0)
        {
            LOGGER.info("allowAgent is not include this agent");
            return false;
        }
        return true;
    }
    
    private JobParament parseParament()
    {
        String parament = this.getParameter();
        return JsonUtils.stringToObject(parament, JobParament.class);
    }
    
    /**
     * 从数据库中获取任务列表
     */
    @Override
    public List<CopyTask> takeData()
    {
        dcManager.notifyCluster("");
        
        // 定时刷新
        copyConfigLocalCache.reflushCopyTaskNumber();
        
        if (!copyConfigLocalCache.isSystemMirrorEnable())
        {
            LOGGER.info("mirror_global_enable is false");
            return null;
        }
        
        if (!copyConfigLocalCache.isSystemMirrorTimerEnable())
        {
            LOGGER.info("this System mirror time enable is false");
            return null;
        }
        
        if (MirrorCommonStatic.TASK_STATE_SYSTEM_PAUSE == copyConfigLocalCache.getSystemMirrorTaskState())
        {
            LOGGER.info("mirror_global_task_state is pause,not send task");
            return null;
        }
        
        JobParament jobParament = parseParament();
        
        if (!checkCurrentTime(jobParament))
        {
            LOGGER.error("checkTime failed,paramter" + jobParament.getExecuteTime() + ",date:" + new Date());
            return null;
        }
        
        if (!checkAgent(jobParament))
        {
            LOGGER.error("checkAgent failed,paramter" + jobParament.getExecuteAgent() + ",date:" + new Date());
            return null;
        }
        
        Limit limit = new Limit();
        limit.setOffset(0L);
        limit.setLength(DEFAULT_LENGTH);
        
        List<CopyTask> lstTask = copyTaskService.getWaittingTaskAndSetPop(limit);
        
        if (null != lstTask)
        {
            LOGGER.debug("lstTask size:" + lstTask.size());
        }
        
        // 要从zk拿到数据，决定是否给dc下发任务
        getZkDate();
        
        // 过滤掉超出上限的任务
        lstTask = filterCopyTask(lstTask);
        
        // 将数据写回ZK
        updateZkDate();
        
        // 获取任务
        return lstTask;
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public boolean available(Object object)
    {
        if (null == object)
        {
            return false;
        }
        
        List<CopyTask> lstTask = (List<CopyTask>) object;
        
        if (lstTask.isEmpty())
        {
            LOGGER.info("lstTask  isEmpty");
            return false;
        }
        return true;
    }
    
    @Override
    public void configChanged(String key, Object value)
    {
        LoggerUtil.regiestThreadLocalLog();
        if (!CONFIG_ZOOKEEPER_KEY_DC_TASKNUM_CHANGE.equals(key))
        {
            return;
        }
        
        LOGGER.info("Reload DC MaxNum Cache Cause By Cluster Notify.");
        // 在这里采用变量，也可以直接调用方法更新cache
        updateFlag = true;
    }
    
}
