package com.huawei.sharedrive.isystem.sysconfig.web;

import java.security.InvalidParameterException;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.huawei.sharedrive.isystem.common.web.AbstractCommonController;
import com.huawei.sharedrive.isystem.syslog.domain.UserLogType;
import com.huawei.sharedrive.isystem.syslog.service.UserLogService;
import com.huawei.sharedrive.isystem.system.service.DirectChainService;

import pw.cdmi.common.domain.DirectChainConfig;
import pw.cdmi.common.log.UserLog;

/**
 * 系统直链配置
 * 
 * @author pWX231110
 * 
 */
@Controller
@RequestMapping(value = "/sysconfig/direct")
public class DirectChainController extends AbstractCommonController
{
    @Autowired
    private DirectChainService directChainService;
    
    @RequestMapping(value = "load", method = RequestMethod.GET)
    public String load(Model model)
    {
        DirectChainConfig directConfig = directChainService.getDirectChain();
        DirectChainConfig secmatrix = directChainService.getSecmatrixn();
        model.addAttribute("directConfig", directConfig);
        model.addAttribute("secmatrix", secmatrix);
        return "sysConfigManage/directChainSetting";
    }
    
    @RequestMapping(value = "save", method = RequestMethod.POST)
    public ResponseEntity<?> save(String path, String secmatrix, HttpServletRequest request, String token)
    {
        UserLog userLog = userLogService.initUserLog(request, UserLogType.DIRECT_CONFIG, new String[]{path,
            secmatrix});
        userLogService.saveUserLog(userLog);
        
        super.checkToken(token);
        
        if (StringUtils.isBlank(path) && StringUtils.isBlank(secmatrix))
        {
            userLog.setDetail(UserLogType.DIRECT_CONFIG.getErrorDetails(new String[]{path, secmatrix}));
            userLog.setType(UserLogType.DIRECT_CONFIG.getValue());
            userLogService.update(userLog);
            throw new InvalidParameterException("path exception: " + path);
        }
        if (StringUtils.isNotBlank(path))
        {
            if (path.length() > 200)
            {
                userLog.setDetail(UserLogType.DIRECT_CONFIG.getErrorDetails(new String[]{path, secmatrix}));
                userLog.setType(UserLogType.DIRECT_CONFIG.getValue());
                userLogService.update(userLog);
                throw new InvalidParameterException("path exception: " + path);
            }
            DirectChainConfig directChainConfig = new DirectChainConfig();
            if (path.charAt(path.length() - 1) == '/' || path.endsWith("\\"))
            {
                path = path.substring(0, path.length() - 1);
            }
            directChainConfig.setPath(path);
            directChainService.save(directChainConfig, null);
        }
        if (StringUtils.isNotBlank(secmatrix))
        {
            if (!StringUtils.equals(secmatrix, "true") && !StringUtils.equals(secmatrix, "false"))
            {
                userLog.setDetail(UserLogType.DIRECT_CONFIG.getErrorDetails(new String[]{path, secmatrix}));
                userLog.setType(UserLogType.DIRECT_CONFIG.getValue());
                userLogService.update(userLog);
                throw new InvalidParameterException("secmatrix exception: " + secmatrix);
            }
            directChainService.save(null, secmatrix);
        }
        
        userLog.setDetail(UserLogType.DIRECT_CONFIG.getDetails(new String[]{path, secmatrix}));
        userLog.setLevel(UserLogService.SUCCESS_LEVEL);
        userLogService.update(userLog);
        return new ResponseEntity<String>(HttpStatus.OK);
    }
    
}
