/**
 * 
 */
package com.huawei.sharedrive.isystem.authapp.web;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolationException;

import org.apache.commons.lang.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.huawei.sharedrive.isystem.authapp.service.AuthAppService;
import com.huawei.sharedrive.isystem.cluster.web.DCDetailManageController;
import com.huawei.sharedrive.isystem.common.web.AbstractCommonController;
import com.huawei.sharedrive.isystem.syslog.domain.UserLogType;
import com.huawei.sharedrive.isystem.syslog.service.UserLogService;
import com.huawei.sharedrive.isystem.syslog.service.impl.UserLogServiceImpl;
import com.huawei.sharedrive.isystem.user.domain.Admin;
import com.huawei.sharedrive.isystem.util.FormValidateUtil;

import pw.cdmi.common.domain.AppAccessKey;
import pw.cdmi.common.log.UserLog;
import pw.cdmi.core.utils.IpUtils;
import pw.cdmi.uam.domain.AuthApp;

/**
 * @author d00199602
 * 
 */
@Controller
@RequestMapping(value = "/appmanage/authapp")
public class AuthAppController extends AbstractCommonController
{
    private static final Logger LOGGER = LoggerFactory.getLogger(DCDetailManageController.class);
    
    @Autowired
    private AuthAppService authAppService;
    
    @Autowired
    private UserLogService userLogService;
    
    @RequestMapping(method = RequestMethod.GET)
    public String enter(Model model)
    {
        model.addAttribute("showCopyPlocy", showCopyPlocy);
        return "appManage/appManageMain";
    }
    
    @RequestMapping(value = "list", method = {RequestMethod.GET})
    public String list(Model model)
    {
        Admin admin = (Admin) SecurityUtils.getSubject().getPrincipal();
        AuthApp app = new AuthApp();
        app.setCreateBy(String.valueOf(admin.getId()));
        List<AuthApp> appList = authAppService.getAuthAppList(app, null, null);
        
        for (AuthApp authApp : appList)
        {
            transApp(authApp);
        }
        
        model.addAttribute("authAppList", appList);
        model.addAttribute("createBy", String.valueOf(admin.getId()));
        return "appManage/appList";
    }
    
    /**
     * 进入创建页面
     * 
     * @param model
     * @return
     */
    @RequestMapping(value = "create", method = RequestMethod.GET)
    public String enterCreate(Model model)
    {
        return "appManage/createApp";
    }
    
    /**
     * 创建应用
     * 
     * @param admin
     * @return
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    @RequestMapping(value = "create", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<?> create(AuthApp authApp, String token, HttpServletRequest request)
    {
        UserLog userLog = userLogService.initUserLog(request,
            UserLogType.APP_CREATE,
            new String[]{authApp.getAuthAppId()});
        userLogService.saveUserLog(userLog);
        
        super.checkToken(token);
        
        if (!FormValidateUtil.isValidAppName(authApp.getAuthAppId()))
        {
            userLog.setDetail(UserLogType.APP_CREATE.getErrorDetails(new String[]{authApp.getAuthAppId()}));
            userLog.setType(UserLogType.APP_CREATE.getValue());
            userLogService.update(userLog);
            throw new ConstraintViolationException(null);
        }
        
        try
        {
            checkAuthRule(authApp);
        }
        catch (ConstraintViolationException e)
        {
            userLog.setDetail(UserLogType.APP_CREATE.getErrorDetails(new String[]{authApp.getAuthAppId()}));
            userLog.setType(UserLogType.APP_CREATE.getValue());
            userLogService.update(userLog);
            throw e;
        }
        
        if (StringUtils.isNotEmpty(authApp.getAuthUrl())
            && authApp.getAuthUrl().charAt(authApp.getAuthUrl().length() - 1) != '/')
        {
            authApp.setAuthUrl(authApp.getAuthUrl() + '/');
        }
        AppAccessKey accessKey = authAppService.create(authApp);
        userLog.setDetail(UserLogType.APP_CREATE.getDetails(new String[]{authApp.getAuthAppId()}));
        userLog.setLevel(UserLogServiceImpl.SUCCESS_LEVEL);
        userLogService.update(userLog);
        return new ResponseEntity(accessKey.getId(), HttpStatus.OK);
    }
    
    /**
     * 进入修改页面
     * 
     * @param model
     * @return
     */
    @RequestMapping(value = "modify", method = RequestMethod.GET)
    public String enterModify(String authAppId, Model model)
    {
        model.addAttribute("authApp", authAppService.getByAuthAppID(authAppId));
        return "appManage/modifyApp";
    }
    
