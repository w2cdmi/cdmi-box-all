package com.huawei.sharedrive.uam.enterprise.service.impl;

import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.util.HtmlUtils;

import com.huawei.sharedrive.uam.enterprise.dao.NetRegionDao;
import com.huawei.sharedrive.uam.enterprise.dao.ResourceStrategyDao;
import com.huawei.sharedrive.uam.enterprise.dao.SafeLevelDao;
import com.huawei.sharedrive.uam.enterprise.dao.SecurityRoleDao;
import com.huawei.sharedrive.uam.enterprise.domain.NetRegion;
import com.huawei.sharedrive.uam.enterprise.domain.ResourceStrategy;
import com.huawei.sharedrive.uam.enterprise.domain.SafeLevel;
import com.huawei.sharedrive.uam.enterprise.domain.SecurityRole;
import com.huawei.sharedrive.uam.enterprise.service.ResourceStrategyService;
import com.huawei.sharedrive.uam.exception.InvalidParamterException;
import com.huawei.sharedrive.uam.exception.NoSuchResourceStrategyException;
import com.huawei.sharedrive.uam.util.SecurityConfigConstants;

import pw.cdmi.box.domain.Page;
import pw.cdmi.box.domain.PageImpl;
import pw.cdmi.box.domain.PageRequest;
import pw.cdmi.core.utils.BundleUtil;

@Component
public class ResourceStrategyServiceImpl implements ResourceStrategyService
{
    
    @Autowired
    private ResourceStrategyDao resourceStrategyDao;
    
    @Autowired
    private SafeLevelDao safeLevelDao;
    
    @Autowired
    private SecurityRoleDao securityDao;
    
    @Autowired
    private NetRegionDao netRegionDao;
    
    public static final int TYPE_OF_SELECT_ALL = -1;
    
    @Override
    public void create(ResourceStrategy resourceStrategy)
    {
        checkResourceStrategy(resourceStrategy);
        
        Date now = new Date();
        resourceStrategy.setCreatedAt(now);
        resourceStrategy.setModifiedAt(now);
        resourceStrategyDao.create(resourceStrategy);
        
    }
    
    private void checkResourceStrategy(ResourceStrategy resourceStrategy)
    {
        long accountId = resourceStrategy.getAccountId();
        SecurityRole securityRole = new SecurityRole();
        securityRole.setAccountId(accountId);
        checkSecurityRoleIdRule(resourceStrategy.getSecurityRoleId(), securityRole);
        
        NetRegion netRegion = new NetRegion();
        netRegion.setAccountId(accountId);
        checkNetRegionIdRule(resourceStrategy.getNetRegionId(), netRegion);
        
        SafeLevel safeLevel = new SafeLevel();
        safeLevel.setAccountId(accountId);
        checkSecurityLevelIdRule(resourceStrategy.getResourceSecurityLevelId(), safeLevel);
    }
    
    @Override
    public Page<ResourceStrategy> getFilterd(ResourceStrategy resourceStrategy, PageRequest pageRequest,
        Locale locale)
    {
        
        int total = resourceStrategyDao.getFilterdCount(resourceStrategy);
        List<ResourceStrategy> content = resourceStrategyDao.getFilterd(resourceStrategy,
            pageRequest.getOrder(),
            pageRequest.getLimit());
        long accountId = resourceStrategy.getAccountId();
        SecurityRole sr = new SecurityRole();
        sr.setAccountId(accountId);
        List<SecurityRole> listSecurityRole = securityDao.getFilterd(sr, null, null);
        
        NetRegion nr = new NetRegion();
        nr.setAccountId(accountId);
        List<NetRegion> listRegion = netRegionDao.getFilterd(nr, null, null);
        
        SafeLevel safeLevel = new SafeLevel();
        safeLevel.setAccountId(accountId);
        List<SafeLevel> listSafeLevel = safeLevelDao.getFilterd(safeLevel, null, null);
        
        Locale[] locales = null;
        for (ResourceStrategy accessConfig : content)
        {
            locales = new Locale[]{Locale.ENGLISH, Locale.CHINESE};
            BundleUtil.addBundle("messages", locales);
            accessConfig.setLocation(HtmlUtils.htmlEscape(accessConfig.getLocation()));
            accessConfig.setSafeRoleName(HtmlUtils.htmlEscape(getSecurityRileById(accessConfig.getSecurityRoleId(),
                listSecurityRole,
                locale)));
            accessConfig.setNetRegionName(HtmlUtils.htmlEscape(getNetRegionNameById(accessConfig.getNetRegionId(),
                listRegion,
                locale)));
            accessConfig.setResourceTypeName(HtmlUtils.htmlEscape(getResourceTypeNameById(accessConfig.getResourceSecurityLevelId(),
                listSafeLevel,
                locale)));
        }
        
        Page<ResourceStrategy> page = new PageImpl<ResourceStrategy>(content, pageRequest, total);
        return page;
        
    }
    
