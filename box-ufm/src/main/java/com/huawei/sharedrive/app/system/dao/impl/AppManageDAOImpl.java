/**
 * 
 */
package com.huawei.sharedrive.app.system.dao.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.huawei.sharedrive.app.system.dao.AppManageDAO;
import com.huawei.sharedrive.app.system.domain.RegistApp;

import pw.cdmi.box.dao.impl.AbstractDAOImpl;

/**
 * @author s00108907
 * 
 */
@Service
@SuppressWarnings("deprecation")
public class AppManageDAOImpl extends AbstractDAOImpl implements AppManageDAO
{
    
    /*
     * (non-Javadoc)
     * 
     * @see com.huawei.sharedrive.app.core.dao.BaseDAO#create(java.lang.Object)
     */
    @Override
    public void create(RegistApp registApp)
    {
        sqlMapClientTemplate.insert("RegistApp.insert", registApp);
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see com.huawei.sharedrive.app.core.dao.BaseDAO#delete(java.io.Serializable)
     */
    @Override
    public void delete(String id)
    {
        sqlMapClientTemplate.delete("RegistApp.delete", id);
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see com.huawei.sharedrive.app.core.dao.BaseDAO#get(java.io.Serializable)
     */
    @Override
    public RegistApp get(String id)
    {
        return (RegistApp) sqlMapClientTemplate.queryForObject("RegistApp.get", id);
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public List<RegistApp> getAll()
    {
        return sqlMapClientTemplate.queryForList("RegistApp.getAll");
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see com.huawei.sharedrive.app.core.dao.BaseDAO#update(java.lang.Object)
     */
    @Override
    public void update(RegistApp registApp)
    {
        sqlMapClientTemplate.update("RegistApp.update", registApp);
    }
    
}
