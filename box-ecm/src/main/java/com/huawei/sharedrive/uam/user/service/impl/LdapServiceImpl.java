package com.huawei.sharedrive.uam.user.service.impl;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Hashtable;

import javax.naming.CommunicationException;
import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.huawei.common.ntlmv2.liferay.NetlogonConnection;
import com.huawei.common.ntlmv2.liferay.NtlmLogonException;
import com.huawei.common.ntlmv2.liferay.NtlmServiceAccount;
import com.huawei.sharedrive.uam.exception.LoginAuthFailedException;
import com.huawei.sharedrive.uam.oauth2.domain.UserToken;
import com.huawei.sharedrive.uam.user.service.LdapConfigService;
import com.huawei.sharedrive.uam.user.service.LdapService;

import pw.cdmi.common.domain.LdapDomainConfig;

@Component
public class LdapServiceImpl implements LdapService
{
    
    private final static String CTX_FACTORY = "com.sun.jndi.ldap.LdapCtxFactory";
    
    private static Logger logger = LoggerFactory.getLogger(LdapServiceImpl.class);
    
    @Autowired
    private LdapConfigService ldapConfigService;
    
    /**
     * 
     * @param userName
     * @param password
     * @param objectSid
     * @return
     * @throws LoginAuthFailedException
     */
    @Override
    public UserToken authenticate(String loginName, String password) throws LoginAuthFailedException
    {
        LdapDomainConfig config = ldapConfigService.getLdapConfig();
        if (config == null)
        {
            return null;
        }
        UserToken user = null;
        Hashtable<String, String> enviroment = getEnviroment(config, loginName, password);
        DirContext dirContext;
        try
        {
            dirContext = new InitialDirContext(enviroment);
            try
            {
                user = getLdapUser(config, dirContext, loginName);
            }
            finally
            {
                dirContext.close();
            }
        }
        catch (CommunicationException e)
        {
            String providerUrl = enviroment.get(Context.PROVIDER_URL);
            providerUrl = providerUrl.replace("ldap://", "");
            providerUrl = providerUrl.replace(':' + String.valueOf(config.getLdapPort()), "");
            ldapConfigService.delDisableActiveDir(providerUrl);
        }
        catch (NamingException e)
        {
            logger.error(e.getMessage(), e);
        }
        return user;
    }
    
    @Override
    public boolean checkLdapConfig(LdapDomainConfig ldapConfig)
    {
        Hashtable<String, String> enviroment = getEnviroment(ldapConfig,
            ldapConfig.getLdapBindAccount(),
            ldapConfig.getLdapBindAccountPassword());
        DirContext dirContext = null;
        try
        {
            dirContext = new InitialDirContext(enviroment);
            dirContext.close();
            return true;
        }
        catch (NamingException e)
        {
            logger.error("Fail in auth LDAP connection", e);
        }
        return false;
    }
    
    @Override
    public boolean checkNtlmServer(LdapDomainConfig ldapConfig)
    {
        NetlogonConnection connection = null;
        try
        {
            SecureRandom random = new SecureRandom();
            connection = new NetlogonConnection();
            connection.connect(ldapConfig.getDomainControlServer()[ldapConfig.getCurrentIndex()],
                ldapConfig.getDomainControlNetBisoName(ldapConfig.getDomainControlServer()[ldapConfig.getCurrentIndex()]),
                new NtlmServiceAccount(ldapConfig.getNtlmConnectPcAccount(),
                    ldapConfig.getNtlmPcAccountPasswd()),
                random);
            return true;
        }
        catch (NtlmLogonException e)
        {
            logger.error("connect domain control fail", e);
        }
        catch (NoSuchAlgorithmException e)
        {
            logger.error("connect domain control fail", e);
        }
        catch (IOException e)
        {
            logger.error("connect domain control fail", e);
        }
        finally
        {
            if (connection != null)
            {
                try
                {
                    connection.disconnect();
                }
                catch (IOException e)
                {
                    logger.error("close domain control conn fail", e);
                }
            }
        }
        return false;
    }
    
