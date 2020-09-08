package com.huawei.sharedrive.app.mirror.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.huawei.sharedrive.app.dataserver.domain.RegionNetworkDistance;
import com.huawei.sharedrive.app.dataserver.service.DCManager;
import com.huawei.sharedrive.app.dataserver.service.RNetDistanceService;
import com.huawei.sharedrive.app.mirror.service.MirrorSystemConfigService;

import pw.cdmi.core.utils.MethodLogAble;

/**
 * 就近访问管理器
 * 
 * @author c00287749
 * 
 */
@Service("nearAccessManager")
public class NearAccessManager
{
    @Autowired
    private MirrorSystemConfigService mirrorSystemConfigService;
    
    @Autowired
    private RNetDistanceService rNetDistanceService;
    
    @Autowired
    private DCManager dcManager;
    
    private static Map<String, Boolean> appNearAccessEnables = new HashMap<String, Boolean>(10);
    
    private static List<RegionNetworkDistance> netDistances = new ArrayList<RegionNetworkDistance>(10);
    
    private static final ReadWriteLock LOCK = new ReentrantReadWriteLock(true);
    
    private static final Logger LOGGER = LoggerFactory.getLogger(NearAccessManager.class);
    
    private static boolean systemNearAccessEnable = false;
    
    public static boolean isSystemNearAccessEnable()
    {
        try
        {
            LOCK.readLock().lock();
            return systemNearAccessEnable;
        }
        finally
        {
            unlock(LOCK.readLock());
        }
    }
    
    private static void setSystemNearAccessEnable(boolean enable)
    {
        NearAccessManager.systemNearAccessEnable = enable;
    }
    
    /**
     * 释放锁，屏蔽异常
     * 
     * @param lock
     */
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
    
    /**
     * 获取是否开启就近上传
     * 
     * @param appId
     * @return
     */
    public boolean getNearAccessEnable(String appId)
    {
        try
        {
            LOCK.readLock().lock();
            Boolean enable = appNearAccessEnables.get(appId);
            if (null == enable)
            {
                return false;
            }
            return enable.booleanValue();
        }
        finally
        {
            unlock(LOCK.readLock());
        }
    }
    
    /**
     * 获取一个最近的资源组
     * 
     * @param accessRegion
     * @return
     */
    public Integer getNearResourceGroup(Integer accessRegionId, List<Integer> lstSrcResourceGroup)
    {
        Integer shortDistanceGroupId = null;
        int tmpshortDistance = Integer.MAX_VALUE;
        if (null == lstSrcResourceGroup || lstSrcResourceGroup.isEmpty())
        {
            return null;
        }
        
        if(null == accessRegionId ||netDistances.isEmpty())
        {
            //如果没有访问区域返回一个存活列表或者无配置
            LOGGER.info("netDistances.isEmpty(),"+lstSrcResourceGroup.get(0));
            return lstSrcResourceGroup.get(0);
        }
    
        int srcRegionid = -1;
        for (Integer srcRegionGroupId : lstSrcResourceGroup)
        {
            srcRegionid = dcManager.getResourceGroup(srcRegionGroupId).getRegionId();
            
            // 遍历资源组
            for (RegionNetworkDistance netd : netDistances)
            {
                shortDistanceGroupId = getShortGroupId(accessRegionId,
                    shortDistanceGroupId,
                    tmpshortDistance,
                    srcRegionid,
                    srcRegionGroupId,
                    netd);
            }
                
            
        }
        
        return shortDistanceGroupId;
    }

    @SuppressWarnings("PMD.ExcessiveParameterList")
    private Integer getShortGroupId(Integer accessRegionId, Integer shortDistanceGroupId,
        int tmpshortDistance, int srcRegionid, Integer srcRegionGroupId, RegionNetworkDistance netd)
    {
        if ((netd.getSrcRegionId() == srcRegionid && netd.getSrcResourceGroupId() == srcRegionGroupId && netd.getDestRegionId() == accessRegionId)
            || (netd.getSrcRegionId() == accessRegionId && netd.getDestRegionId() == srcRegionid && netd.getDestResourceGroupId() == srcRegionGroupId))
        {
            if (netd.getValue() < tmpshortDistance)
            {
                shortDistanceGroupId = srcRegionGroupId;
            }
        }
        return shortDistanceGroupId;
    }
    
    @MethodLogAble
    @PostConstruct
    public void init()
    {
        loadNearAccessConfig();
        loadRegionNetDistance();
    }
    
    public synchronized void loadNearAccessConfig()
    {
        try
        {
            LOCK.writeLock().lock();
            
            appNearAccessEnables.clear();
            
            Map<String, Boolean> map = mirrorSystemConfigService.lstNearAccessEnable();
            
            if (null != map && !map.isEmpty())
            {
                for (Map.Entry<String, Boolean> entry : map.entrySet())
                {
                    LOGGER.info("The app near access flag,appId:" + entry.getKey() + ",value:"
                        + entry.getValue());
                    appNearAccessEnables.put(entry.getKey(), entry.getValue());
                }
                
            }
            
            setSystemNearAccessEnable(mirrorSystemConfigService.isSystemNearAccessEnable());
            LOGGER.info("The system near access enable flag:" + systemNearAccessEnable);
        }
        finally
        {
            unlock(LOCK.writeLock());
        }
        
    }
    
    public synchronized void loadRegionNetDistance()
    {
        try
        {
            LOCK.writeLock().lock();
            List<RegionNetworkDistance> lstTmp = rNetDistanceService.lstRegionNetworkDistance();
            if (null == lstTmp || lstTmp.isEmpty())
            {
                return;
            }
            
            netDistances.clear();
            netDistances.addAll(lstTmp);
            
        }
        finally
        {
            unlock(LOCK.writeLock());
        }
    }
    
  
}
