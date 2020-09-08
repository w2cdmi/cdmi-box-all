package com.huawei.sharedrive.uam.enterprise.manager.impl;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Component;

import com.huawei.sharedrive.uam.enterprise.domain.AccessSpaceConfig;
import com.huawei.sharedrive.uam.enterprise.domain.AccessSpaceConfigExt;
import com.huawei.sharedrive.uam.enterprise.domain.NetRegion;
import com.huawei.sharedrive.uam.enterprise.domain.SecurityRole;
import com.huawei.sharedrive.uam.enterprise.manager.AccessSpaceManager;
import com.huawei.sharedrive.uam.enterprise.service.AccessSpaceConfigService;
import com.huawei.sharedrive.uam.enterprise.service.NetRegionService;
import com.huawei.sharedrive.uam.enterprise.service.SecurityRoleService;
import com.huawei.sharedrive.uam.exception.ExistAccessSpaceConfigConflictException;
import com.huawei.sharedrive.uam.util.SecurityConfigUtil;

import pw.cdmi.box.domain.Page;
import pw.cdmi.box.domain.PageRequest;

@Component
public class AccessSpaceManagerImpl implements AccessSpaceManager
{
    @Autowired
    private AccessSpaceConfigService accessSpaceService;
    
    @Autowired
    private NetRegionService netRegionService;
    
    @Autowired
    private SecurityRoleService securityRoleService;
    
    @SuppressWarnings("PMD.PreserveStackTrace")
    @Override
    public void createConfig(AccessSpaceConfigExt spaceConfig, Locale locale)
    {
        try
        {
            String[] operTypes = spaceConfig.getOperation().split(",");
            AccessSpaceConfig accessSpaceConfig = new AccessSpaceConfig();
            long temp = SecurityConfigUtil.handleOperationMatrix(operTypes);
            transConfig(accessSpaceConfig, spaceConfig);
            accessSpaceConfig.setOperation(temp);
            accessSpaceService.create(accessSpaceConfig, locale);
        }
        catch (DuplicateKeyException e)
        {
            throw new ExistAccessSpaceConfigConflictException(e.getMessage());
        }
    }
    
    @Override
    public void deleteConfig(String id)
    {
        accessSpaceService.delete(id);
    }
    
    @Override
    public Page<AccessSpaceConfig> getAccessSpaceList(AccessSpaceConfigExt accessSpace,
        PageRequest pagerRequest, Locale locale)
    {
        AccessSpaceConfig accessSpaceConfig = new AccessSpaceConfig();
        transConfig(accessSpace, accessSpaceConfig);
        Page<AccessSpaceConfig> spaceConfig = accessSpaceService.getFilterd(accessSpaceConfig,
            pagerRequest,
            locale);
        return spaceConfig;
    }
    
    private List<NetRegion> getNetRegion()
    {
        return getNetRegion(null);
    }
    
    private List<NetRegion> getNetRegion(NetRegion region)
    {
        List<NetRegion> regions = netRegionService.getFilterdList(region);
        return regions;
    }
    
    private List<SecurityRole> getSecurityRole()
    {
        List<SecurityRole> roles = securityRoleService.getFilterdList(null);
        return roles;
    }
    
    private void transConfig(AccessSpaceConfig accessSpaceConfig, AccessSpaceConfigExt spaceConfig)
    {
        String id = UUID.randomUUID().toString().replaceAll("-", "");
        accessSpaceConfig.setId(id);
        accessSpaceConfig.setAccountId(spaceConfig.getAccountId());
        accessSpaceConfig.setClientName(spaceConfig.getClientName());
        accessSpaceConfig.setClientType(spaceConfig.getClientType());
        accessSpaceConfig.setNetRegionId(spaceConfig.getNetRegionId());
        accessSpaceConfig.setSafeRoleId(spaceConfig.getSafeRoleId());
        accessSpaceConfig.setTargetSafeRoleId(spaceConfig.getTargetSafeRoleId());
        accessSpaceConfig.setAccountId(spaceConfig.getAccountId());
        transNameById(accessSpaceConfig, spaceConfig);
    }
    
