package com.huawei.sharedrive.uam.authserver.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.huawei.sharedrive.uam.authserver.manager.AuthServerConfigManager;
import com.huawei.sharedrive.uam.authserver.manager.AuthServerTestingManager;
import com.huawei.sharedrive.uam.common.web.AbstractCommonController;
import com.huawei.sharedrive.uam.enterpriseadminlog.domain.AdminLogType;
import com.huawei.sharedrive.uam.enterpriseadminlog.domain.LogOwner;
import com.huawei.sharedrive.uam.enterpriseadminlog.manager.AdminLogManager;
import com.huawei.sharedrive.uam.system.service.impl.PwdConfuser;

import pw.cdmi.common.domain.enterprise.ldap.LdapBasicConfig;
import pw.cdmi.common.domain.enterprise.ldap.LdapDomainConfig;
import pw.cdmi.common.domain.enterprise.ldap.LdapFiledMapping;
import pw.cdmi.core.utils.IpUtils;

@Controller
@RequestMapping(value = "/enterprise/admin/testauthserver")
public class AuthServerTestingController extends AbstractCommonController
{
    public final static String UAM_LDAP_BASIC_CONFIG = "uam_ldap_basic_config_";
    
    @Autowired
    private AuthServerTestingManager authServerTestingManager;
    
    @Autowired
    private AuthServerConfigManager authServerConfigManager;
    
    @Autowired
    private AdminLogManager adminLogManager;
    
    /**
     * 
     * @param serverId
     * @return
     */
    @RequestMapping(value = "testBasicConfig", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<Boolean> testAuthServer(@RequestParam("token") String token,
        LdapBasicConfig ldapBasicConfig, Long authServerId, HttpServletRequest request, HttpSession session)
    {
        super.checkToken(token);
        LogOwner owner = new LogOwner();
        Long enterpriseId = checkAdminAndGetId();
        owner.setEnterpriseId(enterpriseId);
        owner.setIp(IpUtils.getClientAddress(request));
        if (null != authServerId)
        {
            LdapDomainConfig ldapDomainConfigDb = authServerConfigManager.getAuthServerObject(authServerId);
            ldapBasicConfig.setLdapBindAccountPassword(PwdConfuser.getAppAdPwd(ldapDomainConfigDb,
                ldapBasicConfig.getLdapBindAccountPassword()));
        }
        boolean result = false;
        if (null != ldapBasicConfig)
        {
            LdapDomainConfig ldapConfig = new LdapDomainConfig();
            ldapConfig.setLdapBasicConfig(ldapBasicConfig);
            result = authServerTestingManager.checkAuthConfig(ldapConfig);
        }
        if (result)
        {
            adminLogManager.saveAdminLog(owner, AdminLogType.KEY_TEST_AUTH, null);
        }
        else
        {
            adminLogManager.saveAdminLog(owner, AdminLogType.KEY_TEST_AUTH_ERROR, null);
        }
        
        return new ResponseEntity<Boolean>(result, HttpStatus.OK);
    }
    
    /**
     * 
     * @param serverId
     * @return
     */
    @RequestMapping(value = "testFiledMapping/{authServerId}", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<Boolean> testFiledMapping(@RequestParam("token1") String token,
        @PathVariable Long authServerId, LdapFiledMapping ldapFiledMapping, HttpServletRequest request)
    {
        super.checkToken(token);
        LogOwner owner = new LogOwner();
        Long enterpriseId = checkAdminAndGetId();
        owner.setEnterpriseId(enterpriseId);
        owner.setIp(IpUtils.getClientAddress(request));
        
        boolean result = false;
        
        if (null != ldapFiledMapping)
        {
            LdapDomainConfig config = authServerConfigManager.getAuthServerObject(authServerId);
            if (null == config || null == config.getLdapBasicConfig())
            {
                adminLogManager.saveAdminLog(owner, AdminLogType.KEY_TEST_FILED_MAPPIN_ERROR, null);
                return new ResponseEntity<Boolean>(result, HttpStatus.OK);
            }
            config.setLdapFiledMapping(ldapFiledMapping);
            result = authServerTestingManager.checkFiledMapping(config, authServerId);
        }
        if (result)
        {
            adminLogManager.saveAdminLog(owner, AdminLogType.KEY_TEST_FILED_MAPPIN, null);
        }
        else
        {
            adminLogManager.saveAdminLog(owner, AdminLogType.KEY_TEST_FILED_MAPPIN_ERROR, null);
        }
        
        return new ResponseEntity<Boolean>(result, HttpStatus.OK);
    }
    
    /**
     * 
     * @param serverId
     * @return
     */
    @RequestMapping(value = "testNtml", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<Boolean> testNtml(@RequestParam("token") String token,
        LdapBasicConfig ldapBasicConfig, Long authServerId, HttpServletRequest request, HttpSession session)
    {
        super.checkToken(token);
        LogOwner owner = new LogOwner();
        Long enterpriseId = checkAdminAndGetId();
        owner.setEnterpriseId(enterpriseId);
        owner.setIp(IpUtils.getClientAddress(request));
        boolean result = false;
        
        if (null != ldapBasicConfig)
        {
            LdapDomainConfig ldapConfig = new LdapDomainConfig();
            ldapConfig.setLdapBasicConfig(ldapBasicConfig);
            if (null != authServerId)
            {
                LdapDomainConfig ldapDomainConfigDb = authServerConfigManager.getAuthServerObject(authServerId);
                ldapBasicConfig.setNtlmPcAccountPasswd(PwdConfuser.getAppNtlmPwd(ldapDomainConfigDb,
                    ldapBasicConfig.getNtlmPcAccountPasswd()));
            }
            result = authServerTestingManager.checkNtlmServer(ldapConfig);
        }
        if (result)
        {
            adminLogManager.saveAdminLog(owner, AdminLogType.KEY_TEST_NTML, null);
        }
        else
        {
            adminLogManager.saveAdminLog(owner, AdminLogType.KEY_TEST_NTML_ERROR, null);
        }
        return new ResponseEntity<Boolean>(result, HttpStatus.OK);
    }
    
}
