package com.huawei.sharedrive.uam.system.web;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.huawei.sharedrive.uam.common.web.AbstractCommonController;
import com.huawei.sharedrive.uam.enterpriseadminlog.domain.AdminLogType;
import com.huawei.sharedrive.uam.exception.BaseRunException;
import com.huawei.sharedrive.uam.log.domain.UserLogType;
import com.huawei.sharedrive.uam.oauth2.domain.UserToken;
import com.huawei.sharedrive.uam.security.domain.SecretStaff;
import com.huawei.sharedrive.uam.security.service.SecretStaffService;
import com.huawei.sharedrive.uam.system.service.SecurityService;
import com.huawei.sharedrive.uam.util.ExceptionUtil;

import pw.cdmi.common.domain.SystemConfig;
import pw.cdmi.common.log.UserLog;
import pw.cdmi.core.utils.JsonUtils;

@Controller
@RequestMapping(value = "/app/securityconfig")
public class SecurityConfigController extends AbstractCommonController{

    @Autowired
    private SecurityService securityService;
    @Autowired
    private SecretStaffService  secretStaffService;
    
    @RequestMapping(value = "save", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<?> save(String token, HttpServletRequest request)
    {
        try {
        	super.checkToken(token);
        	SystemConfig systemConfig=new SystemConfig();
        	systemConfig.setAppId(request.getParameter("appId"));
        	systemConfig.setId(request.getParameter("appId"));
        	systemConfig.setValue(request.getParameter("value"));
            securityService.saveSystemConfig(systemConfig);
            return new ResponseEntity<String>(HttpStatus.OK);
		} catch (Exception e) {
			// TODO: handle exception
             return new ResponseEntity<String>(ExceptionUtil.getExceptionClassName(e),
                 HttpStatus.BAD_REQUEST);
		}
    	
    }
    @RequestMapping(value = "saveVisitSecretDoc", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<?> saveStaffVisitSecretDoc(String token, HttpServletRequest request)
    {
        try {
        	super.checkToken(token);
        	String appId=request.getParameter("appId");
        	long accountId = getAccoutId(appId);
        	List<SecretStaff>  list=(List<SecretStaff>) JsonUtils.stringToList(request.getParameter("objects"), ArrayList.class, SecretStaff.class);
            for(int i=0;i<list.size();i++){
            	SecretStaff secretStaff=list.get(i);
            	secretStaff.setAccountId(accountId);
            	secretStaffService.create(secretStaff);
            }
            
        	return new ResponseEntity<String>(HttpStatus.OK);
		} catch (Exception e) {
			// TODO: handle exception
             return new ResponseEntity<String>(ExceptionUtil.getExceptionClassName(e),
                 HttpStatus.BAD_REQUEST);
		}
    	
    }
    @RequestMapping(value = "/secretStaff", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<?> getSecretStaff(String token, HttpServletRequest request) throws BaseRunException
    {
    	List<SecretStaff> list;
        try
        {
           
        	super.checkToken(token);
        	String appId=request.getParameter("appId");
        	long accountId = getAccoutId(appId);
            list= secretStaffService.getByAccountId(accountId);
        }
        catch (RuntimeException e)
        {
            throw e;
        }
        return new ResponseEntity<List<SecretStaff>>(list, HttpStatus.OK);
    }

}