    private void transConfig(AccessSpaceConfigExt accessSpace, AccessSpaceConfig accessSpaceConfig)
    {
        if (accessSpace != null)
        {
            accessSpaceConfig.setNetRegionId(accessSpace.getNetRegionId());
            accessSpaceConfig.setSafeRoleId(accessSpace.getSafeRoleId());
            accessSpaceConfig.setTargetSafeRoleId(accessSpace.getTargetSafeRoleId());
            accessSpaceConfig.setClientType(accessSpace.getClientType());
            accessSpaceConfig.setAccountId(accessSpace.getAccountId());
        }
    }
    
    private void transNameById(AccessSpaceConfig accessSpaceConfig, AccessSpaceConfigExt spaceConfig)
    {
        transRegionName(accessSpaceConfig, spaceConfig);
        List<SecurityRole> roles = getSecurityRole();
        transSafeName(accessSpaceConfig, spaceConfig, roles);
        transTargetName(accessSpaceConfig, spaceConfig, roles);
        
    }
    
    private void transRegionName(AccessSpaceConfig accessSpaceConfig, AccessSpaceConfigExt spaceConfig)
    {
        if (spaceConfig.getNetRegionId() == AccessSpaceConfig.ALL_SELECT)
        {
            accessSpaceConfig.setNetRegionName(AccessSpaceConfig.SELCT_ALL);
        }
        else
        {
            List<NetRegion> regions = getNetRegion();
            for (NetRegion region : regions)
            {
                if (spaceConfig.getNetRegionId().longValue() == region.getId().longValue())
                {
                    accessSpaceConfig.setNetRegionName(region.getNetRegionName());
                    break;
                }
            }
        }
    }
    
    private void transSafeName(AccessSpaceConfig accessSpaceConfig, AccessSpaceConfigExt spaceConfig,
        List<SecurityRole> roles)
    {
        if (spaceConfig.getSafeRoleId() == AccessSpaceConfig.ALL_SELECT)
        {
            accessSpaceConfig.setSafeRoleName(AccessSpaceConfig.SELCT_ALL);
        }
        else
        {
            for (SecurityRole role : roles)
            {
                if (role.getId().longValue() == spaceConfig.getSafeRoleId().longValue())
                {
                    accessSpaceConfig.setSafeRoleName(role.getRoleName());
                    break;
                }
            }
        }
    }
    
    private void transTargetName(AccessSpaceConfig accessSpaceConfig, AccessSpaceConfigExt spaceConfig,
        List<SecurityRole> roles)
    {
        if (spaceConfig.getTargetSafeRoleId() == AccessSpaceConfig.ALL_SELECT)
        {
            accessSpaceConfig.setTargetSafeRoleName(AccessSpaceConfig.SELCT_ALL);
        }
        else
        {
            for (SecurityRole role : roles)
            {
                if (role.getId().longValue() == spaceConfig.getTargetSafeRoleId().longValue())
                {
                    accessSpaceConfig.setTargetSafeRoleName(role.getRoleName());
                    break;
                }
            }
        }
    }
    
    @Override
    public void modify(AccessSpaceConfigExt accessSpaceConfig, Locale locale) throws IOException
    {
        
        String[] operTypes = accessSpaceConfig.getOperation().split(",");
        long operation = SecurityConfigUtil.handleOperationMatrix(operTypes);
        AccessSpaceConfig spaceConfig = new AccessSpaceConfig();
        spaceConfig.setId(accessSpaceConfig.getId());
        spaceConfig.setOperation(operation);
        transConfig(accessSpaceConfig, spaceConfig);
        accessSpaceService.update(spaceConfig, locale);
        
    }
    
    @Override
    public AccessSpaceConfig getById(String id)
    {
        return accessSpaceService.getById(id);
    }
    
}
