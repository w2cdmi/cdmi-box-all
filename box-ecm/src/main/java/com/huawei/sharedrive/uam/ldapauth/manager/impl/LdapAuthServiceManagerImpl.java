package com.huawei.sharedrive.uam.ldapauth.manager.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import javax.annotation.Resource;
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
import javax.naming.ldap.Control;
import javax.naming.ldap.LdapContext;
import javax.naming.ldap.PagedResultsControl;
import javax.naming.ldap.PagedResultsResponseControl;
import javax.naming.ldap.SortControl;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.huawei.sharedrive.uam.authserver.manager.AuthServerConfigManager;
import com.huawei.sharedrive.uam.authserver.service.AuthServerService;
import com.huawei.sharedrive.uam.core.alarm.LdapFailedAlarm;
import com.huawei.sharedrive.uam.enterpriseuser.domain.EnterpriseUser;
import com.huawei.sharedrive.uam.enterpriseuser.manager.EnterpriseUserManager;
import com.huawei.sharedrive.uam.exception.BusinessErrorCode;
import com.huawei.sharedrive.uam.exception.BusinessException;
import com.huawei.sharedrive.uam.exception.LoginAuthFailedException;
import com.huawei.sharedrive.uam.exception.NoSuchAuthServerException;
import com.huawei.sharedrive.uam.ldapauth.manager.AuthServiceManager;
import com.huawei.sharedrive.uam.ldapauth.service.LdapAuthService;
import com.huawei.sharedrive.uam.system.domain.TreeNode;
import com.huawei.sharedrive.uam.user.service.impl.LdapServiceImpl;
import com.huawei.sharedrive.uam.util.PropertiesUtils;
import com.sun.jndi.ldap.ctl.VirtualListViewControl;

import pw.cdmi.common.alarm.Alarm;
import pw.cdmi.common.alarm.AlarmHelper;
import pw.cdmi.common.cache.CacheClient;
import pw.cdmi.common.domain.AuthServer;
import pw.cdmi.common.domain.enterprise.ldap.LdapBasicConfig;
import pw.cdmi.common.domain.enterprise.ldap.LdapDomainConfig;
import pw.cdmi.common.domain.enterprise.ldap.LdapNodeFilterConfig;
import pw.cdmi.common.job.JobExecuteContext;
import pw.cdmi.common.job.JobExecuteRecord;
import pw.cdmi.common.job.quartz.QuartzJobTask;
import pw.cdmi.core.utils.CacheParameterUtils;

@SuppressWarnings("PMD.PreserveStackTrace")
@Service("ldapAuthServiceManager")
public class LdapAuthServiceManagerImpl extends QuartzJobTask implements AuthServiceManager
{
    public final static String LDAP_SPLIT = ";";
    
    private final static String CTX_FACTORY = "com.sun.jndi.ldap.LdapCtxFactory";
    
    private static Logger logger = LoggerFactory.getLogger(LdapServiceImpl.class);
    
    private final static int PAGE_COUNT = 999;
    
    private static byte[] parseControls(Control[] controls)
    {
        byte[] cookie = null;
        if (controls != null)
        {
            for (int i = 0; i < controls.length; i++)
            {
                if (controls[i] instanceof PagedResultsResponseControl)
                {
                    PagedResultsResponseControl prrc = (PagedResultsResponseControl) controls[i];
                    cookie = prrc.getCookie();
                }
            }
        }
        return (cookie == null) ? new byte[0] : cookie;
    }
    
    @Autowired
    private AlarmHelper alarmHelper;
    
    @Autowired
    private AuthServerConfigManager authServerConfigManager;
    
    @Autowired
    private AuthServerService authServerService;
    
    @Resource(name = "cacheClient")
    private CacheClient cacheClient;
    
    @Autowired
    private EnterpriseUserManager enterpriseUserManager;
    
    @Autowired
    private LdapAuthService ldapAuthService;
    
    @Autowired
    private LdapFailedAlarm ldapFailedAlarm;
    
    @Override
	public EnterpriseUser authenticateByMobile(Long authServerId, String mobile, String password) {
		// TODO Auto-generated method stub
		return null;
	}
    
