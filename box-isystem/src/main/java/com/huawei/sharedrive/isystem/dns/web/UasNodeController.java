package com.huawei.sharedrive.isystem.dns.web;

import javax.servlet.http.HttpServletRequest;

import org.apache.thrift.TException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.huawei.sharedrive.isystem.common.web.AbstractCommonController;
import com.huawei.sharedrive.isystem.dns.domain.UasNode;
import com.huawei.sharedrive.isystem.dns.service.UasNodeService;
import com.huawei.sharedrive.isystem.syslog.domain.UserLogType;
import com.huawei.sharedrive.isystem.syslog.service.UserLogService;
import com.huawei.sharedrive.isystem.util.FormValidateUtil;

import pw.cdmi.common.log.UserLog;

@Controller
@RequestMapping(value = "/cluster/uasNode")
public class UasNodeController extends AbstractCommonController
{
    @Autowired
    private UasNodeService uasNodeService;
    
    @SuppressWarnings("rawtypes")
    @RequestMapping(value = "update", method = {RequestMethod.POST})
    public ResponseEntity<?> delete(String token, UasNode uasNode, HttpServletRequest request)
        throws TException
    {
        super.checkToken(token);
        UserLog userLog = userLogService.initUserLog(request,
            UserLogType.UAS_NET,
            new String[]{uasNode.getManagerIp(), uasNode.getNatAddr()});
        userLogService.saveUserLog(userLog);
        if (uasNode.getManagerIp() == null || !FormValidateUtil.isValidIPv4(uasNode.getManagerIp()))
        {
            userLog.setDetail(UserLogType.UAS_NET.getErrorDetails(new String[]{uasNode.getManagerIp(),
                uasNode.getNatAddr()}));
            userLog.setType(UserLogType.UAS_NET.getValue());
            userLogService.update(userLog);
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
        if (uasNode.getNatAddr() != null && !FormValidateUtil.isValidIPv4(uasNode.getNatAddr()))
        {
            userLog.setDetail(UserLogType.UAS_NET.getErrorDetails(new String[]{uasNode.getManagerIp(),
                uasNode.getNatAddr()}));
            userLog.setType(UserLogType.UAS_NET.getValue());
            userLogService.update(userLog);
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
        
        uasNodeService.updateUasNode(uasNode);
        userLog.setDetail(UserLogType.UAS_NET.getDetails(new String[]{uasNode.getManagerIp(),
            uasNode.getNatAddr()}));
        userLog.setLevel(UserLogService.SUCCESS_LEVEL);
        userLogService.update(userLog);
        return new ResponseEntity(HttpStatus.OK);
    }
    
    @RequestMapping(value = "configNateView", method = {RequestMethod.GET})
    public String configNateView(String filter, Integer page, Model model, UasNode uasNode)
    {
        model.addAttribute("uasNode", uasNode);
        return "clusterManage/configNateView";
    }
}
