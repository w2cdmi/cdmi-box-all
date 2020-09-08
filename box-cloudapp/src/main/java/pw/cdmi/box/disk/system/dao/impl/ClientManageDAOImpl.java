package pw.cdmi.box.disk.system.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import pw.cdmi.box.dao.impl.AbstractDAOImpl;
import pw.cdmi.box.disk.system.dao.ClientManageDAO;
import pw.cdmi.common.domain.ClientManage;

@Service("ClientManageDAO")
@SuppressWarnings("deprecation")
public class ClientManageDAOImpl extends AbstractDAOImpl implements ClientManageDAO
{
    
    @Override
    public ClientManage getClient(String appId, String type)
    {
        Map<String, Object> map = new HashMap<String, Object>(2);
        map.put("appId", appId);
        map.put("type", type);
        return (ClientManage) sqlMapClientTemplate.queryForObject("ClientManage.getClient", map);
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public List<ClientManage> getAll(String appId)
    {
        return sqlMapClientTemplate.queryForList("ClientManage.getAll", appId);
    }
    
    @Override
    public ClientManage getVersionInfoByType(String appId, String type)
    {
        Map<String, Object> map = new HashMap<String, Object>(2);
        map.put("appId", appId);
        map.put("type", type);
        return (ClientManage) sqlMapClientTemplate.queryForObject("ClientManage.getVersionInfoByType", map);
    }
    
    @Override
    public ClientManage getClientManageByVer(String type, String version)
    {
        Map<String, Object> map = new HashMap<String, Object>(2);
        map.put("version", version);
        map.put("type", type);
        return (ClientManage) sqlMapClientTemplate.queryForObject("ClientManage.getClientManageByVer", map);
    }
    
}