    @Override
    public EnterpriseUser authenticateByMail(Long authServerId, String mail, String password)
    {
        LdapContext ldapContext = null;
        EnterpriseUser enterpriseUser;
        LdapDomainConfig ldapDomainConfig = authServerConfigManager.getAuthServerObject(authServerId);
        String domainControlServer = ldapAuthService.getDomainControlServerCache(ldapDomainConfig.getLdapBasicConfig()
            .getDomainControlServer(),
            authServerId);
        try
        {
            ldapContext = ldapAuthService.getLdapContext(ldapDomainConfig, domainControlServer);
            enterpriseUser = ldapAuthService.getLdapUserByEmail(ldapDomainConfig,
                ldapContext,
                mail,
                authServerId);
            if (null == enterpriseUser)
            {
                logger.error("find ad user failed loginName:" + mail + " authServerId:" + authServerId);
                throw new LoginAuthFailedException();
            }
            ldapAuthService.authenticate(ldapContext, enterpriseUser.getUserDn(), password);
        }
        catch (CommunicationException e)
        {
            enterpriseUser = null;
            deleteUnusedDomain(domainControlServer, authServerId);
            logger.error(e.getMessage(), e);
        }
        catch (NamingException e)
        {
            enterpriseUser = null;
            logger.error(e.getMessage(), e);
        }
        catch (Exception e)
        {
            enterpriseUser = null;
            logger.error("find ad user failed loginName:" + mail + " authServerId:" + authServerId);
        }
        finally
        {
            closeLdapContext(ldapContext);
        }
        return enterpriseUser;
    }
    
    @Override
    public EnterpriseUser authenticateByName(Long authServerId, String loginName, String password,
        String domain)
    {
        LdapContext ldapContext = null;
        EnterpriseUser enterpriseUser;
        LdapDomainConfig ldapDomainConfig = authServerConfigManager.getAuthServerObject(authServerId);
        String domainControlServer = ldapAuthService.getDomainControlServerCache(ldapDomainConfig.getLdapBasicConfig()
            .getDomainControlServer(),
            authServerId);
        try
        {
            ldapContext = ldapAuthService.getLdapContext(ldapDomainConfig, domainControlServer);
            enterpriseUser = ldapAuthService.getLdapUserByLoginName(ldapDomainConfig,
                ldapContext,
                loginName,
                authServerId);
            if (null == enterpriseUser)
            {
                logger.error("find ad user failed loginName:" + loginName + " authServerId:" + authServerId);
                throw new LoginAuthFailedException();
            }
            ldapAuthService.authenticate(ldapContext, enterpriseUser.getUserDn(), password);
        }
        catch (CommunicationException e)
        {
            enterpriseUser = null;
            deleteUnusedDomain(domainControlServer, authServerId);
            logger.error(e.getMessage(), e);
        }
        catch (NamingException e)
        {
            enterpriseUser = null;
            logger.error(e.getMessage(), e);
        }
        catch (Exception e)
        {
            enterpriseUser = null;
            logger.error("find ad user failed loginName:" + loginName + " authServerId:" + authServerId);
        }
        finally
        {
            closeLdapContext(ldapContext);
        }
        return enterpriseUser;
    }
    
    @Override
    public void checkDeleteUsers(Long authServerId, Long localAuthServerId,
        List<EnterpriseUser> enterpriseUserList)
    {
        LdapContext ldapContext = null;
        LdapDomainConfig ldapDomainConfig = authServerConfigManager.getAuthServerObject(authServerId);
        String domainControlServer = ldapAuthService.getDomainControlServerCache(ldapDomainConfig.getLdapBasicConfig()
            .getDomainControlServer(),
            authServerId);
        try
        {
            ldapContext = ldapAuthService.getLdapContext(ldapDomainConfig, domainControlServer);
            EnterpriseUser enterpriseUser;
            EnterpriseUser ldapEnterpriseUser;
            for (Iterator<EnterpriseUser> it = enterpriseUserList.iterator(); it.hasNext();)
            {
                enterpriseUser = it.next();
                if (localAuthServerId == enterpriseUser.getUserSource())
                {
                    it.remove();
                    continue;
                }
                ldapEnterpriseUser = ldapAuthService.getLdapUserByLoginName(ldapDomainConfig,
                    ldapContext,
                    enterpriseUser.getName(),
                    authServerId);
                if (null != ldapEnterpriseUser)
                {
                    it.remove();
                    continue;
                }
            }
        }
        catch (CommunicationException e)
        {
            deleteUnusedDomain(domainControlServer, authServerId);
            logger.error(e.getMessage(), e);
        }
        catch (NamingException e)
        {
            logger.error(e.getMessage(), e);
        }
        finally
        {
            closeLdapContext(ldapContext);
        }
    }
    
