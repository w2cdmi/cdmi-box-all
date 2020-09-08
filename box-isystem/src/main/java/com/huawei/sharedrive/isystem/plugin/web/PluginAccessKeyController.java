/**
 * 
 */
package com.huawei.sharedrive.isystem.plugin.web;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolationException;

import org.apache.commons.lang.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.thrift.TException;
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
import org.springframework.web.servlet.support.RequestContextUtils;

import com.huawei.sharedrive.isystem.authapp.dao.AuthAppDao;
import com.huawei.sharedrive.isystem.authapp.service.AppAccessKeyService;
import com.huawei.sharedrive.isystem.common.web.AbstractCommonController;
import com.huawei.sharedrive.isystem.exception.BadRquestException;
import com.huawei.sharedrive.isystem.plugin.manager.PluginAccessKeyManager;
import com.huawei.sharedrive.isystem.syslog.domain.UserLogType;
import com.huawei.sharedrive.isystem.syslog.service.UserLogService;
import com.huawei.sharedrive.isystem.user.domain.Admin;
import com.huawei.sharedrive.isystem.util.Constants;
import com.huawei.sharedrive.isystem.util.custom.SecurityScanUtils;
import com.sun.star.util.Date;

import pw.cdmi.common.domain.AppAccessKey;
import pw.cdmi.common.log.UserLog;
import pw.cdmi.uam.domain.AuthApp;

/**
 * 
 * @author q90003805
 * 
 */
@Controller
@RequestMapping(value = "/pluginServer/appaccesskey")
public class PluginAccessKeyController extends AbstractCommonController
{
    public static final Logger LOGGER = LoggerFactory.getLogger(PluginAccessKeyController.class);
    
    private static final String MESSAGE_KEY_SUCCESS = "plugin.AccessKey.message.success";
    
    private static final String MESSAGE_KEY_FAIL = "plugin.AccessKey.message.fail";
    
    @Autowired
    private PluginAccessKeyManager pluginAccessKeyManager;
    
    @Autowired
    private UserLogService userLogService;
    
    @Autowired
    private AppAccessKeyService appAccessKeyService;
    
    @Autowired
    private AuthAppDao authAppDao;
    
    private static final int FIRSTSCAN = 1;
    
    private static final List<String> PLUGIN_APPID_SET = new ArrayList<String>(2);
    
    static
    {
        PLUGIN_APPID_SET.add("PreviewPlugin");
        PLUGIN_APPID_SET.add("SecurityScan");
    }
    
    private static final String TASK_TYPE_SECURITY_SCAN = "SecurityScan";
    