    @Override
    public UserToken getLdapUserByLoginName(String loginName)
    {
        LdapDomainConfig config = ldapConfigService.getLdapConfig();
        if (config == null)
        {
            return null;
        }
        UserToken user = null;
        Hashtable<String, String> enviroment = getEnviroment(config,
            config.getLdapBindAccount(),
            config.getLdapBindAccountPassword());
        DirContext dirContext = null;
        try
        {
            dirContext = new InitialDirContext(enviroment);
            user = getLdapUser(config, dirContext, loginName);
        }
        catch (CommunicationException e)
        {
            String providerUrl = enviroment.get(Context.PROVIDER_URL);
            providerUrl = providerUrl.replace("ldap://", "");
            providerUrl = providerUrl.replace(':' + String.valueOf(config.getLdapPort()), "");
            ldapConfigService.delDisableActiveDir(providerUrl);
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
        return user;
    }
    
    /**
     * 
     * @param sid
     * @return
     */
    private String convertBinarySidToString(byte[] sid)
    {
        StringBuffer sidAsString = new StringBuffer("S-");
        sidAsString.append(sid[0]).append('-');
        StringBuffer sb = new StringBuffer();
        for (int t = 2; t <= 7; t++)
        {
            sb.append(Integer.toHexString(sid[t] & 255));
        }
        sidAsString.append(Long.parseLong(sb.toString(), 16));
        int count = sid[1];
        int currSubAuthOffset;
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
        
        return sidAsString.toString();
    }
    
    @SuppressWarnings("PMD.LooseCoupling")
    private Hashtable<String, String> getEnviroment(LdapDomainConfig config, String userName, String password)
    {
        Hashtable<String, String> enviroment = new Hashtable<String, String>(10);
        enviroment.put(Context.INITIAL_CONTEXT_FACTORY, CTX_FACTORY);
        enviroment.put(Context.PROVIDER_URL,
            "ldap://" + config.getDomainControlServer()[config.getCurrentIndex()] + ':'
                + config.getLdapPort());
        enviroment.put(Context.SECURITY_AUTHENTICATION, "simple");
        enviroment.put(Context.SECURITY_PRINCIPAL, userName + '@' + config.getDnsDomainName());
        enviroment.put(Context.SECURITY_CREDENTIALS, password);
        enviroment.put("java.naming.ldap.attributes.binary", "objectSID");
        enviroment.put(Context.REFERRAL, "follow");
        enviroment.put("com.sun.jndi.ldap.connect.pool", "true");
        return enviroment;
    }
    
    /**
     * 
     * @param config
     * @param dirContext
     * @param userName
     * @return
     */
    private UserToken getLdapUser(LdapDomainConfig config, DirContext dirContext, String userName)
    {
        UserToken user = null;
        try
        {
            SearchControls ctrl = new SearchControls();
            ctrl.setSearchScope(SearchControls.SUBTREE_SCOPE);
            String[] returnedAtts = {"objectSid", LdapDomainConfig.LDAP_LOGIN_NAME,
                LdapDomainConfig.LDAP_REAL_NAME, LdapDomainConfig.LDAP_MAIL, LdapDomainConfig.LDAP_DEPARTMENT};
            ctrl.setReturningAttributes(returnedAtts);
            String filter = "(&(objectClass=user)(objectCategory=Person)(sAMAccountName=" + userName + "))";
            NamingEnumeration<SearchResult> enu = dirContext.search(config.getLdapBaseDN(), filter, ctrl);
            
            SearchResult sr;
            Attributes attributes;
            Attribute atrSid;
            String sid;
            
            while (enu.hasMore())
            {
                sr = enu.next();
                attributes = sr.getAttributes();
                user = new UserToken();
                atrSid = attributes.get("objectSid");
                sid = convertBinarySidToString((byte[]) (atrSid.get()));
                user.setObjectSid(sid);
                user.setLoginName(attributes.get(LdapDomainConfig.LDAP_LOGIN_NAME).get().toString());
                user.setName(attributes.get(LdapDomainConfig.LDAP_REAL_NAME).get().toString());
                if (attributes.get(LdapDomainConfig.LDAP_MAIL) != null)
                {
                    user.setEmail(attributes.get(LdapDomainConfig.LDAP_MAIL).get().toString());
                }
                if (attributes.get(LdapDomainConfig.LDAP_DEPARTMENT) != null)
                {
                    String department = attributes.get(LdapDomainConfig.LDAP_DEPARTMENT).get().toString();
                    String[] departmentArg = department.split("\\(");
                    user.setDepartment(departmentArg[0]);
                }
                user.setDomain(config.getDnsDomainName());
            }
        }
        catch (NamingException e)
        {
            logger.error("find ad user failed!", e);
        }
        return user;
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
}
