package com.huawei.sharedrive.app.mirror.manager;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.annotation.PostConstruct;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import com.huawei.sharedrive.app.common.systemtask.domain.TaskKeyConstant;
import com.huawei.sharedrive.app.dataserver.domain.DataCenter;
import com.huawei.sharedrive.app.mirror.appdatamigration.domain.MigrationProcessInfo;
import com.huawei.sharedrive.app.mirror.appdatamigration.service.MigrationProcessInfoService;
import com.huawei.sharedrive.app.mirror.domain.CopyPolicy;
import com.huawei.sharedrive.app.mirror.domain.MirrorCommonStatic;
import com.huawei.sharedrive.app.mirror.domain.TimeConfig;
import com.huawei.sharedrive.app.mirror.service.CopyPolicyService;
import com.huawei.sharedrive.app.mirror.service.MirrorSystemConfigService;
import com.huawei.sharedrive.app.mirror.service.TimeConfigService;
import com.huawei.sharedrive.app.mirror.service.UserMirrorStatisticInfoService;

import pw.cdmi.common.config.service.ConfigListener;
import pw.cdmi.common.job.ThreadPool;
import pw.cdmi.core.utils.MethodLogAble;

@Service("copyPolicyLocalCache")
@Lazy(false)
public class CopyConfigLocalCache implements ConfigListener
{
    
    @Autowired
    private CopyPolicyService copyPolicyService;
    
    @Autowired
    private MirrorSystemConfigService mirrorSystemConfigService;
    
    @Autowired
    private NearAccessManager nearAccessManager;
    
    @Autowired
    private MigrationProcessInfoService migrationProcessInfoService;
    
    @Autowired
    private UserMirrorStatisticInfoService userMirrorStatisticInfoService;
    
    
    // dcService的注入形成了循环引用。bug已修复，修改了 DCServiceImpl，CopyConfigLocalCache类文件
//    @Autowired
//    private DCService dcService;
    
    @Autowired
    private TimeConfigService timeConfigService;
    
    private static final ReadWriteLock LOCK = new ReentrantReadWriteLock(true);
    
    private static final long MAX_TASK_NUMBER = 1000000L;
    
    private long copyTaskNumber = 0L;
    
    private static List<CopyPolicy> lstCopyPolicy = new ArrayList<CopyPolicy>(10);
    
    private static List<DataCenter> lstDataCenter =new ArrayList<DataCenter>(10);
    
    private static List<TimeConfig> lstTimeConfig = new ArrayList<TimeConfig>(10);
    
    private volatile boolean timeconfigEnable = false;
    
    private volatile boolean mirrorGlobalEnable = false;
    
    private volatile boolean mirrorGlobalEnableTimer = false;
    
    private volatile int mirrorGlobalTaskState = MirrorCommonStatic.TASK_STATE_WAITTING;
    
    private static final Logger LOGGER = LoggerFactory.getLogger(CopyConfigLocalCache.class);
    
    private static void unlock(Lock lock)
    {
        try
        {
            lock.unlock();
        }
        catch (Exception e)
        {
            LOGGER.warn("Unlock Failed.", e);
        }
    }
    
    @Override
    public void configChanged(String key, Object value)
    {
        if (MirrorCommonStatic.COPY_POLICY_CHANGE_KEY.equalsIgnoreCase(key))
        {
            LOGGER.info("mirror configChanged:" + key + ':' + value);
            initCopyPolicyLocalCache();
        }
        else if (MirrorCommonStatic.MIRROR_GLOBAL_CONFIG_CHANGE.equalsIgnoreCase(key))
        {
            LOGGER.info("mirror configChanged:" + key + ':' + value);
            if (MirrorCommonStatic.MIRROR_GLOBAL_ENABLE.equalsIgnoreCase((String) value))
            {
                setSystemMirrorEnable(mirrorSystemConfigService.isSystemMirrorEnable());
                
                int isMrrorGlobalEnable = 1;
                if (mirrorGlobalEnable)
                {
                    isMrrorGlobalEnable = 0;
                }
                
                ThreadPool.execute(new ManagerDSSCopyTask(null, isMrrorGlobalEnable,
                    MirrorCommonStatic.MIRROR_GLOBAL_ENABLE));
            }
            else if (MirrorCommonStatic.MIRROR_GLOBAL_TASK_STATE.equalsIgnoreCase((String) value))
            {
                // reload
                setSystemMirrorTaskState(mirrorSystemConfigService.getSystemMirrorTaskState());
                
                // 管理DSS上任务，每一个收到这个配置，均会发送任务
                ThreadPool.execute(new ManagerDSSCopyTask(null, mirrorGlobalTaskState,
                    MirrorCommonStatic.MIRROR_GLOBAL_TASK_STATE));
                
            }
            else if (MirrorCommonStatic.TIME_CONFIG_CHANGE.equalsIgnoreCase((String) value))
            {
                // 根据时间配置，当前时间配置改变，重新加载配置时间段，并重新计算结果，并将结果下发至dss 
                initTimeConfigLocalCache();
                
                isSystemMirrorTimerEnable();
                
            }
            else if (MirrorCommonStatic.TIME_CONFIG_SWITCH_CHANGE.equalsIgnoreCase((String) value))
            {
             // 根据时间开关配置，当前时间配置改变，重新加载时间开关状态，并重新计算结果，并将结果下发至dss
                timeconfigEnable = mirrorSystemConfigService.isTimeconfigEnable();
                
                isSystemMirrorTimerEnable();
            }
        }
        else if (TaskKeyConstant.MIRROR_BACK_SCAN_TASK.equalsIgnoreCase(key))
        {
            // 任务启动
            LOGGER.warn("Empty to start task");
        }
        else if (MirrorCommonStatic.NEAR_ACCESS_CONFIG_CHANGE.equalsIgnoreCase(key))
        {
            // 就近上传配置修改
            nearAccessManager.loadNearAccessConfig();
        }
        else if (MirrorCommonStatic.REGION_NETWORK_DISTANCE_CHANGE.equalsIgnoreCase(key))
        {
            // 网络配置修改，重新load
            nearAccessManager.loadRegionNetDistance();
        }else if(MirrorCommonStatic.PRIORITY_CHANGE_KEY.equalsIgnoreCase(key))
        {
            //同一存储数据中心下载优先级发生下载。
            initDataCenterLocalCache();
        }
        
    }
    