    @RequestMapping(value = "firstScanSK", method = RequestMethod.GET)
    public String firstScanSK(String appId, String akId, Model model)
    {
        if (appId.equals(TASK_TYPE_SECURITY_SCAN) && !SecurityScanUtils.enableSecurityScan())
        {
            LOGGER.error("user is not allowed to use SecurityScan function[doreset]" + ",TIME:" + new Date()
                + ",RESULT:failed.");
            throw new BadRquestException("Do not enalbe the funciton to SecurityScan");
        }
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
     * 进入接入码列举页面
     * 
     * @param appId
     * @param model
     * @return
     */
    @RequestMapping(method = RequestMethod.GET)
    public String enter(String appId, Model model)
    {
        if (appId.equals(TASK_TYPE_SECURITY_SCAN) && !SecurityScanUtils.enableSecurityScan())
        {
            LOGGER.error("user is not allowed to use SecurityScan function[doreset]" + ",TIME:" + new Date()
                + ",RESULT:failed.");
            throw new BadRquestException("Do not enalbe the funciton to SecurityScan");
        }
        List<AppAccessKey> appAccesskey = pluginAccessKeyManager.getByAppId(appId);
        for (AppAccessKey accessKey : appAccesskey)
        {
            accessKey.setSecretKey(Constants.DISPLAY_STAR_VALUE);
        }
        model.addAttribute("accessKeyList", appAccesskey);
        model.addAttribute("appId", appId);
        return "appManage/pluginServerClusterCode";
    }
    
    /**
     * 删除接入码
     * 
     * @param appAccessKeyId
     * @return
     */
    @SuppressWarnings(
    {"rawtypes", "unchecked"})
    @RequestMapping(value = "delete", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<?> delete(String appAccessKeyId, String appId, HttpServletRequest request,
        String token)
    {
        super.checkToken(token);
        if (appId.equals(TASK_TYPE_SECURITY_SCAN) && !SecurityScanUtils.enableSecurityScan())
        {
            LOGGER.error("user is not allowed to use SecurityScan function[doreset]" + ",TIME:" + new Date()
                + ",RESULT:failed.");
            throw new BadRquestException("Do not enalbe the funciton to SecurityScan");
        }
        UserLog userLog = userLogService.initUserLog(request,
            UserLogType.PLUGIN_ACCESSKEY_DELETE,
            new String[]
            {appId, appAccessKeyId});
        userLogService.saveUserLog(userLog);
        
        Locale locale = RequestContextUtils.getLocaleResolver(request).resolveLocale(request);
        AppAccessKey key = pluginAccessKeyManager.getById(appAccessKeyId);
        if (key == null)
        {
            throw new ConstraintViolationException("can not found appAccessKey", null);
        }
        if (!appId.equals(key.getAppId()))
        {
            return new ResponseEntity("appId  not equals key.getAppId() ", HttpStatus.BAD_REQUEST);
        }
        List<AppAccessKey> appAccessKeyList = pluginAccessKeyManager.getByAppId(key.getAppId());
        if (appAccessKeyList != null && appAccessKeyList.size() <= 1)
        {
            return new ResponseEntity("at least contain one accessKey", HttpStatus.BAD_REQUEST);
        }
        String[] s = new String[]
        {key.getAppId(), appAccessKeyId};
        
        String message = null;
        try
        {
            List<String> list = pluginAccessKeyManager.delete(appAccessKeyId);
            message = getSetAccesKeyMessage(locale, list);
            userLog.setDetail(UserLogType.PLUGIN_ACCESSKEY_DELETE.getDetails(s));
            userLog.setLevel(UserLogService.SUCCESS_LEVEL);
            userLogService.update(userLog);
        }
        catch (TException e)
        {
            LOGGER.debug(e.getMessage(), e);
            return new ResponseEntity(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity(message, HttpStatus.OK);
    }
    
    /**
     * 创建接入码
     * 
     * @param admin
     * @return
     */
    @SuppressWarnings(
    {"rawtypes", "unchecked"})
    @RequestMapping(value = "create", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<?> create(String appId, HttpServletRequest request, String token)
    {
        super.checkToken(token);
        if (appId.equals(TASK_TYPE_SECURITY_SCAN) && !SecurityScanUtils.enableSecurityScan())
        {
            LOGGER.error("user is not allowed to use SecurityScan function[doreset]" + ",TIME:" + new Date()
                + ",RESULT:failed.");
            throw new BadRquestException("Do not enalbe the funciton to SecurityScan");
        }
        UserLog userLog = userLogService.initUserLog(request, UserLogType.PLUGIN_ACCESSKEY_ADD, new String[]
        {appId, ""});
        userLogService.saveUserLog(userLog);
        Map<String, List<String>> key = null;
        try
        {
            key = pluginAccessKeyManager.createAppAccessKeyForApp(appId);
            if (key == null)
            {
                return new ResponseEntity("LimitEceeded", HttpStatus.CONFLICT);
            }
            userLog.setDetail(UserLogType.PLUGIN_ACCESSKEY_ADD.getDetails(new String[]
            {appId, key.get("key").get(0)}));
            userLog.setLevel(UserLogService.SUCCESS_LEVEL);
            userLogService.update(userLog);
            
        }
        catch (TException e)
        {
            LOGGER.debug(e.getMessage(), e);
            return new ResponseEntity(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity(key.get("key").get(0), HttpStatus.OK);
    }
    
    private String getSetAccesKeyMessage(Locale locale, List<String> list)
    {
        StringBuffer sb = new StringBuffer();
        if (null == list)
        {
            return null;
        }
        if (null != list.get(0))
        {
            String[] s =
            {list.get(0)};
            sb.append(messageSource.getMessage(MESSAGE_KEY_SUCCESS, s, locale));
            return sb.toString();
        }
        sb.append(' ');
        if (null != list.get(1))
        {
            String[] s =
            {list.get(1)};
            sb.append(messageSource.getMessage(MESSAGE_KEY_FAIL, s, locale));
            
        }
        return sb.toString();
    }
}
