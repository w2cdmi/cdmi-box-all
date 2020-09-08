package com.huawei.sharedrive.uam.enterprise.service.impl;

import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.util.HtmlUtils;

import com.huawei.sharedrive.uam.enterprise.dao.AccessConfigDao;
import com.huawei.sharedrive.uam.enterprise.dao.NetRegionDao;
import com.huawei.sharedrive.uam.enterprise.dao.SafeLevelDao;
import com.huawei.sharedrive.uam.enterprise.dao.SecurityRoleDao;
import com.huawei.sharedrive.uam.enterprise.domain.AccessConfig;
import com.huawei.sharedrive.uam.enterprise.domain.AccessSpaceConfig;
import com.huawei.sharedrive.uam.enterprise.domain.NetRegion;
import com.huawei.sharedrive.uam.enterprise.domain.SafeLevel;
import com.huawei.sharedrive.uam.enterprise.domain.SecurityRole;
import com.huawei.sharedrive.uam.enterprise.service.AccessConfigService;
import com.huawei.sharedrive.uam.exception.InvalidParamterException;
import com.huawei.sharedrive.uam.util.SecurityConfigConstants;
import com.huawei.sharedrive.uam.util.SecurityConfigUtil;

import pw.cdmi.box.domain.Page;
import pw.cdmi.box.domain.PageImpl;
import pw.cdmi.box.domain.PageRequest;
import pw.cdmi.core.utils.BundleUtil;

@Component
public class AccessConfigServiceImpl implements AccessConfigService
{
    @Autowired
    private AccessConfigDao accessConfigDao;
    
    @Autowired
    private NetRegionDao netRegionDao;
    
    @Autowired
    private SafeLevelDao safeLevelDao;
    
    @Autowired
    private SecurityRoleDao securityDao;
    
    @Override
    public long create(AccessConfig accessConfig, Locale locale)
    {
        
        checkAccessConfigParameter(accessConfig);
        
        Date now = new Date();
        accessConfig.setCreatedAt(now);
        accessConfig.setModifiedAt(now);
        accessConfig.setClientType((long) SecurityConfigConstants.TYPE_OF_SELECT_ALL);
        
        handleAccessConfigVO(accessConfig, locale);
        
        return accessConfigDao.create(accessConfig);
        
    }
    
    @Override
    public void delete(long id, long accountId)
    {
        accessConfigDao.delete(id, accountId);
    }
    
    @Override
    public void deleteByFilter(AccessConfig filter)
    {
        
        accessConfigDao.deleteByContidion(filter);
        
    }
    
    @Override
    public long getByDomainExclusiveId(AccessConfig accessConfig)
    {
        return accessConfigDao.getByDomainExclusiveId(accessConfig);
    }
    
    @Override
    public AccessConfig getById(long id)
    {
        
        return accessConfigDao.getById(id);
    }
    
    @Override
    public AccessConfig getByAllType(AccessSpaceConfig accessSpaceConfig)
    {
        
        return accessConfigDao.getByAllType(accessSpaceConfig);
    }
    
