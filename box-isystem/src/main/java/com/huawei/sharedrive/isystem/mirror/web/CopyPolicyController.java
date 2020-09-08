package com.huawei.sharedrive.isystem.mirror.web;

import java.security.InvalidParameterException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.huawei.sharedrive.isystem.common.web.AbstractCommonController;
import com.huawei.sharedrive.isystem.mirror.domain.CopyPolicy;
import com.huawei.sharedrive.isystem.mirror.domain.CopyPolicySiteInfo;
import com.huawei.sharedrive.isystem.mirror.domain.TimeConfig;
import com.huawei.sharedrive.isystem.mirror.manager.MirrorManager;
import com.huawei.sharedrive.isystem.mirror.manager.MirrorQueryManager;
import com.huawei.sharedrive.isystem.mirror.web.checker.CopyPolicyChecker;
import com.huawei.sharedrive.isystem.plugin.domain.DCTreeNode;
import com.huawei.sharedrive.isystem.syslog.domain.UserLogType;
import com.huawei.sharedrive.isystem.syslog.service.UserLogService;

import pw.cdmi.common.log.UserLog;
import pw.cdmi.core.utils.JsonUtils;
import pw.cdmi.uam.domain.AuthApp;

@Controller
@RequestMapping(value = "/mirror/copyPolicy")
public class CopyPolicyController extends AbstractCommonController
{
    public static final Logger LOGGER = LoggerFactory.getLogger(CopyPolicyController.class);
    
    @Autowired
    private MirrorManager mirrorManager;
    
    @Autowired
    private MirrorQueryManager mirrorQueryManager;
    
    @RequestMapping(value = "globalEnable", method = {RequestMethod.POST})
    public ResponseEntity<String> createCopyPolicy(boolean globalEnable, HttpServletRequest request,
        String token)
    {
        checkCopyPlocyIsOpen();
        UserLog userLog = userLogService.initUserLog(request,
            UserLogType.GLOBAL_ENABLE,
            new String[]{globalEnable + ""});
        userLogService.saveUserLog(userLog);
        
        super.checkToken(token);
        
        mirrorManager.setMirrorGlobalEnable(globalEnable);
        userLog.setDetail(UserLogType.GLOBAL_ENABLE.getDetails(new String[]{globalEnable + ""}));
        userLog.setLevel(UserLogService.SUCCESS_LEVEL);
        userLogService.update(userLog);
        return new ResponseEntity<String>(HttpStatus.OK);
    }
    
    @RequestMapping(value = "createCopyPolicy", method = RequestMethod.POST)
    public ResponseEntity<String> createCopyPolicy(String copyPolicy, String lstCopyPolicyDataSiteInfo,
        HttpServletRequest request, String token)
    {
        checkCopyPlocyIsOpen();
        UserLog userLog = userLogService.initUserLog(request, UserLogType.COPYPLICY_CREATE, new String[]{""});
        userLogService.saveUserLog(userLog);
        super.checkToken(token);
        CopyPolicy policy = null;
        policy = JsonUtils.stringToObject(copyPolicy, CopyPolicy.class);
        
        @SuppressWarnings("unchecked")
        List<CopyPolicySiteInfo> lstDataSiteInfo = (List<CopyPolicySiteInfo>) JsonUtils.stringToList(lstCopyPolicyDataSiteInfo,
            List.class,
            CopyPolicySiteInfo.class);
        policy.setLstCopyPolicyDataSiteInfo(lstDataSiteInfo);
        
        try
        {
            CopyPolicyChecker.checkPolicy(policy, mirrorQueryManager);
        }
        catch (InvalidParameterException e)
        {
            userLog.setDetail(UserLogType.COPYPLICY_CREATE.getCommonErrorParamDetails(new String[]{""}));
            userLog.setType(UserLogType.COPYPLICY_CREATE.getValue());
            userLogService.update(userLog);
            throw e;
        }
        
        if (!checkCopyPolicy(policy))
        {
            return new ResponseEntity<String>("BadCopyPolicyInfoConflict", HttpStatus.BAD_REQUEST);
        }
        
        if (!mirrorManager.createCopyPolicy(policy))
        {
            return new ResponseEntity<String>("BadCopyPolicyInfo", HttpStatus.BAD_REQUEST);
        }
        userLog.setDetail(UserLogType.COPYPLICY_CREATE.getDetails(new String[]{policy.getName()}));
        userLog.setLevel(UserLogService.SUCCESS_LEVEL);
        userLogService.update(userLog);
        return new ResponseEntity<String>(HttpStatus.OK);
    }
    
    @RequestMapping(value = "createPage", method = {RequestMethod.GET})
    public String createCopyPolicyPage(Model model, HttpServletRequest request)
    {
        checkCopyPlocyIsOpen();
        List<AuthApp> authApps = mirrorQueryManager.getAuthAppList();
        model.addAttribute("authApps", authApps);
        return "copyPolicy/createCopyPolicy";
    }
    
    @RequestMapping(value = "createMigration", method = {RequestMethod.GET})
    public String createDataMigration(Model model, HttpServletRequest request)
    {
        checkCopyPlocyIsOpen();
        List<AuthApp> authApps = mirrorQueryManager.getAuthAppList();
        model.addAttribute("authApps", authApps);
        return "copyPolicy/createDataMigration";
    }
    
