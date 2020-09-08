package pw.cdmi.box.uam.enterpriseradminlog.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pw.cdmi.box.domain.Limit;
import pw.cdmi.box.uam.enterpriseradminlog.dao.EnterpriseAdminlogDao;
import pw.cdmi.box.uam.enterpriseradminlog.domain.EnterpriseAdminLog;
import pw.cdmi.box.uam.enterpriseradminlog.domain.QueryCondition;

@Service
public class EnterpriseAdminlogServiceImpl implements EnterpriseAdminlogService
{
    @Autowired
    private EnterpriseAdminlogDao logDao;
    
    @Override
    public List<EnterpriseAdminLog> queryList(QueryCondition qc, Limit limit)
    {
        return logDao.getEnterpriseAdminlog(qc, limit);
    }
    
    @Override
    public int getCount(QueryCondition qc)
    {
        return logDao.getCount(qc);
    }
    
}
