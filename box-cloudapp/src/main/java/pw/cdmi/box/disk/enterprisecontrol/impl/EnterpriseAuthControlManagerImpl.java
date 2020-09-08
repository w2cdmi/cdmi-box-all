package pw.cdmi.box.disk.enterprisecontrol.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import pw.cdmi.box.disk.authserver.domain.AccountAuthserver;
import pw.cdmi.box.disk.authserver.manager.AccountAuthserverManager;
import pw.cdmi.box.disk.authserver.manager.AuthServerConfigManager;
import pw.cdmi.box.disk.authserver.manager.AuthServerManager;
import pw.cdmi.box.disk.enterprise.manager.EnterpriseAccountManager;
import pw.cdmi.box.disk.enterprise.manager.EnterpriseManager;
import pw.cdmi.box.disk.enterprisecontrol.EnterpriseAuthControlManager;
import pw.cdmi.box.domain.PageRequest;
import pw.cdmi.common.domain.AuthServer;
import pw.cdmi.common.domain.enterprise.Enterprise;
import pw.cdmi.common.domain.enterprise.EnterpriseAccount;
import pw.cdmi.common.domain.enterprise.ldap.LdapDomainConfig;
import pw.cdmi.core.exception.BadRquestException;

@Component
public class EnterpriseAuthControlManagerImpl implements EnterpriseAuthControlManager
{
    private static final String DOMAIN_NAME_SPLIT = "/";
    
    private static final String DOMAIN_NAME_SPLIT_FORWARD = "\\";
    
    public static final String LOGIN_NAME_KEY = "loginName";
    
    public static final String DOMAIN_NAME_KEY = "domainName";
    
    private static final Logger LOGGER = LoggerFactory.getLogger(EnterpriseAuthControlManager.class);
    
    public static final int INITIAL_SIZE = 10;
    
    public static final int DEFAULT_PAGE_SIZE = 20;
    
    @Autowired
    private EnterpriseManager enterpriseManager;
    
    @Autowired
    private EnterpriseAccountManager enterpriseAccountManager;
    
    @Autowired
    private AuthServerManager authServerManager;
    
    @Autowired
    private AuthServerConfigManager authServerConfigManager;
    
    @Autowired
    private AccountAuthserverManager accountAuthserverManager;
    
    @Override
    public Map<String, String> getWebDomainLoginName(String loginName)
    {
        Map<String, String> map = new HashMap<String, String>(INITIAL_SIZE);
        String domainName = "";
        String selName = loginName;
        String tempSplit = null;
        if (loginName.contains(DOMAIN_NAME_SPLIT))
        {
            tempSplit = DOMAIN_NAME_SPLIT;
        }
        else
        {
            if (loginName.contains(DOMAIN_NAME_SPLIT_FORWARD))
            {
                tempSplit = DOMAIN_NAME_SPLIT_FORWARD + DOMAIN_NAME_SPLIT_FORWARD;
            }
        }
        if (null != tempSplit)
        {
            String[] loginNameArg = loginName.split(tempSplit);
            if (loginNameArg.length < 2)
            {
                throw new BadRquestException("loginNameArg length is less than 2");
            }
            domainName = loginNameArg[0];
            selName = loginNameArg[1];
        }
        map.put(LOGIN_NAME_KEY, selName);
        map.put(DOMAIN_NAME_KEY, domainName);
        return map;
    }
    
    @Override
    public boolean isCanNtlm(String appId)
    {
        try
        {
            if (StringUtils.isBlank(appId))
            {
                LOGGER.error("appId is null");
                return false;
            }
            PageRequest request = new PageRequest();
            request.setSize(DEFAULT_PAGE_SIZE);
            List<Enterprise> enterpriseList = enterpriseManager.listEnterprise();
            if (enterpriseList == null)
            {
                LOGGER.error("enterpriseList is null");
                return false;
            }
            if (enterpriseList.size() != 1)
            {
                LOGGER.error("enterpriseList out of range size:" + enterpriseList.size());
                return false;
            }
            Enterprise enterprise = enterpriseList.get(0);
            EnterpriseAccount enterpriseAccount = enterpriseAccountManager.getByEnterpriseApp(enterprise.getId(),
                appId);
            if (null == enterpriseAccount)
            {
                LOGGER.error("enterpriseAccount is null appId:" + appId + " enterpriseId:"
                    + enterprise.getId());
                return false;
            }
            
            List<AuthServer> authServerList = authServerManager.getByEnterpriseId(enterprise.getId());
            AuthServer authServer = checkAndGetADAuthServer(authServerList);
            if (null == authServer)
            {
                return false;
            }
            AccountAuthserver accountAuthserver = accountAuthserverManager.getByAccountAuthId(enterpriseAccount.getAccountId(),
                authServer.getId());
            if (null == accountAuthserver)
            {
                LOGGER.error("authServer is unbind appId:" + appId + " enterpriseId:" + enterprise.getId()
                    + " accountId:" + enterpriseAccount.getAccountId());
                return false;
            }
            LdapDomainConfig ldapDomainConfig = authServerConfigManager.getAuthServerObject(authServer.getId());
            if (null == ldapDomainConfig || null == ldapDomainConfig.getLdapBasicConfig()
                || !ldapDomainConfig.getLdapBasicConfig().getIsNtlm())
            {
                return false;
            }
        }
        catch (IndexOutOfBoundsException e)
        {
            LOGGER.error("get authserver failed", e);
            return false;
        }
        return true;
    }
    
    private AuthServer checkAndGetADAuthServer(List<AuthServer> authServerList)
    {
        AuthServer reAuthServer = null;
        if (null == authServerList || authServerList.size() < 1)
        {
            LOGGER.error("signEnterprise authServer is null");
            return null;
        }
        int countAD = 0;
        for (AuthServer authServer : authServerList)
        {
            if (AuthServer.AUTH_TYPE_AD.equalsIgnoreCase(authServer.getType()))
            {
                countAD = countAD + 1;
                reAuthServer = authServer;
            }
        }
        if (countAD != 1)
        {
            LOGGER.error("there is too much ad authservers countAD:" + countAD);
            return null;
        }
        return reAuthServer;
    }
}
