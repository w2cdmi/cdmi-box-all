package com.huawei.sharedrive.uam.enterpriseuser.web;

import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.shiro.SecurityUtils;
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
import org.springframework.web.servlet.support.RequestContextUtils;
import org.springframework.web.util.HtmlUtils;

import com.huawei.sharedrive.uam.authserver.manager.AuthServerConfigManager;
import com.huawei.sharedrive.uam.authserver.manager.AuthServerManager;
import com.huawei.sharedrive.uam.common.web.AbstractCommonController;
import com.huawei.sharedrive.uam.enterprise.manager.EnterpriseAccountManager;
import com.huawei.sharedrive.uam.enterpriseadminlog.domain.AdminLogType;
import com.huawei.sharedrive.uam.enterpriseadminlog.domain.EnterpriseAdminLog;
import com.huawei.sharedrive.uam.enterpriseadminlog.domain.LogOwner;
import com.huawei.sharedrive.uam.enterpriseadminlog.domain.QueryCondition;
import com.huawei.sharedrive.uam.enterpriseadminlog.manager.AdminLogManager;
import com.huawei.sharedrive.uam.enterpriseuser.domain.EnterpriseUser;
import com.huawei.sharedrive.uam.enterpriseuser.domain.LdapCheck;
import com.huawei.sharedrive.uam.enterpriseuser.manager.EnterpriseUserManager;
import com.huawei.sharedrive.uam.enterpriseuser.manager.LdapCheckManager;
import com.huawei.sharedrive.uam.enterpriseuser.manager.SyncEnterpriseUserManager;
import com.huawei.sharedrive.uam.enterpriseuser.manager.impl.SyncDeleteEnterpriseUserThread;
import com.huawei.sharedrive.uam.exception.ForbiddenException;
import com.huawei.sharedrive.uam.exception.InvalidParamterException;
import com.huawei.sharedrive.uam.ldapauth.manager.impl.LdapAuthServiceManagerImpl;
import com.huawei.sharedrive.uam.log.domain.OperateDescription;
import com.huawei.sharedrive.uam.log.domain.OperateType;
import com.huawei.sharedrive.uam.log.manager.SystemLogManager;
import com.huawei.sharedrive.uam.system.domain.TreeNode;
import com.huawei.sharedrive.uam.user.domain.Admin;
import com.huawei.sharedrive.uam.util.Constants;

import pw.cdmi.box.domain.Limit;
import pw.cdmi.box.domain.Page;
import pw.cdmi.box.domain.PageImpl;
import pw.cdmi.box.domain.PageRequest;
import pw.cdmi.common.domain.AuthServer;
import pw.cdmi.common.domain.enterprise.ldap.LdapBasicConfig;
import pw.cdmi.common.domain.enterprise.ldap.LdapDomainConfig;
import pw.cdmi.core.utils.IpUtils;

@Controller
@RequestMapping(value = "/enterprise/admin/ldapuser")
public class EnterpriseLdapUserController extends AbstractCommonController
{
    private final static Logger LOGGER = LoggerFactory.getLogger(EnterpriseLdapUserController.class);
    
    @Autowired
    private AuthServerConfigManager authServerConfigManager;
    
    @Autowired
    private LdapAuthServiceManagerImpl ldapAuthServiceManager;
    
    @Autowired
    private AuthServerManager authServerManager;
    
    @Autowired
    private SyncEnterpriseUserManager syncEnterpriseUserManager;
    
    @Autowired
    private LdapCheckManager ldapCheckManager;
    
    @Autowired
    private EnterpriseUserManager enterpriseUserManager;
    
    @Autowired
    private SystemLogManager systemLogManager;
    
    @Autowired
    private AdminLogManager adminLogManager;
    
    @Autowired
    private EnterpriseAccountManager enterpriseAccountManager;
    