    /**
     * 修改应用
     * 
     * @param admin
     * @return
     */
    @SuppressWarnings("rawtypes")
    @RequestMapping(value = "modify", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<?> modify(AuthApp authApp, String token, HttpServletRequest request)
    {
        UserLog userLog = userLogService.initUserLog(request,
            UserLogType.APP_MODIFY,
            new String[]{authApp.getAuthAppId()});
        userLogService.saveUserLog(userLog);
        super.checkToken(token);
        
        if (!FormValidateUtil.isValidAppName(authApp.getAuthAppId()))
        {
            userLog.setDetail(UserLogType.APP_MODIFY.getErrorDetails(new String[]{authApp.getAuthAppId()}));
            userLog.setType(UserLogType.APP_MODIFY.getValue());
            userLogService.update(userLog);
            throw new ConstraintViolationException(null);
        }
        
        AuthApp app = authAppService.getByAuthAppID(authApp.getAuthAppId());
        Admin admin = (Admin) SecurityUtils.getSubject().getPrincipal();
        if (null == app || !app.getCreateBy().equals(String.valueOf(admin.getId())))
        {
            LOGGER.error("Forbidden to update the App. AppId:" + authApp.getAuthAppId() + ",AdminId:"
                + admin.getId() + ",IP:" + IpUtils.getClientAddress(request) + ",RESULT:failed.");
            throw new ConstraintViolationException(null);
        }
        
        try
        {
            checkAuthRule(authApp);
        }
        catch (ConstraintViolationException e)
        {
            userLog.setDetail(UserLogType.APP_MODIFY.getErrorDetails(new String[]{authApp.getAuthAppId()}));
            userLog.setType(UserLogType.APP_MODIFY.getValue());
            userLogService.update(userLog);
            throw e;
        }
        
        if (StringUtils.isNotEmpty(authApp.getAuthUrl())
            && authApp.getAuthUrl().charAt(authApp.getAuthUrl().length() - 1) != '/')
        {
            authApp.setAuthUrl(authApp.getAuthUrl() + '/');
        }
        
        authAppService.updateAuthApp(authApp);
        userLog.setDetail(UserLogType.APP_MODIFY.getDetails(new String[]{authApp.getAuthAppId()}));
        userLog.setLevel(UserLogServiceImpl.SUCCESS_LEVEL);
        userLogService.update(userLog);
        return new ResponseEntity(HttpStatus.OK);
    }
    
    private void checkAuthRule(AuthApp authApp)
    {
        if (authApp.getNearestStore() != 0 && authApp.getNearestStore() != 1)
        {
            throw new ConstraintViolationException(null);
        }
        if (authApp.getQosPort() != null && !FormValidateUtil.isValidPort(authApp.getQosPort()))
        {
            throw new ConstraintViolationException(null);
        }
        if (authApp.getAuthUrl().length() > 64)
        {
            throw new ConstraintViolationException(null);
        }
    }
    
    private void transApp(AuthApp authApp)
    {
        if (authApp == null)
        {
            return;
        }
        if (authApp.getModifiedAt() == null)
        {
            authApp.setModifiedAt(authApp.getCreatedAt());
        }
        if (StringUtils.isBlank(authApp.getAuthUrl()))
        {
            authApp.setAuthUrl("-");
        }
    }
}
