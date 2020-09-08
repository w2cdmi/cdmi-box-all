package com.huawei.sharedrive.isystem.authapp.dao.impl;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.huawei.sharedrive.isystem.authapp.dao.AppAccessKeyDAO;

import pw.cdmi.box.dao.impl.AbstractDAOImpl;
import pw.cdmi.common.domain.AppAccessKey;
import pw.cdmi.core.utils.EDToolsEnhance;

@Service
@SuppressWarnings({"unchecked", "deprecation"})
public class AppAccessKeyDAOImpl extends AbstractDAOImpl implements AppAccessKeyDAO
{
    private static final Logger LOGGER = LoggerFactory.getLogger(AppAccessKeyDAOImpl.class);
    
    @Override
    public AppAccessKey getById(String id)
    {
        AppAccessKey appAccessKey = (AppAccessKey) sqlMapClientTemplate.queryForObject("AppAccessKey.getById",
            id);
        if (appAccessKey != null)
        {
            appAccessKey.setSecretKey(EDToolsEnhance.decode(appAccessKey.getSecretKey(), appAccessKey.getSecretKeyEncodeKey()));
        }
        return appAccessKey;
    }
    
    @Override
    public List<AppAccessKey> getByAppId(String appId)
    {
        List<AppAccessKey> accessKeyLists = sqlMapClientTemplate.queryForList("AppAccessKey.getByAppId",
            appId);
        for (AppAccessKey app : accessKeyLists)
        {
            app.setSecretKey(EDToolsEnhance.decode(app.getSecretKey(), app.getSecretKeyEncodeKey()));
        }
        return accessKeyLists;
    }
    
    @Override
    public int delete(String id)
    {
        return sqlMapClientTemplate.delete("AppAccessKey.delete", id);
    }
    
    @Override
    public void create(AppAccessKey appAccessKey)
    {
        appAccessKey.setCreatedAt(new Date());
        Map<String, String> encodedKeys = EDToolsEnhance.encode(appAccessKey.getSecretKey());
        appAccessKey.setSecretKey(encodedKeys.get(EDToolsEnhance.ENCRYPT_CONTENT));
        appAccessKey.setSecretKeyEncodeKey(encodedKeys.get(EDToolsEnhance.ENCRYPT_KEY));
        LOGGER.info("set crypt in isystem.AppAccessKey");
        sqlMapClientTemplate.insert("AppAccessKey.insert", appAccessKey);
    }
    
    @Override
    public int deleteByAppId(String appId)
    {
        return sqlMapClientTemplate.delete("AppAccessKey.deleteByAppId", appId);
    }
    
    @Override
    public void updateFirstScan(AppAccessKey appAccess)
    {
        appAccess.setFirstScan(1);
        sqlMapClientTemplate.update("AppAccessKey.updateFirstScan", appAccess);
    }
    
}
