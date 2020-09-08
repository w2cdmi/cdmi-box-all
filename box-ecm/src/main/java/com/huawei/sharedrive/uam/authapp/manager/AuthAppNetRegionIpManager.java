/*
 * Copyright Notice:
 *      Copyright  1998-2009, Huawei Technologies Co., Ltd.  ALL Rights Reserved.
 *
 *      Warning: This computer software sourcecode is protected by copyright law
 *      and international treaties. Unauthorized reproduction or distribution
 *      of this sourcecode, or any portion of it, may result in severe civil and
 *      criminal penalties, and will be prosecuted to the maximum extent
 *      possible under the law.
 */
package com.huawei.sharedrive.uam.authapp.manager;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.huawei.sharedrive.uam.authapp.domain.AuthAppNetRegionIp;
import com.huawei.sharedrive.uam.authapp.domain.AuthAppNetRegionIpCacheObj;
import com.huawei.sharedrive.uam.authapp.service.AuthAppNetRegionIpService;
import com.huawei.sharedrive.uam.authapp.service.AuthAppService;
import com.huawei.sharedrive.uam.exception.ExistNetworkRegionIpConflictException;
import com.huawei.sharedrive.uam.exception.InvalidParamterException;
import com.huawei.sharedrive.uam.exception.NoSuchAuthAppException;
import com.huawei.sharedrive.uam.exception.NoSuchAuthAppNetRegionConfigException;
import com.huawei.sharedrive.uam.oauth2.domain.UserToken;
import com.huawei.sharedrive.uam.user.service.UserService;

import pw.cdmi.box.http.request.RestRegionInfo;
import pw.cdmi.common.config.service.ConfigListener;
import pw.cdmi.common.config.service.ConfigManager;
import pw.cdmi.core.utils.IpUtils;
import pw.cdmi.core.utils.MethodLogAble;
import pw.cdmi.uam.domain.AuthApp;

@Service("authAppNetRegionIpManager")
public class AuthAppNetRegionIpManager implements ConfigListener
{
    private static final AuthAppNetRegionIpCacheObj DEFAULT_IP_REGION = new AuthAppNetRegionIpCacheObj(0L,
        0L, null);
    
    private static final Logger LOGGER = LoggerFactory.getLogger(AuthAppNetRegionIpManager.class);
    
    private static final int MAX_CACHE_SIZE = 5000;
    
    @Autowired
    private AuthAppManager authAppManager;
    
    @Autowired
    private AuthAppNetRegionIpService authAppNetRegionIpService;
    
    @Autowired
    private AuthAppService authAppService;
    
    @Autowired
    private ConfigManager configManager;
    
    @Autowired
    private UserService userService;
    
    @Override
    public void configChanged(String key, Object value)
    {
        // value is appId
        if (key.equals(AuthAppNetRegionIp.class.getName()))
        {
            if (null == value)
            {
                return;
            }
            
            String authAppId = (String) value;
            LOGGER.info("change for authapp: {}", authAppId);
            
            refreshCacheForApp(authAppId, true);
        }
    }
    
    @MethodLogAble
    public void createAuthAppNetRegionIp(AuthAppNetRegionIp authAppNetRegionIp)
    {
        validateConfig(authAppNetRegionIp, false);
        
        authAppNetRegionIpService.createAuthAppNetRegionIp(authAppNetRegionIp);
        
        refreshCacheForApp(authAppNetRegionIp.getAuthAppId(), true);
        
        configManager.setConfig(AuthAppNetRegionIp.class.getName(), authAppNetRegionIp.getAuthAppId());
    }
    
    public void deleteNetworkRegionConfigs(long[] ids)
    {
        if (null == ids)
        {
            LOGGER.warn("ids is null.");
            return;
        }
        
        for (long id : ids)
        {
            doDeleteById(id);
        }
    }
    
    public Integer getAuthAppNetRegionIp(String appId, String ip)
    {
        if (StringUtils.isBlank(appId) || StringUtils.isBlank(ip))
        {
            LOGGER.warn("appId or ip is null. appId : {}; ip: {}", appId, ip);
            return null;
        }
        
        AuthAppNetRegionIpCacheObj authAppNetRegionIp = getFromCache(appId, ip);
        
        if (null != authAppNetRegionIp)
        {
            return authAppNetRegionIp.getRegionId();
        }
        Integer ipCount;
        ipCount = IpCountCache.getCountFromCach(appId);
        if (null == ipCount || ipCount <= MAX_CACHE_SIZE)
        {
            refreshCacheForApp(appId, true);
        }
        
        AuthAppNetRegionIp ipConfig = authAppNetRegionIpService.getAuthAppNetRegionIpConfig(appId, ip);
        
        if (null != ipConfig)
        {
            return ipConfig.getRegionId();
        }
        
        return null;
    }
    
