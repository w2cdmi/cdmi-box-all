package com.huawei.sharedrive.uam.ldapauth.manager.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.huawei.sharedrive.uam.enterpriseuser.domain.EnterpriseUser;
import com.huawei.sharedrive.uam.enterpriseuser.manager.EnterpriseUserManager;
import com.huawei.sharedrive.uam.exception.InternalServerErrorException;
import com.huawei.sharedrive.uam.ldapauth.manager.AuthServiceManager;
import com.huawei.sharedrive.uam.system.domain.TreeNode;
import com.huawei.sharedrive.uam.user.domain.User;
import com.huawei.sharedrive.uam.user.service.UserService;
import com.huawei.sharedrive.uam.user.service.impl.LdapServiceImpl;

import pw.cdmi.common.domain.enterprise.ldap.LdapDomainConfig;
import pw.cdmi.core.encrypt.HashPassword;
import pw.cdmi.core.utils.HashPasswordUtil;

@Service("localAuthServiceManager")
public class LocalAuthServiceManagerImpl implements AuthServiceManager
{
    
    private static Logger logger = LoggerFactory.getLogger(LdapServiceImpl.class);
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private EnterpriseUserManager enterpriseUserManager;
    
    @Override
    public EnterpriseUser authenticateByName(Long authServerId, String loginName, String password,
        String domain)
    
    {
        if (StringUtils.isBlank(password) || StringUtils.isBlank(loginName))
        {
            logger.error("password or loginName is null");
            return null;
        }
        EnterpriseUser enterpriseUser = enterpriseUserManager.getEUserByLoginName(loginName, domain);
        if (null == enterpriseUser)
        {
            logger.error("enterpriseUser is null " + "loginName:" + loginName);
            return null;
        }
        HashPassword hashPassword = new HashPassword();
        hashPassword.setHashPassword(enterpriseUser.getPassword());
        hashPassword.setIterations(enterpriseUser.getIterations());
        hashPassword.setSalt(enterpriseUser.getSalt());
        if (!HashPasswordUtil.validatePassword(password, hashPassword))
        {
            logger.error("password is not corrected");
            return null;
        }
        return enterpriseUser;
    }
    
    @Override
    public EnterpriseUser authenticateByMail(Long authServerId, String mail, String password)
    
    {
        if (StringUtils.isBlank(password) || StringUtils.isBlank(mail))
        {
            logger.error("password or loginName is null");
            return null;
        }
        EnterpriseUser enterpriseUser = enterpriseUserManager.getEUserByLoginName(mail, null);
        if (null == enterpriseUser)
        {
            logger.error("enterpriseUser is null " + "loginName:" + mail);
            return null;
        }
        HashPassword hashPassword = new HashPassword();
        hashPassword.setHashPassword(enterpriseUser.getPassword());
        hashPassword.setIterations(enterpriseUser.getIterations());
        hashPassword.setSalt(enterpriseUser.getSalt());
        if (!HashPasswordUtil.validatePassword(password, hashPassword))
        {
            logger.error("password is not corrected");
            return null;
        }
        return enterpriseUser;
    }
    
    @Override
    public EnterpriseUser authenticateByMobile(Long authServerId, String mobile, String password)
    
    {
        if (StringUtils.isBlank(password) || StringUtils.isBlank(mobile))
        {
            logger.error("password or loginName is null");
            return null;
        }
        EnterpriseUser enterpriseUser = enterpriseUserManager.getEUserByLoginName(mobile, null);
        if (null == enterpriseUser)
        {
            logger.error("enterpriseUser is null " + "loginName:" + mobile);
            return null;
        }
        HashPassword hashPassword = new HashPassword();
        hashPassword.setHashPassword(enterpriseUser.getPassword());
        hashPassword.setIterations(enterpriseUser.getIterations());
        hashPassword.setSalt(enterpriseUser.getSalt());
        if (!HashPasswordUtil.validatePassword(password, hashPassword))
        {
            logger.error("password is not corrected");
            return null;
        }
        return enterpriseUser;
    }
    
    public EnterpriseUser authenticateByStaff(Long authServerId, String staff, String password)
    
    {
        if (StringUtils.isBlank(password) || StringUtils.isBlank(staff))
        {
            logger.error("password or loginName is null");
            return null;
        }
        EnterpriseUser enterpriseUser = enterpriseUserManager.getEUserByLoginName(staff, null);
        if (null == enterpriseUser)
        {
            logger.error("enterpriseUser is null " + "loginName:" + staff);
            return null;
        }
        HashPassword hashPassword = new HashPassword();
        hashPassword.setHashPassword(enterpriseUser.getPassword());
        hashPassword.setIterations(enterpriseUser.getIterations());
        hashPassword.setSalt(enterpriseUser.getSalt());
        if (!HashPasswordUtil.validatePassword(password, hashPassword))
        {
            logger.error("password is not corrected");
            return null;
        }
        return enterpriseUser;
    }
    
    @Override
    public EnterpriseUser getUserByLoginName(Long authServerId, String loginName)
    {
        throw new InternalServerErrorException("unimplememt exception when getUserByLoginName");
    }
    
    @Override
    public void checkDeleteUsers(Long authServerId, Long localAuthServerId,
        List<EnterpriseUser> enterpriseUserList)
    {
        throw new InternalServerErrorException("unimplememt exception when checkDeleteUsers");
    }
    
    @Override
    public List<EnterpriseUser> searchUsers(LdapDomainConfig ldapDomainConfig, Long authServerId,
        Long enterpriseId, String searchName, int limit)
    {
        List<EnterpriseUser> userList = new ArrayList<EnterpriseUser>(limit);
        return userList;
    }
    
    @Override
    public List<EnterpriseUser> listAllUsers(LdapDomainConfig ldapDomainConfig, Long authServerId, String dn,
        boolean isSearchLimit)
    {
        throw new InternalServerErrorException("unimplememt exception when listAllUsers");
    }
    
    public List<User> getUsersByAppId(String appId)
    {
        return userService.getUsersByAppId(appId);
    }
    
    @Override
    public List<TreeNode> getTreeNode(Long authServerId, String dn, int pageNumber)
    {
        throw new InternalServerErrorException("unimplememt exception when getTreeNode");
    }
}