    @RequestMapping(value = "delete", method = {RequestMethod.POST})
    public ResponseEntity<String> deleteCopyPolicy(String ids, HttpServletRequest request, String token)
    {
        checkCopyPlocyIsOpen();
        super.checkToken(token);
        
        String[] arrId = coventIds(ids);
        
        for (String id : arrId)
        {
            deleteCopyPolicyForOne(id, request, token);
        }
        
        return new ResponseEntity<String>(HttpStatus.OK);
    }
    
    @RequestMapping(value = "", method = {RequestMethod.GET})
    public String enter(Model model)
    {
        checkCopyPlocyIsOpen();
        model.addAttribute("showCopyPlocy", showCopyPlocy);
        return "copyPolicy/copyPolicyMain";
    }
    
    @RequestMapping(value = "list", method = {RequestMethod.GET})
    public String getListCopyPolicy(Model model)
    {
        checkCopyPlocyIsOpen();
        List<CopyPolicy> views = mirrorQueryManager.getListCopyPolicy();
        List<TimeConfig> timeconfigs=mirrorQueryManager.getListTimeConfig();
        boolean globalEnable = mirrorQueryManager.isMirrorGlobalEnable();
        boolean timeconfigEnable=mirrorQueryManager.isTimeConfigGlobalEnable();
        model.addAttribute("copyPlolicies", views);
        model.addAttribute("timeConfigs",timeconfigs);
        model.addAttribute("globalEnable", globalEnable);
        model.addAttribute("timeconfigEnable", timeconfigEnable);
        return "copyPolicy/copyPolicyList";
    }
    
    @RequestMapping(value = "listTreeNode", method = {RequestMethod.POST})
    public ResponseEntity<String> getTreeNode(Integer id, String token)
    {
        super.checkToken(token);
        List<DCTreeNode> list = mirrorQueryManager.getTreeNode(id);
        return new ResponseEntity<String>(JsonUtils.toJson(list), HttpStatus.OK);
    }
    
    private boolean checkCopyPolicy(CopyPolicy policy)
    {
        List<CopyPolicy> lstCopyPolicies = mirrorQueryManager.getListCopyPolicy();
        List<CopyPolicySiteInfo> oldCopyPolicySiteInfos = null;
        List<CopyPolicySiteInfo> newCopyPolicySiteInfos = policy.getLstCopyPolicyDataSiteInfo();
        if (lstCopyPolicies == null || lstCopyPolicies.isEmpty())
        {
            return true;
        }
        for (CopyPolicy copyPolicy2 : lstCopyPolicies)
        {
            if (copyPolicy2.getAppId().equals(policy.getAppId()))
            {
                oldCopyPolicySiteInfos = copyPolicy2.getLstCopyPolicyDataSiteInfo();
                if (!checkSiteInfo(oldCopyPolicySiteInfos, newCopyPolicySiteInfos))
                {
                    if (!checkOther(policy, copyPolicy2))
                    {
                        return false;
                    }
                }
            }
        }
        
        return true;
    }
    
    private boolean checkOther(CopyPolicy policy, CopyPolicy copyPolicy2)
    {
        if (policy.getType() == 0 || copyPolicy2.getType() == 0)
        {
            return false;
        }
        if (copyPolicy2.getUserConfig().getUserType() == policy.getState())
        {
            return false;
        }
        return true;
    }
    
    private boolean checkSiteInfo(List<CopyPolicySiteInfo> oldCopyPolicySiteInfos,
        List<CopyPolicySiteInfo> newCopyPolicySiteInfos)
    {
        for (CopyPolicySiteInfo copyPolicySiteInfo : oldCopyPolicySiteInfos)
        {
            for (CopyPolicySiteInfo copyPolicySiteInfo2 : newCopyPolicySiteInfos)
            {
                if (copyPolicySiteInfo.getSrcResourceGroupId() == copyPolicySiteInfo2.getSrcResourceGroupId()
                    && copyPolicySiteInfo.getDestResourceGroupId() == copyPolicySiteInfo2.getDestResourceGroupId())
                {
                    return false;
                }
                if (copyPolicySiteInfo2.getSrcResourceGroupId() == copyPolicySiteInfo2.getDestResourceGroupId())
                {
                    return false;
                }
            }
        }
        return true;
    }
    
    private String[] coventIds(String ids)
    {
        if (StringUtils.isNotBlank(ids))
        {
            ids = ids.trim();
            String[] arrId = ids.split(",");
            return arrId;
        }
        
        return new String[0];
    }
    
    private void deleteCopyPolicyForOne(String id, HttpServletRequest request, String token)
    {
        CopyPolicy policy = null;
        super.checkToken(token);
        try
        {
            UserLog userLog = userLogService.initUserLog(request, UserLogType.COPYPLICY_DEL, new String[]{id});
            userLogService.saveUserLog(userLog);
            policy = mirrorQueryManager.getCopyPolicy(Integer.parseInt(id));
            if (null != policy)
            {
                mirrorManager.deleteCopyPolicy(policy);
                userLog.setDetail(UserLogType.COPYPLICY_DEL.getDetails(new String[]{policy.getName()}));
                userLog.setLevel(UserLogService.SUCCESS_LEVEL);
                userLogService.update(userLog);
            }
        }
        catch (Exception e)
        {
            LOGGER.error("update status exception " + id, e);
        }
    }
    
}