    @PostConstruct
    public void init()
    {
        List<String> authAppList = authAppManager.getAppId();
        if(CollectionUtils.isNotEmpty(authAppList))
        {
            for (String appId : authAppList)
            {
                refreshCacheForApp(appId, false);
            }
        }
    }
    
    public boolean nearlyStoreSupported(String appId)
    {
        AuthApp authApp = authAppService.getByAuthAppID(appId);
        if (null == authApp)
        {
            LOGGER.warn("no such authApp:  {}.", appId);
            throw new NoSuchAuthAppException("no such authApp: " + appId);
        }
        return authApp.getNetworkRegionStatus() == AuthApp.NETWORK_REGION_CONFIG_OPENED;
    }
    
    public void setLoginMatchNetRegion(String regionIp, String appId, UserToken userToken)
    {
        if (this.nearlyStoreSupported(appId) && StringUtils.isNotBlank(regionIp))
        {
            Integer region = this.getAuthAppNetRegionIp(appId, regionIp);
            
            userToken.setLoginRegion(region);
            LOGGER.info("Intranet Login, region: {}", region);
        }
    }
    
    @MethodLogAble
    public void updateAuthAppNetRegionIp(AuthAppNetRegionIp authAppNetRegionIp)
    {
        if (null == authAppNetRegionIp || null == authAppNetRegionIp.getId())
        {
            String message = "authAppNetRegionIp is null or id is null.";
            LOGGER.warn(message);
            throw new InvalidParamterException(message);
        }
        
        validateConfig(authAppNetRegionIp, true);
        
        authAppNetRegionIpService.updateAuthAppNetRegionIp(authAppNetRegionIp);
        
        refreshCacheForApp(authAppNetRegionIp.getAuthAppId(), true);
        
        configManager.setConfig(AuthAppNetRegionIp.class.getName(), authAppNetRegionIp.getAuthAppId());
    }
    
    private AuthAppNetRegionIp doDeleteById(long configId)
    {
        AuthAppNetRegionIp config = authAppNetRegionIpService.getAuthAppNetRegionIpConfig(configId);
        if (null == config)
        {
            LOGGER.warn("config for id {} not exists.", configId);
            throw new NoSuchAuthAppNetRegionConfigException("config for id {" + configId + "} not exists.");
        }
        
        authAppNetRegionIpService.deleteById(configId);
        
        refreshCacheForApp(config.getAuthAppId(), true);
        
        configManager.setConfig(AuthAppNetRegionIp.class.getName(), config.getAuthAppId());
        
        return config;
    }
    
    private AuthAppNetRegionIpCacheObj getFromCache(String appId, String ip)
    {
        List<AuthAppNetRegionIpCacheObj> ips = null;
        ips = IpListCache.getIpListFromCach(appId);
        if (null == ips)
        {
            return null;
        }
        
        long ipValue = IpUtils.toLong(ip);
        
        for (AuthAppNetRegionIpCacheObj ipConfig : ips)
        {
            if (ipValue >= ipConfig.getIpStartValue() && ipValue <= ipConfig.getIpEndValue())
            {
                return ipConfig;
            }
        }
        
        return DEFAULT_IP_REGION;
    }
    
    private String getRegionName(AuthAppNetRegionIp authAppNetRegionIp)
    {
        List<RestRegionInfo> regions = userService.getRegionInfo(authAppNetRegionIp.getAuthAppId());
        
        if (null == regions || regions.isEmpty())
        {
            LOGGER.warn("cann't get regions list for app {}.", authAppNetRegionIp.getAuthAppId());
            return null;
        }
        
        for (RestRegionInfo region : regions)
        {
            if (region.getRegionId() == authAppNetRegionIp.getRegionId())
            {
                return region.getRegionName();
            }
        }
        
        LOGGER.warn("cann't get regions list for app {}, regionId {}.",
            authAppNetRegionIp.getAuthAppId(),
            authAppNetRegionIp.getRegionId());
        return null;
    }
    
