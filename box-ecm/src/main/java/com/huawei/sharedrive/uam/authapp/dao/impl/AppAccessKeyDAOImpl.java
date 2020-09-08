package com.huawei.sharedrive.uam.authapp.dao.impl;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.huawei.sharedrive.uam.authapp.dao.AppAccessKeyDAO;

import pw.cdmi.box.dao.impl.AbstractDAOImpl;
import pw.cdmi.common.domain.AppAccessKey;
import pw.cdmi.core.utils.EDToolsEnhance;

@Service
@SuppressWarnings({"unchecked", "deprecation"})
public class AppAccessKeyDAOImpl extends AbstractDAOImpl implements AppAccessKeyDAO
{
    private static Logger logger = LoggerFactory.getLogger(AppAccessKeyDAOImpl.class);
    
    @Override
    public AppAccessKey getById(String id)
    {
        AppAccessKey appAccessKey = (AppAccessKey) sqlMapClientTemplate.queryForObject("AppAccessKey.getById",
            id);
        if (appAccessKey != null)
        {
            appAccessKey.setSecretKey(EDToolsEnhance.decode(appAccessKey.getSecretKey(), appAccessKey.getSecretKeyEncodeKey()));
            appAccessKey.setSecretKeyEncodeKey(null);
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
            app.setSecretKeyEncodeKey(null);
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
        String secretKey = appAccessKey.getSecretKey();
        if (!StringUtils.isBlank(secretKey))
        {
            Map<String, String> map = EDToolsEnhance.encode(secretKey);
            appAccessKey.setSecretKey(map.get(EDToolsEnhance.ENCRYPT_CONTENT));
            appAccessKey.setSecretKeyEncodeKey(map.get(EDToolsEnhance.ENCRYPT_KEY));
            logger.info("set crypt in uam.AppAccessKey");
        }
        sqlMapClientTemplate.insert("AppAccessKey.insert", appAccessKey);
    }
    
    @Override
    public int deleteByAppId(String appId)
    {
        return sqlMapClientTemplate.delete("AppAccessKey.deleteByAppId", appId);
    }
    
}
