package pw.cdmi.box.uam.enterpriseradminlog.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import pw.cdmi.box.dao.impl.CacheableSqlMapClientDAO;
import pw.cdmi.box.domain.Limit;
import pw.cdmi.box.uam.enterpriseradminlog.domain.EnterpriseAdminLog;
import pw.cdmi.box.uam.enterpriseradminlog.domain.QueryCondition;
import pw.cdmi.core.utils.HashTool;

@SuppressWarnings({"deprecation", "unchecked"})
@Service
public class EnterpriseAdminlogDaoImpl extends CacheableSqlMapClientDAO implements EnterpriseAdminlogDao
{
    public static final int TABLE_COUNT = 100;
    
    @Override
    public List<EnterpriseAdminLog> getEnterpriseAdminlog(QueryCondition qc, Limit limit)
    {
        
        Map<String, Object> params = new HashMap<String, Object>(4);
        params.put("tableSuffix", getTableSuffix(qc.getEnterpriseId()));
        params.put("filter", qc);
        params.put("limit", limit);
        params.put("enterpriseId", qc.getEnterpriseId());
        List<EnterpriseAdminLog> list = sqlMapClientTemplate.queryForList("EnterpriseAdminLog.getEnterpriseAdminlog",
            params);
        return list;
    }
    
    public static String getTableSuffix(long enterpriseId)
    {
        int table = (int) (HashTool.apply(String.valueOf(enterpriseId)) % TABLE_COUNT);
        return "_" + table;
    }
    
    @Override
    public int getCount(QueryCondition qc)
    {
        Map<String, Object> params = new HashMap<String, Object>(2);
        params.put("tableSuffix", getTableSuffix(qc.getEnterpriseId()));
        params.put("filter", qc);
        return (int) sqlMapClientTemplate.queryForObject("EnterpriseAdminLog.getCount", params);
    }
    
}
