package com.huawei.sharedrive.uam.ldapauth.service.impl;

import java.security.InvalidParameterException;
import java.security.SecureRandom;
import java.util.Hashtable;

import javax.annotation.Resource;
import javax.naming.CommunicationException;
import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.huawei.sharedrive.uam.enterpriseuser.domain.EnterpriseUser;
import com.huawei.sharedrive.uam.exception.LoginAuthFailedException;
import com.huawei.sharedrive.uam.ldapauth.service.LdapAuthService;
import com.huawei.sharedrive.uam.system.domain.TreeNode;
import com.huawei.sharedrive.uam.util.PatternRegUtil;
import com.huawei.sharedrive.uam.util.PropertiesUtils;

import pw.cdmi.common.cache.CacheClient;
import pw.cdmi.common.domain.enterprise.ldap.LdapBasicConfig;
import pw.cdmi.common.domain.enterprise.ldap.LdapDomainConfig;
import pw.cdmi.core.utils.CacheParameterUtils;

@SuppressWarnings("PMD.PreserveStackTrace")
@Service("ldapAuthService")
public class LdapAuthServiceImpl implements LdapAuthService
{
    private final static String CTX_FACTORY = "com.sun.jndi.ldap.LdapCtxFactory";
    
    public final static String LDAP_SPLIT = ";";
    
    private final static Logger LOGGER = LoggerFactory.getLogger(LdapAuthServiceImpl.class);
    
    @Resource(name = "cacheClient")
    private CacheClient cacheClient;
    
    @Override
    public LdapContext getLdapContext(LdapDomainConfig config, String domainControlServer)
        throws CommunicationException, NamingException
    {
        Hashtable<String, String> enviroment = getEnviroment(config, domainControlServer);
        LdapContext ldapContext = new InitialLdapContext(enviroment, null);
        return ldapContext;
    }
    
    @Override
    public void authenticate(LdapContext ldapContext, String userDN, String password)
    {
        try
        {
            ldapContext.addToEnvironment(Context.SECURITY_PRINCIPAL, userDN);
            
            ldapContext.addToEnvironment(Context.SECURITY_CREDENTIALS, password);
            ldapContext.reconnect(null);
        }
        catch (Exception e)
        {
            LOGGER.error("auth failed userDN:" + userDN, e);
            throw new LoginAuthFailedException();
        }
    }
    
    @Override
    public EnterpriseUser getLdapUserByLoginName(LdapDomainConfig config, LdapContext ldapContext,
        String userName, Long authServerId)
    {
        EnterpriseUser user = null;
        try
        {
            if (!PatternRegUtil.checkLdapLoginName(userName))
            {
                LOGGER.error("invalid username:" + userName + " authServerId:" + authServerId);
                return null;
            }
            String filter = config.getLdapBasicConfig()
                .getSyncFilter()
                .replace(LdapDomainConfig.AUTH_RULE_STR, userName);
            Object objectBaseDN = cacheClient.getCache(CacheParameterUtils.AUTHSERVER_BASE_DN + authServerId
                + "_" + userName);
            if (null == objectBaseDN)
            {
                user = getEnterpriseUser(config, ldapContext, filter, userName, authServerId);
            }
            else
            {
                String baseDN = (String) objectBaseDN;
                user = getLdapUserByBaseDN(config, ldapContext, filter, baseDN);
                cacheClient.setCache(CacheParameterUtils.AUTHSERVER_BASE_DN + authServerId + "_" + userName,
                    baseDN,
                    CacheParameterUtils.AUTHSERVER_BASE_DN_TIME_OUT);
            }
        }
        catch (Exception e)
        {
            LOGGER.error("[ldapauth] get ldap user failed, name:" + userName + " authServerId:"
                + authServerId, e);
        }
        return user;
    }
    
    @Override
    public EnterpriseUser getLdapUserByEmail(LdapDomainConfig config, LdapContext ldapContext, String email,
        Long authServerId)
    {
        EnterpriseUser user = null;
        try
        {
            PatternRegUtil.checkMailLegal(email);
            String filter = config.getLdapFiledMapping().getEmail() + "=" + email;
            Object objectBaseDN = cacheClient.getCache(CacheParameterUtils.AUTHSERVER_BASE_DN + authServerId
                + "_" + email);
            if (null == objectBaseDN)
            {
                String baseDNList = config.getLdapBasicConfig().getLdapBaseDN();
                String ldapSplit = PropertiesUtils.getProperty("ldap.split");
                if (StringUtils.isBlank(ldapSplit))
                {
                    ldapSplit = LDAP_SPLIT;
                }
                String[] baseDNStr = baseDNList.split(ldapSplit);
                for (int i = 0; i < baseDNStr.length; i++)
                {
                    user = getLdapUserByBaseDN(config, ldapContext, filter, baseDNStr[i]);
                    if (null != user)
                    {
                        cacheClient.setCache(CacheParameterUtils.AUTHSERVER_BASE_DN + authServerId + "_"
                            + email, baseDNStr[i], CacheParameterUtils.AUTHSERVER_BASE_DN_TIME_OUT);
                        break;
                    }
                }
            }
            else
            {
                String baseDN = (String) objectBaseDN;
                user = getLdapUserByBaseDN(config, ldapContext, filter, baseDN);
                cacheClient.setCache(CacheParameterUtils.AUTHSERVER_BASE_DN + authServerId + "_" + email,
                    baseDN,
                    CacheParameterUtils.AUTHSERVER_BASE_DN_TIME_OUT);
            }
        }
        catch (Exception e)
        {
            LOGGER.error("[ldapauth] get ldap user failed, authServerId:" + authServerId, e);
        }
        return user;
    }
    
