package com.huawei.sharedrive.uam.enterpriseadminlog.manager;

import java.util.Locale;

import com.huawei.sharedrive.uam.enterpriseadminlog.domain.AdminLogType;
import com.huawei.sharedrive.uam.enterpriseadminlog.domain.EnterpriseAdminLog;
import com.huawei.sharedrive.uam.enterpriseadminlog.domain.LogOwner;
import com.huawei.sharedrive.uam.enterpriseadminlog.domain.QueryCondition;

import pw.cdmi.box.domain.Page;

public interface AdminLogManager
{
    void saveAdminLog(LogOwner owner, AdminLogType logType, String[] paramsDesc);
    
    Page<EnterpriseAdminLog> getSyncLog(QueryCondition qc, Locale locale);
}
