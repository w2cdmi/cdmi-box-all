package com.huawei.sharedrive.uam.enterpriseuser.dao;

import java.util.List;

import com.huawei.sharedrive.uam.accountuser.domain.UserAccount;
import com.huawei.sharedrive.uam.enterpriseuser.domain.EnterpriseUser;
import com.huawei.sharedrive.uam.enterpriseuser.domain.UserLdap;

import pw.cdmi.box.domain.Limit;
import pw.cdmi.box.domain.Order;

public interface UserLdapDAO
{
    
    void insertList(List<UserLdap> userLdapList);
    
    List<UserLdap> getByUserLdap(UserLdap userLdap);
    
    List<EnterpriseUser> getFilterd(UserLdap userLdap, EnterpriseUser enterpriseUser, Order order, Limit limit);
    
    int getFilterdCount(UserLdap userLdap, EnterpriseUser enterpriseUser);
    
    List<String> getSessionList();
    
    List<String> getFilterdId(UserLdap filter);
    
    void deleteBySessionId(String sessionId);
    
    @SuppressWarnings("PMD.ExcessiveParameterList")
    List<UserAccount> getUserAccountFilterd(UserLdap userLdap, Long enterpriseId, Long accountId,
        String filter, Integer status, Order order, Limit limit);
    
    int getUserAccountFilterdCount(UserLdap userLdap, Long enterpriseId, Long accountId, String filter,
        Integer status);
}