    @Override
    public String getDomainControlServerCache(String domainControlServer, Long authServerId)
    {
        Object obj = cacheClient.getCache(CacheParameterUtils.AUTHSERVER_DOMAIN_CONTROL_SERVER + authServerId);
        String domainControlServerCache = String.valueOf(obj == null ? "" : obj);
        String ldapSplit = PropertiesUtils.getProperty("ldap.split");
        if (StringUtils.isBlank(ldapSplit))
        {
            ldapSplit = LDAP_SPLIT;
        }
        if (StringUtils.isBlank(domainControlServerCache))
        {
            cacheClient.setCache(CacheParameterUtils.AUTHSERVER_DOMAIN_CONTROL_SERVER + authServerId,
                domainControlServer,
                CacheParameterUtils.AUTHSERVER_DOMAIN_CONTROL_SERVER_TIME_OUT);
            domainControlServerCache = domainControlServer;
        }
        String[] domainControlServerStr = domainControlServerCache.split(ldapSplit);
        int serverNumber = new SecureRandom().nextInt(domainControlServerStr.length);
        return domainControlServerStr[serverNumber];
    }
    
    private EnterpriseUser getLdapUserByBaseDN(LdapDomainConfig config, LdapContext ldapContext,
        String filter, String baseDN)
    {
        EnterpriseUser enterpriseUser = null;
        try
        {
            SearchControls ctrl = new SearchControls();
            ctrl.setSearchScope(SearchControls.SUBTREE_SCOPE);
            String[] returnedAtts = {config.getLdapFiledMapping().getUniqueKey(),
                config.getLdapFiledMapping().getDescription(), config.getLdapFiledMapping().getEmail(),
                config.getLdapFiledMapping().getLoginName(), config.getLdapFiledMapping().getName()};
            ctrl.setReturningAttributes(returnedAtts);
            NamingEnumeration<SearchResult> enu = ldapContext.search(TreeNode.formatDn(ldapContext, baseDN),
                filter,
                ctrl);
            SearchResult sr;
            Attributes attributes;
            String userDN;
            while (enu.hasMoreElements())
            {
                sr = enu.next();
                attributes = sr.getAttributes();
                userDN = sr.getNameInNamespace();
                enterpriseUser = (EnterpriseUser) getUserByAttri(config, attributes);
                if (enterpriseUser != null && StringUtils.isNotBlank(userDN))
                {
                    enterpriseUser.setUserDn(userDN);
                }
            }
        }
        catch (NamingException e)
        {
            LOGGER.warn("find ad user failed! name:" + filter + " basedn:" + baseDN, e);
        }
        catch (Exception e)
        {
            LOGGER.warn("find ad user failed! name:" + filter + " basedn:" + baseDN, e);
        }
        return enterpriseUser;
    }
    
    @Override
    public EnterpriseUser getUserByAttri(LdapDomainConfig config, Attributes attributes)
        throws NamingException
    {
        EnterpriseUser user = new EnterpriseUser();
        Attribute atrSid;
        String sid;
        if ("objectSid".equals(config.getLdapFiledMapping().getUniqueKey()))
        {
            atrSid = attributes.get("objectSid");
            if (null == atrSid)
            {
                return null;
            }
            sid = convertBinarySidToString((byte[]) (atrSid.get()));
        }
        else
        {
            atrSid = attributes.get(config.getLdapFiledMapping().getUniqueKey());
            if (null == atrSid)
            {
                return null;
            }
            sid = atrSid.get().toString();
        }
        user.setObjectSid(sid);
        
        atrSid = attributes.get(config.getLdapFiledMapping().getLoginName());
        if (null == atrSid)
        {
            return null;
        }
        user.setName(atrSid.get().toString());
        atrSid = attributes.get(config.getLdapFiledMapping().getName());
        if (null == atrSid)
        {
            return null;
        }
        user.setAlias(atrSid.get().toString());
        if (StringUtils.isNotBlank(config.getLdapFiledMapping().getEmail()))
        {
            atrSid = attributes.get(config.getLdapFiledMapping().getEmail());
            if (null != atrSid)
            {
                user.setEmail(atrSid.get().toString());
            }
        }
        if (StringUtils.isNotBlank(config.getLdapFiledMapping().getDescription()))
        {
            atrSid = attributes.get(config.getLdapFiledMapping().getDescription());
            if (null != atrSid)
            {
                user.setDescription(atrSid.get().toString());
            }
        }
        return user;
    }
    
