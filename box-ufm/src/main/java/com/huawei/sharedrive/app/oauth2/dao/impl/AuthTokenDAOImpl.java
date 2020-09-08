package com.huawei.sharedrive.app.oauth2.dao.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.huawei.sharedrive.app.oauth2.dao.AuthTokenDAO;
import com.huawei.sharedrive.app.oauth2.domain.AuthToken;

import pw.cdmi.box.dao.impl.AbstractDAOImpl;

@Service("authTokenDAO")
@SuppressWarnings("all")
public class AuthTokenDAOImpl extends AbstractDAOImpl implements AuthTokenDAO
{
    
    @Override
    public void create(AuthToken t)
    {
        sqlMapClientTemplate.insert("AuthToken.insert", t);
    }
    
    @Override
    public void delete(String id)
    {
        sqlMapClientTemplate.delete("AuthToken.delete", id, 1);
    }
    
    @Override
    public int deleteDeviceSNToken(String deviceSN)
    {
        return sqlMapClientTemplate.delete("AuthToken.deleteDeviceSNToken", deviceSN);
    }
    
    @Override
    public int deleteExpiredToken()
    {
        return sqlMapClientTemplate.delete("AuthToken.deleteExpiredToken");
    }
    
    @Override
    public AuthToken get(String id)
    {
        return (AuthToken) sqlMapClientTemplate.queryForObject("AuthToken.get", id);
    }
    
    @Override
    public AuthToken getByRefreshToken(String refreshToken)
    {
        return (AuthToken) sqlMapClientTemplate.queryForObject("AuthToken.getByRefreshToken", refreshToken);
    }
    
    @Override
    public List<AuthToken> getExpiredToken(long offset)
    {
        return sqlMapClientTemplate.queryForList("AuthToken.getExpiredToken", offset);
    }
    
    @Override
    public void update(AuthToken t)
    {
        sqlMapClientTemplate.update("AuthToken.update", t, 1);
    }
    
}
