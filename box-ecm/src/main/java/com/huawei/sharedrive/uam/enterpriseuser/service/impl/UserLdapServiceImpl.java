package com.huawei.sharedrive.uam.enterpriseuser.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.huawei.sharedrive.uam.accountuser.domain.UserAccount;
import com.huawei.sharedrive.uam.enterpriseuser.dao.UserLdapDAO;
import com.huawei.sharedrive.uam.enterpriseuser.domain.EnterpriseUser;
import com.huawei.sharedrive.uam.enterpriseuser.domain.UserLdap;
import com.huawei.sharedrive.uam.enterpriseuser.service.UserLdapService;

import pw.cdmi.box.domain.Page;
import pw.cdmi.box.domain.PageImpl;
import pw.cdmi.box.domain.PageRequest;

@Component
public class UserLdapServiceImpl implements UserLdapService
{
    
    @Autowired
    private UserLdapDAO userDAO;
    
    @Override
    public void insertList(String sessionId, String dn, Long authServerId, List<EnterpriseUser> userList)
    {
        List<UserLdap> userLdapList = new ArrayList<UserLdap>(10);
        UserLdap userLdap;
        for (EnterpriseUser user : userList)
        {
            userLdap = new UserLdap();
            userLdap.setSessionId(sessionId);
            userLdap.setDn(dn);
            userLdap.setLoginName(user.getName());
            userLdap.setAuthServerId(authServerId);
            userLdapList.add(userLdap);
        }
        userDAO.insertList(userLdapList);
    }
    
    @Override
    public List<UserLdap> getByUserLdap(String sessionId, String dn, Long authServerId)
    {
        UserLdap userLdap = new UserLdap();
        userLdap.setSessionId(sessionId);
        userLdap.setDn(dn);
        userLdap.setAuthServerId(authServerId);
        return userDAO.getByUserLdap(userLdap);
    }
    
    @Override
    public Page<EnterpriseUser> getPagedUser(UserLdap userLdap, EnterpriseUser enterpriseUser,
        PageRequest pageRequest)
    {
        int total = userDAO.getFilterdCount(userLdap, enterpriseUser);
        List<EnterpriseUser> content = userDAO.getFilterd(userLdap,
            enterpriseUser,
            pageRequest.getOrder(),
            pageRequest.getLimit());
        Page<EnterpriseUser> page = new PageImpl<EnterpriseUser>(content, pageRequest, total);
        return page;
    }
    
    @SuppressWarnings("PMD.ExcessiveParameterList")
    @Override
    public Page<UserAccount> getPagedUserAccount(UserLdap userLdap, Long enterpriseId, Long accountId,
        String filter, Integer status, PageRequest pageRequest)
    {
        int total = userDAO.getUserAccountFilterdCount(userLdap, enterpriseId, accountId, filter, status);
        List<UserAccount> content = userDAO.getUserAccountFilterd(userLdap,
            enterpriseId,
            accountId,
            filter,
            status,
            pageRequest.getOrder(),
            pageRequest.getLimit());
        Page<UserAccount> page = new PageImpl<UserAccount>(content, pageRequest, total);
        return page;
    }
    
    @Override
    public List<String> getSessionList()
    {
        return userDAO.getSessionList();
    }
    
    @Override
    public void deleteBySessionId(String sessionId)
    {
        userDAO.deleteBySessionId(sessionId);
    }
    
    @Override
    public List<String> getFilterdId(UserLdap filter)
    {
        return userDAO.getFilterdId(filter);
    }
}