    @Override
    public void doTask(JobExecuteContext context, JobExecuteRecord record)
    {
        List<AuthServer> authServerList = authServerService.getAllAuthServer();
        if (CollectionUtils.isNotEmpty(authServerList))
        {
            boolean result = true;
            String type;
            Long authServerId;
            LdapDomainConfig ldapDomainConfig;
            for (AuthServer authServer : authServerList)
            {
                type = authServer.getType();
                if (type.equals(AuthServer.AUTH_TYPE_LOCAL))
                {
                    continue;
                }
                authServerId = authServer.getId();
                ldapDomainConfig = authServerConfigManager.getAuthServerObject(authServerId);
                if (ldapDomainConfig == null)
                {
                    continue;
                }
                result = result & checkLdapByAppId(ldapDomainConfig, authServerId);
            }
            
            if (!result)
            {
                record.setSuccess(false);
                record.setOutput("ldap check failed.");
            }
        }
    }
    
    @Override
    public List<TreeNode> getTreeNode(Long authServerId, String dn, int pageNumber)
    {
        List<TreeNode> treeNodeList = new ArrayList<TreeNode>(10);
        LdapContext ldapContext = null;
        try
        {
            LdapDomainConfig config = authServerConfigManager.getAuthServerObject(authServerId);
            String domainControlServer = ldapAuthService.getDomainControlServerCache(config.getLdapBasicConfig()
                .getDomainControlServer(),
                authServerId);
            ldapContext = ldapAuthService.getLdapContext(config, domainControlServer);
            if (StringUtils.isBlank(dn))
            {
                dn = config.getLdapBasicConfig().getLdapNodeBaseDN();
            }
            String pageCount = config.getLdapBasicConfig().getPageCount();
            int pageSize = 0;
            int pageCountFlag = 0;
            if (StringUtils.isNotBlank(pageCount))
            {
                pageSize = Integer.parseInt(pageCount);
                pageCountFlag = pageSize;
            }
            if (pageSize == 0)
            {
                pageSize = PAGE_COUNT;
            }
            
            if (pageCountFlag > 0)
            {
                VirtualListViewControl ctl = new VirtualListViewControl(pageNumber * pageSize + 1, 0, 0,
                    pageSize - 1, Control.CRITICAL);
                ldapContext.setRequestControls(new Control[]{
                    new SortControl(TreeNode.SORT_BY, Control.CRITICAL), ctl});
            }
            
            SearchControls ctrl = new SearchControls();
            ctrl.setSearchScope(SearchControls.ONELEVEL_SCOPE);
            NamingEnumeration<SearchResult> enu = ldapContext.search(TreeNode.formatDn(ldapContext, dn),
                config.getLdapBasicConfig().getSearchNodeFilter(),
                ctrl);
            SearchResult sr;
            List<String> listSyncNode = new ArrayList<String>(10);
            List<String> listDisplayNode = new ArrayList<String>(10);
            getSyncAndDisplayNode(config, listSyncNode, listDisplayNode);
            while (enu.hasMoreElements())
            {
                sr = enu.next();
                addNode(treeNodeList, sr, listSyncNode, listDisplayNode);
            }
            
        }
        catch (CommunicationException e)
        {
            logger.error("get tree node failed authServerId:" + authServerId + " dn:" + dn, e);
            throw new BusinessException(BusinessErrorCode.INTERNAL_SERVER_ERROR,
                "get tree node failed authServerId:" + authServerId + " dn:" + dn);
        }
        catch (NamingException e)
        {
            logger.error("get tree node failed authServerId:" + authServerId + " dn:" + dn, e);
            throw new BusinessException(BusinessErrorCode.INTERNAL_SERVER_ERROR,
                "get tree node failed authServerId:" + authServerId + " dn:" + dn);
        }
        catch (IOException e)
        {
            logger.error("get tree node failed authServerId:" + authServerId + " dn:" + dn, e);
            throw new BusinessException(BusinessErrorCode.INTERNAL_SERVER_ERROR,
                "get tree node failed authServerId:" + authServerId + " dn:" + dn);
        }
        catch (NoSuchAuthServerException e)
        {
            logger.error("get tree node failed authServerId:" + authServerId + " dn:" + dn, e);
            throw new NoSuchAuthServerException("get tree node failed authServerId:" + authServerId + " dn:"
                + dn);
        }
        catch (Exception e)
        {
            logger.error("get tree node failed authServerId:" + authServerId + " dn:" + dn, e);
            throw new BusinessException(BusinessErrorCode.INTERNAL_SERVER_ERROR,
                "get tree node failed authServerId:" + authServerId + " dn:" + dn);
        }
        finally
        {
            closeLdapContext(ldapContext);
        }
        return treeNodeList;
    }
    