    private void refreshCacheForApp(String authAppId, boolean isValidationAppId)
    {
        List<AuthAppNetRegionIpCacheObj> ips;
        
        IpListCache.removeFromIpListCach(authAppId);
        IpCountCache.removeFromIpCountCach(authAppId);
        if(isValidationAppId)
        {
            AuthApp authApp = authAppManager.getByAuthAppID(authAppId);
            if (null == authApp)
            {
                LOGGER.warn("authApp [ {} ] not exists.", authAppId);
                return;
            }
        }
        List<AuthAppNetRegionIp> newIps = authAppNetRegionIpService.listAllNetworkRegion(authAppId);
        
        if (null == newIps)
        {
            ips = new ArrayList<AuthAppNetRegionIpCacheObj>(0);
            IpListCache.putIntoIpListCach(authAppId, ips);
            IpCountCache.putIntoIpCountCach(authAppId, 0);
            
            LOGGER.info("there is not region config for authApp [ {} ].", authAppId);
            return;
        }
        
        IpCountCache.putIntoIpCountCach(authAppId, newIps.size());
        
        if (newIps.size() > MAX_CACHE_SIZE)
        {
            LOGGER.warn("cann't not put net region ip config for authApp [ {} ] to cache.", authAppId);
            return;
        }
        
        ips = new ArrayList<AuthAppNetRegionIpCacheObj>(newIps.size());
        
        AuthAppNetRegionIpCacheObj cacheObject = null;
        for (AuthAppNetRegionIp ipConfig : newIps)
        {
            cacheObject = new AuthAppNetRegionIpCacheObj(ipConfig.getIpStartValue(), ipConfig.getIpEndValue(),
                ipConfig.getRegionId());
            ips.add(cacheObject);
        }
        
        IpListCache.putIntoIpListCach(authAppId, ips);
    }
    
    private void validateConfig(AuthAppNetRegionIp authAppNetRegionIp, boolean isUpdate)
    {
        if (null == authAppNetRegionIp)
        {
            String message = "authAppNetRegionIp is null";
            LOGGER.warn(message);
            throw new InvalidParamterException(message);
        }
        
        validateIpInfo(authAppNetRegionIp);
        
        if (StringUtils.isBlank(authAppNetRegionIp.getAuthAppId()))
        {
            LOGGER.warn("authAppId is null");
            throw new InvalidParamterException("authAppId is null");
        }
        
        AuthApp authApp = authAppService.getByAuthAppID(authAppNetRegionIp.getAuthAppId());
        if (null == authApp)
        {
            LOGGER.warn("authAppId {} not exists.", authAppNetRegionIp.getAuthAppId());
            throw new InvalidParamterException("authAppId " + authAppNetRegionIp.getAuthAppId()
                + " not exists");
        }
        
        authAppNetRegionIp.setIpStartValue(IpUtils.toLong(authAppNetRegionIp.getIpStart()));
        authAppNetRegionIp.setIpEndValue(IpUtils.toLong(authAppNetRegionIp.getIpEnd()));
        
        if (authAppNetRegionIp.getIpStartValue() > authAppNetRegionIp.getIpEndValue())
        {
            LOGGER.warn("startIp : {}  is max than endIp : {}.",
                authAppNetRegionIp.getIpStart(),
                authAppNetRegionIp.getIpEnd());
            throw new InvalidParamterException("startIp is max than endIp");
        }
        
        AuthAppNetRegionIp start = authAppNetRegionIpService.getAuthAppNetRegionIpConfig(authAppNetRegionIp.getAuthAppId(),
            authAppNetRegionIp.getIpStart());
        if (null != start && !(isUpdate && start.getId().equals(authAppNetRegionIp.getId())))
        {
            LOGGER.warn("startIp is already in config {}.", start);
            throw new ExistNetworkRegionIpConflictException();
        }
        
        AuthAppNetRegionIp end = authAppNetRegionIpService.getAuthAppNetRegionIpConfig(authAppNetRegionIp.getAuthAppId(),
            authAppNetRegionIp.getIpEnd());
        if (null != end && !(isUpdate && end.getId().equals(authAppNetRegionIp.getId())))
        {
            LOGGER.warn("endIp is already in config {}.", end);
            throw new ExistNetworkRegionIpConflictException();
        }
        
        String regionName = getRegionName(authAppNetRegionIp);
        if (StringUtils.isBlank(regionName))
        {
            LOGGER.warn("region is not exists for regionId {}.", authAppNetRegionIp.getRegionId());
            throw new InvalidParamterException("region is not exists.");
        }
        authAppNetRegionIp.setRegionName(regionName);
    }

    private void validateIpInfo(AuthAppNetRegionIp authAppNetRegionIp)
    {
        if (StringUtils.isBlank(authAppNetRegionIp.getIpStart())
            || !IpUtils.isIPv4LiteralAddress(authAppNetRegionIp.getIpStart())
            || StringUtils.isBlank(authAppNetRegionIp.getIpEnd())
            || !IpUtils.isIPv4LiteralAddress(authAppNetRegionIp.getIpEnd()))
        {
            throw new InvalidParamterException("invalid ip address");
        }
    }
}
