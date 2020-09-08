package pw.cdmi.box.uam.enterpriseradminlog.service;

import java.util.List;

import pw.cdmi.box.domain.Limit;
import pw.cdmi.box.uam.enterpriseradminlog.domain.EnterpriseAdminLog;
import pw.cdmi.box.uam.enterpriseradminlog.domain.QueryCondition;

public interface EnterpriseAdminlogService
{
    List<EnterpriseAdminLog> queryList(QueryCondition qc, Limit limit);
    
    int getCount(QueryCondition qc);
    
}
