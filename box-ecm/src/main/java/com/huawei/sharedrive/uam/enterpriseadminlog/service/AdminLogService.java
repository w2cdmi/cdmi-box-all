/**
 * 
 */
package com.huawei.sharedrive.uam.enterpriseadminlog.service;

import java.util.List;

import com.huawei.sharedrive.uam.enterpriseadminlog.domain.AdminLogType;
import com.huawei.sharedrive.uam.enterpriseadminlog.domain.EnterpriseAdminLog;
import com.huawei.sharedrive.uam.enterpriseadminlog.domain.LogOwner;
import com.huawei.sharedrive.uam.enterpriseadminlog.domain.QueryCondition;

import pw.cdmi.box.domain.Limit;

public interface AdminLogService
{
    EnterpriseAdminLog getAdminLog(LogOwner owner);
    
    void saveAdminLog(EnterpriseAdminLog adminLog, AdminLogType logType, String[] paramsDesc);
    
    List<EnterpriseAdminLog> queryList(QueryCondition qc, Limit limit);
    
    int getCount(QueryCondition qc);
    
    int getFilterCount(long enterpriseId, String operateType);
    
    EnterpriseAdminLog getMinCreateTime(long enterpriseId, String operateType);
    
    void deleteImportUserRecordLog(EnterpriseAdminLog adminLog);
}
