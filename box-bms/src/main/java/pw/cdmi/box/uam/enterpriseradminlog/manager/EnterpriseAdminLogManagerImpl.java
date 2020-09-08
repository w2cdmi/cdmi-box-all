package pw.cdmi.box.uam.enterpriseradminlog.manager;

import java.util.List;
import java.util.Locale;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import pw.cdmi.box.domain.Page;
import pw.cdmi.box.domain.PageImpl;
import pw.cdmi.box.uam.enterpriseradminlog.domain.EnterpriseAdminLog;
import pw.cdmi.box.uam.enterpriseradminlog.domain.QueryCondition;
import pw.cdmi.box.uam.enterpriseradminlog.service.EnterpriseAdminlogService;
import pw.cdmi.core.utils.BundleUtil;

@Component
public class EnterpriseAdminLogManagerImpl implements EnterpriseAdminLogManager
{
    
    @Autowired
    private EnterpriseAdminlogService service;
    
    @Override
    public Page<EnterpriseAdminLog> queryByPage(QueryCondition qc, Locale locale)
    {
        List<EnterpriseAdminLog> list = service.queryList(qc, qc.getPageRequest().getLimit());
        int totalCount = service.getCount(qc);
        Page<EnterpriseAdminLog> pagelist = new PageImpl<EnterpriseAdminLog>(list, qc.getPageRequest(),
            totalCount);
        for (EnterpriseAdminLog enterpriseAdminLog : pagelist)
        {
            if (StringUtils.isNotBlank(enterpriseAdminLog.getOperatLevel()))
            {
                enterpriseAdminLog.setOperatLevel(BundleUtil.getText("systemLog",
                    locale,
                    enterpriseAdminLog.getOperatLevel()));
            }
        }
        return pagelist;
    }
    
}
