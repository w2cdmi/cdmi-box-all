package pw.cdmi.box.uam.user.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import pw.cdmi.box.dao.impl.AbstractDAOImpl;
import pw.cdmi.box.uam.user.dao.AdminAppPermissionDAO;
import pw.cdmi.box.uam.user.domain.AdminAppPermission;

@Service
@SuppressWarnings({"deprecation", "unchecked"})
public class AdminAppPermissionDAOImpl extends AbstractDAOImpl implements AdminAppPermissionDAO
{
    @Override
    public List<String> getAppByAdminId(Long adminId)
    {
        return sqlMapClientTemplate.queryForList("AdminAppPermission.getAppByAdminId", adminId);
    }
    
    @Override
    public List<Long> getAdminByAppId(String appId)
    {
        return sqlMapClientTemplate.queryForList("AdminAppPermission.getAdminByAppId", appId);
    }
    
    @Override
    public void save(AdminAppPermission adminAppPermission)
    {
        sqlMapClientTemplate.insert("AdminAppPermission.insert", adminAppPermission);
    }
    
    @Override
    public void delete(String appId, Long adminId)
    {
        Map<String, Object> map = new HashMap<String, Object>(2);
        map.put("appId", appId);
        map.put("adminId", adminId);
        sqlMapClientTemplate.delete("AdminAppPermission.delete", map);
    }
    
    @Override
    public void deleteByAdminId(Long adminId)
    {
        sqlMapClientTemplate.delete("AdminAppPermission.deleteByAdminId", adminId);
    }
    
}
