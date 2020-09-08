package com.huawei.sharedrive.uam.authserver.web;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

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
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.util.HtmlUtils;

import com.huawei.sharedrive.uam.authserver.domain.AccountAuthserver;
import com.huawei.sharedrive.uam.authserver.manager.AccountAuthserverManager;
import com.huawei.sharedrive.uam.authserver.manager.AuthServerManager;
import com.huawei.sharedrive.uam.common.web.AbstractCommonController;
import com.huawei.sharedrive.uam.enterprise.manager.EnterpriseAccountManager;
import com.huawei.sharedrive.uam.enterprise.manager.EnterpriseManager;
import com.huawei.sharedrive.uam.enterpriseadminlog.domain.AdminLogType;
import com.huawei.sharedrive.uam.enterpriseadminlog.domain.LogOwner;
import com.huawei.sharedrive.uam.enterpriseadminlog.manager.AdminLogManager;
import com.huawei.sharedrive.uam.exception.BusinessException;
import com.huawei.sharedrive.uam.exception.ErrorCode;
import com.huawei.sharedrive.uam.exception.ForbiddenException;
import com.huawei.sharedrive.uam.exception.InvalidParamterException;
import com.huawei.sharedrive.uam.log.domain.OperateDescription;
import com.huawei.sharedrive.uam.log.domain.OperateType;
import com.huawei.sharedrive.uam.log.manager.SystemLogManager;
import com.huawei.sharedrive.uam.openapi.rest.UserAPIController;
import com.huawei.sharedrive.uam.util.Constants;
import com.huawei.sharedrive.uam.util.PropertiesUtils;

import pw.cdmi.box.domain.Page;
import pw.cdmi.box.domain.PageRequest;
import pw.cdmi.common.domain.AuthServer;
import pw.cdmi.common.domain.enterprise.Enterprise;
import pw.cdmi.common.domain.enterprise.EnterpriseAccount;
import pw.cdmi.core.utils.IpUtils;

@Controller
@RequestMapping(value = "/enterprise/admin/authserver")
public class AuthServerController extends AbstractCommonController
{
    private final static Logger LOGGER = LoggerFactory.getLogger(AuthServerController.class);
    
    private static final Boolean NETWORK_AUTH_STATUS_SWITCH = Boolean.parseBoolean(PropertiesUtils.getProperty("network.Auth.Status.Switch",
        "false"));
    
    @Autowired
    private AuthServerManager authServerManager;
    
    @Autowired
    private AccountAuthserverManager accountAuthserverManager;
    
    @Autowired
    private SystemLogManager systemLogManager;
    
    @Autowired
    private EnterpriseManager enterpriseManager;
    
    @Autowired
    private EnterpriseAccountManager enterpriseAccountManager;
    
    @Autowired
    private AdminLogManager adminLogManager;
    
    /**
     * list authServer by enterprise id
     * 
     * @param enterpriseId
     * @param model
     * @return
     */
    @RequestMapping(value = "enterList", method = RequestMethod.GET)
    public String enterList(Model model)
    {
        long enterpriseId = checkAdminAndGetId();
        Enterprise enterprise = enterpriseManager.getById(enterpriseId);
        if (null == enterprise)
        {
            LOGGER.error("enterprise is null");
        }
        else
        {
            model.addAttribute("networkAuthStatus", 0);
            if (enterprise.getNetworkAuthStatus() != null)
            {
                model.addAttribute("networkAuthStatus", enterprise.getNetworkAuthStatus());
            }
            model.addAttribute("networkAuthStatusSwitch", NETWORK_AUTH_STATUS_SWITCH);
        }
        return "app/authServer/authServer";
    }
    