    @RequestMapping(value = "syncUsers/{authServerId}", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<?> syncUsers(@PathVariable Long authServerId, HttpServletRequest req, String token)
    {
        super.checkToken(token);
        AuthServer authServer = authServerManager.getAuthServer(authServerId);
        LogOwner logOwner = new LogOwner();
        Admin admin = (Admin) SecurityUtils.getSubject().getPrincipal();
        logOwner.setIp(IpUtils.getClientAddress(req));
        logOwner.setLoginName(admin.getLoginName());
        if (null == authServer)
        {
            adminLogManager.saveAdminLog(logOwner, AdminLogType.KEY_SYNCUSERS_USER_VALIDATE, null);
            LOGGER.error("no such authServerId:" + authServerId);
            throw new InvalidParamterException("no such authServerId:" + authServerId);
        }
        String[] description = new String[]{authServer.getName()};
        if (StringUtils.equals(AuthServer.AUTH_TYPE_LOCAL, authServer.getType()))
        {
            adminLogManager.saveAdminLog(logOwner, AdminLogType.KEY_SYNCUSERS_USER_VALIDATE, description);
            throw new InvalidParamterException("wrong authServerType:" + authServer.getType());
        }
        try
        {
            checkAdminAndGetId();
        }
        catch (ForbiddenException e)
        {
            adminLogManager.saveAdminLog(logOwner, AdminLogType.KEY_SYNCUSERS_USER_VALIDATE, description);
            throw e;
        }
        catch (InvalidParamterException e)
        {
            adminLogManager.saveAdminLog(logOwner, AdminLogType.KEY_SYNCUSERS_USER_VALIDATE, description);
            throw e;
        }
        try
        {
            syncEnterpriseUserManager.syncEnterpriseUser(authServerId, logOwner);
        }
        catch (Exception e)
        {
            adminLogManager.saveAdminLog(logOwner, AdminLogType.KEY_SYNCUSERS_USER_ERROR, description);
            return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<String>(HttpStatus.OK);
    }
    
    @RequestMapping(value = "syncDeleteUsers", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<?> syncDeleteUsers(HttpServletRequest req, String token)
    {
        super.checkToken(token);
        String[] description = new String[]{getEnterpriseName()};
        String logId = systemLogManager.save(req,
            OperateType.EnterpriseEmployees,
            OperateDescription.ENTERPRISE_EMPLOYEES_CLEAR_CHECK,
            null,
            description);
        
        Long enterpriseId = checkAdminAndGetId();
        SyncDeleteEnterpriseUserThread syncDeleteEnterpriseUserThread = new SyncDeleteEnterpriseUserThread(
            enterpriseId, ldapCheckManager, syncEnterpriseUserManager);
        new Thread(syncDeleteEnterpriseUserThread).start();
        systemLogManager.updateSuccess(logId);
        return new ResponseEntity<String>(HttpStatus.OK);
    }
    
    @RequestMapping(value = "getDeleteUsers", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<Page<EnterpriseUser>> getDeleteUsers(Integer page, String token)
    {
        super.checkToken(token);
        Long enterpriseId = checkAdminAndGetId();
        
        Limit limit = new Limit();
        long offset = 0;
        int pageTemp = 0;
        if (page == null)
        {
            page = pageTemp;
        }
        if (page > 0)
        {
            offset = (page - 1) * Constants.DEFAULT_OTHER_SIZE;
        }
        limit.setOffset(offset);
        limit.setLength(Constants.DEFAULT_OTHER_SIZE);
        
        List<EnterpriseUser> enterpriseUserList = syncEnterpriseUserManager.listByLdapStatus(enterpriseId,
            limit);
        int total = syncEnterpriseUserManager.getByLdapStatusCount(enterpriseId);
        PageRequest request = new PageRequest();
        request.setSize(Constants.DEFAULT_OTHER_SIZE);
        request.setPage(page);
        Page<EnterpriseUser> enterpriseUserPage = new PageImpl<EnterpriseUser>(enterpriseUserList, request,
            total);
        List<EnterpriseUser> listEnterpriseUser = enterpriseUserPage.getContent();
        for (EnterpriseUser user : listEnterpriseUser)
        {
            user.setAlias(HtmlUtils.htmlEscape(user.getAlias()));
            user.setDescription(HtmlUtils.htmlEscape(user.getDescription()));
            user.setEmail(HtmlUtils.htmlEscape(user.getEmail()));
            user.setMobile(HtmlUtils.htmlEscape(user.getMobile()));
            user.setName(HtmlUtils.htmlEscape(user.getName()));
            user.setObjectSid(HtmlUtils.htmlEscape(user.getObjectSid()));
            user.setUserDn(HtmlUtils.htmlEscape(user.getUserDn()));
            user.setValidateKey(HtmlUtils.htmlEscape(user.getValidateKey()));
        }
        return new ResponseEntity<Page<EnterpriseUser>>(enterpriseUserPage, HttpStatus.OK);
    }
    
    @RequestMapping(value = "enterDeleteUsers/{authServerId}", method = RequestMethod.GET)
    public String enterDeleteUsers(@PathVariable Long authServerId, Model model)
    {
        enterpriseAccountManager.bindAppCheck();
        Long enterpriseId = checkAdminAndGetId();
        int total = syncEnterpriseUserManager.getByLdapStatusCount(enterpriseId);
        LdapCheck ldapCheck = ldapCheckManager.get(enterpriseId);
        if (null != ldapCheck)
        {
            ldapCheck.setUserCount(Long.valueOf(total));
        }
        model.addAttribute("ldapCheck", ldapCheck);
        model.addAttribute("authServrName", getAuthServerName(authServerId));
        return "enterprise/admin/user/ldapCheckList";
    }
    
    @RequestMapping(value = "enterSyncUserLog/{authServerId}", method = RequestMethod.GET)
    public String enterSyncUserLog(@PathVariable Long authServerId, Model model)
    {
        enterpriseAccountManager.bindAppCheck();
        model.addAttribute("authServrName", getAuthServerName(authServerId));
        return "enterprise/admin/user/syncUserLog";
    }
    
    @RequestMapping(value = "getSyncUserLog", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<Page<EnterpriseAdminLog>> getSyncUserLog(Integer page, String filter,
        Long authServerId, String token, HttpServletRequest request)
    {
        super.checkToken(token);
        Long enterpriseId = checkAdminAndGetId();
        
        Locale locale = RequestContextUtils.getLocaleResolver(request).resolveLocale(request);
        String language = locale.getLanguage();
        if ("zh_CN".equals(language) || "zh".equals(language))
        {
            locale = Locale.CHINA;
        }
        else
        {
            locale = Locale.ENGLISH;
        }
        QueryCondition qc = new QueryCondition();
        PageRequest pageRequest = new PageRequest();
        pageRequest.setSize(Constants.DEFAULT_OTHER_SIZE);
        if (null != page)
        {
            pageRequest.setPage(page);
        }
        qc.setPageRequest(pageRequest);
        qc.setEnterpriseId(enterpriseId);
        qc.setAuthServerId(authServerId);
        qc.setOperatDesc(filter);
        Page<EnterpriseAdminLog> pageList = adminLogManager.getSyncLog(qc, locale);
        
        return new ResponseEntity<Page<EnterpriseAdminLog>>(pageList, HttpStatus.OK);
    }
    
    @SuppressWarnings("PMD.PreserveStackTrace")
    @RequestMapping(value = "delete", method = RequestMethod.POST)
    public ResponseEntity<?> delete(String ids, HttpServletRequest req, String token)
    {
        super.checkToken(token);
        String[] description = new String[]{getEnterpriseName(), ids};
        long enterpriseId = checkAdminAndGetId();
        LogOwner owner = new LogOwner();
        owner.setEnterpriseId(enterpriseId);
        owner.setIp(IpUtils.getClientAddress(req));
        if (StringUtils.isBlank(ids))
        {
            adminLogManager.saveAdminLog(owner, AdminLogType.KEY_CLEAR_LDAP_USER_ERROR, description);
            LOGGER.error("ids is null");
            throw new InvalidParamterException();
        }
        String[] idArray = ids.split(",");
        try
        {
            for (String iter : idArray)
            {
                enterpriseUserManager.delete(Long.parseLong(iter), enterpriseId);
            }
            adminLogManager.saveAdminLog(owner, AdminLogType.KEY_CLEAR_LDAP_USER, description);
        }
        catch (RuntimeException e)
        {
            adminLogManager.saveAdminLog(owner, AdminLogType.KEY_CLEAR_LDAP_USER_ERROR, description);
            throw new InvalidParamterException();
        }
        return new ResponseEntity<String>(HttpStatus.OK);
    }
    
    @RequestMapping(value = "deleteAll", method = RequestMethod.POST)
    public ResponseEntity<?> deleteAll(HttpServletRequest req, String token)
    {
        super.checkToken(token);
        String[] description;
        long enterpriseId = checkAdminAndGetId();
        LogOwner owner = new LogOwner();
        owner.setEnterpriseId(enterpriseId);
        owner.setIp(IpUtils.getClientAddress(req));
        List<EnterpriseUser> enterpriseUserList = syncEnterpriseUserManager.listByLdapStatus(enterpriseId,
            null);
        try
        {
            
            if (null == enterpriseUserList || enterpriseUserList.size() < 1)
            {
                description = new String[]{getEnterpriseName(), null};
                LOGGER.error("delete all ldapuser fail,user list is null");
                adminLogManager.saveAdminLog(owner, AdminLogType.KEY_CLEAR_LDAP_ALLUSER_ERROR, description);
                return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
            }
            StringBuffer buf = new StringBuffer();
            for (int i = 0; i < enterpriseUserList.size(); i++)
            {
                buf.append(enterpriseUserList.get(i).getCloudUserId());
                if (i < enterpriseUserList.size() - 1)
                {
                    buf.append(',');
                }
                enterpriseUserManager.delete(enterpriseUserList.get(i).getId(), enterpriseId);
            }
            description = new String[]{getEnterpriseName(), buf.toString()};
            adminLogManager.saveAdminLog(owner, AdminLogType.KEY_CLEAR_LDAP_ALLUSER, description);
        }
        catch (RuntimeException e)
        {
            LOGGER.error("delete all ldapuser fail");
            description = new String[]{getEnterpriseName(), null};
            adminLogManager.saveAdminLog(owner, AdminLogType.KEY_CLEAR_LDAP_ALLUSER_ERROR, description);
            return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<String>(HttpStatus.OK);
    }
    
    @RequestMapping(value = "initTreeName/{authServerId}", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<TreeNode> initTreeName(@PathVariable Long authServerId, String token)
    {
        super.checkToken(token);
        TreeNode treeNode = new TreeNode();
        LdapDomainConfig config = authServerConfigManager.getAuthServerObject(authServerId);
        if (config != null)
        {
            LdapBasicConfig ldapBasicConfig = config.getLdapBasicConfig();
            if (ldapBasicConfig != null)
            {
                String dn = ldapBasicConfig.getLdapNodeBaseDN();
                treeNode.setBaseDn(HtmlUtils.htmlEscape(dn));
                String pageCount = ldapBasicConfig.getPageCount();
                if (StringUtils.isNotBlank(pageCount))
                {
                    treeNode.setPage(Integer.parseInt(pageCount));
                }
            }
        }
        
        return new ResponseEntity<TreeNode>(treeNode, HttpStatus.OK);
    }
    
    @RequestMapping(value = "listTreeNode/{authServerId}", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<List<TreeNode>> listTreeNode(@RequestParam("baseDn") String baseDn,
        @RequestParam("page") int page, @PathVariable Long authServerId, String token)
    {
        super.checkToken(token);
        List<TreeNode> treeNodeList = ldapAuthServiceManager.getTreeNode(authServerId, baseDn, page);
        for (TreeNode tn : treeNodeList)
        {
            tn.setBaseDn(HtmlUtils.htmlEscape(tn.getBaseDn()));
            tn.setName(HtmlUtils.htmlEscape(tn.getName()));
        }
        return new ResponseEntity<List<TreeNode>>(treeNodeList, HttpStatus.OK);
    }
    
    private String getAuthServerName(long authServerId)
    {
        try
        {
            AuthServer authServer = authServerManager.getAuthServer(authServerId);
            return authServer.getName();
        }
        catch (Exception e)
        {
            return null;
        }
    }
    
}
