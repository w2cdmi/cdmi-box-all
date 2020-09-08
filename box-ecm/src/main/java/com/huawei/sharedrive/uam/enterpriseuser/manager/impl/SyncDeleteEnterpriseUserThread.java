package com.huawei.sharedrive.uam.enterpriseuser.manager.impl;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.huawei.sharedrive.uam.enterpriseuser.domain.LdapCheck;
import com.huawei.sharedrive.uam.enterpriseuser.manager.LdapCheckManager;
import com.huawei.sharedrive.uam.enterpriseuser.manager.SyncEnterpriseUserManager;

public class SyncDeleteEnterpriseUserThread implements Runnable
{
    private static final Logger LOGGER = LoggerFactory.getLogger(SyncDeleteEnterpriseUserThread.class);
    
    private Long enterpriseId;
    
    private LdapCheckManager ldapCheckManager;
    
    private SyncEnterpriseUserManager syncEnterpriseUserManager;
    
    public SyncDeleteEnterpriseUserThread(Long enterpriseId, LdapCheckManager ldapCheckManager,
        SyncEnterpriseUserManager syncEnterpriseUserManager)
    {
        this.enterpriseId = enterpriseId;
        this.ldapCheckManager = ldapCheckManager;
        this.syncEnterpriseUserManager = syncEnterpriseUserManager;
    }
    
    @Override
    public void run()
    {
        LdapCheck ldapCheck = ldapCheckManager.get(enterpriseId);
        Date lastCheckTime = new Date();
        if (null != ldapCheck)
        {
            if (LdapCheck.CHECK_UNFINISH.equals(ldapCheck.getCheckStatus()))
            {
                LOGGER.error("check unfinished ");
                return;
            }
            ldapCheck.setCheckStatus(LdapCheck.CHECK_UNFINISH);
            ldapCheck.setLastCheckTime(lastCheckTime);
            ldapCheckManager.update(ldapCheck);
        }
        else
        {
            ldapCheck = new LdapCheck();
            ldapCheck.setCheckStatus(LdapCheck.CHECK_UNFINISH);
            ldapCheck.setEnterpriseId(enterpriseId);
            ldapCheck.setLastCheckTime(lastCheckTime);
            ldapCheckManager.insert(ldapCheck);
        }
        try
        {
            syncEnterpriseUserManager.syncDeleteEnterpriseUser(enterpriseId);
        }
        catch (Exception e)
        {
            LOGGER.error("sync delete user failed ", e);
        }
        finally
        {
            ldapCheck.setCheckStatus(LdapCheck.CHECK_FINISHED);
            ldapCheckManager.update(ldapCheck);
        }
        
    }
    
}
