package com.huawei.sharedrive.uam.enterpriseuser.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.huawei.sharedrive.uam.accountuser.dao.impl.UserAccountDaoImpl;
import com.huawei.sharedrive.uam.accountuser.domain.UserAccount;
import com.huawei.sharedrive.uam.enterpriseuser.dao.UserLdapDAO;
import com.huawei.sharedrive.uam.enterpriseuser.domain.EnterpriseUser;
import com.huawei.sharedrive.uam.enterpriseuser.domain.UserLdap;

import pw.cdmi.box.dao.impl.CacheableSqlMapClientDAO;
import pw.cdmi.box.domain.Limit;
import pw.cdmi.box.domain.Order;

@SuppressWarnings({"unchecked", "deprecation"})
@Service
public class UserLdapDAOImpl extends CacheableSqlMapClientDAO implements UserLdapDAO
{
    @Override
    public void insertList(List<UserLdap> userLdapList)
    {
        for (UserLdap tempUserLdap : userLdapList)
        {
            tempUserLdap.setId(UUID.randomUUID().toString().replaceAll("-", ""));
        }
        sqlMapClientTemplate.insert("UserLdap.insertUserList", userLdapList);
    }
    
    @Override
    public List<UserLdap> getByUserLdap(UserLdap userLdap)
    {
        return sqlMapClientTemplate.queryForList("UserLdap.getByUserLdap", userLdap);
    }
    
    @Override
    public List<EnterpriseUser> getFilterd(UserLdap userLdap, EnterpriseUser enterpriseUser, Order order,
        Limit limit)
    {
        Map<String, Object> map = new HashMap<String, Object>(8);
        map.put("sessionId", userLdap.getSessionId());
        map.put("dn", userLdap.getDn());
        map.put("authServerId", userLdap.getAuthServerId());
        map.put("enterpriseId", enterpriseUser.getEnterpriseId());
        map.put("filter", enterpriseUser.getName());
        String tableSuffix = EnterpriseUserDaoImpl.getTableSuffix(enterpriseUser.getEnterpriseId());
        map.put("tableSuffix", tableSuffix);
        map.put("order", order);
        map.put("limit", limit);
        return sqlMapClientTemplate.queryForList("UserLdap.getFilterd", map);
    }
    
    @Override
    public int getFilterdCount(UserLdap userLdap, EnterpriseUser enterpriseUser)
    {
        Map<String, Object> map = new HashMap<String, Object>(6);
        map.put("sessionId", userLdap.getSessionId());
        map.put("dn", userLdap.getDn());
        map.put("authServerId", userLdap.getAuthServerId());
        map.put("enterpriseId", enterpriseUser.getEnterpriseId());
        map.put("filter", enterpriseUser.getName());
        String tableSuffix = EnterpriseUserDaoImpl.getTableSuffix(enterpriseUser.getEnterpriseId());
        map.put("tableSuffix", tableSuffix);
        return (Integer) sqlMapClientTemplate.queryForObject("UserLdap.getFilterdCount", map);
    }
    
    @SuppressWarnings("PMD.ExcessiveParameterList")
    @Override
    public List<UserAccount> getUserAccountFilterd(UserLdap userLdap, Long enterpriseId, Long accountId,
        String filter, Integer status, Order order, Limit limit)
    {
        Map<String, Object> map = new HashMap<String, Object>(11);
        map.put("sessionId", userLdap.getSessionId());
        map.put("dn", userLdap.getDn());
        map.put("authServerId", userLdap.getAuthServerId());
        map.put("enterpriseId", enterpriseId);
        map.put("filter", filter);
        map.put("accountId", accountId);
        map.put("status", status);
        String tableSuffix = EnterpriseUserDaoImpl.getTableSuffix(enterpriseId);
        String accountTableSuffix = UserAccountDaoImpl.getTableSuffix(accountId);
        map.put("tableSuffix", tableSuffix);
        map.put("accountTableSuffix", accountTableSuffix);
        if (order == null)
        {
            order = new Order();
            order.setDesc(true);
            order.setField("createdAt");
        }
        map.put("order", order);
        map.put("limit", limit);
        return sqlMapClientTemplate.queryForList("UserLdap.getUserAccountFilterd", map);
    }
    
    @Override
    public int getUserAccountFilterdCount(UserLdap userLdap, Long enterpriseId, Long accountId,
        String filter, Integer status)
    {
        Map<String, Object> map = new HashMap<String, Object>(9);
        map.put("sessionId", userLdap.getSessionId());
        map.put("dn", userLdap.getDn());
        map.put("authServerId", userLdap.getAuthServerId());
        map.put("enterpriseId", enterpriseId);
        map.put("filter", filter);
        map.put("accountId", accountId);
        map.put("status", status);
        String tableSuffix = EnterpriseUserDaoImpl.getTableSuffix(enterpriseId);
        String accountTableSuffix = UserAccountDaoImpl.getTableSuffix(accountId);
        map.put("tableSuffix", tableSuffix);
        map.put("accountTableSuffix", accountTableSuffix);
        return (Integer) sqlMapClientTemplate.queryForObject("UserLdap.getUserAccountFilterdCount", map);
    }
    
    @Override
    public List<String> getSessionList()
    {
        return sqlMapClientTemplate.queryForList("UserLdap.getSessionList");
    }
    
    @Override
    public void deleteBySessionId(String sessionId)
    {
        sqlMapClientTemplate.delete("UserLdap.deleteBysessionId", sessionId);
    }
    
    @Override
    public List<String> getFilterdId(UserLdap filter)
    {
        return sqlMapClientTemplate.queryForList("UserLdap.getFilterdId", filter);
    }
}