    private String convertBinarySidToString(byte[] sid)
    {
        StringBuffer sidAsString = new StringBuffer("S-");
        sidAsString.append(sid[0]).append('-');
        StringBuffer sb = new StringBuffer();
        for (int t = 2; t <= 7; t++)
        {
            sb.append(Integer.toHexString(sid[t] & 255));
        }
        
        try
        {
            sidAsString.append(Long.parseLong(sb.toString(), 16));
        }
        catch (NumberFormatException e)
        {
            throw new InvalidParameterException(e.getMessage());
        }
        int count = sid[1];
        int currSubAuthOffset;
        try
        {
            for (int i = 0; i < count; i++)
            {
                currSubAuthOffset = i * 4;
                sb.setLength(0);
                sb.append(toHexString((byte) (sid[11 + currSubAuthOffset] & 255)));
                sb.append(toHexString((byte) (sid[10 + currSubAuthOffset] & 255)));
                sb.append(toHexString((byte) (sid[9 + currSubAuthOffset] & 255)));
                sb.append(toHexString((byte) (sid[8 + currSubAuthOffset] & 255)));
                sidAsString.append('-').append(Long.parseLong(sb.toString(), 16));
            }
        }
        catch (NumberFormatException e)
        {
            throw new InvalidParameterException(e.getMessage());
        }
        return sidAsString.toString();
    }
    
    private String toHexString(byte b)
    {
        String hexString = Integer.toHexString(b & 255);
        if (hexString.length() % 2 != 0)
        {
            hexString = '0' + hexString;
        }
        return hexString;
    }
    
    @SuppressWarnings("PMD.LooseCoupling")
    private Hashtable<String, String> getEnviroment(LdapDomainConfig config, String domainControlServer)
    {
        Hashtable<String, String> enviroment = new Hashtable<String, String>(10);
        enviroment.put(Context.INITIAL_CONTEXT_FACTORY, CTX_FACTORY);
        enviroment.put(Context.PROVIDER_URL, "ldap://" + domainControlServer + ':'
            + config.getLdapBasicConfig().getLdapPort());
        enviroment.put(Context.SECURITY_AUTHENTICATION, "simple");
        enviroment.put(Context.SECURITY_PRINCIPAL, config.getLdapBasicConfig().getLdapBindAccount());
        enviroment.put(Context.SECURITY_CREDENTIALS, config.getLdapBasicConfig().getLdapBindAccountPassword());
        enviroment.put("java.naming.ldap.attributes.binary", "objectSID");
        enviroment.put(Context.REFERRAL, "follow");
        enviroment.put("com.sun.jndi.ldap.connect.pool", PropertiesUtils.getProperty("ldap.pool"));
        boolean isCloseProtocol = LdapBasicConfig.PROTOCOL_CLOSE.equalsIgnoreCase(config.getLdapBasicConfig()
            .getSecurityProtocol());
        if (!isCloseProtocol && StringUtils.isNotBlank(config.getLdapBasicConfig().getSecurityProtocol()))
        {
            enviroment.put(Context.SECURITY_PROTOCOL, LdapBasicConfig.PROTOCOL_SSL);
        }
        return enviroment;
    }
    
    private EnterpriseUser getEnterpriseUser(LdapDomainConfig config, LdapContext ldapContext, String filter,
        String userName, Long authServerId)
    {
        EnterpriseUser user = null;
        String baseDNList = config.getLdapBasicConfig().getLdapBaseDN();
        String ldapSplit = PropertiesUtils.getProperty("ldap.split");
        if (StringUtils.isBlank(ldapSplit))
        {
            ldapSplit = LDAP_SPLIT;
        }
        String[] baseDNStr = baseDNList.split(ldapSplit);
        for (int i = 0; i < baseDNStr.length; i++)
        {
            user = getLdapUserByBaseDN(config, ldapContext, filter, baseDNStr[i]);
            if (null != user)
            {
                cacheClient.setCache(CacheParameterUtils.AUTHSERVER_BASE_DN + authServerId + "_" + userName,
                    baseDNStr[i],
                    CacheParameterUtils.AUTHSERVER_BASE_DN_TIME_OUT);
                break;
            }
        }
        return user;
    }
    
}