    /**
     * list authServer by enterprise id
     * 
     * @param enterpriseId
     * @param model
     * @return
     */
    @RequestMapping(value = "list", method = RequestMethod.POST)
    public ResponseEntity<Page<AuthServer>> list(Integer page, HttpServletRequest req, String token)
    {
        super.checkToken(token);
        
        PageRequest request = new PageRequest();
        if (null == page || page < 1)
        {
            page = 1;
        }
        request.setSize(Constants.DEFAULT_OTHER_SIZE);
        request.setPage(page);
        
        Long enterpriseId = null;
        Page<AuthServer> authServers = null;
        try
        {
            enterpriseId = checkAdminAndGetId();
            authServers = authServerManager.getByNoStatus(enterpriseId, request);
            LOGGER.info("[authServer] list authServer by enterpriseId:" + enterpriseId);
        }
        catch (ForbiddenException fexception)
        {
            LOGGER.error("[authServer] fexception enterpriseId:" + enterpriseId);
            return new ResponseEntity<Page<AuthServer>>(authServers, fexception.getHttpcode());
        }
        catch (InvalidParamterException iexception)
        {
            LOGGER.error("[authServer] InvalidParamterException enterpriseId:" + enterpriseId);
            return new ResponseEntity<Page<AuthServer>>(authServers, iexception.getHttpcode());
        }
        AuthServer.htmlEscape(authServers.getContent());
        for (AuthServer iter : authServers.getContent())
        {
            iter.setLdapBasicXml(null);
            iter.setLdapNodesFilterXml(null);
            iter.setFiledMappingXml(null);
        }
        return new ResponseEntity<Page<AuthServer>>(authServers, HttpStatus.OK);
    }
    
    @RequestMapping(value = "create", method = RequestMethod.GET)
    public String enterCreateUser()
    {
        return "app/authServer/createAuthServer";
    }
    