    @Override
    public EnterpriseUser getUserByLoginName(Long authServerId, String loginName)
    {
        LdapContext ldapContext = null;
        EnterpriseUser enterpriseUser;
        LdapDomainConfig ldapDomainConfig = authServerConfigManager.getAuthServerObject(authServerId);
        if (null == ldapDomainConfig || null == ldapDomainConfig.getLdapBasicConfig())
        {
            return null;
        }
        String domainControlServer = ldapAuthService.getDomainControlServerCache(ldapDomainConfig.getLdapBasicConfig()
            .getDomainControlServer(),
            authServerId);
        try
        {
            ldapContext = ldapAuthService.getLdapContext(ldapDomainConfig, domainControlServer);
            enterpriseUser = getEnterpriseUser(ldapDomainConfig, ldapContext, loginName, authServerId);
        }
        catch (CommunicationException e)
        {
            enterpriseUser = null;
            deleteUnusedDomain(domainControlServer, authServerId);
            logger.error(e.getMessage(), e);
        }
        catch (NamingException e)
        {
            enterpriseUser = null;
            logger.error(e.getMessage(), e);
        }
        catch (LoginAuthFailedException e)
        {
            enterpriseUser = null;
            logger.error("find ad user failed loginName:" + loginName + " authServerId:" + authServerId);
        }
        finally
        {
            closeLdapContext(ldapContext);
        }
        return enterpriseUser;
    }
    
    @Override
    public List<EnterpriseUser> listAllUsers(LdapDomainConfig ldapDomainConfig, Long authServerId, String dn,
        boolean isSearchLimit)
    {
        List<EnterpriseUser> userList = new ArrayList<EnterpriseUser>(10);
        LdapContext ldapContext = null;
        String domainControlServer = "";
        try
        {
            domainControlServer = ldapAuthService.getDomainControlServerCache(ldapDomainConfig.getLdapBasicConfig()
                .getDomainControlServer(),
                authServerId);
            ldapContext = ldapAuthService.getLdapContext(ldapDomainConfig, domainControlServer);
            SearchControls ctrl = new SearchControls();
            if (ldapContext != null)
            {
                getUserList(ldapDomainConfig, userList, ldapContext, ctrl, dn, isSearchLimit);
            }
        }
        catch (CommunicationException e)
        {
            deleteUnusedDomain(domainControlServer, authServerId);
            logger.error(e.getMessage(), e);
        }
        catch (NamingException e)
        {
            logger.error("Fail in auth LDAP connection", e);
            return null;
        }
        finally
        {
            closeLdapContext(ldapContext);
        }
        return userList;
    }
    
    @Override
    public List<EnterpriseUser> searchUsers(LdapDomainConfig ldapDomainConfig, Long authServerId,
        Long enterpriseId, String searchName, int limit)
    {
        if (null == ldapDomainConfig || null == ldapDomainConfig.getLdapBasicConfig())
        {
            return null;
        }
        List<EnterpriseUser> userList = new ArrayList<EnterpriseUser>(limit);
        LdapContext ldapContext = null;
        String domainControlServer = "";
        try
        {
            domainControlServer = ldapAuthService.getDomainControlServerCache(ldapDomainConfig.getLdapBasicConfig()
                .getDomainControlServer(),
                authServerId);
            ldapContext = ldapAuthService.getLdapContext(ldapDomainConfig, domainControlServer);
            SearchControls ctrl = new SearchControls();
            ctrl.setSearchScope(SearchControls.SUBTREE_SCOPE);
            ctrl.setCountLimit(limit);
            String filter = ldapDomainConfig.getLdapBasicConfig()
                .getSearchFilter()
                .replace(LdapDomainConfig.AUTH_RULE_STR, searchName);
            String ldapBaseDN = ldapDomainConfig.getLdapBasicConfig().getLdapBaseDN();
            if (StringUtils.isBlank(ldapBaseDN))
            {
                return null;
            }
            String ldapSplit = PropertiesUtils.getProperty("ldap.split");
            if (StringUtils.isBlank(ldapSplit))
            {
                ldapSplit = LDAP_SPLIT;
            }
            String[] ldapBaseDNStr = ldapBaseDN.split(ldapSplit);
            for (int i = 0; i < ldapBaseDNStr.length; i++)
            {
                if (getUserListFromOneBaseDN(ldapDomainConfig,
                    enterpriseId,
                    limit,
                    userList,
                    ldapContext,
                    ctrl,
                    filter,
                    ldapBaseDNStr,
                    i))
                {
                    return userList;
                }
            }
        }
        catch (CommunicationException e)
        {
            deleteUnusedDomain(domainControlServer, authServerId);
            logger.error(e.getMessage(), e);
        }
        catch (NamingException e)
        {
            logger.error("Fail in auth LDAP connection", e);
        }
        finally
        {
            closeLdapContext(ldapContext);
        }
        return userList;
    }
    
