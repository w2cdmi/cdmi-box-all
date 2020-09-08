package com.huawei.sharedrive.uam.user.service.impl;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import javax.naming.CommunicationException;
import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.huawei.sharedrive.uam.system.dao.SystemConfigDAO;
import com.huawei.sharedrive.uam.user.service.LdapConfigService;

import pw.cdmi.common.domain.LdapDomainConfig;
import pw.cdmi.common.domain.SystemConfig;
import pw.cdmi.core.utils.EDToolsEnhance;

@Component
public class LdapConfigServiceImpl implements LdapConfigService
{
    private static Logger logger = LoggerFactory.getLogger(LdapConfigServiceImpl.class);
    
    private static final String CTX_FACTORY = "com.sun.jndi.ldap.LdapCtxFactory";
    
    @Autowired
    private SystemConfigDAO systemConfigDAO;
    
    @Override
    public LdapDomainConfig getLdapConfigIgnoreCache()
    {
        List<SystemConfig> itemList = systemConfigDAO.getByPrefix(null,
            null,
            LdapDomainConfig.LDAP_DOMAIN_CONFIG_PREFIX);
        LdapDomainConfig myConfig = LdapDomainConfig.buildLdapConfig(itemList);
        if (myConfig != null)
        {
            myConfig.setLdapBindAccountPassword(EDToolsEnhance.decode(myConfig.getLdapBindAccountPassword(),
                myConfig.getLdapBindAccountPasswordEncodeKey()));
            myConfig.setLdapBindAccountPasswordEncodeKey(null);
            myConfig.setNtlmPcAccountPasswd(EDToolsEnhance.decode(myConfig.getNtlmPcAccountPasswd(),
                myConfig.getNtlmPcAccountPasswdEncodeKey()));
            myConfig.setNtlmPcAccountPasswdEncodeKey(null);
        }
        return myConfig;
    }
    
    @Override
    public LdapDomainConfig getLdapConfig()
    {
        List<SystemConfig> itemList = systemConfigDAO.getByPrefix(null,
            null,
            LdapDomainConfig.LDAP_DOMAIN_CONFIG_PREFIX);
        LdapDomainConfig ldapConfigCache = LdapDomainConfig.buildLdapConfig(itemList);
        if (ldapConfigCache != null)
        {
            ldapConfigCache.setLdapBindAccountPassword(EDToolsEnhance.decode(ldapConfigCache.getLdapBindAccountPassword(),
                ldapConfigCache.getLdapBindAccountPasswordEncodeKey()));
            ldapConfigCache.setLdapBindAccountPasswordEncodeKey(null);
            ldapConfigCache.setNtlmPcAccountPasswd(EDToolsEnhance.decode(ldapConfigCache.getNtlmPcAccountPasswd(),
                ldapConfigCache.getNtlmPcAccountPasswdEncodeKey()));
            ldapConfigCache.setNtlmPcAccountPasswdEncodeKey(null);
        }
        return ldapConfigCache;
    }
    
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void saveLdapConfig(LdapDomainConfig ldapConfig)
    {
        String ldapBindAccountPassword = ldapConfig.getLdapBindAccountPassword();
        if (!StringUtils.isBlank(ldapBindAccountPassword))
        {
            Map<String, String> map = EDToolsEnhance.encode(ldapBindAccountPassword);
            ldapConfig.setLdapBindAccountPassword(map.get(EDToolsEnhance.ENCRYPT_CONTENT));
            ldapConfig.setLdapBindAccountPasswordEncodeKey(map.get(EDToolsEnhance.ENCRYPT_KEY));
            logger.info("set crypt in uam.LdapConfig");
        }
        String ntlmPcAccountPasswd = ldapConfig.getNtlmPcAccountPasswd();
        if (!StringUtils.isBlank(ntlmPcAccountPasswd))
        {
            Map<String, String> map = EDToolsEnhance.encode(ntlmPcAccountPasswd);
            ldapConfig.setNtlmPcAccountPasswd(map.get(EDToolsEnhance.ENCRYPT_CONTENT));
            ldapConfig.setNtlmPcAccountPasswdEncodeKey(map.get(EDToolsEnhance.ENCRYPT_KEY));
            logger.info("set crypt in uam.LdapConfig");
        }
        
        List<SystemConfig> domainServerList = systemConfigDAO.getByPrefix(null,
            null,
            "ldapConfig.domainControlServer.");
        for (SystemConfig systemConfig : domainServerList)
        {
            systemConfigDAO.delete(systemConfig.getId());
        }
        List<SystemConfig> itemList = ldapConfig.toConfigItem();
        for (SystemConfig systemConfig : itemList)
        {
            if (systemConfigDAO.get(systemConfig.getId()) == null)
            {
                systemConfigDAO.create(systemConfig);
            }
            else
            {
                systemConfigDAO.update(systemConfig);
            }
        }
        ldapConfig.setLdapBindAccountPassword(ldapBindAccountPassword);
        ldapConfig.setNtlmPcAccountPasswd(ntlmPcAccountPasswd);
    }
    