    public List<CopyPolicy> getCopyPolicy(String appId)
    {
        
        if (StringUtils.isBlank(appId))
        {
            return null;
        }
        
        List<CopyPolicy> lstPolicy = new ArrayList<CopyPolicy>(10);
        for (CopyPolicy policy : lstCopyPolicy)
        {
            if (policy.getAppId().equalsIgnoreCase(appId))
            {
                lstPolicy.add(policy);
            }
        }
        return lstPolicy;
    }
    
    public List<CopyPolicy> getLstCopyPolicy(String appId)
    {
        if (StringUtils.isBlank(appId))
        {
            return null;
        }
        List<CopyPolicy> lstPolicy = new ArrayList<CopyPolicy>(10);
        for (CopyPolicy copyPolicy : lstCopyPolicy)
        {
            if (copyPolicy.getAppId().equalsIgnoreCase(appId)
                && copyPolicy.getState() == MirrorCommonStatic.POLICY_STATE_COMMON)
            {
                // 数据迁移策略的启用不能从添加时刻启用，而是从后台扫描开始
                if (copyPolicy.getCopyType() == MirrorCommonStatic.POLICY_COPY_TYPE_MIGRATION)
                {
                    MigrationProcessInfo migrationProcessInfo = migrationProcessInfoService.getLastUnDoneMigrationProcess(copyPolicy.getId());
                    if (null == migrationProcessInfo)
                    {
                        LOGGER.info("get migrationProcessInfo by policy id:" + copyPolicy.getId()
                            + " is null");
                        continue;
                    }
                }
                
                lstPolicy.add(copyPolicy);
            }
        }
        return lstPolicy;
    }
    
    public boolean isSystemMirrorEnable()
    {
        LOGGER.info("mirror_global_enable:" + mirrorGlobalEnable);
        return mirrorGlobalEnable;
    }
    
    public boolean isSystemMirrorTimerEnable()
    {
        LOGGER.info("mirror_global_enable:" + mirrorGlobalEnable);
        boolean oldStatus = mirrorGlobalEnableTimer;
        mirrorGlobalEnableTimer= CheckTimeConfigTools.isCurrentTimeInSetting(timeconfigEnable, lstTimeConfig);
        if(oldStatus != mirrorGlobalEnableTimer)
        {
            int isMrrorGlobalTimeEnable = 1;
            if (mirrorGlobalEnableTimer)
            {
                isMrrorGlobalTimeEnable = 0;
            }
            
            ThreadPool.execute(new ManagerDSSCopyTask(null, isMrrorGlobalTimeEnable,
                MirrorCommonStatic.MIRROR_GLOBAL_ENABLE_TIMER));
        }
        
        return mirrorGlobalEnableTimer;
    }
    
    
    public boolean isTimeEnable()
    {
        return timeconfigEnable;
    }
    
    public int getSystemMirrorTaskState()
    {
        return mirrorGlobalTaskState;
    }
    
    @MethodLogAble
    @PostConstruct
    public void init()
    {
        
        initCopyPolicyLocalCache();
        
        initDataCenterLocalCache();
        
        initTimeConfigLocalCache();
        
        mirrorGlobalEnable = mirrorSystemConfigService.isSystemMirrorEnable();
        
        timeconfigEnable = mirrorSystemConfigService.isTimeconfigEnable();
        
        mirrorGlobalTaskState = mirrorSystemConfigService.getSystemMirrorTaskState();
        LOGGER.info("mirror_global_enable init:" + mirrorGlobalEnable);
        
        //初始化时，将ac异地复制控制状态下发到dss，保证dss先于ufm启动时，dss状态正确
        sendMirrorControlStatusToDss();
        
    }
    
