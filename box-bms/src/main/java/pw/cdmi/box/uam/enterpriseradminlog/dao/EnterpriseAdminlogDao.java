package pw.cdmi.box.uam.enterpriseradminlog.dao;

import java.util.List;

import pw.cdmi.box.domain.Limit;
import pw.cdmi.box.uam.enterpriseradminlog.domain.EnterpriseAdminLog;
import pw.cdmi.box.uam.enterpriseradminlog.domain.QueryCondition;

public interface EnterpriseAdminlogDao
{
    List<EnterpriseAdminLog> getEnterpriseAdminlog(QueryCondition qc, Limit limit);
    
    int getCount(QueryCondition qc);
}
