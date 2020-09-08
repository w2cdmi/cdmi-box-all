package com.huawei.sharedrive.isystem.plugin.manager.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.thrift.TException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.huawei.sharedrive.isystem.plugin.manager.PluginAccessKeyManager;
import com.huawei.sharedrive.isystem.plugin.service.PluginAccessKeyService;
import com.huawei.sharedrive.isystem.plugin.service.PluginService;
import com.huawei.sharedrive.thrift.pluginserver.TAccessKey;
import com.huawei.sharedrive.thrift.pluginserver.TPluginServerCluster;

import pw.cdmi.common.domain.AppAccessKey;

@Service
public class PluginAccessKeyManagerImpl implements PluginAccessKeyManager
{
    public static final Logger LOGGER = LoggerFactory.getLogger(PluginAccessKeyManagerImpl.class);
    
    @Autowired
    private PluginAccessKeyService pluginAccessKeyService;
    
    @Autowired
    private PluginService pluginService;
    
    @Override
    public AppAccessKey getById(String id)
    {
        // TODO Auto-generated method stub
        return pluginAccessKeyService.getById(id);
    }
    
    @Override
    public List<AppAccessKey> getByAppId(String appId)
    {
        // TODO Auto-generated method stub
        return pluginAccessKeyService.getByAppId(appId);
    }
    
    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public List<String> delete(String id) throws TException
    {
        // TODO Auto-generated method stub
        AppAccessKey accessKey = pluginAccessKeyService.getById(id);
        if (accessKey == null)
        {
            return null;
        }
        String appId = accessKey.getAppId();
        pluginAccessKeyService.delete(id);
        List<AppAccessKey> list = getByAppId(appId);
        if (null != list && !list.isEmpty())
        {
            return setAccessKey(list.get(list.size() - 1), appId);
        }
        else
        {
            return null;
        }
        
    }
    
    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Map<String, List<String>> createAppAccessKeyForApp(String appId) throws TException
    {
        AppAccessKey accessKey = pluginAccessKeyService.createAppAccessKeyForApp(appId);
        Map<String, List<String>> map = null;
        
        if (null != accessKey)
        {
            map = new HashMap<String, List<String>>(2);
            List<String> key = new ArrayList<String>(1);
            key.add(accessKey.getId());
            map.put("key", key);
            map.put("message", setAccessKey(accessKey, appId));
        }
        return map;
    }
    
    private List<String> setAccessKey(AppAccessKey accessKey, String appId) throws TException
    {
        TPluginServerCluster pluginServerCluster = new TPluginServerCluster();
        pluginServerCluster.setAppId(appId);
        
        List<TPluginServerCluster> clusters = pluginService.listPluginServerCluster(pluginServerCluster);
        TAccessKey tAccessKey = new TAccessKey(accessKey.getId(), accessKey.getSecretKey());
        StringBuffer success = new StringBuffer();
        StringBuffer fail = new StringBuffer();
        for (TPluginServerCluster cluster : clusters)
        {
            setAccessKeyForOne(tAccessKey, success, fail, cluster);
        }
        List<String> list = new ArrayList<String>(2);
        list.add(success.toString());
        list.add(fail.toString());
        return list;
    }
    
    private void setAccessKeyForOne(TAccessKey tAccessKey, StringBuffer success, StringBuffer fail,
        TPluginServerCluster cluster)
    {
        try
        {
            pluginService.setAccessKey(tAccessKey, cluster);
            success.append(cluster.getName());
            LOGGER.info("Success cluster" + cluster.getName());
        }
        catch (TException e)
        {
            LOGGER.info(cluster.getName(), e);
            fail.append(cluster.getName());
        }
    }
    
}
