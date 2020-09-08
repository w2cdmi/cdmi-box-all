package com.huawei.sharedrive.app.oauth2.dao.impl;

import org.springframework.stereotype.Service;

import com.huawei.sharedrive.app.oauth2.dao.AuthCodeDAO;
import com.huawei.sharedrive.app.oauth2.domain.AuthCode;

import pw.cdmi.box.dao.impl.AbstractDAOImpl;

@Service("authCodeDAO")
@SuppressWarnings("all")
public class AuthCodeDAOImpl extends AbstractDAOImpl implements AuthCodeDAO
{
    
    @Override
    public void create(AuthCode t)
    {
        sqlMapClientTemplate.insert("AuthCode.insert", t);
    }
    
    @Override
    public void delete(String id)
    {
        sqlMapClientTemplate.delete("AuthCode.delete", id);
    }
    
    @Override
    public AuthCode get(String id)
    {
        return (AuthCode) sqlMapClientTemplate.queryForObject("AuthCode.get", id);
    }
    
    @Override
    public AuthCode getAuthCodeByUserId(long userId, String clientId)
    {
        AuthCode code = new AuthCode();
        code.setUserId(userId);
        code.setClientId(clientId);
        return (AuthCode) sqlMapClientTemplate.queryForObject("AuthCode.getAuthCodeByUserId", code);
    }
    
    @Override
    public void update(AuthCode t)
    {
        sqlMapClientTemplate.update("AuthCode.update", t);
    }
    
}