    /**
     * create authServer
     * 
     * @param authServer
     * @return
     */
    @RequestMapping(value = "create", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<?> createAuthServer(AuthServer authServer, HttpServletRequest req, String token)
    {
        super.checkToken(token);
        
        String[] description = new String[]{getEnterpriseName(), authServer.getType(), authServer.getName(),
            authServer.getDescription()};
        Long enterpriseId = null;
        LogOwner owner = new LogOwner();
        try
        {
            enterpriseId = checkAdminAndGetId();
            owner.setEnterpriseId(enterpriseId);
            owner.setIp(IpUtils.getClientAddress(req));
            checkAuthServer(authServer);
            authServer.setEnterpriseId(enterpriseId);
            authServerManager.createAuthServer(authServer);
            
            LOGGER.info("[authServer] create authServer enterpriseId:" + enterpriseId + " id:"
                + authServer.getId());
        }
        catch (ForbiddenException fexception)
        {
            adminLogManager.saveAdminLog(owner, AdminLogType.KEY_ADD_AUTH_ERROR, description);
            LOGGER.error("[authServer] fexception enterpriseId:" + enterpriseId);
            return new ResponseEntity<String>(fexception.getCode(), fexception.getHttpcode());
        }
        catch (InvalidParamterException iexception)
        {
            adminLogManager.saveAdminLog(owner, AdminLogType.KEY_ADD_AUTH_ERROR, description);
            LOGGER.error("[authServer] InvalidParamterException enterpriseId:" + enterpriseId);
            return new ResponseEntity<String>(iexception.getCode(), iexception.getHttpcode());
        }
        adminLogManager.saveAdminLog(owner, AdminLogType.KEY_ADD_AUTH, description);
        return new ResponseEntity<String>(HttpStatus.OK);
    }
    
    /**
     * create authServer
     * 
     * @param authServer
     * @return
     */
    @RequestMapping(value = "delete/{authServerId}", method = RequestMethod.DELETE)
    @ResponseBody
    public ResponseEntity<?> deleteAuthServer(@PathVariable(value = "authServerId") Long authServerId,
        HttpServletRequest req, String token)
    {
        super.checkToken(token);
        String[] description = new String[]{getEnterpriseName(), getAuthServerName(authServerId)};
        String id = systemLogManager.save(req,
            OperateType.AuthserverType,
            OperateDescription.AUTHSERVER_TYPE_DELETE,
            null,
            description);
        
        Long enterpriseId = null;
        try
        {
            enterpriseId = checkAdminAndGetId();
            authServerManager.deleteAuthServer(authServerId);
            LOGGER.info("[authServer] delete authserver authServerId:" + authServerId);
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
        systemLogManager.updateSuccess(id);
        return new ResponseEntity<String>(HttpStatus.OK);
    }
    
    @RequestMapping(value = "updateNetworkAuthStatus/{networkAuthStatus}", method = RequestMethod.POST)
    public String updateNetworkAuthStatus(@PathVariable(value = "networkAuthStatus") Byte networkAuthStatus,
        Model model, HttpServletRequest req, String token)
    {
        super.checkToken(token);
        String isOpen = "false";
        byte networkAuthStatusTemp = 0;
        if (networkAuthStatus == null)
        {
            networkAuthStatus = networkAuthStatusTemp;
        }
        if (networkAuthStatus == 1)
        {
            isOpen = "true";
        }
        String[] description = new String[]{getEnterpriseName(), isOpen};
        String id = systemLogManager.save(req,
            OperateType.AuthserverType,
            OperateDescription.AUTHSERVER_TYPE_NETWORK_AUTH,
            null,
            description);
        
        long enterpriseId = checkAdminAndGetId();
        enterpriseManager.updateNetworkAuthStatus(enterpriseId, networkAuthStatus);
        model.addAttribute("networkAuthStatus", networkAuthStatus);
        
        systemLogManager.updateSuccess(id);
        return "app/authServer/authServer";
    }
    
    /**
     * get bind app list
     * 
     * @param authServerId
     * @return
     */
    @RequestMapping(value = "bindAppList/{authServerId}", method = RequestMethod.GET)
    public String bindAppList(@PathVariable(value = "authServerId") Long authServerId, Model model)
    {
        Long enterpriseId = null;
        List<AccountAuthserver> accountAuthServers = null;
        try
        {
            enterpriseId = checkAdminAndGetId();
            Enterprise enterprise = enterpriseManager.getById(enterpriseId);
            if (null == enterprise)
            {
                LOGGER.error("enterprise is null enterpriseId:" + enterpriseId);
                throw new BusinessException("enterprise is null enterpriseId:" + enterpriseId);
            }
            Byte networkAuthStatus = enterprise.getNetworkAuthStatus();
            model.addAttribute("networkAuthStatus", networkAuthStatus);
            LOGGER.info("[authServer] create authServer enterpriseId:" + enterpriseId + " id:" + authServerId);
            accountAuthServers = accountAuthserverManager.listBindApp(enterpriseId, authServerId);
            AuthServer authServer = authServerManager.getAuthServer(authServerId);
            if (null != authServer)
            {
                model.addAttribute("authType", HtmlUtils.htmlEscape(authServer.getType()));
                model.addAttribute("name", authServer.getName());
            }
        }
        catch (ForbiddenException fexception)
        {
            LOGGER.error("[authServer] fexception enterpriseId:" + enterpriseId);
            return fexception.getCode();
        }
        catch (InvalidParamterException iexception)
        {
            LOGGER.error("[authServer] InvalidParamterException enterpriseId:" + enterpriseId);
            return iexception.getCode();
        }
        
        model.addAttribute("accountAuthServers", accountAuthServers);
        model.addAttribute("authServerId", authServerId);
        
        return "app/authServer/bindApp";
    }
    
    @RequestMapping(value = "bindApp/{authServerId}", method = RequestMethod.GET)
    public String unBindAppList(@PathVariable(value = "authServerId") Long authServerId, Model model)
    {
        Long enterpriseId = null;
        List<AccountAuthserver> accountAuthServers = null;
        try
        {
            enterpriseId = checkAdminAndGetId();
            LOGGER.info("[authServer] create authServer enterpriseId:" + enterpriseId + " id:" + authServerId);
            accountAuthServers = accountAuthserverManager.listUnBindApp(enterpriseId, authServerId);
            AuthServer authServer = authServerManager.getAuthServer(authServerId);
            if (null != authServer)
            {
                model.addAttribute("authType", HtmlUtils.htmlEscape(authServer.getType()));
                model.addAttribute("name", authServer.getName());
            }
        }
        catch (ForbiddenException fexception)
        {
            LOGGER.error("[authServer] fexception enterpriseId:" + enterpriseId);
            return fexception.getCode();
        }
        catch (InvalidParamterException iexception)
        {
            LOGGER.error("[authServer] InvalidParamterException enterpriseId:" + enterpriseId);
            return iexception.getCode();
        }
        
        model.addAttribute("accountAuthServers", accountAuthServers);
        model.addAttribute("authServerId", authServerId);
        
        return "app/authServer/createBindApp";
    }
    
    /**
     * create bind app
     * 
     * @param accountIds
     * @param type
     * @return
     */
    @RequestMapping(value = "bindApp", method = RequestMethod.POST)
    public ResponseEntity<?> bindApp(String accountIds, Long authServerId, HttpServletRequest req,
        String token)
    {
        super.checkToken(token);
        Long enterpriseId = checkAdminAndGetId();
        LogOwner owner = new LogOwner();
        owner.setEnterpriseId(enterpriseId);
        owner.setIp(IpUtils.getClientAddress(req));
        
        String[] accountAuthServerStr = accountIds.split(";");
        LOGGER.info("[authServer] accountAuthServers :" + accountIds);
        String accountAuth;
        String[] accountAuthArg;
        
        String typeDetail;
        for (int i = 0; i < accountAuthServerStr.length; i++)
        {
            accountAuth = accountAuthServerStr[i];
            accountAuthArg = accountAuth.split(",");
            if (accountAuthArg.length != 2)
            {
                adminLogManager.saveAdminLog(owner, AdminLogType.KEY_BIND_APP_ERROR, new String[]{
                    getEnterpriseName(), getAuthServerName(authServerId)});
                return new ResponseEntity<String>(ErrorCode.INVALID_PARAMTER.name(), HttpStatus.BAD_REQUEST);
            }
            typeDetail = AdminLogType.KEY_BINDAPP_OPENACCOUNT_OPEN.getDetails();
            
            tryBindApp(authServerId, owner, accountAuthArg, typeDetail);
        }
        return new ResponseEntity<String>(HttpStatus.OK);
    }
    
    @SuppressWarnings("PMD.PreserveStackTrace")
    private void tryBindApp(Long authServerId, LogOwner owner, String[] accountAuthArg, String typeDetail)
    {
        EnterpriseAccount enterpriseAccount;
        String[] description;
        Long accountId = null;
        byte type;
        try
        {
            accountId = Long.parseLong(accountAuthArg[0]);
            type = Byte.parseByte(accountAuthArg[1]);
            
            if (type == 1)
            {
                typeDetail = AdminLogType.KEY_AUTHORIZE_ROUNCECREATEACCOUNT.getDetails();
            }
            enterpriseAccount = enterpriseAccountManager.getByAccountId(accountId);
            String appId = null;
            if (null != enterpriseAccount)
            {
                appId = enterpriseAccount.getAuthAppId();
                owner.setAppId(appId);
            }
            description = new String[]{getEnterpriseName(), getAuthServerName(authServerId), typeDetail,
                appId};
        }
        catch (NumberFormatException e)
        {
            description = new String[]{getEnterpriseName(), getAuthServerName(authServerId), typeDetail};
            adminLogManager.saveAdminLog(owner, AdminLogType.KEY_BIND_APP_ERROR, description);
            throw new InvalidParamterException(e.getMessage());
        }
        accountAuthserverManager.bindApp(authServerId, accountId, type);
        adminLogManager.saveAdminLog(owner, AdminLogType.KEY_BIND_APP, description);
    }
    
    @RequestMapping(value = "entermodifyBindApp/{accountId}/{authServerId}", method = RequestMethod.GET)
    public String entermodifyBindApp(@PathVariable(value = "accountId") Long accountId,
        @PathVariable(value = "authServerId") Long authServerId, Model model)
    {
        AccountAuthserver accountAuthserver = accountAuthserverManager.getByAccountAuthId(accountId,
            authServerId);
        AuthServer authServer = authServerManager.getAuthServer(authServerId);
        model.addAttribute("accountAuthserver", accountAuthserver);
        model.addAttribute("authServer", authServer);
        return "app/authServer/modifyBindApp";
    }
    
    @RequestMapping(value = "modifyBindApp", method = RequestMethod.POST)
    public ResponseEntity<?> modifyBindApp(AccountAuthserver accountAuthserver, HttpServletRequest req,
        String token)
    {
        super.checkToken(token);
        EnterpriseAccount enterpriseAccount = enterpriseAccountManager.getByAccountId(accountAuthserver.getAccountId());
        String appId = null;
        if (null != enterpriseAccount)
        {
            appId = enterpriseAccount.getAuthAppId();
        }
        else
        {
            LOGGER.error("modify bind app fail,enterpriseAccount is null");
            return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
        }
        
        Long enterpriseId = checkAdminAndGetId();
        LogOwner owner = new LogOwner();
        owner.setEnterpriseId(enterpriseId);
        owner.setIp(IpUtils.getClientAddress(req));
        owner.setAppId(appId);
        
        int authServerType = accountAuthserver.getType();
        String authServerTypeDetail = null;
        
        if (authServerType == 1)
        {
            authServerTypeDetail = AdminLogType.KEY_AUTHORIZE_ROUNCECREATEACCOUNT.getDetails();
        }
        else if (authServerType == 2)
        {
            authServerTypeDetail = AdminLogType.KEY_BINDAPP_OPENACCOUNT_OPEN.getDetails();
        }
        else
        {
            LOGGER.error("modify bind app fail,authServerType error");
            adminLogManager.saveAdminLog(owner, AdminLogType.KEY_UPDAYE_OPENACCT_ERROR, new String[]{" ",
                getEnterpriseName(), getAuthServerName(accountAuthserver.getAuthServerId()), appId});
            return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
        }
        String[] description = new String[]{authServerTypeDetail, getEnterpriseName(),
            getAuthServerName(accountAuthserver.getAuthServerId()), appId};
        try
        {
            accountAuthserverManager.bindApp(accountAuthserver.getAuthServerId(),
                accountAuthserver.getAccountId(),
                accountAuthserver.getType());
        }
        catch (Exception e)
        {
            LOGGER.error("update open account way fail");
            adminLogManager.saveAdminLog(owner, AdminLogType.KEY_UPDAYE_OPENACCT_ERROR, description);
            return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
        }
        adminLogManager.saveAdminLog(owner, AdminLogType.KEY_UPDAYE_OPENACCT, description);
        return new ResponseEntity<String>(HttpStatus.OK);
    }
    
    @RequestMapping(value = "unbindApp", method = RequestMethod.POST)
    public ResponseEntity<?> unbindApp(Long accountId, Long authServerId, HttpServletRequest req, String token)
    {
        super.checkToken(token);
        LogOwner owner = new LogOwner();
        owner.setEnterpriseId(checkAdminAndGetId());
        owner.setIp(IpUtils.getClientAddress(req));
        try
        {
            EnterpriseAccount enterpriseAccount = enterpriseAccountManager.getByAccountId(accountId);
            String appId = null;
            if (null != enterpriseAccount)
            {
                appId = enterpriseAccount.getAuthAppId();
            }
            else
            {
                LOGGER.error("unbind app fail");
                return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
            }
            owner.setAppId(appId);
            String[] description = new String[]{getEnterpriseName(), getAuthServerName(authServerId), appId};
            int result = accountAuthserverManager.unBindApp(authServerId, accountId);
            if (result < 1)
            {
                LOGGER.error("unbind app fail");
                adminLogManager.saveAdminLog(owner, AdminLogType.KEY_DELETE_BIND_APP_ERROR, description);
                return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
            }
            adminLogManager.saveAdminLog(owner, AdminLogType.KEY_DELETE_BIND_APP, description);
        }
        catch (RuntimeException e)
        {
            LOGGER.error("unbind app fail");
            adminLogManager.saveAdminLog(owner, AdminLogType.KEY_DELETE_BIND_APP_ERROR, new String[]{
                getEnterpriseName(), getAuthServerName(authServerId)});
            return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
        }
        accountAuthserverManager.unBindApp(authServerId, accountId);
        return new ResponseEntity<String>(HttpStatus.OK);
    }
    
    @RequestMapping(value = "changeStatus", method = RequestMethod.POST)
    public ResponseEntity<?> changeStatus(long id, byte status, HttpServletRequest request, String token)
    {
        super.checkToken(token);
        Long enterpriseId = checkAdminAndGetId();
        LogOwner owner = new LogOwner();
        owner.setEnterpriseId(enterpriseId);
        owner.setIp(IpUtils.getClientAddress(request));
        AdminLogType adminLogType;
        if (0 == status)
        {
            adminLogType = AdminLogType.getAdminLogType(AdminLogType.KEY_START_AUTH.getTypeCode());
        }
        else if (1 == status)
        {
            adminLogType = AdminLogType.getAdminLogType(AdminLogType.KEY_STOP_AUTH.getTypeCode());
        }
        else
        {
            adminLogType = AdminLogType.getAdminLogType(AdminLogType.KEY_CHANGE_AUTH_STATUS_ERROR.getTypeCode());
            adminLogManager.saveAdminLog(owner, adminLogType, new String[]{getEnterpriseName(),
                getAuthServerName(id)});
            return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
        }
        AuthServer authServer = authServerManager.getAuthServerNoStatus(id);
        String[] description = new String[]{getEnterpriseName(), authServer.getType(), getAuthServerName(id),
            authServer.getDescription()};
        try
        {
            authServerManager.updateStatus(id, status);
            adminLogManager.saveAdminLog(owner, adminLogType, description);
        }
        catch (RuntimeException e)
        {
            LOGGER.error("stop auth fail");
            adminLogManager.saveAdminLog(owner, adminLogType, description);
            return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
        }
        authServerManager.updateStatus(id, status);
        return new ResponseEntity<String>(HttpStatus.OK);
    }
    
    private String getAuthServerName(long authServerId)
    {
        try
        {
            AuthServer authServer = authServerManager.getAuthServerNoStatus(authServerId);
            return authServer.getName();
        }
        catch (Exception e)
        {
            return null;
        }
    }
    
    private void checkAuthServer(AuthServer authServer)
    {
        if (null == authServer)
        {
            throw new InvalidParamterException("authServer is null");
        }
        checkAuthServerType(authServer);
        checkAuthServerName(authServer);
        if (authServer.getDescription().length() > 255)
        {
            throw new InvalidParamterException("description is invalid:" + authServer.getDescription());
        }
    }
    
    private void checkAuthServerName(AuthServer authServer)
    {
        if (StringUtils.isBlank(authServer.getName()))
        {
            throw new InvalidParamterException("name is blank:" + authServer.getName());
        }
        if (authServer.getName().length() > 64)
        {
            throw new InvalidParamterException("name is invalid:" + authServer.getName());
        }
    }
    
    private void checkAuthServerType(AuthServer authServer)
    {
        if (StringUtils.isBlank(authServer.getType()))
        {
            throw new InvalidParamterException("type is blank:" + authServer.getType());
        }
        if (authServer.getType().length() > 64)
        {
            throw new InvalidParamterException("type is invalid:" + authServer.getType());
        }
        if (!UserAPIController.AUTH_TYPE_LDAP.equals(authServer.getType())
            && !UserAPIController.AUTH_TYPE_AD.equals(authServer.getType()))
        {
            throw new InvalidParamterException("type is invalid:" + authServer.getType());
        }
    }
    
}
