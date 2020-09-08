package com.huawei.sharedrive.app.filelabel.dao.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import com.huawei.sharedrive.app.filelabel.domain.BaseFileLabelInfo;
import com.huawei.sharedrive.app.filelabel.domain.FileLabel;
import com.huawei.sharedrive.app.filelabel.domain.LatestViewFileLabel;
import com.huawei.sharedrive.app.filelabel.util.FileLabelContants;

import pw.cdmi.common.cache.CacheClient;
import pw.cdmi.core.utils.JsonUtils;

/**
 * 
 * Desc  : 文件标签的缓存操作
 * Author: 77235
 * Date	 : 2016年12月2日
 */
@Repository("fileLabelCacheDao")
public class FileLabelCacheDao {
    private static final Logger LOGGER = LoggerFactory.getLogger(FileLabelCacheDao.class);
    
    private static final long DEFAULT_LOCK_CACCHE_TIME = 30;
    
    @Resource
    private CacheClient cacheClient;
    
    /**
     * 新增企业缓存标签
     * @param enterpriseId
     * @param baseFileLabelInfo
     */
    @SuppressWarnings("unchecked")
    public void addCacheFilelabelForEnterprise(long enterpriseId, BaseFileLabelInfo baseFileLabelInfo){
        String lockKey = FileLabelContants.CONST_LOCK_PREFIX_FLAG + enterpriseId;
        String cacheKey = FileLabelContants.CONST_CACHE_PREFIX_FLAG + enterpriseId;
        boolean isObtainedLock = false;
        
        try { 
            if(tryLock(lockKey, FileLabelContants.CONST_DEFAULT_SLEEP_TIME, TimeUnit.MILLISECONDS, 
                    FileLabelContants.CONST_DEFAULT_CYCLE_TIMES)){
                isObtainedLock = true;
                List<Long> enterpriseLabels = null;
                Object cacheObj = cacheClient.getCache(cacheKey);
                
                if (cacheObj != null){
                    enterpriseLabels = (List<Long>) JsonUtils.stringToList(cacheObj.toString(), List.class, long.class);
                }
                if (enterpriseLabels == null){
                    enterpriseLabels = new ArrayList<Long>();
                }
                
                enterpriseLabels.add(baseFileLabelInfo.getId());
                cacheClient.replaceCacheNoExpire(cacheKey, JsonUtils.toJson(enterpriseLabels));
            }
        } catch(Exception ex){
            LOGGER.error("[FileLabelCacheDao] error:", ex);
        }finally{
            if (isObtainedLock){
                unlock(lockKey); 
            }
        }
    }
    
    /**
     * 初始化用户最近使用的标签信息
     * @param enterpriseId
     * @param userId
     * @return
     */
    public void initUserLatestViewedFilelabel(long enterpriseId, long userId, List<LatestViewFileLabel> viewedLabels) {
        String latestCacheKey = FileLabelContants.CONST_CACHE_PREFIX_FLAG + enterpriseId + ":latestview:"  + userId;
        Object cacheObj = cacheClient.getCache(latestCacheKey);
      
        if (cacheObj == null && viewedLabels != null) {
            cacheClient.addCacheNoExpire(latestCacheKey, JsonUtils.toJson(viewedLabels));
        }
        
    }
    
    /**
     * 检索用户最近5次访问的标签
     * @param enterpriseId
     * @param userId
     * @return
     */
    @SuppressWarnings("unchecked")
    public List<LatestViewFileLabel> queryFilelabelForUserLatestViewed(long enterpriseId, long userId) {
        String latestCacheKey = FileLabelContants.CONST_CACHE_PREFIX_FLAG + enterpriseId + ":latestview:"  + userId;
        Object cacheObj = cacheClient.getCache(latestCacheKey);
        List<LatestViewFileLabel> cacheList = null;
        
        if (cacheObj != null) {
            cacheList = (List<LatestViewFileLabel>) JsonUtils.stringToList(cacheObj.toString(), List.class,
                LatestViewFileLabel.class);
        }
        
        return cacheList;
    }

    /**
     * 更新用户最近访问标签
     * @param enterpriseId
     * @param userId
     * @param latestViewFileLabel
     */
    @SuppressWarnings("unchecked")
    public void updateFilelabelForUserLatestViewed(long enterpriseId, long userId,
        LatestViewFileLabel latestViewFileLabel) {
        String latestCacheKey = FileLabelContants.CONST_CACHE_PREFIX_FLAG + enterpriseId + ":latestview:" + userId;
        
        try {
            List<LatestViewFileLabel> cacheList;
            Object cacheObj = cacheClient.getCache(latestCacheKey);
            
            if (cacheObj == null) {
                cacheList = new ArrayList<LatestViewFileLabel>(5);
            } else {
                cacheList = (List<LatestViewFileLabel>) JsonUtils.stringToList(cacheObj.toString(),
                    List.class, LatestViewFileLabel.class);
            }
            
            if (!cacheList.isEmpty()) {
                Iterator<LatestViewFileLabel> it = cacheList.iterator();
                
                LatestViewFileLabel tempFilelabel;
                while (it.hasNext()) {
                    tempFilelabel = it.next();
                    
                    if (StringUtils.equalsIgnoreCase(tempFilelabel.getLabelName(),
                        latestViewFileLabel.getLabelName())) {
                        it.remove();
                    }
                }
            }
            
            cacheList.add(latestViewFileLabel);
            cacheList = cacheList.subList(cacheList.size() > FileLabelContants.CONST_BIZ_MAX_CACHE_ITEM_FOR_USER
                    ? cacheList.size() - FileLabelContants.CONST_BIZ_MAX_CACHE_ITEM_FOR_USER : 0, cacheList.size());
            
            if (cacheClient.checkCacheExists(latestCacheKey)) {
                cacheClient.replaceCacheNoExpire(latestCacheKey, JsonUtils.toJson(cacheList));
            } else {
                cacheClient.addCacheNoExpire(latestCacheKey, JsonUtils.toJson(cacheList));
            }
        } catch (Exception ex) {
            // ignore
            LOGGER.error("[FileLabelCacheDao] updateFilelabelForUserLatestViewed error:", ex);
        }
    }

