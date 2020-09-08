package com.huawei.sharedrive.uam.enterprise.web;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.huawei.sharedrive.uam.common.web.AbstractCommonController;
import com.huawei.sharedrive.uam.enterprise.domain.AccountSecConfig;
import com.huawei.sharedrive.uam.enterprise.domain.AccountSecConfigExt;
import com.huawei.sharedrive.uam.enterprise.manager.AccountSecManager;
import com.huawei.sharedrive.uam.enterpriseadminlog.domain.AdminLogType;
import com.huawei.sharedrive.uam.enterpriseadminlog.domain.LogOwner;
import com.huawei.sharedrive.uam.enterpriseadminlog.manager.AdminLogManager;
import com.huawei.sharedrive.uam.exception.InvalidParamterException;
import com.huawei.sharedrive.uam.util.SecurityConfigConstants;

import pw.cdmi.core.utils.IpUtils;

@Controller
@RequestMapping(value = "/enterprise/security")
public class AccountSecController extends AbstractCommonController
{
    private static final Logger LOGGER = LoggerFactory.getLogger(AccountSecController.class);
    
    @Autowired
    private AccountSecManager accountSecManager;
    
    @Autowired
    private AdminLogManager adminLogManager;
    
    @RequestMapping(value = "getSpaceSwitch/{appId}/", method = RequestMethod.POST)
    public ResponseEntity<?> getSwitch(@PathVariable(value = "appId") String appId, String token)
    {
        super.checkToken(token);
        AccountSecConfig secConfig = accountSecManager.getSwitch((int) getAccoutId(appId));
        if (null == secConfig)
        {
            return new ResponseEntity<String>("0", HttpStatus.OK);
        }
        return new ResponseEntity<String>(String.valueOf(secConfig.getEnableSpaceSec()), HttpStatus.OK);
    }
    
    @RequestMapping(value = "updateSpaceSwitch/{appId}/", method = RequestMethod.POST)
    public ResponseEntity<?> updateSwitch(@PathVariable(value = "appId") String appId,
        AccountSecConfigExt secConfigExt, HttpServletRequest httpRequest, String token)
    {
        super.checkToken(token);
        String[] description = new String[]{getEnterpriseName(), appId};
        LogOwner owner = new LogOwner();
        owner.setEnterpriseId(checkAdminAndGetId());
        owner.setAppId(appId);
        owner.setIp(IpUtils.getClientAddress(httpRequest));
        Byte isOpen = secConfigExt.getEnableSpaceSec();
        if (isOpen != SecurityConfigConstants.SWITCH_OPEN && isOpen != SecurityConfigConstants.SWITCH_CLOSE)
        {
            adminLogManager.saveAdminLog(owner, AdminLogType.KEY_SPACE_SWITCH_CHANGE_ERROR, description);
            throw new InvalidParamterException("switch value is invalid");
        }
        AdminLogType adminlogType;
        if (isOpen == SecurityConfigConstants.SWITCH_OPEN)
        {
            adminlogType = AdminLogType.getAdminLogType(AdminLogType.KEY_SPACE_SWITCH_CHANGE_OPEN.getTypeCode());
        }
        else
        {
            adminlogType = AdminLogType.getAdminLogType(AdminLogType.KEY_SPACE_SWITCH_CHANGE_CLOSE.getTypeCode());
        }
        
        long accountId = getAccoutId(appId);
        secConfigExt.setAccountId((int) accountId);
        secConfigExt.setEnableFileSec(secConfigExt.getEnableSpaceSec());
        try
        {
            accountSecManager.updateSwitch(secConfigExt);
            adminLogManager.saveAdminLog(owner, adminlogType, description);
        }
        catch (RuntimeException e)
        {
            LOGGER.error("modify switch status fail");
            adminlogType = AdminLogType.getAdminLogType(AdminLogType.KEY_SPACE_SWITCH_CHANGE_ERROR.getTypeCode());
            adminLogManager.saveAdminLog(owner, adminlogType, description);
            return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<String>(HttpStatus.OK);
    }
}
