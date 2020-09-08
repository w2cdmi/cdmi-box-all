package com.huawei.sharedrive.uam.authserver.web;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.ConstraintViolationException;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.huawei.sharedrive.uam.authserver.manager.AuthServerConfigManager;
import com.huawei.sharedrive.uam.authserver.manager.AuthServerJobManager;
import com.huawei.sharedrive.uam.authserver.manager.AuthServerManager;
import com.huawei.sharedrive.uam.common.web.AbstractCommonController;
import com.huawei.sharedrive.uam.enterprise.manager.EnterpriseAccountManager;
import com.huawei.sharedrive.uam.enterpriseadminlog.domain.AdminLogType;
import com.huawei.sharedrive.uam.enterpriseadminlog.domain.LogOwner;
import com.huawei.sharedrive.uam.enterpriseadminlog.manager.AdminLogManager;
import com.huawei.sharedrive.uam.exception.ForbiddenException;
import com.huawei.sharedrive.uam.exception.InvalidParamterException;
import com.huawei.sharedrive.uam.exception.NoSuchAuthServerException;
import com.huawei.sharedrive.uam.ldapauth.manager.impl.LdapAuthServiceManagerImpl;
import com.huawei.sharedrive.uam.log.domain.OperateDescription;
import com.huawei.sharedrive.uam.log.domain.OperateType;
import com.huawei.sharedrive.uam.log.manager.SystemLogManager;
import com.huawei.sharedrive.uam.system.domain.TreeNode;
import com.huawei.sharedrive.uam.system.service.impl.PwdConfuser;

import pw.cdmi.common.domain.AuthServer;
import pw.cdmi.common.domain.enterprise.ldap.LdapBasicConfig;
import pw.cdmi.common.domain.enterprise.ldap.LdapDomainConfig;
import pw.cdmi.common.domain.enterprise.ldap.LdapFiledMapping;
import pw.cdmi.common.domain.enterprise.ldap.LdapNodeFilterConfig;
import pw.cdmi.common.job.exception.JobException;
import pw.cdmi.core.utils.IpUtils;

@Controller
@RequestMapping(value = "/enterprise/admin/configauthserver")
public class AuthServerConfigController extends AbstractCommonController
{
    private final static Logger LOGGER = LoggerFactory.getLogger(AbstractCommonController.class);
    
    public final static String UAM_LDAP_BASIC_CONFIG = "uam_ldap_basic_config_";
    
    @Autowired
    private AuthServerConfigManager authServerConfigManager;
    
    @Autowired
    private AuthServerManager authServerManager;
    
    @Autowired
    private AuthServerJobManager authServerJobManager;
    
    @Autowired
    private LdapAuthServiceManagerImpl ldapAuthServiceManager;
    
    @Autowired
    private SystemLogManager systemLogManager;
    
    @Autowired
    private AdminLogManager adminlogManager;
    
    @Autowired
    private EnterpriseAccountManager enterpriseAccountManager;
    
    /**
     * get authServerId configed
     * 
     * @param authServerId
     * @param model
     * @return
     */
    @RequestMapping(value = "config/{authServerId}", method = RequestMethod.GET)
    public String enterConfig(@PathVariable(value = "authServerId") Long authServerId, Model model,
        HttpSession session)
    {
        checkAdminAndGetId();
        enterpriseAccountManager.bindAppCheck();
        LdapDomainConfig ldapDomainConfig = authServerConfigManager.getAuthServerObject(authServerId);
        AuthServer authServer = authServerManager.getAuthServer(authServerId);
        if (ldapDomainConfig.getLdapBasicConfig() != null)
        {
            List<LdapDomainConfig> list = new ArrayList<LdapDomainConfig>(10);
            list.add(ldapDomainConfig);
            LdapDomainConfig.htmlEscape(list);
            if (StringUtils.isNotBlank(ldapDomainConfig.getLdapBasicConfig().getLdapBindAccountPassword()))
            {
                ldapDomainConfig.getLdapBasicConfig()
                    .setLdapBindAccountPassword(PwdConfuser.DEFAULT_SHOW_PWD);
            }
            else
            {
                ldapDomainConfig.getLdapBasicConfig().setLdapBindAccountPassword("");
            }
            if (StringUtils.isNotBlank(ldapDomainConfig.getLdapBasicConfig().getNtlmPcAccountPasswd()))
            {
                ldapDomainConfig.getLdapBasicConfig().setNtlmPcAccountPasswd(PwdConfuser.DEFAULT_SHOW_PWD);
            }
            else
            {
                ldapDomainConfig.getLdapBasicConfig().setNtlmPcAccountPasswd("");
            }
            
            model.addAttribute("serverConfig", ldapDomainConfig);
        }
        List<AuthServer> authList = new ArrayList<AuthServer>(10);
        authList.add(authServer);
        AuthServer.htmlEscape(authList);
        model.addAttribute("authServer", authServer);
        return "app/authServer/configAuthServerLdapAuth";
    }
    
