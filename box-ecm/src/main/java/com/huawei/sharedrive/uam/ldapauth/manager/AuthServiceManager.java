package com.huawei.sharedrive.uam.ldapauth.manager;

import java.util.List;

import com.huawei.sharedrive.uam.enterpriseuser.domain.EnterpriseUser;
import com.huawei.sharedrive.uam.system.domain.TreeNode;

import pw.cdmi.common.domain.enterprise.ldap.LdapDomainConfig;

public interface AuthServiceManager
{
    EnterpriseUser authenticateByName(Long authServerId, String loginName, String password, String domain);
    
    EnterpriseUser authenticateByMail(Long authServerId, String email, String password);
    
    EnterpriseUser authenticateByMobile(Long authServerId, String mobile, String password);
    
    List<EnterpriseUser> searchUsers(LdapDomainConfig ldapDomainConfig, Long authServerId, Long enterpriseId,
        String searchName, int limit);
    
    List<EnterpriseUser> listAllUsers(LdapDomainConfig ldapDomainConfig, Long authServerId, String dn,
        boolean isSearchLimit);
    
    List<TreeNode> getTreeNode(Long appId, String dn, int pageNumber);
    
    EnterpriseUser getUserByLoginName(Long authServerId, String loginName);
    
    void checkDeleteUsers(Long authServerId, Long localAuthServerId, List<EnterpriseUser> enterpriseUserList);

	EnterpriseUser authenticateByStaff(Long authServerId, String staff, String password);
    
}
