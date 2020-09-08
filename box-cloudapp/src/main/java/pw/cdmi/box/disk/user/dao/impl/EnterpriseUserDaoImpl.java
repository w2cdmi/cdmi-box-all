package pw.cdmi.box.disk.user.dao.impl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import pw.cdmi.box.dao.impl.AbstractDAOImpl;
import pw.cdmi.box.disk.core.dao.util.HashTool;
import pw.cdmi.box.disk.user.dao.EnterpriseUserDao;
import pw.cdmi.box.disk.user.domain.EnterpriseUser;

@Service
public class EnterpriseUserDaoImpl extends AbstractDAOImpl implements EnterpriseUserDao
{
    
    private static final int TABLE_COUNT = 100;
    
    @SuppressWarnings("deprecation")
    @Override
    public EnterpriseUser get(long userId, long enterpriseId)
    {
        
        Map<String, Object> map = new HashMap<String, Object>(3);
        map.put("tableSuffix", getTableSuffix(enterpriseId));
        map.put("userId", userId);
        map.put("enterpriseId", enterpriseId);
        EnterpriseUser enterpriseUser = (EnterpriseUser) sqlMapClientTemplate.queryForObject("EnterpriseUser.get",
            map);
        return enterpriseUser;
    }
    @SuppressWarnings("deprecation")
    @Override
    public EnterpriseUser getByLoginname(String name, long enterpriseId)
    {
        
        Map<String, Object> map = new HashMap<String, Object>(3);
        map.put("tableSuffix", getTableSuffix(enterpriseId));
        map.put("name", name);
        map.put("enterpriseId", enterpriseId);
        EnterpriseUser enterpriseUser = (EnterpriseUser) sqlMapClientTemplate.queryForObject("EnterpriseUser.getByLoginname",
            map);
        return enterpriseUser;
    }
    private String getTableSuffix(long enterpriseId)
    {
        int table = (int) (HashTool.apply(String.valueOf(enterpriseId)) % TABLE_COUNT);
        return "_" + table;
    }
}