    @SuppressWarnings("PMD.ExcessiveParameterList")
    private boolean getUserListFromOneBaseDN(LdapDomainConfig ldapDomainConfig, Long enterpriseId, int limit,
        List<EnterpriseUser> userList, LdapContext ldapContext, SearchControls ctrl, String filter,
        String[] ldapBaseDNStr, int i)
    {
        boolean isFind = false;
        try
        {
            NamingEnumeration<SearchResult> enu = ldapContext.search(TreeNode.formatDn(ldapContext,
                ldapBaseDNStr[i]), filter, ctrl);
            Attributes attributes;
            EnterpriseUser user;
            while (enu.hasMoreElements())
            {
                attributes = enu.next().getAttributes();
                user = ldapAuthService.getUserByAttri(ldapDomainConfig, attributes);
                if (null == user)
                {
                    continue;
                }
                checkEamil(enterpriseId, user);
                userList.add(user);
                if (userList.size() >= limit)
                {
                    isFind = true;
                    break;
                }
            }
        }
        catch (Exception e)
        {
            logger.warn("list failed", e);
        }
        return isFind;
    }
    
    private void addNode(List<TreeNode> treeNodeList, SearchResult sr, List<String> listSyncNode,
        List<String> listDisplayNode)
    {
        TreeNode node = new TreeNode();
        node.setName(sr.getName());
        String baseDn = sr.getNameInNamespace();
        node.setBaseDn(baseDn);
        node.setChecked(listSyncNode.contains(baseDn.toLowerCase(Locale.ENGLISH))
            || listDisplayNode.contains(baseDn.toLowerCase(Locale.ENGLISH)));
        treeNodeList.add(node);
    }
    
    private void checkEamil(long enterpriseId, EnterpriseUser enterpriseUser)
    {
        if (StringUtils.isBlank(enterpriseUser.getEmail()))
        {
            EnterpriseUser enterpriseUserDao = enterpriseUserManager.getByObjectSid(enterpriseUser.getObjectSid(),
                enterpriseId);
            if (null != enterpriseUserDao)
            {
                enterpriseUser.setEmail(enterpriseUserDao.getEmail());
            }
        }
    }
    
    private boolean isResult(String domainControlServer, LdapDomainConfig ldapDomainConfig,
        StringBuffer domainControlServerBuffer)
    {
        String ldapSplit = PropertiesUtils.getProperty("ldap.split");
        if (StringUtils.isBlank(ldapSplit))
        {
            ldapSplit = LDAP_SPLIT;
        }
        String[] domainControlServerStr = domainControlServer.split(ldapSplit);
        boolean result = true;
        boolean isAuth;
        Alarm alarm;
        for (int i = 0; i < domainControlServerStr.length; i++)
        {
            isAuth = checkLdapByNoCache(ldapDomainConfig, domainControlServerStr[i]);
            alarm = new LdapFailedAlarm(ldapFailedAlarm, domainControlServerStr[i]);
            if (isAuth)
            {
                if (i == (domainControlServerStr.length - 1))
                {
                    domainControlServerBuffer.append(domainControlServerStr[i]);
                }
                else
                {
                    domainControlServerBuffer.append(domainControlServerStr[i] + ";");
                }
                alarmHelper.sendRecoverAlarm(alarm);
                result = true;
            }
            else
            {
                alarmHelper.sendAlarm(alarm);
                result = false;
            }
        }
        return result;
    }
    
    private boolean checkLdapByAppId(LdapDomainConfig ldapDomainConfig, long authServerId)
    {
        boolean result = true;
        try
        {
            String domainControlServer = ldapDomainConfig.getLdapBasicConfig().getDomainControlServer();
            StringBuffer domainControlServerBuffer = new StringBuffer();
            if (StringUtils.isBlank(domainControlServer))
            {
                return true;
            }
            result = isResult(domainControlServer, ldapDomainConfig, domainControlServerBuffer);
            domainControlServer = domainControlServerBuffer.toString();
            if (StringUtils.isNotBlank(domainControlServer))
            {
                cacheClient.setCache(CacheParameterUtils.AUTHSERVER_DOMAIN_CONTROL_SERVER + authServerId,
                    domainControlServer);
            }
        }
        catch (Exception e)
        {
            logger.error("check failed" + e.toString());
            result = false;
        }
        
        return result;
    }
    
