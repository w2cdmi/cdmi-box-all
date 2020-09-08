package com.huawei.sharedrive.app.oauth2.dao.impl;

import org.springframework.stereotype.Service;

import com.huawei.sharedrive.app.oauth2.dao.AuthClientDAO;
import com.huawei.sharedrive.app.oauth2.domain.AuthClient;

import pw.cdmi.box.dao.impl.AbstractDAOImpl;

@Service("authClientDAO")
@SuppressWarnings("all")
public class AuthClientDAOImpl extends AbstractDAOImpl implements AuthClientDAO
{
    
    @Override
    public void create(AuthClient t)
    {
        sqlMapClientTemplate.insert("AuthClient.insert", t);
    }
    
    @Override
    public void delete(String id)
    {
        sqlMapClientTemplate.delete("AuthClient.delete", id, 1);
    }
    
    @Override
    public AuthClient get(String id)
    {
        return (AuthClient) sqlMapClientTemplate.queryForObject("AuthClient.get", id);
    }
    
    @Override
    public void update(AuthClient t)
    {
        sqlMapClientTemplate.update("AuthClient.update", t);
    }
    
}
