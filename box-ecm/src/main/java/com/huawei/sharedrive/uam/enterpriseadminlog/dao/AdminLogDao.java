package com.huawei.sharedrive.uam.enterpriseadminlog.dao;

import java.util.List;

import com.huawei.sharedrive.uam.enterpriseadminlog.domain.EnterpriseAdminLog;
import com.huawei.sharedrive.uam.enterpriseadminlog.domain.QueryCondition;

import pw.cdmi.box.domain.Limit;

public interface AdminLogDao
{
    void createAdminLog(EnterpriseAdminLog adminLog);
    
    List<EnterpriseAdminLog> getEnterpriseAdminlog(QueryCondition qc, Limit limit);
    
    int getCount(QueryCondition qc);
    
    int getFilterCount(long enterpriseId, String operateType);
    
    EnterpriseAdminLog getMinCreateTime(long enterpriseId, String operateType);
    
    void deleteImportUserRecordLog(EnterpriseAdminLog adminLog);
}
