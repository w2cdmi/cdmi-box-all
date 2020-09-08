package com.huawei.sharedrive.uam.enterprise.web;

import javax.servlet.http.HttpServletRequest;

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
@RequestMapping(value = "/enterprise/fileCopy")
public class FileCopySecController extends AbstractCommonController
{
    
    @Autowired
    private AccountSecManager accountSecManager;
    
   
    @Autowired
    private AdminLogManager adminlogManager;
    
    @RequestMapping(value = "getSpaceSwitch/{appId}/", method = RequestMethod.POST)
    public ResponseEntity<?> getSwitch(@PathVariable(value = "appId") String appId, String token)
    {
        super.checkToken(token);
        AccountSecConfig secConfig = accountSecManager.getSwitch((int) getAccoutId(appId));
        if (null == secConfig)
        {
            return new ResponseEntity<String>("0", HttpStatus.OK);
        }
        return new ResponseEntity<String>(String.valueOf(secConfig.getEnableFileCopySec()), HttpStatus.OK);
    }
    
    @RequestMapping(value = "updateSpaceSwitch/{appId}/", method = RequestMethod.POST)
    public ResponseEntity<?> updateSwitch(@PathVariable(value = "appId") String appId,
        AccountSecConfigExt secConfigExt, HttpServletRequest httpRequest, String token)
    {
        super.checkToken(token);
        Byte isOpen = secConfigExt.getEnableFileCopySec();
        LogOwner owner = new LogOwner();
        owner.setEnterpriseId(checkAdminAndGetId());
        owner.setIp(IpUtils.getClientAddress(httpRequest));
        owner.setAppId(appId);
        AdminLogType adminlogType;
        if (isOpen != SecurityConfigConstants.SWITCH_OPEN && isOpen != SecurityConfigConstants.SWITCH_CLOSE)
        {
            adminlogManager.saveAdminLog(owner, AdminLogType.KEY_FILECOPY_SWITCH_CHANGE_ERROR, new String[]{
                getEnterpriseName(), appId});
            throw new InvalidParamterException("switch value is invalid");
        }
        if (isOpen == SecurityConfigConstants.SWITCH_OPEN)
        {
            adminlogType = AdminLogType.getAdminLogType(AdminLogType.KEY_FILECOPY_SWITCH_CHANGE_OPEN.getTypeCode());
        }
        else
        {
            adminlogType = AdminLogType.getAdminLogType(AdminLogType.KEY_FILECOPY_SWITCH_CHANGE_CLOSE.getTypeCode());
        }
        
        String[] description = new String[]{getEnterpriseName(), appId};
        
        long accountId = getAccoutId(appId);
        secConfigExt.setAccountId((int) accountId);
        accountSecManager.updateSwitch(secConfigExt);
        adminlogManager.saveAdminLog(owner, adminlogType, description);
        return new ResponseEntity<String>(HttpStatus.OK);
    }
}