    /**
     * 新增标签
     * @param enterpriseId
     * @param fileLabel
     */
    public void addCacheFilelabel(FileLabel fileLabel, boolean isBind){
        String flCacheKey = FileLabelContants.CONST_CACHE_PREFIX_FLAG + fileLabel.getEnterpriseId() + 
                FileLabelContants.CONST_KEY_DELIMETER_FLAG + fileLabel.getId();
        try {
            Object cacheObj = cacheClient.getCache(flCacheKey);
            if (cacheObj == null){
                cacheClient.addCacheNoExpire(flCacheKey, JsonUtils.toJson(fileLabel));
            } 
            
            if(isBind) {
                updateFilelabelBindtimes(fileLabel.getEnterpriseId(), fileLabel.getId(), 1);
            }
        } catch(Exception ex){
            LOGGER.error("[FileLabelCacheDao] error:", ex);
        }
    }
    
    /**
     * 获取标签
     * @param enterpriseId
     * @param fileLabel
     */
    public FileLabel getCacheFilelabel(long enterpriseId, long labelId){
        String cacheKey = FileLabelContants.CONST_CACHE_PREFIX_FLAG + enterpriseId + 
                FileLabelContants.CONST_KEY_DELIMETER_FLAG + labelId;
        FileLabel retLabel = null;
        
        try {
            Object cacheObj = cacheClient.getCache(cacheKey);
            if(cacheObj != null){
                return JsonUtils.stringToObject(cacheObj.toString(), FileLabel.class);
            }
        } catch(Exception ex){
            LOGGER.error("[FileLabelCacheDao] error:", ex);
        }
        
        return retLabel;
    }
    
    /**
     * 更新标签绑定次数
     * @param enterpriseId
     * @param fileLabelId
     * @param addTimes
     */
    public void updateFilelabelBindtimes(long enterpriseId, long fileLabelId, int addTimes){
        String lockKey = FileLabelContants.CONST_LOCK_PREFIX_FLAG + enterpriseId + FileLabelContants.CONST_KEY_DELIMETER_FLAG + fileLabelId;
        String cacheKey = FileLabelContants.CONST_CACHE_PREFIX_FLAG + enterpriseId + FileLabelContants.CONST_KEY_DELIMETER_FLAG + fileLabelId;
        boolean isObtainedLock = false;
        
        try {
            if(tryLock(lockKey, FileLabelContants.CONST_DEFAULT_SLEEP_TIME, TimeUnit.MILLISECONDS, 1)){
                isObtainedLock = true;
                Object cacheObj = cacheClient.getCache(cacheKey);
                FileLabel cacheFileLabel = null;
                
                if (null != cacheObj){
                    cacheFileLabel = (FileLabel) JsonUtils.stringToObject(cacheObj.toString(), FileLabel.class);
                }
                
                if(cacheFileLabel != null){
                    cacheFileLabel.setBindedTimes(cacheFileLabel.getBindedTimes() + addTimes);
                    cacheClient.replaceCacheNoExpire(cacheKey, JsonUtils.toJson(cacheFileLabel));
                }
            }
        }catch(Exception ex){
            LOGGER.error("[FileLabelCacheDao] updateFilelabelBindtimes error:", ex);
        } finally{
            if (isObtainedLock){
                unlock(lockKey); 
            }
        }
    }
    
    /**
     * 尝试获取锁
     * @param lockKey
     * @param time
     * @param unit
     * @return
     */
    public boolean tryLock(String lockKey, long time, TimeUnit unit, int cycleTimes){
        boolean retFlag = false;
        int localCycleTimes = cycleTimes;

        while (localCycleTimes-- > 0) {
            boolean hasLocked = cacheClient.addCache(lockKey, FileLabelContants.CONST_DEFAULT_LOCKED_VALUE, 
                    DEFAULT_LOCK_CACCHE_TIME);
           
            if (hasLocked) {
                retFlag = true;
                break;
            }
            
            try {
                unit.sleep(time);
            } catch (InterruptedException e) {
            	 LOGGER.error("[FileLabelCacheDao] tryLock error:" + e.getMessage(), e);
            }
        }
        
        return retFlag;
    }
    
    /**
     * 释放锁
     * @param lockKey
     */
    public void unlock(String lockKey){
        cacheClient.deleteCache(lockKey);
    }
}
