package pw.cdmi.box.uam.enterpriseradminlog.manager;

import java.util.Locale;

import pw.cdmi.box.domain.Page;
import pw.cdmi.box.uam.enterpriseradminlog.domain.EnterpriseAdminLog;
import pw.cdmi.box.uam.enterpriseradminlog.domain.QueryCondition;

public interface EnterpriseAdminLogManager
{
    
    Page<EnterpriseAdminLog> queryByPage(QueryCondition qc, Locale locale);
    
}