    @Override
    public Page<AccessConfig> getFilterd(AccessConfig filter, PageRequest pageRequest, Locale locale)
    {
        if (filter.getSafeRoleId() != null
            && filter.getSafeRoleId() == SecurityConfigConstants.TYPE_OF_SELECT_ALL)
        {
            filter.setSafeRoleId(null);
        }
        
        if (filter.getSpaceRoleId() != null
            && filter.getSpaceRoleId() == SecurityConfigConstants.TYPE_OF_SELECT_ALL)
        {
            filter.setSpaceRoleId(null);
        }
        
        if (filter.getNetRegionId() != null
            && filter.getNetRegionId() == SecurityConfigConstants.TYPE_OF_SELECT_ALL)
        {
            filter.setNetRegionId(null);
        }
        if (filter.getClientType() != null
            && filter.getClientType() == SecurityConfigConstants.TYPE_OF_SELECT_ALL)
        {
            filter.setClientType(null);
        }
        int total = accessConfigDao.getFilterdCount(filter);
        List<AccessConfig> content;
        if (pageRequest != null)
        {
            content = accessConfigDao.getFilterd(filter, pageRequest.getOrder(), pageRequest.getLimit());
        }
        else
        {
            content = accessConfigDao.getFilterd(filter, null, null);
        }
        
        long accountId = filter.getAccountId();
        SecurityRole sr = new SecurityRole();
        sr.setAccountId(accountId);
        List<SecurityRole> listSecurityRole = securityDao.getFilterd(sr, null, null);
        
        NetRegion nr = new NetRegion();
        nr.setAccountId(accountId);
        List<NetRegion> listRegion = netRegionDao.getFilterd(nr, null, null);
        
        SafeLevel safeLevel = new SafeLevel();
        safeLevel.setAccountId(accountId);
        List<SafeLevel> listSafeLevel = safeLevelDao.getFilterd(safeLevel, null, null);
        
        for (AccessConfig accessConfig : content)
        {
            accessConfig.setSafeRoleName(HtmlUtils.htmlEscape(getSecurityRileById(accessConfig.getSafeRoleId(),
                listSecurityRole,
                locale)));
            accessConfig.setSpaceRoleName(HtmlUtils.htmlEscape(getSecurityRileById(accessConfig.getSpaceRoleId(),
                listSecurityRole,
                locale)));
            
            accessConfig.setNetRegionName(HtmlUtils.htmlEscape(getNetRegionNameById(accessConfig.getNetRegionId(),
                listRegion,
                locale)));
            accessConfig.setDownLoadResrouceTypeIds(HtmlUtils.htmlEscape(SecurityConfigUtil.getResourceTypeByIds(accessConfig.getDownLoadResrouceTypeIds(),
                locale,
                listSafeLevel)));
            accessConfig.setPreviewResourceTypeIds(HtmlUtils.htmlEscape(SecurityConfigUtil.getResourceTypeByIds(accessConfig.getPreviewResourceTypeIds(),
                locale,
                listSafeLevel)));
        }
        
        Page<AccessConfig> page = new PageImpl<AccessConfig>(content, pageRequest, total);
        return page;
        
    }
    
    @Override
    public List<AccessConfig> getFilterdList(AccessConfig filter)
    {
        
        List<AccessConfig> content = accessConfigDao.getFilterd(filter, null, null);
        return content;
        
    }
    
    @Override
    public boolean isDuplicateNetConfigValues(AccessConfig accessConfig)
    {
        return accessConfigDao.isDuplicateValues(accessConfig);
    }
    
    @Override
    public boolean isDuplicateValues(AccessConfig accessConfig)
    {
        return accessConfigDao.isDuplicateValues(accessConfig);
    }
    
    @Override
    public void updateSecurityRole(AccessConfig accessConfig, Locale locale)
    {
        checkAccessConfigParameter(accessConfig);
        
        Date now = new Date();
        accessConfig.setModifiedAt(now);
        accessConfig.setClientType((long) SecurityConfigConstants.TYPE_OF_SELECT_ALL);
        
        handleAccessConfigVO(accessConfig, locale);
        
        accessConfigDao.update(accessConfig);
        
    }
    
    private void checkAccessConfigParameter(AccessConfig accessConfig)
    {
        checkNetRegionIdRule(accessConfig);
        
        checkSecurityIdRule(accessConfig);
        
        checkTargetSecurityIdRule(accessConfig);
        
        checkResrouceTypeIdsRule(accessConfig.getAccountId(), accessConfig.getDownLoadResrouceTypeIds());
        
        checkResrouceTypeIdsRule(accessConfig.getAccountId(), accessConfig.getPreviewResourceTypeIds());
    }
    