    /**
     * 
     * @param itemList
     * @return
     */
    @Override
    public void delDisableActiveDir(String domainControlServer)
    {
        LdapDomainConfig ldapConfigCache = getLdapConfig();
        if (ldapConfigCache == null)
        {
            return;
        }
        String[] domainControlServerStr = ldapConfigCache.getDomainControlServer();
        List<String> domainControlServerList = new ArrayList<String>(10);
        if (domainControlServerStr != null && domainControlServerStr.length > 0)
        {
            for (int i = 0; i < domainControlServerStr.length; i++)
            {
                if (domainControlServer.equals(domainControlServerStr[i]))
                {
                    continue;
                }
                domainControlServerList.add(domainControlServerStr[i]);
            }
        }
        if (!domainControlServerList.isEmpty())
        {
            domainControlServerList.toArray(new String[domainControlServerList.size()]);
        }
    }
    
    @Override
    public void refreshLdapConfig()
    {
        List<SystemConfig> itemList = systemConfigDAO.getByPrefix(null,
            null,
            LdapDomainConfig.LDAP_DOMAIN_CONFIG_PREFIX);
        LdapDomainConfig ldapConfig = LdapDomainConfig.buildLdapConfig(itemList);
        LdapDomainConfig ldapDomainConfig = null;
        String[] domainControlServerStr = null;
        if (ldapConfig != null)
        {
            ldapDomainConfig = ldapConfig;
            domainControlServerStr = ldapDomainConfig.getDomainControlServer();
        }
        if (domainControlServerStr != null && domainControlServerStr.length <= 1)
        {
            return;
        }
        List<String> domainControlServerList = new ArrayList<String>(10);
        if (domainControlServerStr != null && domainControlServerStr.length > 0)
        {
            for (int i = 0; i < domainControlServerStr.length; i++)
            {
                if (isEnableActiveDir(ldapDomainConfig, domainControlServerStr[i]))
                {
                    domainControlServerList.add(domainControlServerStr[i]);
                }
            }
        }
        if (!domainControlServerList.isEmpty())
        {
            domainControlServerList.toArray(new String[domainControlServerList.size()]);
        }
    }
    
    /**
     * 
     * @param ldapDomainConfig
     * @param domainControlServerStr
     * @return
     */
    private boolean isEnableActiveDir(LdapDomainConfig ldapDomainConfig, String domainControlServerStr)
    {
        boolean isEnableActiveDir = true;
        Hashtable<String, String> enviroment = getEnviroment(ldapDomainConfig,
            ldapDomainConfig.getLdapBindAccount(),
            ldapDomainConfig.getLdapBindAccountPassword(),
            domainControlServerStr);
        DirContext dirContext = null;
        try
        {
            dirContext = new InitialDirContext(enviroment);
        }
        catch (CommunicationException e)
        {
            isEnableActiveDir = false;
        }
        catch (NamingException e)
        {
            logger.error(e.getMessage(), e);
        }
        finally
        {
            if (dirContext != null)
            {
                try
                {
                    dirContext.close();
                }
                catch (NamingException e)
                {
                    logger.error("Fail in close LDAP connection", e);
                }
            }
        }
        return isEnableActiveDir;
    }
    
    /**
     * 
     * @param config
     * @param userName
     * @param password
     * @param domainControlServer
     * @return
     */
    @SuppressWarnings("PMD.LooseCoupling")
    private Hashtable<String, String> getEnviroment(LdapDomainConfig config, String userName,
        String password, String domainControlServer)
    {
        Hashtable<String, String> enviroment = new Hashtable<String, String>(6);
        enviroment.put(Context.INITIAL_CONTEXT_FACTORY, CTX_FACTORY);
        enviroment.put(Context.PROVIDER_URL, "ldap://" + domainControlServer + ':' + config.getLdapPort());
        enviroment.put(Context.SECURITY_AUTHENTICATION, "simple");
        enviroment.put(Context.SECURITY_PRINCIPAL, userName + '@' + config.getDnsDomainName());
        enviroment.put(Context.SECURITY_CREDENTIALS, password);
        enviroment.put("java.naming.ldap.attributes.binary", "objectSID");
        return enviroment;
        
    }
}
