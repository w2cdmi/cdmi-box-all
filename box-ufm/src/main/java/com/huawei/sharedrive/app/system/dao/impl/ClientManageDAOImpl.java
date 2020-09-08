/**
 * 
 */
package com.huawei.sharedrive.app.system.dao.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.huawei.sharedrive.app.system.dao.ClientManageDAO;

import pw.cdmi.box.dao.impl.AbstractDAOImpl;
import pw.cdmi.common.domain.ClientManage;

/**
 * @author d00199602
 * 
 */
@Service("clientManageDAO")
@SuppressWarnings("deprecation")
public class ClientManageDAOImpl extends AbstractDAOImpl implements ClientManageDAO
{
    
    @Override
    public ClientManage getClient(String type)
    {
        return (ClientManage) sqlMapClientTemplate.queryForObject("ClientManage.getClient", type);
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public List<ClientManage> getAll()
    {
        return sqlMapClientTemplate.queryForList("ClientManage.getAll");
    }
    
}
