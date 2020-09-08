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
package pw.cdmi.box.uam.authapp.manager;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pw.cdmi.box.uam.authapp.domain.AuthAppNetRegionIp;
import pw.cdmi.box.uam.authapp.domain.AuthAppNetRegionIpCacheObj;
import pw.cdmi.box.uam.authapp.service.AuthAppNetRegionIpService;
import pw.cdmi.box.uam.authapp.service.AuthAppService;
import pw.cdmi.box.uam.exception.ExistNetworkRegionIpConflictException;
import pw.cdmi.box.uam.exception.InvalidParamterException;
import pw.cdmi.box.uam.exception.NoSuchAuthAppException;
import pw.cdmi.box.uam.exception.NoSuchAuthAppNetRegionConfigException;
import pw.cdmi.box.uam.httpclient.domain.RestRegionInfo;
import pw.cdmi.box.uam.log.domain.OperateDescription;
import pw.cdmi.box.uam.log.domain.OperateType;
import pw.cdmi.box.uam.log.manager.SystemLogManager;
import pw.cdmi.box.uam.user.service.UserService;
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
    
    @Autowired
    private SystemLogManager systemLogManager;
    
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
            
            refreshCacheForApp(authAppId);
        }
    }
    
    @MethodLogAble
    public void createAuthAppNetRegionIp(AuthAppNetRegionIp authAppNetRegionIp)
    {
        validateConfig(authAppNetRegionIp, false);
        
        authAppNetRegionIpService.createAuthAppNetRegionIp(authAppNetRegionIp);
        
        refreshCacheForApp(authAppNetRegionIp.getAuthAppId());
        
        configManager.setConfig(AuthAppNetRegionIp.class.getName(), authAppNetRegionIp.getAuthAppId());
    }
    
    public void deleteByAuthAppId(String authAppId)
    {
        // 这个地方没有办法删除缓存，则通过zookeeper通知集群，刷新NET_CONFIG_COUNT本地缓存，从而保证不会读到脏数据
        authAppNetRegionIpService.deleteByAuthAppId(authAppId);
        
        refreshCacheForApp(authAppId);
        
        configManager.setConfig(AuthAppNetRegionIp.class.getName(), authAppId);
    }
    
    public void deleteById(long configId)
    {
        AuthAppNetRegionIp config = doDeleteById(configId);
        configManager.setConfig(AuthAppNetRegionIp.class.getName(), config.getAuthAppId());
    }
    
    public void deleteNetworkRegionConfigs(HttpServletRequest req, long[] ids)
    {
        if (null == ids)
        {
            LOGGER.warn("ids is null.");
            return;
        }
        AuthAppNetRegionIp regionIpConfig;
        String[] description;
        String flag;
        for (long id : ids)
        {
            regionIpConfig = authAppNetRegionIpService.getAuthAppNetRegionIpConfig(id);
            if (regionIpConfig == null)
            {
                LOGGER.warn("regionIpConfig is null ;" + id);
                continue;
            }
            description = new String[]{regionIpConfig.getRegionName(), regionIpConfig.getAuthAppId(),
                regionIpConfig.getIpStart(), regionIpConfig.getIpEnd()};
 
            flag = systemLogManager.saveFailLog(req,
                OperateType.NetworkRegionIp,
                OperateDescription.NETWORK_REGION_IP_DELETE,
                null,
                description);
            doDeleteById(id);
            systemLogManager.updateSuccess(flag);
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
            refreshCacheForApp(appId);
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
        List<AuthApp> authAppList = authAppManager.getAuthAppList(null, null, null);
        if (null == authAppList)
        {
            return;
        }
        
        for (AuthApp authApp : authAppList)
        {
            refreshCacheForApp(authApp.getAuthAppId());
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
    
    @MethodLogAble
    public void updateAuthAppNetRegionIp(AuthAppNetRegionIp authAppNetRegionIp)
    {
        if (null == authAppNetRegionIp || null == authAppNetRegionIp.getId())
        {
            String message = "authAppNetRegionIp is null or id is null.";
            LOGGER.warn(message);
            throw new InvalidParamterException(message);
        }
        
        // 校验
        validateConfig(authAppNetRegionIp, true);
        
        authAppNetRegionIpService.updateAuthAppNetRegionIp(authAppNetRegionIp);
        
        refreshCacheForApp(authAppNetRegionIp.getAuthAppId());
        
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
        
        refreshCacheForApp(config.getAuthAppId());
        
        configManager.setConfig(AuthAppNetRegionIp.class.getName(), config.getAuthAppId());
        
        return config;
    }
    
    private AuthAppNetRegionIpCacheObj getFromCache(String appId, String ip)
    {
        List<AuthAppNetRegionIpCacheObj> ips = IpListCache.getIpListFromCach(appId);
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
        
        // 返回默认值，说明该应用配置过就近上传IP，但是在配置的内容中找不到对应的值，避免重复去数据查询
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
            if (region.getId() == authAppNetRegionIp.getRegionId())
            {
                return region.getName();
            }
        }
        
        LOGGER.warn("cann't get regions list for app {}, regionId {}.",
            authAppNetRegionIp.getAuthAppId(),
            authAppNetRegionIp.getRegionId());
        return null;
    }
    
    private void refreshCacheForApp(String authAppId)
    {
        
        IpListCache.removeFromIpListCach(authAppId);
        IpCountCache.removeFromIpCountCach(authAppId);
        
        AuthApp authApp = authAppManager.getByAuthAppID(authAppId);
        if (null == authApp)
        {
            LOGGER.warn("authApp [ {} ] not exists.", authAppId);
            return;
        }
        
        List<AuthAppNetRegionIp> newIps = authAppNetRegionIpService.listAllNetworkRegion(authAppId);
        
        if (null == newIps)
        {
            List<AuthAppNetRegionIpCacheObj> ips = new ArrayList<AuthAppNetRegionIpCacheObj>(0);
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
        
        List<AuthAppNetRegionIpCacheObj> ips = new ArrayList<AuthAppNetRegionIpCacheObj>(newIps.size());
        AuthAppNetRegionIpCacheObj obj;
        for (AuthAppNetRegionIp ipConfig : newIps)
        {
            obj = new AuthAppNetRegionIpCacheObj(ipConfig.getIpStartValue(), ipConfig.getIpEndValue(),
                ipConfig.getRegionId());
            ips.add(obj);
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
        
        // IP格式不正确
        if (StringUtils.isBlank(authAppNetRegionIp.getIpStart())
            || !IpUtils.isIPv4LiteralAddress(authAppNetRegionIp.getIpStart())
            || StringUtils.isBlank(authAppNetRegionIp.getIpEnd())
            || !IpUtils.isIPv4LiteralAddress(authAppNetRegionIp.getIpEnd()))
        {
            throw new InvalidParamterException("invalid ip address");
        }
        
        if (StringUtils.isBlank(authAppNetRegionIp.getAuthAppId()))
        {
            LOGGER.warn("authAppId is null");
            throw new InvalidParamterException("authAppId is null");
        }
        
        // authapp不存在
        AuthApp authApp = authAppService.getByAuthAppID(authAppNetRegionIp.getAuthAppId());
        if (null == authApp)
        {
            LOGGER.warn("authAppId {} not exists.", authAppNetRegionIp.getAuthAppId());
            throw new InvalidParamterException("authAppId " + authAppNetRegionIp.getAuthAppId()
                + " not exists");
        }
        
        authAppNetRegionIp.setIpStartValue(IpUtils.toLong(authAppNetRegionIp.getIpStart()));
        authAppNetRegionIp.setIpEndValue(IpUtils.toLong(authAppNetRegionIp.getIpEnd()));
        
        // 开始IP必须小于等于结束IP
        if (authAppNetRegionIp.getIpStartValue() > authAppNetRegionIp.getIpEndValue())
        {
            LOGGER.warn("startIp : {}  is max than endIp : {}.",
                authAppNetRegionIp.getIpStart(),
                authAppNetRegionIp.getIpEnd());
            throw new InvalidParamterException("startIp is max than endIp");
        }
        
        // 配置不允许重复存在，IP段不允许叠加
        AuthAppNetRegionIp start = authAppNetRegionIpService.getAuthAppNetRegionIpConfig(authAppNetRegionIp.getAuthAppId(),
            authAppNetRegionIp.getIpStart());
        if (null != start && !(isUpdate && start.getId().equals(authAppNetRegionIp.getId())))
        {
            LOGGER.warn("startIp is already in config {}.", start);
            throw new ExistNetworkRegionIpConflictException();
        }
        
        AuthAppNetRegionIp end = authAppNetRegionIpService.getAuthAppNetRegionIpConfig(authAppNetRegionIp.getAuthAppId(),
            authAppNetRegionIp.getIpEnd());
        // 如果是更新操作
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
}
