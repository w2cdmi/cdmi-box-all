/**
 * 
 */
package com.huawei.sharedrive.isystem.authapp.web;

import java.util.ArrayList;
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

import com.huawei.sharedrive.isystem.authapp.dao.AuthAppDao;
import com.huawei.sharedrive.isystem.authapp.service.AppAccessKeyService;
import com.huawei.sharedrive.isystem.authapp.service.AuthAppService;
import com.huawei.sharedrive.isystem.cluster.web.DCDetailManageController;
import com.huawei.sharedrive.isystem.common.web.AbstractCommonController;
import com.huawei.sharedrive.isystem.syslog.domain.UserLogType;
import com.huawei.sharedrive.isystem.syslog.service.UserLogService;
import com.huawei.sharedrive.isystem.syslog.service.impl.UserLogServiceImpl;
import com.huawei.sharedrive.isystem.user.domain.Admin;
import com.huawei.sharedrive.isystem.util.Constants;

import pw.cdmi.common.domain.AppAccessKey;
import pw.cdmi.common.log.UserLog;
import pw.cdmi.core.utils.IpUtils;
import pw.cdmi.uam.domain.AuthApp;

/**
 * 
 * @author q90003805
 * 
 */
@Controller
@RequestMapping(value = "/appmanage/appaccesskey")
public class AppAccessKeyController extends AbstractCommonController
{
    private static final Logger LOGGER = LoggerFactory.getLogger(DCDetailManageController.class);
    
    @Autowired
    private AuthAppService authAppService;
    
    @Autowired
    private AppAccessKeyService appAccessKeyService;
    
    @Autowired
    private UserLogService userLogService;
    
    @Autowired
    private AuthAppDao authAppDao;
    
    private static final int FIRSTSCAN = 1;
    
    private static final List<String> PLUGIN_APPID_SET = new ArrayList<String>(2);
    
    static
    {
        PLUGIN_APPID_SET.add("PreviewPlugin");
        PLUGIN_APPID_SET.add("SecurityScan");
    }
    
    /**
     * 进入接入码列举页面
     * 
     * @param appId
     * @param model
     * @return
     */
    @RequestMapping(method = RequestMethod.GET)
    public String enter(String appId, Model model, HttpServletRequest request)
    {
        AuthApp app = authAppService.getByAuthAppID(appId);
        Admin admin = (Admin) SecurityUtils.getSubject().getPrincipal();
        if (null == app || !app.getCreateBy().equals(String.valueOf(admin.getId())))
        {
            LOGGER.error("Forbidden to access the AppAccessKey. AppId:" + appId + ",AdminId:" + admin.getId()
                + ",IP:" + IpUtils.getClientAddress(request) + ",RESULT:failed.");
            throw new ConstraintViolationException(null);
        }
        
        List<AppAccessKey> appAccesskey = appAccessKeyService.getByAppId(appId);
        for (AppAccessKey accessKey : appAccesskey)
        {
            accessKey.setSecretKey(Constants.DISPLAY_STAR_VALUE);
        }
        model.addAttribute("accessKeyList", appAccesskey);
        model.addAttribute("appId", appId);
        return "appManage/appCode";
    }
    
    @RequestMapping(value = "firstScanSK", method = RequestMethod.GET)
    public String firstScanSK(String appId, String akId, Model model)
    {
        Admin admin = (Admin) SecurityUtils.getSubject().getPrincipal();
        AppAccessKey appsk = appAccessKeyService.getById(akId);
        AuthApp app = authAppDao.getByAuthAppID(appId);
        if (appsk.getFirstScan() != FIRSTSCAN)
        {
            appAccessKeyService.updateFirstScan(appsk);
        }
        model.addAttribute("accessKeyList", appsk);
        model.addAttribute("appId", appId);
        
        if (!PLUGIN_APPID_SET.contains(appId))
        {
            if (!StringUtils.equals(app.getCreateBy(), String.valueOf(admin.getId())))
            {
                throw new ConstraintViolationException("the app can't show ", null);
            }
        }
        return "appManage/firstScanSKCode";
    }
    
    /**
     * 删除接入码
     * 
     * @param appAccessKeyId
     * @return
     */
    @SuppressWarnings("rawtypes")
    @RequestMapping(value = "delete", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<?> delete(String appId, String appAccessKeyId, HttpServletRequest request,
        String token)
    {
        super.checkToken(token);
        String[] s = new String[]{"", appAccessKeyId};
        UserLog userLog = userLogService.initUserLog(request, UserLogType.APP_DELETE_KEY, s);
        userLog.setDetail(UserLogType.APP_DELETE_KEY.getDetails(new String[]{appId, appAccessKeyId}));
        userLogService.saveUserLog(userLog);
        
        AuthApp app = authAppService.getByAuthAppID(appId);
        Admin admin = (Admin) SecurityUtils.getSubject().getPrincipal();
        if (null == app || !app.getCreateBy().equals(String.valueOf(admin.getId())))
        {
            LOGGER.error("Forbidden to delete the AppAccessKey. AppId:" + appId + ",AdminId:" + admin.getId()
                + ",IP:" + IpUtils.getClientAddress(request) + ",RESULT:failed.");
            throw new ConstraintViolationException(null);
        }
        
        AppAccessKey key = appAccessKeyService.getById(appAccessKeyId);
        if (key == null)
        {
            throw new ConstraintViolationException("can not found appAccessKey", null);
        }
        
        userLog.setDetail(UserLogType.APP_DELETE_KEY.getDetails(new String[]{key.getAppId(), appAccessKeyId}));
        userLog.setLevel(UserLogServiceImpl.SUCCESS_LEVEL);
        userLogService.update(userLog);
        appAccessKeyService.delete(appAccessKeyId);
        return new ResponseEntity(HttpStatus.OK);
    }
    
    /**
     * 创建接入码
     * 
     * @param admin
     * @return
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    @RequestMapping(value = "create", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<?> create(String appId, HttpServletRequest request, String token)
    {
        super.checkToken(token);
        UserLog userLog = userLogService.initUserLog(request, UserLogType.APP_CREATE_KEY, new String[]{appId});
        userLogService.saveUserLog(userLog);
        
        AuthApp app = authAppService.getByAuthAppID(appId);
        Admin admin = (Admin) SecurityUtils.getSubject().getPrincipal();
        if (null == app || !app.getCreateBy().equals(String.valueOf(admin.getId())))
        {
            LOGGER.error("Forbidden to create the AppAccessKey. AppId:" + appId + ",AdminId:" + admin.getId()
                + ",IP:" + IpUtils.getClientAddress(request) + ",RESULT:failed.");
            throw new ConstraintViolationException(null);
        }
        
        AppAccessKey key = appAccessKeyService.createAppAccessKeyForApp(appId);
        if (key == null)
        {
            return new ResponseEntity("LimitEceeded", HttpStatus.CONFLICT);
        }
        userLog.setDetail(UserLogType.APP_CREATE_KEY.getDetails(new String[]{appId, key.getId()}));
        userLog.setLevel(UserLogServiceImpl.SUCCESS_LEVEL);
        userLogService.update(userLog);
        return new ResponseEntity(key.getId(), HttpStatus.OK);
    }
    
}
