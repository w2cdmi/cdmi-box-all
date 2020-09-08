/**
 * 
 */
package com.huawei.sharedrive.isystem.system.dao.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.huawei.sharedrive.isystem.system.dao.ClientManageDAO;

import pw.cdmi.box.dao.impl.AbstractDAOImpl;
import pw.cdmi.common.domain.ClientManage;

/**
 * @author d00199602
 * 
 */
@Service("ClientManageDAO")
@SuppressWarnings("deprecation")
public class ClientManageDAOImpl extends AbstractDAOImpl implements ClientManageDAO
{
    
    @SuppressWarnings("unchecked")
    @Override
    public List<ClientManage> getAll()
    {
        return sqlMapClientTemplate.queryForList("ClientManage.getAll");
    }
    
    @Override
    public void insert(ClientManage clientManage)
    {
        clientManage.setId(getNextAvailableClientId());
        clientManage.setReleaseDate(new Date());
        sqlMapClientTemplate.insert("ClientManage.insert", clientManage);
    }
    
    @Override
    public void delete(String type)
    {
        sqlMapClientTemplate.delete("ClientManage.delete", type);
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