    private boolean checkLdapByNoCache(LdapDomainConfig ldapConfig, String domainControlServer)
    {
        Hashtable<String, String> enviroment = getEnviromentByNoCacge(ldapConfig, domainControlServer);
        DirContext dirContext = null;
        try
        {
            dirContext = new InitialDirContext(enviroment);
        }
        catch (CommunicationException e)
        {
            logger.error(e.getMessage(), e);
            return false;
        }
        catch (NamingException e)
        {
            logger.error("Fail in auth LDAP connection", e);
            return false;
        }
        finally
        {
            closeLdapContext(dirContext);
        }
        return true;
    }
    
    private void closeLdapContext(DirContext dirContext)
    {
        if (dirContext != null)
        {
            try
            {
                dirContext.close();
            }
            catch (NamingException e)
            {
                logger.error("close domain control conn fail", e);
            }
        }
    }
    
    private void closeLdapContext(LdapContext ldapContext)
    {
        if (ldapContext != null)
        {
            try
            {
                ldapContext.close();
            }
            catch (NamingException e)
            {
                logger.error("Fail in close LDAP connection", e);
                
            }
        }
    }
    
    private void deleteUnusedDomain(String unusedDomainControlServer, Long authServerId)
    {
        Object obj = cacheClient.getCache(CacheParameterUtils.AUTHSERVER_DOMAIN_CONTROL_SERVER + authServerId);
        String domainControlServer = String.valueOf(obj == null ? "" : obj);
        if (StringUtils.isBlank(domainControlServer))
        {
            return;
        }
        String ldapSplit = PropertiesUtils.getProperty("ldap.split");
        if (StringUtils.isBlank(ldapSplit))
        {
            ldapSplit = LDAP_SPLIT;
        }
        String[] domainControlServerStr = domainControlServer.split(ldapSplit);
        if (domainControlServerStr.length == 1)
        {
            return;
        }
        StringBuffer controlServerBuffer = new StringBuffer();
        for (int i = 0; i < domainControlServerStr.length; i++)
        {
            if (domainControlServerStr[i].equals(unusedDomainControlServer))
            {
                continue;
            }
            if (i == domainControlServerStr.length - 1)
            {
                controlServerBuffer.append(domainControlServerStr[i]);
            }
            else
            {
                controlServerBuffer.append(domainControlServerStr[i] + ldapSplit);
            }
        }
        cacheClient.setCache(CacheParameterUtils.AUTHSERVER_DOMAIN_CONTROL_SERVER + authServerId,
            controlServerBuffer.toString(),
            CacheParameterUtils.AUTHSERVER_DOMAIN_CONTROL_SERVER_TIME_OUT);
    }
    
    @SuppressWarnings("PMD.LooseCoupling")
    private Hashtable<String, String> getEnviromentByNoCacge(LdapDomainConfig config,
        String domainControlServer)
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
    
    private void addMember(NamingEnumeration<?> ne, List<String> memberDnList, boolean isSearchLimit)
        throws NamingException
    {
        Attribute attr;
        while (ne.hasMore())
        {
            attr = (Attribute) ne.next();
            lisAttrValue(attr, memberDnList, isSearchLimit);
            if (isSearchLimit && memberDnList.size() == TreeNode.LDAP_SEARCH_GROUP_LIMIT)
            {
                return;
            }
        }
    }
    
    private void getGroupMemberDn(Attributes attributes, List<String> memberDnList, boolean isSearchLimit)
    {
        if (attributes != null)
        {
            NamingEnumeration<?> ne = attributes.getAll();
            try
            {
                addMember(ne, memberDnList, isSearchLimit);
            }
            catch (NamingException e)
            {
                logger.error("Fail find group'member");
            }
            
        }
        
    }
    
    private void getSyncAndDisplayNode(LdapDomainConfig ldapDomainConfig, List<String> syncList,
        List<String> displayList)
    {
        if (ldapDomainConfig != null)
        {
            LdapNodeFilterConfig ldapNodeFilterConfig = ldapDomainConfig.getLdapNodeFilterConfig();
            if (ldapNodeFilterConfig != null)
            {
                String syncNode = ldapNodeFilterConfig.getSyncNode();
                if (StringUtils.isNotBlank(syncNode))
                {
                    syncList.addAll(new ArrayList<String>(
                        Arrays.asList(syncNode.split(LdapAuthServiceManagerImpl.LDAP_SPLIT))));
                }
                String displayNode = ldapNodeFilterConfig.getDisplayNode();
                if (StringUtils.isNotBlank(displayNode))
                {
                    displayList.addAll(new ArrayList<String>(
                        Arrays.asList(displayNode.split(LdapAuthServiceManagerImpl.LDAP_SPLIT))));
                }
            }
        }
    }
    