    /**
     * 判断是否允许创建任务
     * 
     * @return
     */
    public boolean isAllowCreateTaskByDB()
    {
        if (this.copyTaskNumber < MAX_TASK_NUMBER)
        {
            return true;
        }
        return false;
    }
    
    public void reflushCopyTaskNumber()
    {
        this.copyTaskNumber = userMirrorStatisticInfoService.getCopyTaskTotal();
    }
    
    public void setSystemMirrorEnable(boolean bEnable)
    {
        mirrorGlobalEnable = bEnable;
    }
    
    public void setSystemMirrorTimerEnable(boolean bEnable)
    {
        mirrorGlobalEnableTimer = bEnable;
    }
    
    public void setSystemMirrorTaskState(int state)
    {
        mirrorGlobalTaskState = state;
    }
    
    private synchronized void initCopyPolicyLocalCache()
    {
        try
        {
            LOCK.writeLock().lock();
            lstCopyPolicy.clear();
            loadCopyPolicy();
        }
        finally
        {
            unlock(LOCK.writeLock());
        }
    }
    
    private synchronized void initDataCenterLocalCache()
    {
        try
        {
            LOCK.writeLock().lock();
            lstDataCenter.clear();
            //loadPriorityDataCenter();
        }
        finally
        {
            unlock(LOCK.writeLock());
        }
    }
    
    private synchronized void initTimeConfigLocalCache()
    {
        try
        {
            LOCK.writeLock().lock();
            lstTimeConfig.clear();
            loadTimeConfig();
        }
        finally
        {
            unlock(LOCK.writeLock());
        }
    }
    
    /**
     * 加载数据库
     */
    private void loadCopyPolicy()
    {
        List<CopyPolicy> lstPolicy = copyPolicyService.listCopyPolicy();
        if (null == lstPolicy || lstPolicy.isEmpty())
        {
            return;
        }
        lstCopyPolicy.addAll(lstPolicy);
    }
    
//    /**
//     * 循环引用bug： 加载数据库获取所有下载优先级最高的数据中心
//     */
//    private void loadPriorityDataCenter()
//    {
//        List<DataCenter> lstdc = dcService.listPriorityDataCenter();
//        if (null == lstdc || lstdc.isEmpty())
//        {
//            return;
//        }
//        lstDataCenter.addAll(lstdc);
//    }
    //  循环引用bug修复
    public synchronized void addDataCenters(List<DataCenter> dataCenterLst) {
    	if(CollectionUtils.isNotEmpty(dataCenterLst)) {
    		lstDataCenter.addAll(dataCenterLst);
    	}
    }
    
    /**
     * 加载数据库获取所有异地复制执行时间段
     */
    private void loadTimeConfig()
    {
        List<TimeConfig> lsttc = timeConfigService.getAllTimeConfig();
        if (null == lsttc || lsttc.isEmpty())
        {
            return;
        }
        lstTimeConfig.addAll(lsttc);
    }
    
    public DataCenter getPriortyDCbyRegionid(Integer regionid)
    {
        if(null == regionid)
        {
            LOGGER.info("regionid is null,can not get priorty dataCenter");
            return null;
        }
        
        if(lstDataCenter.isEmpty())
        {
            LOGGER.info("dataCenter priorty is not setting");
            return null;
        }
        
        DataCenter reDc= null;
        for (DataCenter dc : lstDataCenter)
        {
            if(dc.getRegion().getId() == regionid)
            {
                reDc = dc;
                break;
            }
        }
        
        return reDc;
    }
    
    public List<TimeConfig> getAllTimeConfig()
    {
        if(null == lstTimeConfig)
        {
            return null;
        }
        
        return lstTimeConfig;
    }
    
    
    public void sendMirrorControlStatusToDss()
    {
        //下发异地复制开关状态到dss
        int isMrrorGlobalEnable = 1;
        if (mirrorGlobalEnable)
        {
            isMrrorGlobalEnable = 0;
        }
        
        ThreadPool.execute(new ManagerDSSCopyTask(null, isMrrorGlobalEnable,
            MirrorCommonStatic.MIRROR_GLOBAL_ENABLE));
        
        //下发异地暂停开关状态到dss
        ThreadPool.execute(new ManagerDSSCopyTask(null, mirrorGlobalTaskState,
            MirrorCommonStatic.MIRROR_GLOBAL_TASK_STATE));
        
        //下发定时控制到dss
        int isMrrorGlobalTimeEnable = 1;
        if (mirrorGlobalEnableTimer)
        {
            isMrrorGlobalTimeEnable = 0;
        }
        
        ThreadPool.execute(new ManagerDSSCopyTask(null, isMrrorGlobalTimeEnable,
            MirrorCommonStatic.MIRROR_GLOBAL_ENABLE_TIMER));
        
    }
    
    
}
