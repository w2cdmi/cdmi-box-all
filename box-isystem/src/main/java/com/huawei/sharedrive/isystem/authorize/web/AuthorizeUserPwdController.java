/**
 * 
 */
package com.huawei.sharedrive.isystem.authorize.web;

import javax.servlet.http.HttpServletRequest;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.InvalidSessionException;
import org.apache.shiro.session.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.huawei.sharedrive.isystem.common.web.AbstractCommonController;
import com.huawei.sharedrive.isystem.syslog.domain.UserLogType;
import com.huawei.sharedrive.isystem.syslog.service.UserLogService;
import com.huawei.sharedrive.isystem.syslog.service.impl.UserLogServiceImpl;
import com.huawei.sharedrive.isystem.user.domain.Admin;
import com.huawei.sharedrive.isystem.user.service.AdminService;
import com.huawei.sharedrive.isystem.user.service.AdminUpdateService;
import com.huawei.sharedrive.isystem.util.Constants;
import com.huawei.sharedrive.isystem.util.FormValidateUtil;
import com.huawei.sharedrive.isystem.util.Validate;

import pw.cdmi.common.log.UserLog;

/**
 * 
 * 
 * 鎺堟潈绠＄悊
 * 
 * @author d00199602
 * 
 */
@Controller
@RequestMapping(value = "/authorize/user")
public class AuthorizeUserPwdController extends AbstractCommonController
{
    @Autowired
    private AdminService adminService;
    
    @Autowired
    private AdminUpdateService adminUpdateService;
    
    @Autowired
    private UserLogService userLogService;
    
    @RequestMapping(method = RequestMethod.GET)
    public String enter(Model model)
    {
        Admin admin = (Admin) SecurityUtils.getSubject().getPrincipal();
        model.addAttribute("account", adminService.get(admin.getId()));
        return "authorizeManage/changeName";
    }
    
    /**
     * 淇敼绠＄悊鍛樺瘑鐮�
     * 
     * @param admin
     * @return
     */
    @SuppressWarnings("rawtypes")
    @RequestMapping(value = "modifyName", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<?> modifyName(Admin inputAdmin, HttpServletRequest request, String token)
    {
        
        Admin sessAdmin = (Admin) SecurityUtils.getSubject().getPrincipal();
        UserLog userLog = userLogService.initUserLog(request,
            UserLogType.ADMIN_NAME,
            new String[]{sessAdmin.getName(), inputAdmin.getName()});
        userLogService.saveUserLog(userLog);
        
        super.checkToken(token);
        
        String temp = inputAdmin.getName();
        Validate.valiDateUserName(temp);
        
        if (inputAdmin.getName().equals(sessAdmin.getName()))
        {
            return new ResponseEntity<String>("userNameNotChange", HttpStatus.BAD_REQUEST);
        }
        
        if (!FormValidateUtil.isValidName(inputAdmin.getName()))
        {
            userLog.setDetail(UserLogType.ADMIN_NAME.getErrorDetails(new String[]{sessAdmin.getName(),
                inputAdmin.getName()}));
            userLog.setType(UserLogType.ADMIN_NAME.getValue());
            userLogService.update(userLog);
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
        
        inputAdmin.setId(sessAdmin.getId());
        adminUpdateService.changeName(inputAdmin);
        userLog.setDetail(UserLogType.ADMIN_NAME.getDetails(new String[]{sessAdmin.getName(),
            inputAdmin.getName()}));
        userLog.setLevel(UserLogServiceImpl.SUCCESS_LEVEL);
        userLogService.update(userLog);
        
        /** 更新用戶名 */
        try {
			sessAdmin.setName(inputAdmin.getName());
			Session session = SecurityUtils.getSubject().getSession(false);
			if(null != session){
			    session.setAttribute(Constants.SESS_USER_NAME, temp);
			}
		} catch (InvalidSessionException e) {
			// IGNORE
		}
        
        return new ResponseEntity(HttpStatus.OK);
    }
}
