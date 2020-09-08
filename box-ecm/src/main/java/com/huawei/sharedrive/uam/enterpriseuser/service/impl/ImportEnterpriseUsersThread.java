package com.huawei.sharedrive.uam.enterpriseuser.service.impl;

import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import com.huawei.sharedrive.uam.enterpriseadminlog.domain.LogOwner;
import com.huawei.sharedrive.uam.enterpriseuser.domain.EnterpriseUser;
import com.huawei.sharedrive.uam.enterpriseuser.domain.ImportEnterpriseUser;
import com.huawei.sharedrive.uam.enterpriseuser.manager.ImportEnterpriseUserManager;

public class ImportEnterpriseUsersThread implements Runnable
{
    private List<ImportEnterpriseUser> userList;
    private LogOwner owner;
    private ImportEnterpriseUserManager enterpriseUserManager;
    private Locale locale;

    @SuppressWarnings("PMD.ExcessiveParameterList")
    public ImportEnterpriseUsersThread(List<ImportEnterpriseUser> userList, LogOwner owner, Locale locale, ImportEnterpriseUserManager enterpriseUserManager)
    {
        this.userList = userList;
        this.enterpriseUserManager = enterpriseUserManager;
        this.owner = owner;
        this.locale = locale;
    }
    
    @Override
    public void run()
    {
        enterpriseUserManager.importUserListToDB(userList, owner, locale);
    }
    
}
