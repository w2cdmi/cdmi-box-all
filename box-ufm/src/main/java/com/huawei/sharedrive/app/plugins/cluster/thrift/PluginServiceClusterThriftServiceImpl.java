package com.huawei.sharedrive.app.plugins.cluster.thrift;

import org.apache.thrift.TException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.huawei.sharedrive.app.authapp.service.AppAccessKeyService;
import com.huawei.sharedrive.thrift.plugins.cluster.PluginServiceClusterThriftService.Iface;

import pw.cdmi.common.domain.AppAccessKey;

import com.huawei.sharedrive.thrift.plugins.cluster.TAccessKey;

public class PluginServiceClusterThriftServiceImpl implements Iface
{
    private static final Logger LOGGER = LoggerFactory.getLogger(PluginServiceClusterThriftServiceImpl.class);
    
    @Autowired
    private AppAccessKeyService appAccessKeyService;

    @Override
    public TAccessKey getAccessKey(String appId) throws TException
    {
        LOGGER.info("Get accesskey for app {}", appId);
        AppAccessKey accessKey = appAccessKeyService.getByAppId(appId);
        if(accessKey == null)
        {
            return null;
        }
        return new TAccessKey(accessKey.getId(), accessKey.getSecretKey());
    }
    
}