    @RequestMapping(value = "checkAuthServer/{authServerId}", method = RequestMethod.GET)
    public ResponseEntity<?> checkAuthServer(@PathVariable(value = "authServerId") Long authServerId)
    {
        authServerManager.getAuthServer(authServerId);
        return new ResponseEntity<String>(HttpStatus.OK);
    }
    
    /**
     * update ldap basic config
     * 
     * @param authServerId
     * @param token
     * @param ldapBasicConfig
     * @param request
     * @return
     */
    @SuppressWarnings({"rawtypes", "unchecked", "unused"})
    @RequestMapping(value = "configBasic/{authServerId}", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<?> configLdapBasciConfig(@PathVariable Long authServerId, HttpSession session,
        @RequestParam("token") String token, LdapBasicConfig ldapBasicConfig, HttpServletRequest request)
        throws InstantiationException, IllegalAccessException, ClassNotFoundException
    {
        super.checkToken(token);
        LogOwner owner = new LogOwner();
        owner.setIp(IpUtils.getClientAddress(request));
        owner.setEnterpriseId(checkAdminAndGetId());
        String[] description = new String[]{getEnterpriseName(), getAuthServerName(authServerId),
            ldapBasicConfig.getDomainControlServer(), String.valueOf(ldapBasicConfig.getLdapPort()),
            ldapBasicConfig.getLdapBaseDN(), ldapBasicConfig.getLdapBindAccount(),
            String.valueOf(ldapBasicConfig.getIsNtlm()), ldapBasicConfig.getNtlmPcAccount(),
            ldapBasicConfig.getNetBiosDomainName(), ldapBasicConfig.getLdapDns()};
        
        LdapDomainConfig ldapDomainConfigDb = authServerConfigManager.getAuthServerObject(authServerId);
        ldapBasicConfig.setLdapBindAccountPassword(PwdConfuser.getAppAdPwd(ldapDomainConfigDb,
            ldapBasicConfig.getLdapBindAccountPassword()));
        ldapBasicConfig.setNtlmPcAccountPasswd(PwdConfuser.getAppNtlmPwd(ldapDomainConfigDb,
            ldapBasicConfig.getNtlmPcAccountPasswd()));
        
        Long enterpriseId = null;
        try
        {
            enterpriseId = checkAdminAndGetId();
            Set violations = validator.validate(ldapBasicConfig);
            if (!violations.isEmpty())
            {
                adminlogManager.saveAdminLog(owner,
                    AdminLogType.KEY_ADMIN_SET_AUTHENTICATION_ERROR,
                    description);
                throw new ConstraintViolationException(violations);
            }
            checkLdapBasicConfig(ldapBasicConfig);
            authServerConfigManager.updateLdapBasciConfig(ldapBasicConfig, authServerId);
        }
        
        catch (ForbiddenException fexception)
        {
            LOGGER.error("[authServer] fexception enterpriseId:" + enterpriseId);
            adminlogManager.saveAdminLog(owner, AdminLogType.KEY_ADMIN_SET_AUTHENTICATION_ERROR, description);
            return new ResponseEntity<String>(fexception.getCode(), fexception.getHttpcode());
        }
        catch (InvalidParamterException iexception)
        {
            LOGGER.error("[authServer] InvalidParamterException enterpriseId:" + enterpriseId);
            adminlogManager.saveAdminLog(owner, AdminLogType.KEY_ADMIN_SET_AUTHENTICATION_ERROR, description);
            return new ResponseEntity<String>(iexception.getCode(), iexception.getHttpcode());
        }
        catch (NoSuchAuthServerException nosuchException)
        {
            LOGGER.error("[authServer] authServer is null" + authServerId);
            adminlogManager.saveAdminLog(owner, AdminLogType.KEY_ADMIN_SET_AUTHENTICATION_ERROR, description);
            return new ResponseEntity<String>(nosuchException.getCode(), nosuchException.getHttpcode());
        }
        catch (Exception exception)
        {
            adminlogManager.saveAdminLog(owner, AdminLogType.KEY_ADMIN_SET_AUTHENTICATION_ERROR, description);
            return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
        }
        adminlogManager.saveAdminLog(owner, AdminLogType.KEY_ADMIN_SET_AUTHENTICATION, description);
        return new ResponseEntity<String>(HttpStatus.OK);
    }
    
    /**
     * config file mapping
     * 
     * @param authServerId
     * @param token
     * @param ldapFiledMapping
     * @param request
     * @return
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws ClassNotFoundException
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    @RequestMapping(value = "configMapping/{authServerId}", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<?> configMappingConfig(@PathVariable Long authServerId,
        @RequestParam("token1") String token, LdapFiledMapping ldapFiledMapping, HttpServletRequest request)
        throws InstantiationException, IllegalAccessException, ClassNotFoundException
    {
        LogOwner owner = new LogOwner();
        owner.setIp(IpUtils.getClientAddress(request));
        String[] description = new String[]{getEnterpriseName(), getAuthServerName(authServerId),
            ldapFiledMapping.getUniqueKey(), ldapFiledMapping.getLoginName(), ldapFiledMapping.getName(),
            ldapFiledMapping.getEmail(), ldapFiledMapping.getDescription(),
            ldapFiledMapping.getTestUserName()};
        Long enterpriseId = null;
        try
        {
            enterpriseId = checkAdminAndGetId();
            owner.setEnterpriseId(enterpriseId);
            super.checkToken(token);
            Set violations = validator.validate(ldapFiledMapping);
            if (!violations.isEmpty())
            {
                adminlogManager.saveAdminLog(owner, AdminLogType.KEY_ADMIN_FIELD_MAPPING_ERROR, description);
                throw new ConstraintViolationException(violations);
            }
            authServerConfigManager.updateLdapFiledMapping(ldapFiledMapping, authServerId);
            adminlogManager.saveAdminLog(owner, AdminLogType.KEY_ADMIN_FIELD_MAPPING, description);
        }
        
        catch (ForbiddenException fexception)
        {
            LOGGER.error("[authServer] fexception enterpriseId:" + enterpriseId);
            adminlogManager.saveAdminLog(owner, AdminLogType.KEY_ADMIN_FIELD_MAPPING_ERROR, description);
            return new ResponseEntity<String>(fexception.getCode(), fexception.getHttpcode());
        }
        catch (InvalidParamterException iexception)
        {
            LOGGER.error("[authServer] InvalidParamterException enterpriseId:" + enterpriseId);
            adminlogManager.saveAdminLog(owner, AdminLogType.KEY_ADMIN_FIELD_MAPPING_ERROR, description);
            return new ResponseEntity<String>(iexception.getCode(), iexception.getHttpcode());
        }
        catch (NoSuchAuthServerException nosuchException)
        {
            LOGGER.error("[authServer] authServer is null" + authServerId);
            adminlogManager.saveAdminLog(owner, AdminLogType.KEY_ADMIN_FIELD_MAPPING_ERROR, description);
            return new ResponseEntity<String>(nosuchException.getCode(), nosuchException.getHttpcode());
        }
        catch (Exception exception)
        {
            adminlogManager.saveAdminLog(owner, AdminLogType.KEY_ADMIN_FIELD_MAPPING_ERROR, description);
            return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<String>(HttpStatus.OK);
    }
    
    @SuppressWarnings({"rawtypes", "unchecked", "unused"})
    @RequestMapping(value = "configNodeFilter/{authServerId}", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<?> configNodeFilter(@PathVariable Long authServerId,
        @RequestParam("token4") String token, LdapNodeFilterConfig ldapNodeFilterConfig,
        HttpServletRequest request) throws InstantiationException, IllegalAccessException,
        ClassNotFoundException
    {
        LogOwner owner = new LogOwner();
        owner.setIp(IpUtils.getClientAddress(request));
        owner.setEnterpriseId(checkAdminAndGetId());
        String[] description = new String[]{getEnterpriseName(), getAuthServerName(authServerId),
            ldapNodeFilterConfig.getSyncNode(), ldapNodeFilterConfig.getUserObjectClass(),
            ldapNodeFilterConfig.getGroupObjectClass(), ldapNodeFilterConfig.getMemberFlag(),
            String.valueOf(ldapNodeFilterConfig.getIsTimingSync()), ldapNodeFilterConfig.getSyncCycle()};
        Long enterpriseId = null;
        try
        {
            enterpriseId = checkAdminAndGetId();
            super.checkToken(token);
            
            if ((StringUtils.isBlank(ldapNodeFilterConfig.getGroupObjectClass()) && StringUtils.isNotBlank(ldapNodeFilterConfig.getMemberFlag()))
                || (StringUtils.isNotBlank(ldapNodeFilterConfig.getGroupObjectClass()) && StringUtils.isBlank(ldapNodeFilterConfig.getMemberFlag())))
            {
                LOGGER.error("Paramter UserObjectClass and GroupObjectClass error groupObjectClass:"
                    + ldapNodeFilterConfig.getGroupObjectClass() + " memberFlag:"
                    + ldapNodeFilterConfig.getMemberFlag());
                adminlogManager.saveAdminLog(owner,
                    AdminLogType.KEY_ADMIN_CONFIG_NODE_FILTER_ERROR,
                    description);
                throw new ConstraintViolationException(null);
            }
            Set violations = validator.validate(ldapNodeFilterConfig);
            if (!violations.isEmpty())
            {
                adminlogManager.saveAdminLog(owner,
                    AdminLogType.KEY_ADMIN_CONFIG_NODE_FILTER_ERROR,
                    description);
                throw new ConstraintViolationException(violations);
            }
            String lowerSyncNode = ldapNodeFilterConfig.getSyncNode();
            String lowerDisplayNode = ldapNodeFilterConfig.getDisplayNode();
            check(ldapNodeFilterConfig.getSyncCycle());
            if (StringUtils.isNotBlank(lowerSyncNode))
            {
                lowerSyncNode = lowerSyncNode.toLowerCase(Locale.ENGLISH);
                ldapNodeFilterConfig.setSyncNode(lowerSyncNode);
            }
            if (StringUtils.isNotBlank(lowerDisplayNode))
            {
                lowerDisplayNode = lowerDisplayNode.toLowerCase(Locale.ENGLISH);
                ldapNodeFilterConfig.setDisplayNode(lowerDisplayNode);
            }
            authServerConfigManager.updateLdapNodeFilterConfig(ldapNodeFilterConfig, authServerId);
            authServerJobManager.addJobManager(ldapNodeFilterConfig, authServerId);
            adminlogManager.saveAdminLog(owner, AdminLogType.KEY_ADMIN_CONFIG_NODE_FILTER, description);
        }
        catch (ForbiddenException fexception)
        {
            LOGGER.error("[authServer] fexception enterpriseId:" + enterpriseId);
            adminlogManager.saveAdminLog(owner, AdminLogType.KEY_ADMIN_CONFIG_NODE_FILTER_ERROR, description);
            return new ResponseEntity<String>(fexception.getCode(), fexception.getHttpcode());
        }
        catch (InvalidParamterException iexception)
        {
            LOGGER.error("[authServer] InvalidParamterException enterpriseId:" + enterpriseId);
            adminlogManager.saveAdminLog(owner, AdminLogType.KEY_ADMIN_CONFIG_NODE_FILTER_ERROR, description);
            return new ResponseEntity<String>(iexception.getCode(), iexception.getHttpcode());
        }
        catch (NoSuchAuthServerException nosuchException)
        {
            LOGGER.error("[authServer] authServer is null" + authServerId);
            adminlogManager.saveAdminLog(owner, AdminLogType.KEY_ADMIN_CONFIG_NODE_FILTER_ERROR, description);
            return new ResponseEntity<String>(nosuchException.getCode(), nosuchException.getHttpcode());
        }
        catch (JobException jobException)
        {
            LOGGER.error("[authServer] jobException" + jobException.getMessage());
            adminlogManager.saveAdminLog(owner, AdminLogType.KEY_ADMIN_CONFIG_NODE_FILTER_ERROR, description);
        }
        catch (Exception exception)
        {
            adminlogManager.saveAdminLog(owner, AdminLogType.KEY_ADMIN_CONFIG_NODE_FILTER_ERROR, description);
        }
        return new ResponseEntity<String>(HttpStatus.OK);
    }
    
    @RequestMapping(value = "filterNode/{authServerId}", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<?> filterNode(@RequestParam("token3") String token,
        @PathVariable Long authServerId, LdapBasicConfig ldapBasicConfig, HttpServletRequest request)
    {
        String[] description = null;
        if (ldapBasicConfig != null)
        {
            description = new String[]{getEnterpriseName(), getAuthServerName(authServerId),
                ldapBasicConfig.getLdapNodeBaseDN(), ldapBasicConfig.getSearchNodeFilter()};
        }
        String id = systemLogManager.save(request,
            OperateType.FilterNode,
            OperateDescription.AUTHSERVER_FILTER_NODE,
            null,
            description);
        
        Long enterpriseId = null;
        TreeNode treeNode = null;
        try
        {
            enterpriseId = checkAdminAndGetId();
            super.checkToken(token);
            if (ldapBasicConfig != null)
            {
                if (StringUtils.isBlank(ldapBasicConfig.getLdapNodeBaseDN())
                    || StringUtils.isBlank(ldapBasicConfig.getSearchNodeFilter())
                    || ldapBasicConfig.getLdapNodeBaseDN().length() > 2048
                    || ldapBasicConfig.getSearchNodeFilter().length() > 2048)
                {
                    LOGGER.error("Paramter NodeFilter error LdapNodeBaseDN:"
                        + ldapBasicConfig.getLdapNodeBaseDN() + " SearchNodeFilter:"
                        + ldapBasicConfig.getSearchNodeFilter());
                    throw new ConstraintViolationException(null);
                }
                LdapDomainConfig ldapDomainConfig = authServerConfigManager.updateFilterNode(ldapBasicConfig,
                    authServerId);
                if (null == ldapDomainConfig)
                {
                    LOGGER.warn("[authServer] ldapDomainConfig is null authServerId:" + authServerId);
                    return new ResponseEntity<TreeNode>(HttpStatus.OK);
                }
                treeNode = new TreeNode();
                treeNode.setName(ldapDomainConfig.getLdapBasicConfig().getLdapNodeBaseDN());
                treeNode.setChecked(false);
                
                String ldapNodeBaseDn = ldapDomainConfig.getLdapBasicConfig().getLdapNodeBaseDN();
                if (authServerConfigManager.isSyncAndDisplayNode(ldapDomainConfig,
                    authServerId,
                    ldapNodeBaseDn))
                {
                    treeNode.setChecked(true);
                }
            }
        }
        
        catch (ForbiddenException fexception)
        {
            LOGGER.error("[authServer] fexception enterpriseId:" + enterpriseId);
            return new ResponseEntity<String>(fexception.getCode(), fexception.getHttpcode());
        }
        catch (InvalidParamterException iexception)
        {
            LOGGER.error("[authServer] InvalidParamterException enterpriseId:" + enterpriseId);
            return new ResponseEntity<String>(iexception.getCode(), iexception.getHttpcode());
        }
        catch (NoSuchAuthServerException nosuchException)
        {
            LOGGER.error("[authServer] authServer is null" + authServerId);
            return new ResponseEntity<String>(nosuchException.getCode(), nosuchException.getHttpcode());
        }
        systemLogManager.updateSuccess(id);
        return new ResponseEntity<TreeNode>(treeNode, HttpStatus.OK);
    }
    
    @RequestMapping(value = "userSearchRule/{authServerId}", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<?> userSearchRule(@RequestParam("token5") String token,
        @PathVariable Long authServerId, LdapBasicConfig ldapBasicConfig, HttpServletRequest request)
    {
        String[] description = null;
        if (ldapBasicConfig != null)
        {
            description = new String[]{getEnterpriseName(), getAuthServerName(authServerId),
                ldapBasicConfig.getSearchFilter(), ldapBasicConfig.getSyncFilter(),
                String.valueOf(ldapBasicConfig.getPageCount())};
        }
        LogOwner owner = new LogOwner();
        owner.setEnterpriseId(checkAdminAndGetId());
        owner.setIp(IpUtils.getClientAddress(request));
        
        Long enterpriseId = null;
        boolean result = false;
        try
        {
            enterpriseId = checkAdminAndGetId();
            super.checkToken(token);
            if (ldapBasicConfig != null)
            {
                if (StringUtils.isBlank(ldapBasicConfig.getSearchFilter())
                    || StringUtils.isBlank(ldapBasicConfig.getSyncFilter())
                    || ldapBasicConfig.getSearchFilter().length() > 2048
                    || ldapBasicConfig.getSyncFilter().length() > 2048
                    || (StringUtils.isNotBlank(ldapBasicConfig.getPageCount()) && (Integer.parseInt(ldapBasicConfig.getPageCount()) > 1000 || Integer.parseInt(ldapBasicConfig.getPageCount()) < 1)))
                {
                    LOGGER.error("Paramter UserSearchRule error");
                    throw new ConstraintViolationException(null);
                }
                
                authServerConfigManager.updateSearchRule(ldapBasicConfig, authServerId);
                result = true;
            }
            adminlogManager.saveAdminLog(owner, AdminLogType.KEY_USER_SEARCH_RULE, description);
        }
        
        catch (ForbiddenException fexception)
        {
            LOGGER.error("[authServer] fexception enterpriseId:" + enterpriseId);
            adminlogManager.saveAdminLog(owner, AdminLogType.KEY_USER_SEARCH_RULE_ERROR, description);
            return new ResponseEntity<String>(fexception.getCode(), fexception.getHttpcode());
        }
        catch (InvalidParamterException iexception)
        {
            LOGGER.error("[authServer] InvalidParamterException enterpriseId:" + enterpriseId);
            adminlogManager.saveAdminLog(owner, AdminLogType.KEY_USER_SEARCH_RULE_ERROR, description);
            return new ResponseEntity<String>(iexception.getCode(), iexception.getHttpcode());
        }
        catch (NoSuchAuthServerException nosuchException)
        {
            LOGGER.error("[authServer] authServer is null" + authServerId);
            adminlogManager.saveAdminLog(owner, AdminLogType.KEY_USER_SEARCH_RULE_ERROR, description);
            return new ResponseEntity<String>(nosuchException.getCode(), nosuchException.getHttpcode());
        }
        catch (RuntimeException exception)
        {
            LOGGER.error("[authServer] authServer is null" + authServerId);
            adminlogManager.saveAdminLog(owner, AdminLogType.KEY_USER_SEARCH_RULE_ERROR, description);
            return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
        }
        catch (Exception exception)
        {
            LOGGER.error("[authServer] authServer is null" + authServerId);
            adminlogManager.saveAdminLog(owner, AdminLogType.KEY_USER_SEARCH_RULE_ERROR, description);
            return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<Boolean>(result, HttpStatus.OK);
    }
    
    @RequestMapping(value = "listTreeNode/{authServerId}", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<List<TreeNode>> listTreeNode(@RequestParam("baseDn") String baseDn,
        @RequestParam("page") int page, @PathVariable Long authServerId, String token)
    {
        super.checkToken(token);
        if (StringUtils.isBlank(baseDn))
        {
            throw new InvalidParamterException("baseDn is black");
        }
        List<TreeNode> treeNodeList = ldapAuthServiceManager.getTreeNode(authServerId, baseDn, page);
        
        return new ResponseEntity<List<TreeNode>>(treeNodeList, HttpStatus.OK);
    }
    
    private String getAuthServerName(long authServerId)
    {
        try
        {
            AuthServer authServer = authServerManager.getAuthServer(authServerId);
            return authServer.getName();
        }
        catch (NoSuchAuthServerException e)
        {
            return null;
        }
    }
    
    private void checkLdapBasicConfig(LdapBasicConfig ldapBasicConfig)
    {
        if (!ldapBasicConfig.getIsNtlm())
        {
            return;
        }
        if (StringUtils.isBlank(ldapBasicConfig.getNtlmPcAccount()))
        {
            throw new InvalidParamterException("ntlmPcAccount is blank:" + ldapBasicConfig.getNtlmPcAccount());
        }
        if (StringUtils.isBlank(ldapBasicConfig.getNtlmPcAccountPasswd()))
        {
            throw new InvalidParamterException("ntlmPcAccountPasswd is blank:"
                + ldapBasicConfig.getNtlmPcAccountPasswd());
        }
        if (StringUtils.isBlank(ldapBasicConfig.getNetBiosDomainName()))
        {
            throw new InvalidParamterException("netBiosDomainName is blank:"
                + ldapBasicConfig.getNetBiosDomainName());
        }
        if (StringUtils.isBlank(ldapBasicConfig.getLdapBaseDN()))
        {
            throw new InvalidParamterException("ldapDns is blank:" + ldapBasicConfig.getLdapBaseDN());
        }
    }
    
    public static void check(String syncCycle)
    {
        if (StringUtils.isEmpty(syncCycle))
        {
            return;
        }
        String regex = "([1-5]{1}[0-9]{1}|[0-9]) ([1-5]{1}[0-9]{1}|[0-9]) (2[0-3]{1}|1[0-9]{1}|[0-9]) \\* \\* \\?";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(syncCycle);
        if (!matcher.matches())
        {
            throw new InvalidParamterException("syncCycle exception" + syncCycle);
        }
        
    }
}