    @Override
    public Page<ResourceStrategy> queryFilterd(List<ResourceStrategy> content,
        ResourceStrategy resourceStrategy, PageRequest pageRequest, Locale locale)
    {
        
        int total = resourceStrategyDao.getFilterdCount(resourceStrategy);
        long accountId = resourceStrategy.getAccountId();
        SecurityRole sr = new SecurityRole();
        sr.setAccountId(accountId);
        List<SecurityRole> listSecurityRole = securityDao.getFilterd(sr, null, null);
        
        NetRegion nr = new NetRegion();
        nr.setAccountId(accountId);
        List<NetRegion> listRegion = netRegionDao.getFilterd(nr, null, null);
        
        SafeLevel safeLevel = new SafeLevel();
        safeLevel.setAccountId(accountId);
        List<SafeLevel> listSafeLevel = safeLevelDao.getFilterd(safeLevel, null, null);
        
        Locale[] locales = null;
        for (ResourceStrategy accessConfig : content)
        {
            locales = new Locale[]{Locale.ENGLISH, Locale.CHINESE};
            BundleUtil.addBundle("messages", locales);
            accessConfig.setLocation(HtmlUtils.htmlEscape(accessConfig.getLocation()));
            accessConfig.setSafeRoleName(HtmlUtils.htmlEscape(getSecurityRileById(accessConfig.getSecurityRoleId(),
                listSecurityRole,
                locale)));
            accessConfig.setNetRegionName(HtmlUtils.htmlEscape(getNetRegionNameById(accessConfig.getNetRegionId(),
                listRegion,
                locale)));
            accessConfig.setResourceTypeName(HtmlUtils.htmlEscape(getResourceTypeNameById(accessConfig.getResourceSecurityLevelId(),
                listSafeLevel,
                locale)));
        }
        
        Page<ResourceStrategy> page = new PageImpl<ResourceStrategy>(content, pageRequest, total);
        return page;
        
    }
    
    private String getSecurityRileById(long id, List<SecurityRole> listSecurityRole, Locale locale)
    {
        if (id == TYPE_OF_SELECT_ALL)
        {
            return BundleUtil.getText("messages", locale, "security.role.any", null);
        }
        String roleName = "";
        for (SecurityRole s : listSecurityRole)
        {
            if (s.getId() == id)
            {
                roleName = s.getRoleName();
            }
            
        }
        return roleName;
    }
    
    private String getResourceTypeNameById(long id, List<SafeLevel> listSafeLevel, Locale locale)
    {
        if (id == TYPE_OF_SELECT_ALL)
        {
            return BundleUtil.getText("messages", locale, "common.select.all", null);
        }
        String name = "";
        for (SafeLevel s : listSafeLevel)
        {
            if (s.getId() == id)
            {
                name = s.getSafeLevelName();
            }
            
        }
        return name;
    }
    
    private String getNetRegionNameById(long id, List<NetRegion> listNetRegion, Locale locale)
    {
        if (id == TYPE_OF_SELECT_ALL)
        {
            return BundleUtil.getText("messages", locale, "network.region.any", null);
        }
        String roleName = "";
        for (NetRegion s : listNetRegion)
        {
            if (s.getId() == id)
            {
                roleName = s.getNetRegionName();
            }
            
        }
        return roleName;
    }
    
    @Override
    public void delete(ResourceStrategy resourceStrategy)
    {
        resourceStrategyDao.deleteByContidion(resourceStrategy);
    }
    
    @Override
    public void update(ResourceStrategy resourceStrategy)
    {
        if (resourceStrategy.getNetRegionId() == SecurityConfigConstants.TYPE_OF_SELECT_ALL
            && resourceStrategy.getSecurityRoleId() == SecurityConfigConstants.TYPE_OF_SELECT_ALL)
        {
            throw new InvalidParamterException("the regionid and security id cannot be -1 at the same time");
        }
        checkResourceStrategy(resourceStrategy);
        
        Date now = new Date();
        resourceStrategy.setModifiedAt(now);
        resourceStrategyDao.update(resourceStrategy);
    }
    
    @Override
    public ResourceStrategy getResourceStrategy(ResourceStrategy resourceStrategy)
    {
        List<ResourceStrategy> list = resourceStrategyDao.getResourceStrategyById(resourceStrategy);
        if (list.isEmpty())
        {
            throw new NoSuchResourceStrategyException();
        }
        return list.get(0);
    }
    
    private void checkSecurityRoleIdRule(long id, SecurityRole securityRole)
    {
        if (id == -1)
        {
            return;
        }
        
        List<SecurityRole> listSecurityRole = securityDao.getFilterd(securityRole, null, null);
        boolean isAllowed = false;
        for (SecurityRole s : listSecurityRole)
        {
            if (s.getId() == id)
            {
                isAllowed = true;
                break;
            }
        }
        if (!isAllowed)
        {
            throw new InvalidParamterException();
        }
    }
    
    private void checkNetRegionIdRule(long id, NetRegion netRegion)
    {
        if (id == -1)
        {
            return;
        }
        
        List<NetRegion> listRegion = netRegionDao.getFilterd(netRegion, null, null);
        boolean isAllowed = false;
        for (NetRegion s : listRegion)
        {
            if (s.getId() == id)
            {
                isAllowed = true;
                break;
            }
        }
        if (!isAllowed)
        {
            throw new InvalidParamterException();
        }
    }
    
    private void checkSecurityLevelIdRule(long id, SafeLevel safeLevel)
    {
        List<SafeLevel> listSafeLevel = safeLevelDao.getFilterd(safeLevel, null, null);
        boolean isAllowed = false;
        for (SafeLevel s : listSafeLevel)
        {
            if (s.getId() == id)
            {
                isAllowed = true;
                break;
            }
        }
        if (!isAllowed)
        {
            throw new InvalidParamterException();
        }
    }
    
    @Override
    public boolean isDuplicateValues(ResourceStrategy resourceStrategy)
    {
        return resourceStrategyDao.isDuplicateValues(resourceStrategy);
    }
    
    @Override
    public ResourceStrategy getResourceStrategyId(long id)
    {
        return resourceStrategyDao.getByStrategyId(id);
    }
    
    @Override
    public void deleteStrategy(long id)
    {
        resourceStrategyDao.deleteStrategy(id);
    }
}