    @SuppressWarnings("PMD.ExcessiveParameterList")
    private void getUserList(LdapDomainConfig config, List<EnterpriseUser> userList, LdapContext ldapContext,
        SearchControls ctrl, String dn, boolean isSearchLimit)
    {
        String ldapSplit = PropertiesUtils.getProperty("ldap.split");
        if (StringUtils.isBlank(ldapSplit))
        {
            ldapSplit = LDAP_SPLIT;
        }
        
        String[] returnedAtts = {config.getLdapFiledMapping().getUniqueKey(),
            config.getLdapFiledMapping().getDescription(), config.getLdapFiledMapping().getEmail(),
            config.getLdapFiledMapping().getLoginName(), config.getLdapFiledMapping().getName()};
        String[] returnedAttsGroup = config.getLdapNodeFilterConfig().getMemberFlag().split(ldapSplit);
        String[] arrSyncNodeUser = {dn};
        String syncNode = config.getLdapNodeFilterConfig().getSyncNode();
        String[] arrSyncNode = syncNode.split(LDAP_SPLIT);
        String filter = config.getLdapNodeFilterConfig().getUserObjectClass();
        String filterGroup = config.getLdapNodeFilterConfig().getGroupObjectClass();
        List<String> memberDnList = new ArrayList<String>(10);
        
        // Sync users by select dn
        if (dn == null)
        {
            getUserListByBaseDN(config,
                userList,
                ldapContext,
                ctrl,
                filter,
                arrSyncNode,
                returnedAtts,
                null,
                isSearchLimit);
        }
        // Get users by dn
        else
        {
            getUserListByBaseDN(config,
                userList,
                ldapContext,
                ctrl,
                filter,
                arrSyncNodeUser,
                returnedAtts,
                null,
                isSearchLimit);
        }
        if (isSearchLimit && userList.size() >= TreeNode.LDAP_SEARCH_USER_LIMIT)
        {
            return;
        }
        // groups
        if (StringUtils.isNotBlank(config.getLdapNodeFilterConfig().getGroupObjectClass())
            && StringUtils.isNotBlank(config.getLdapNodeFilterConfig().getMemberFlag()))
        {
            
            if (dn == null)
            {
                getUserListByBaseDN(config,
                    userList,
                    ldapContext,
                    ctrl,
                    filterGroup,
                    arrSyncNode,
                    returnedAttsGroup,
                    memberDnList,
                    isSearchLimit);
            }
            else
            {
                getUserListByBaseDN(config,
                    userList,
                    ldapContext,
                    ctrl,
                    filterGroup,
                    arrSyncNodeUser,
                    returnedAttsGroup,
                    memberDnList,
                    isSearchLimit);
            }
            
            String[] userArr = memberDnList.toArray(new String[]{});
            getUserListByBaseDN(config,
                userList,
                ldapContext,
                ctrl,
                filter,
                userArr,
                returnedAtts,
                null,
                isSearchLimit);
        }
        
    }
    
    @SuppressWarnings("PMD.ExcessiveParameterList")
    private void getUserListByBaseDN(LdapDomainConfig config, List<EnterpriseUser> userList,
        LdapContext ldapContext, SearchControls ctrl, String filter, String[] arrSyncNode,
        String[] returnedAtts, List<String> memberDnList, boolean isSearchLimit)
    {
        byte[] cookie = null;
        try
        {
            String pageCount = config.getLdapBasicConfig().getPageCount();
            int page = 0;
            int pageCountFlag = 0;
            if (StringUtils.isNotBlank(pageCount))
            {
                page = Integer.parseInt(pageCount);
                pageCountFlag = page;
            }
            if (page == 0)
            {
                page = PAGE_COUNT;
            }
            if (pageCountFlag > 0)
            {
                ldapContext.setRequestControls(new Control[]{new PagedResultsControl(page, Control.CRITICAL)});
            }
            ctrl.setSearchScope(SearchControls.SUBTREE_SCOPE);
            ctrl.setCountLimit(page);
            ctrl.setReturningAttributes(returnedAtts);
            NamingEnumeration<SearchResult> enu;
            Attributes attributes;
            SearchResult searchResult;
            boolean isEnd = false;
            for (String node : arrSyncNode)
            {
                do
                {
                    enu = ldapContext.search(TreeNode.formatDn(ldapContext, node), filter, ctrl);
                    while (enu.hasMoreElements())
                    {
                        searchResult = enu.next();
                        attributes = searchResult.getAttributes();
                        isEnd = isSearchEnd(config, userList, memberDnList, isSearchLimit, attributes);
                        
                        if (isEnd)
                        {
                            return;
                        }
                    }
                    
                    if (pageCountFlag > 0)
                    {
                        cookie = parseControls(ldapContext.getResponseControls());
                        ldapContext.setRequestControls(new Control[]{new PagedResultsControl(page, cookie,
                            Control.CRITICAL)});
                    }
                } while ((cookie != null) && (cookie.length != 0));
            }
            
        }
        catch (NamingException e)
        {
            logger.error("Fail find user", e);
        }
        catch (IOException e)
        {
            logger.error("Fail find user", e);
        }
    }
    
