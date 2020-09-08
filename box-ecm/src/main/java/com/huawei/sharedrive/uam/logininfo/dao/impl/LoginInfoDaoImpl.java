package com.huawei.sharedrive.uam.logininfo.dao.impl;

import java.util.List;
import java.util.Locale;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import com.huawei.sharedrive.uam.core.dao.util.HashTool;
import com.huawei.sharedrive.uam.logininfo.dao.LoginInfoDao;
import com.huawei.sharedrive.uam.logininfo.domain.LoginInfo;

import pw.cdmi.box.dao.impl.CacheableSqlMapClientDAO;

@SuppressWarnings({"deprecation", "unchecked"})
@Service
public class LoginInfoDaoImpl extends CacheableSqlMapClientDAO implements LoginInfoDao
{
    
    private static final int TABLE_COUNT = 100;
    
    @Override
    public void create(LoginInfo loginInfo)
    {
        loginInfo.setTableSuffix(getTableSuffix(loginInfo.getLoginName()));
        sqlMapClientTemplate.insert("LoginInfo.insert", loginInfo);
    }
    
    @Override
    public List<LoginInfo> getByLoginName(String loginName)
    {
        LoginInfo loginInfo = new LoginInfo();
        loginInfo.setTableSuffix(getTableSuffix(loginName));
        loginInfo.setLoginName(loginName);
        return sqlMapClientTemplate.queryForList("LoginInfo.getByLoginName", loginInfo);
    }
    
    @Override
    public LoginInfo getByNameDomain(String loginName, String domain)
    {
        LoginInfo loginInfo = new LoginInfo();
        loginInfo.setTableSuffix(getTableSuffix(loginName));
        loginInfo.setLoginName(loginName);
        loginInfo.setDomainName(domain);
        return (LoginInfo) sqlMapClientTemplate.queryForObject("LoginInfo.getByNameDomain", loginInfo);
    }
    
    @Override
    public void delByNameEnterId(String loginName, Long enterpriseId)
    {
        if (StringUtils.isNotBlank(loginName) && null != enterpriseId)
        {
            LoginInfo loginInfo = new LoginInfo();
            loginInfo.setLoginName(loginName);
            loginInfo.setEnterpriseId(enterpriseId);
            loginInfo.setTableSuffix(getTableSuffix(loginInfo.getLoginName()));
            sqlMapClientTemplate.delete("LoginInfo.delByNameEnterId", loginInfo);
        }
    }
    
    private String getTableSuffix(String loginName)
    {
        String tableSuffix;
        int table = (int) (HashTool.apply(loginName.toLowerCase(Locale.ENGLISH)) % TABLE_COUNT);
        tableSuffix = "_" + table;
        return tableSuffix;
    }
    
}
