package pw.cdmi.box.uam.system.dao.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import pw.cdmi.box.dao.impl.AbstractDAOImpl;
import pw.cdmi.box.uam.system.dao.ClientManageDAO;
import pw.cdmi.common.domain.ClientManage;

@Service("ClientManageDAO")
@SuppressWarnings("deprecation")
public class ClientManageDAOImpl extends AbstractDAOImpl implements ClientManageDAO
{
    
    @SuppressWarnings("unchecked")
    @Override
    public List<ClientManage> getAll(String appId)
    {
        return sqlMapClientTemplate.queryForList("ClientManage.getAll", appId);
    }
    
    @Override
    public void insert(ClientManage clientManage)
    {
        clientManage.setId(getNextAvailableClientId());
        clientManage.setReleaseDate(new Date());
        sqlMapClientTemplate.insert("ClientManage.insert", clientManage);
    }
    
    @Override
    public void delete(String appId, String type)
    {
        Map<String, Object> map = new HashMap<String, Object>(2);
        map.put("appId", appId);
        map.put("type", type);
        sqlMapClientTemplate.delete("ClientManage.delete", map);
    }
    
    private long getNextAvailableClientId()
    {
        Map<String, Object> map = new HashMap<String, Object>(2);
        map.put("param", "clientManageId");
        sqlMapClientTemplate.queryForObject("getNextId", map);
        long id = (Long) map.get("returnid");
        return id;
    }
    
}
