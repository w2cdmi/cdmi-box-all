package com.huawei.sharedrive.uam.accountuser.manager.impl;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.huawei.sharedrive.uam.accountuser.domain.UserAccount;
import com.huawei.sharedrive.uam.accountuser.domain.UserAccountExtend;
import com.huawei.sharedrive.uam.accountuser.manager.ListUserAccountManager;
import com.huawei.sharedrive.uam.accountuser.manager.UserAccountManager;
import com.huawei.sharedrive.uam.authserver.manager.AuthServerManager;
import com.huawei.sharedrive.uam.enterprise.domain.SecurityRole;
import com.huawei.sharedrive.uam.enterprise.manager.SecurityRoleManager;
import com.huawei.sharedrive.uam.enterprise.service.EnterpriseAccountService;
import com.huawei.sharedrive.uam.enterpriseuser.domain.UserLdap;
import com.huawei.sharedrive.uam.enterpriseuser.manager.LdapUserManager;
import com.huawei.sharedrive.uam.exception.BusinessErrorCode;
import com.huawei.sharedrive.uam.exception.BusinessException;
import com.huawei.sharedrive.uam.httpclient.rest.UserHttpClient;
import com.huawei.sharedrive.uam.openapi.domain.RestUserCreateRequest;
import com.huawei.sharedrive.uam.user.domain.Admin;
import com.huawei.sharedrive.uam.user.service.UserService;

import pw.cdmi.box.domain.Page;
import pw.cdmi.box.domain.PageImpl;
import pw.cdmi.box.domain.PageRequest;
import pw.cdmi.box.http.request.RestRegionInfo;
import pw.cdmi.common.domain.AuthServer;
import pw.cdmi.common.domain.enterprise.EnterpriseAccount;
import pw.cdmi.core.restrpc.RestClient;
import pw.cdmi.core.utils.SqlUtils;

@Component
public class ListUserAccountManagerImpl implements ListUserAccountManager
{
    private static final Logger LOGGER = LoggerFactory.getLogger(ListUserAccountManager.class);
    
    @Autowired
    private AuthServerManager authServerManager;
    
    @Autowired
    private UserAccountManager userAccountManager;
    
    @Autowired
    private LdapUserManager ldapUserManager;
    
    @Autowired
    private EnterpriseAccountService enterpriseAccountService;
    
    @Autowired
    private UserService userService;
    
    @Resource
    private RestClient ufmClientService;
    
    @Autowired
    private SecurityRoleManager securityRoleManager;
    
    @Override
    public Page<UserAccountExtend> getPagedUserAccount(UserLdap userLdap, String appId, String filter,
        Integer status, PageRequest pageRequest)
    {
        Admin sessAdmin = (Admin) SecurityUtils.getSubject().getPrincipal();
        long enterpriseId = sessAdmin.getEnterpriseId();
        AuthServer authServer = authServerManager.getAuthServer(userLdap.getAuthServerId());
        if (null == authServer)
        {
            LOGGER.error("[userAccount] find AuthServer failed" + " enterpriseId:" + enterpriseId
                + " authServerId:" + userLdap.getAuthServerId());
            throw new BusinessException(BusinessErrorCode.INTERNAL_SERVER_ERROR, "find AuthServer failed");
        }
        EnterpriseAccount enterpriseAccount = enterpriseAccountService.getByEnterpriseApp(enterpriseId, appId);
        if (null == enterpriseAccount)
        {
            LOGGER.error("[userAccount] find enterpriseAccount failed" + " enterpriseId:" + enterpriseId
                + " appId:" + appId);
            throw new BusinessException(BusinessErrorCode.INTERNAL_SERVER_ERROR,
                "find enterpriseAccount failed");
        }
        Page<UserAccount> page = null;
        if (StringUtils.isNotBlank(userLdap.getDn()))
        {
            ldapUserManager.insertLdapUser(userLdap.getSessionId(),
                userLdap.getDn(),
                userLdap.getAuthServerId());
            page = ldapUserManager.getPagedUserAccount(userLdap,
                enterpriseId,
                enterpriseAccount.getAccountId(),
                SqlUtils.stringToSqlLikeFields(filter),
                status,
                pageRequest);
        }
        else
        {
            UserAccount userAccount = new UserAccount();
            userAccount.setEnterpriseId(enterpriseId);
            userAccount.setStatus(status);
            page = userAccountManager.getPagedUserAccount(userAccount,
                appId,
                userLdap.getAuthServerId(),
                pageRequest,
                SqlUtils.stringToSqlLikeFields(filter));
        }
        List<UserAccount> list = page.getContent();
        List<UserAccountExtend> listExtend = new ArrayList<UserAccountExtend>(10);
        SecurityRole securityRole;
        if (CollectionUtils.isNotEmpty(list))
        {
            UserHttpClient userHttpClient = new UserHttpClient(ufmClientService);
            RestUserCreateRequest user;
            UserAccountExtend userAccountExtend;
            for (UserAccount userAccount : list)
            {
                user = userHttpClient.getUserInfo(userAccount.getCloudUserId(), enterpriseAccount);
                if (user != null)
                {
                    userAccount.setSpaceUsed(user.getSpaceUsed());
                    userAccount.setSpaceQuota(user.getSpaceQuota());
                    userAccount.setFileCount(user.getFileCount());
                }
                userAccountExtend = new UserAccountExtend();
                BeanUtils.copyProperties(userAccount, userAccountExtend);
                securityRole = securityRoleManager.getById(userAccount.getRoleId());
                if (null != securityRole)
                {
                    userAccountExtend.setRoleName(securityRole.getRoleName());
                }
                else
                {
                    userAccountExtend.setRoleName(String.valueOf(userAccount.getRoleId()));
                }
                listExtend.add(userAccountExtend);
            }
        }
        Page<UserAccountExtend> pageExtend = new PageImpl<UserAccountExtend>(listExtend, pageRequest,
            page.getTotalElements());
        pageExtend.setRestRegionInfo(getRegionInfo(appId));
        return pageExtend;
    }
    
    @Override
    public List<UserAccount> getUserAccountList(UserLdap userLdap, UserAccount userAccount, String appId,
        String filter)
    {
        PageRequest pageRequest = new PageRequest();
        pageRequest.setSize(Integer.MAX_VALUE);
        pageRequest.setPage(1);
        Page<UserAccount> page = null;
        if (StringUtils.isNotBlank(userLdap.getDn()))
        {
            ldapUserManager.insertLdapUser(userLdap.getSessionId(),
                userLdap.getDn(),
                userLdap.getAuthServerId());
            page = ldapUserManager.getPagedUserAccount(userLdap,
                userAccount.getEnterpriseId(),
                userAccount.getAccountId(),
                SqlUtils.stringToSqlLikeFields(filter),
                userAccount.getStatus(),
                pageRequest);
        }
        else
        {
            UserAccount tmpUserAccount = new UserAccount();
            tmpUserAccount.setEnterpriseId(userAccount.getEnterpriseId());
            tmpUserAccount.setStatus(userAccount.getStatus());
            page = userAccountManager.getPagedUserAccount(tmpUserAccount,
                appId,
                userLdap.getAuthServerId(),
                pageRequest,
                SqlUtils.stringToSqlLikeFields(filter));
        }
        List<UserAccount> userAccountList = page.getContent();
        return userAccountList;
    }
    
    private List<RestRegionInfo> getRegionInfo(String appId)
    {
        return userService.getRegionInfo(appId);
    }
}
