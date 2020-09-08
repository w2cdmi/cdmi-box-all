package com.huawei.sharedrive.isystem.plugin.manager;

import java.util.List;
import java.util.Map;

import org.apache.thrift.TException;

import pw.cdmi.common.domain.AppAccessKey;

public interface PluginAccessKeyManager
{
    
    AppAccessKey getById(String id);
    
    List<AppAccessKey> getByAppId(String appId);
    
    List<String> delete(String id) throws TException;
    
    Map<String,List<String>> createAppAccessKeyForApp(String appId) throws TException;
}