    private void checkNetRegionIdRule(AccessConfig accessConfig)
    {
        NetRegion nr = new NetRegion();
        nr.setAccountId(accessConfig.getAccountId());
        List<NetRegion> listRegion = netRegionDao.getFilterd(nr, null, null);
        long id = accessConfig.getNetRegionId();
        if (id == -1)
        {
            return;
        }
        boolean isAllowed = false;
        for (NetRegion netRegion : listRegion)
        {
            if (netRegion.getId() == id)
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
    
    private boolean isAllowed(String[] splitStr, List<SafeLevel> list)
    {
        boolean isAllowed = false;
        long id;
        for (String s : splitStr)
        {
            for (SafeLevel op : list)
            {
                id = op.getId();
                if (Long.parseLong(s) != id)
                {
                    isAllowed = false;
                    
                }
                else
                {
                    isAllowed = true;
                    break;
                }
            }
        }
        return isAllowed;
    }
    
    private void checkResrouceTypeIdsRule(long accountId, String operationIds)
    {
        if (operationIds.equals(SecurityConfigConstants.TYPE_OF_ALL_RESOURCE_STRATEGY))
        {
            return;
        }
        SafeLevel safeLevel = new SafeLevel();
        safeLevel.setAccountId(accountId);
        List<SafeLevel> list = safeLevelDao.getFilterd(safeLevel, null, null);
        String[] splitStr;
        if (StringUtils.isNotBlank(operationIds))
        {
            splitStr = operationIds.split(",");
        }
        else
        {
            return;
        }
        
        boolean isAllowed = false;
        try
        {
            isAllowed = isAllowed(splitStr, list);
        }
        catch (NumberFormatException e)
        {
            isAllowed = false;
        }
        if (!isAllowed)
        {
            throw new InvalidParamterException();
        }
        
    }
    
    private void checkSecurityIdRule(AccessConfig accessConfig)
    {
        SecurityRole sr = new SecurityRole();
        sr.setAccountId(accessConfig.getAccountId());
        List<SecurityRole> listSecurityRole = securityDao.getFilterd(sr, null, null);
        long id = accessConfig.getSafeRoleId();
        if (id == -1)
        {
            return;
        }
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
    
    private void checkTargetSecurityIdRule(AccessConfig accessConfig)
    {
        SecurityRole sr = new SecurityRole();
        sr.setAccountId(accessConfig.getAccountId());
        List<SecurityRole> listSecurityRole = securityDao.getFilterd(sr, null, null);
        long id = accessConfig.getSpaceRoleId();
        if (id == -1)
        {
            return;
        }
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
    
    private String getNetRegionNameById(long id, List<NetRegion> listNetRegion, Locale locale)
    {
        if (id == SecurityConfigConstants.TYPE_OF_SELECT_ALL)
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
    
    private String getSecurityRileById(long id, List<SecurityRole> listSecurityRole, Locale locale)
    {
        if (id == SecurityConfigConstants.TYPE_OF_SELECT_ALL)
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
    
    private void handleAccessConfigVO(AccessConfig accessConfig, Locale locale)
    {
        long accountId = accessConfig.getAccountId();
        SecurityRole securityRole = new SecurityRole();
        securityRole.setAccountId(accountId);
        List<SecurityRole> listSecurityRole = securityDao.getFilterd(securityRole, null, null);
        
        NetRegion nr = new NetRegion();
        nr.setAccountId(accountId);
        List<NetRegion> listRegion = netRegionDao.getFilterd(nr, null, null);
        
        accessConfig.setSafeRoleName(getSecurityRileById(accessConfig.getSafeRoleId(),
            listSecurityRole,
            locale));
        
        accessConfig.setSpaceRoleName(getSecurityRileById(accessConfig.getSpaceRoleId(),
            listSecurityRole,
            locale));
        
        accessConfig.setNetRegionName(getNetRegionNameById(accessConfig.getNetRegionId(), listRegion, locale));
    }
    
    @Override
    public List<AccessConfig> getAccessConfigList(long accountId)
    {
        List<AccessConfig> content = accessConfigDao.getAccessConfigList(accountId);
        return content;
    }
    
    @Override
    public void deleteByCondition(AccessConfig accessconfig)
    {
        accessConfigDao.deleteByContidion(accessconfig);
    }
    
}