    private boolean isSearchEnd(LdapDomainConfig config, List<EnterpriseUser> userList,
        List<String> memberDnList, boolean isSearchLimit, Attributes attributes) throws NamingException
    {
        boolean isEnd = false;
        EnterpriseUser enterpriseUser;
        if (memberDnList != null)
        {
            getGroupMemberDn(attributes, memberDnList, isSearchLimit);
            if (isSearchLimit && memberDnList.size() == TreeNode.LDAP_SEARCH_GROUP_LIMIT)
            {
                isEnd = true;
            }
        }
        else
        {
            enterpriseUser = ldapAuthService.getUserByAttri(config, attributes);
            if (enterpriseUser != null)
            {
                userList.add(enterpriseUser);
                if (isSearchLimit && userList.size() == TreeNode.LDAP_SEARCH_USER_LIMIT)
                {
                    isEnd = true;
                }
            }
        }
        return isEnd;
    }
    
    private void addMember(Enumeration<?> values, List<String> memberDnList, boolean isSearchLimit)
    {
        Object value;
        while (values.hasMoreElements())
        {
            value = values.nextElement();
            memberDnList.add((String) value);
            if (isSearchLimit && memberDnList.size() == TreeNode.LDAP_SEARCH_GROUP_LIMIT)
            {
                return;
            }
        }
    }
    
    private void lisAttrValue(Attribute attr, List<String> memberDnList, boolean isSearchLimit)
    {
        try
        {
            Enumeration<?> values = attr.getAll();
            if (values != null)
            {
                addMember(values, memberDnList, isSearchLimit);
            }
        }
        catch (NamingException e)
        {
            logger.error("Fail find group'member");
        }
    }
    
    private EnterpriseUser getEnterpriseUser(LdapDomainConfig ldapDomainConfig, LdapContext ldapContext,
        String loginName, Long authServerId)
    {
        EnterpriseUser enterpriseUser = ldapAuthService.getLdapUserByLoginName(ldapDomainConfig,
            ldapContext,
            loginName,
            authServerId);
        if (null == enterpriseUser)
        {
            logger.error("find ad user failed loginName:" + loginName + " authServerId:" + authServerId);
        }
        return enterpriseUser;
    }

	@Override
	public EnterpriseUser authenticateByStaff(Long authServerId, String staff, String password) {
		// TODO Auto-generated method stub
		        LdapContext ldapContext = null;
		        EnterpriseUser enterpriseUser;
		        LdapDomainConfig ldapDomainConfig = authServerConfigManager.getAuthServerObject(authServerId);
		        String domainControlServer = ldapAuthService.getDomainControlServerCache(ldapDomainConfig.getLdapBasicConfig()
		            .getDomainControlServer(),
		            authServerId);
		        try
		        {
		            ldapContext = ldapAuthService.getLdapContext(ldapDomainConfig, domainControlServer);
		            enterpriseUser = ldapAuthService.getLdapUserByLoginName(ldapDomainConfig,
		                ldapContext,
		                staff,
		                authServerId);
		            if (null == enterpriseUser)
		            {
		                logger.error("find ad user failed loginName:" + staff + " authServerId:" + authServerId);
		                throw new LoginAuthFailedException();
		            }
		            ldapAuthService.authenticate(ldapContext, enterpriseUser.getUserDn(), password);
		        }
		        catch (CommunicationException e)
		        {
		            enterpriseUser = null;
		            deleteUnusedDomain(domainControlServer, authServerId);
		            logger.error(e.getMessage(), e);
		        }
		        catch (NamingException e)
		        {
		            enterpriseUser = null;
		            logger.error(e.getMessage(), e);
		        }
		        catch (Exception e)
		        {
		            enterpriseUser = null;
		            logger.error("find ad user failed loginName:" + staff + " authServerId:" + authServerId);
		        }
		        finally
		        {
		            closeLdapContext(ldapContext);
		        }
		        return enterpriseUser;
		    }
		    

}
