package com.huawei.sharedrive.app.mirror.datamigration.dao.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.huawei.sharedrive.app.mirror.datamigration.dao.UserDataMigrationTaskDAO;
import com.huawei.sharedrive.app.mirror.datamigration.domain.UserDataMigrationTask;

import pw.cdmi.box.dao.impl.CacheableSqlMapClientDAO;
import pw.cdmi.box.domain.Limit;

@Component
public class UserDataMigrationTaskDAOImpl extends CacheableSqlMapClientDAO implements
    UserDataMigrationTaskDAO
{
    
    private static final Logger LOGGER = LoggerFactory.getLogger(UserDataMigrationTaskDAOImpl.class);
    
    @SuppressWarnings("deprecation")
    @Override
    public void insert(UserDataMigrationTask task)
    {
        sqlMapClientTemplate.insert("UserDataMigrationTask.insert", task);
        
        /**
         *  缓存处理，删除有之前查询引起的在CACHE_KEY_PREFIX_NOT_EXISTING_ID而不存在的值
         *  并添加新值
         */
        if (isCacheSupported())
        {
            //删除缓存
            deleteCache(UserDataMigrationTask.CACHE_KEY_PREFIX_NOT_EXISTING_ID + task.getCloudUserId());
            
            String key = UserDataMigrationTask.CACHE_KEY_PREFIX_ID + task.getCloudUserId();
            setCache(key,task);
        }
        
    }
    
    @SuppressWarnings("deprecation")
    @Override
    public int updateStatus(UserDataMigrationTask task)
    {
        if (isCacheSupported())
        {
            //删除缓存
            deleteCache(UserDataMigrationTask.CACHE_KEY_PREFIX_ID + task.getCloudUserId());
        }
        
        return sqlMapClientTemplate.update("UserDataMigrationTask.updateStatus", task);
    }
    
    @SuppressWarnings("deprecation")
    @Override
    public int delete(UserDataMigrationTask task)
    {
        if (isCacheSupported())
        {
            //删除缓存
            deleteCache(UserDataMigrationTask.CACHE_KEY_PREFIX_ID + task.getCloudUserId());
            deleteCache(UserDataMigrationTask.CACHE_KEY_PREFIX_NOT_EXISTING_ID + task.getCloudUserId());
        }
        
        return sqlMapClientTemplate.delete("UserDataMigrationTask.delete", task);
    }
    
    private  void setCache(String key,Object value)
    {
        try
        {
            getCacheClient().setCache(key, value);
        }
        catch(Exception e)
        {
            LOGGER.info(e.getMessage(),e);
        }
        
    }
    
    
    private  Object getCache(String key)
    {
        try
        {
            return getCacheClient().getCache(key);
        }
        catch(Exception e)
        {
            LOGGER.info(e.getMessage(),e);
            return null;
        }
        
    }
    
    
    private  void deleteCache(String key)
    {
        try
        {
            getCacheClient().deleteCache(key);
        }
        catch(Exception e)
        {
            LOGGER.info(e.getMessage(),e);
        }
        
    }
    
    
    
    
    @SuppressWarnings("deprecation")
    @Override
    public UserDataMigrationTask getByUserId(long userId)
    {
        //获取缓存
        if (isCacheSupported())
        {
            String key = UserDataMigrationTask.CACHE_KEY_PREFIX_ID + userId;
            UserDataMigrationTask task = (UserDataMigrationTask) getCache(key);
            if (null != task)
            {
                return task;
            }
            
            String notKey = UserDataMigrationTask.CACHE_KEY_PREFIX_NOT_EXISTING_ID +userId;
            String value = (String) getCache(notKey);
            
            //如果不为空，也等于CACHED_NOT_EXISTING值
            if( StringUtils.isNotBlank(value) && UserDataMigrationTask.CACHED_NOT_EXISTING.equalsIgnoreCase(value))
            {
                return null;
            }
                
            task = (UserDataMigrationTask) sqlMapClientTemplate.queryForObject("UserDataMigrationTask.getByUserId",
                Long.valueOf(userId));
            
            if (task == null)
            {
                //数据库查询，如果没有Key，也写入
                LOGGER.info("get Migration task from db is null,write it to not exist cache");
                setCache(notKey,UserDataMigrationTask.CACHED_NOT_EXISTING);
                return null;
            }
            
            setCache(key,task);
            return task;
        }
        
        return (UserDataMigrationTask) sqlMapClientTemplate.queryForObject("UserDataMigrationTask.getByUserId",
            Long.valueOf(userId));
    }
    
    @SuppressWarnings("deprecation")
    @Override
    public UserDataMigrationTask getOneTaskToExe(String exeAgent)
    {
        
        UserDataMigrationTask task = (UserDataMigrationTask) sqlMapClientTemplate.queryForObject("UserDataMigrationTask.getOneTask");
        if (null == task)
        {
            return null;
        }
        
        if (isCacheSupported())
        {
            deleteCache(UserDataMigrationTask.CACHE_KEY_PREFIX_ID + task.getCloudUserId());
            
        }
        
        task.setExeAgent(exeAgent);
        task.setStatus(UserDataMigrationTask.EXECUTE_SCAN_STATUS);
        sqlMapClientTemplate.update("UserDataMigrationTask.updateStatus", task);
        return task;
    }
    
    @SuppressWarnings("deprecation")
    @Override
    public int update(UserDataMigrationTask task)
    {
        if (isCacheSupported())
        {
            deleteCache(UserDataMigrationTask.CACHE_KEY_PREFIX_ID + task.getCloudUserId());
            deleteCache(UserDataMigrationTask.CACHE_KEY_PREFIX_NOT_EXISTING_ID + task.getCloudUserId());
        }
        
        return sqlMapClientTemplate.update("UserDataMigrationTask.update", task);
    }
    
    @SuppressWarnings("deprecation")
    @Override
    public int getNotCompletedTaskTotal()
    {
        
        return  (Integer) sqlMapClientTemplate.queryForObject("UserDataMigrationTask.getNotCompletedTaskTotal");

    }
    
    @SuppressWarnings("deprecation")
    @Override
    public UserDataMigrationTask getOneTaskByStatus(int status)
    {
        return (UserDataMigrationTask) sqlMapClientTemplate.queryForObject("UserDataMigrationTask.getOneTaskByStatus",
            Integer.valueOf(status));
    }
    
    @Override
    public UserDataMigrationTask updateProgressForEveryFile(long userId, long filesSize)
    {
        return updateProgress(userId, 1, filesSize);
    }
    
    @SuppressWarnings("deprecation")
    @Override
    public UserDataMigrationTask updateProgress(long userId, long filesTotal, long filesSize)
    {
        UserDataMigrationTask task = new UserDataMigrationTask();
        task.setCurSizes(filesSize);
        task.setCloudUserId(userId);
        task.setCurFiles(1);
        task.setModifiedAt(new Date());
        sqlMapClientTemplate.update("UserDataMigrationTask.updateProgress", task);
        
        task =  (UserDataMigrationTask) sqlMapClientTemplate.queryForObject("UserDataMigrationTask.getByUserId",
            Long.valueOf(userId));
        
        if (isCacheSupported())
        {
            setCache(UserDataMigrationTask.CACHE_KEY_PREFIX_ID + task.getCloudUserId(),task);
        }
        
        return task;
    }
    
    @SuppressWarnings({"deprecation", "unchecked"})
    @Override
    public List<UserDataMigrationTask> listTask(Limit limit)
    {
        
        Map<String, Object> map = new HashMap<String, Object>(1);
        map.put("limit", limit);
        
        return sqlMapClientTemplate.queryForList("UserDataMigrationTask.list", map);
        
    }
    
}
