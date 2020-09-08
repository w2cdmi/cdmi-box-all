package com.huawei.sharedrive.uam.enterprise.manager.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.huawei.sharedrive.uam.enterprise.domain.AccountBasicConfig;
import com.huawei.sharedrive.uam.enterprise.manager.AccountBasicConfigManager;
import com.huawei.sharedrive.uam.enterprise.service.AccountBasicConfigService;
import com.huawei.sharedrive.uam.system.service.AppBasicConfigService;

import pw.cdmi.common.domain.AppBasicConfig;

@Component
@Deprecated
public class AccountBasicConfigManagerImpl implements AccountBasicConfigManager
{
    @Autowired
    private AccountBasicConfigService basicConfigService;
    
    @Autowired
	private AppBasicConfigService appBasicConfigService;
    
//    private static final String UNLIMIT_CONFIG = "-1";
    
    @Override
    public AccountBasicConfig queryAccountBasicConfig(AccountBasicConfig appBasicConfig,String appId)
    {
        boolean exitAccountBasicConfig = isExitAccountBasicConfig(appBasicConfig);
        if (exitAccountBasicConfig)
        {
            return basicConfigService.get(appBasicConfig);
        }
        AccountBasicConfig accountBasicConfig = new AccountBasicConfig();
        AppBasicConfig  appBasic = appBasicConfigService.getAppBasicConfig(appId);
        accountBasicConfig.setAccountId(appBasicConfig.getAccountId());
        accountBasicConfig.setUserSpaceQuota(appBasic.getUserSpaceQuota()+"");
        accountBasicConfig.setUserVersions(appBasic.getMaxFileVersions()+"");
        accountBasicConfig.setEnableTeamSpace(appBasic.isEnableTeamSpace());
        accountBasicConfig.setMaxTeamSpaces(appBasic.getMaxTeamSpaces()+"");
        accountBasicConfig.setTeamSpaceQuota(appBasic.getTeamSpaceQuota()+"");
        accountBasicConfig.setTeamSpaceVersions(appBasic.getMaxFileVersions()+"");
        return new AccountBasicConfig(accountBasicConfig);
    }
    
    @Override
    public void addAccountBasicConfig(AccountBasicConfig appBasicConfig)
    {
        boolean exitAccountBasicConfig = isExitAccountBasicConfig(appBasicConfig);
        if (exitAccountBasicConfig)
        {
            basicConfigService.update(appBasicConfig);
        }
        else
        {
            basicConfigService.insert(appBasicConfig);
        }
        
    }
    
    public boolean isExitAccountBasicConfig(AccountBasicConfig appBasicConfig)
    {
        int accountIdNum = basicConfigService.getAccountIdNum(appBasicConfig);
        if (accountIdNum > 0)
        {
            return true;
        }
        return false;
    }
    
    @Override
    public boolean paramCheck(AccountBasicConfig appBasicConfig)
    {
        if (rangeCheck(appBasicConfig.getMaxTeamSpaces(), 10000, -1))
        {
            return true;
        }
        if (rangeCheck(appBasicConfig.getTeamSpaceVersions(), 10000, -1))
        {
            return true;
        }
        if (rangeCheck(appBasicConfig.getUserVersions(), 10000, -1))
        {
            return true;
        }
        if (rangeCheck(appBasicConfig.getUserSpaceQuota(), 104857600, -1))
        {
            return true;
        }
        if (rangeCheck(appBasicConfig.getTeamSpaceQuota(), 104857600, -1))
        {
            return true;
        }
        if (!appBasicConfig.isEnableTeamSpace())
        {
            appBasicConfig.setMaxTeamSpaces("0");
            appBasicConfig.setTeamSpaceQuota("0");
            appBasicConfig.setTeamSpaceVersions("0");
        }
        return false;
    }
    
    @Override
    public void convertToNull(AccountBasicConfig appBasicConfig)
    {
        if (!appBasicConfig.isEnableTeamSpace())
        {
            appBasicConfig.setMaxTeamSpaces(null);
            appBasicConfig.setTeamSpaceQuota(null);
            appBasicConfig.setTeamSpaceVersions(null);
        }
    }
    
    @Override
    public boolean rangeCheck(String param, Integer max, Integer min)
    {
        Integer integer;
        try
        {
            integer = Integer.valueOf(param);
        }
        catch (NumberFormatException e)
        {
            return true;
        }
        if (integer < min || integer > max)
        {
            return true;
        }
        return false;
    }
}
