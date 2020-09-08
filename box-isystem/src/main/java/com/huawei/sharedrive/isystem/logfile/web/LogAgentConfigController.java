/*
 * Copyright Notice:
 *      Copyright  1998-2009, Huawei Technologies Co., Ltd.  ALL Rights Reserved.
 *
 *      Warning: This computer software sourcecode is protected by copyright law
 *      and international treaties. Unauthorized reproduction or distribution
 *      of this sourcecode, or any portion of it, may result in severe civil and
 *      criminal penalties, and will be prosecuted to the maximum extent
 *      possible under the law.
 */
package com.huawei.sharedrive.isystem.logfile.web;

import java.security.InvalidParameterException;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Validator;

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

import com.huawei.sharedrive.isystem.common.web.AbstractCommonController;
import com.huawei.sharedrive.isystem.exception.BusinessException;
import com.huawei.sharedrive.isystem.logfile.domain.FSEndpoint;
import com.huawei.sharedrive.isystem.logfile.service.LogAgentService;
import com.huawei.sharedrive.isystem.syslog.domain.UserLogType;
import com.huawei.sharedrive.isystem.syslog.service.UserLogService;

import pw.cdmi.common.log.UserLog;

/**
 * 
 * @author s90006125
 * 
 */
@Controller
@RequestMapping(value = "/sysconfig/logagentconfig")
public class LogAgentConfigController extends AbstractCommonController
{
    @Autowired
    private Validator validator;
    
    @Autowired
    private LogAgentService logAgentService;
    
    @Autowired
    private UserLogService userLogService;
    
    @Autowired
    private LogAgentUtils logAgentUtils;
    
    @RequestMapping(value = "/{clusterId}", method = RequestMethod.GET)
    public String enter(@PathVariable(value = "clusterId") int clusterId, Model model)
    {
        FSEndpoint endpoint = logAgentService.getFSEndpointByClusterId(clusterId);
        if (endpoint != null)
        {
            logAgentUtils.setModelInfo(model, endpoint.getFsType(), endpoint.getEndpoint());
        }
        return "sysConfigManage/logagentSetting";
    }
    
    @SuppressWarnings({"rawtypes"})
    @RequestMapping(value = "/{clusterId}/save", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<?> save(@PathVariable(value = "clusterId") int clusterId,
        @RequestParam(value = "fsType", required = true) String fsType,
        @RequestParam(value = "endpoint", required = true) String endpoint, HttpServletRequest request,
        String token) throws BusinessException
    {
        UserLog userLog = userLogService.initUserLog(request,
            UserLogType.LOGAGENT_SAVE,
            new String[]{clusterId + ""});
        
        userLogService.saveUserLog(userLog);
        
        super.checkToken(token);
        
        try
        {
            endpoint = logAgentUtils.validateAndGetRealEndpoint(validator, fsType, endpoint);
        }
        catch (InvalidParameterException e)
        {
            userLog.setDetail(UserLogType.LOGAGENT_SAVE.getErrorDetails(new String[]{clusterId + ""}));
            userLog.setType(UserLogType.LOGAGENT_SAVE.getValue());
            userLogService.update(userLog);
            throw e;
        }
        
        logAgentService.setFSEndpointForCluster(clusterId, fsType, endpoint);
        
        userLog.setDetail(UserLogType.LOGAGENT_SAVE.getDetails(new String[]{clusterId + ""}));
        userLog.setLevel(UserLogService.SUCCESS_LEVEL);
        userLogService.update(userLog);
        return new ResponseEntity(HttpStatus.OK);
    }
}
