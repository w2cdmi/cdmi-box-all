package com.huawei.sharedrive.isystem.mirror.web;

import java.security.InvalidParameterException;
import java.util.ArrayList;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.huawei.sharedrive.isystem.common.web.AbstractCommonController;
import com.huawei.sharedrive.isystem.exception.InvalidParamException;
import com.huawei.sharedrive.isystem.mirror.appdatamigration.domain.MigrationEverydayProcess;
import com.huawei.sharedrive.isystem.mirror.appdatamigration.domain.MigrationProcessInfo;
import com.huawei.sharedrive.isystem.mirror.appdatamigration.manager.AppDataMigrationManager;
import com.huawei.sharedrive.isystem.mirror.domain.CopyPolicy;
import com.huawei.sharedrive.isystem.mirror.domain.CopyPolicySiteInfo;
import com.huawei.sharedrive.isystem.mirror.domain.MirrorCommonStatic;
import com.huawei.sharedrive.isystem.mirror.manager.MirrorManager;
import com.huawei.sharedrive.isystem.mirror.manager.MirrorQueryManager;
import com.huawei.sharedrive.isystem.mirror.web.checker.CopyPolicyChecker;
import com.huawei.sharedrive.isystem.mirror.web.view.CopyPlolicyView;
import com.huawei.sharedrive.isystem.mirror.web.view.CopyPolicySiteInfoView;
import com.huawei.sharedrive.isystem.mirror.web.view.MigrationSpeedView;
import com.huawei.sharedrive.isystem.syslog.domain.UserLogType;
import com.huawei.sharedrive.isystem.syslog.domain.UserLogTypeCopyTask;
import com.huawei.sharedrive.isystem.syslog.service.UserLogService;

import pw.cdmi.common.log.UserLog;
import pw.cdmi.core.utils.JsonUtils;
import pw.cdmi.uam.domain.AuthApp;

@Controller
@RequestMapping(value = "/mirror/copyPolicy")
public class CopyPolicyUpdateController extends AbstractCommonController
{
    public static final Logger LOGGER = LoggerFactory.getLogger(CopyPolicyUpdateController.class);
    
    // 状态：暂停
    private final static int POLICY_STATE_PAUSE = 1;
    
    @Autowired
    private AppDataMigrationManager appDataMigrationManager;
    
    @Autowired
    private MirrorManager mirrorManager;
    
    @Autowired
    private MirrorQueryManager mirrorQueryManager;
    
    @RequestMapping(value = "modifiyPage/{policyId}", method = {RequestMethod.GET})
    public String modifiyCopyPolicyPage(Model model, @PathVariable int policyId)
    {
        checkCopyPlocyIsOpen();
        List<AuthApp> authApps = mirrorQueryManager.getAuthAppList();
        model.addAttribute("authApps", authApps);
        CopyPolicy copyPolicy = mirrorQueryManager.getCopyPolicy(policyId);
        List<CopyPolicySiteInfoView> siteViews = new ArrayList<CopyPolicySiteInfoView>(1);
        CopyPlolicyView view = mirrorQueryManager.getCopyPolicyView(copyPolicy);
        if (view != null)
        {
            siteViews = view.getSiteViews();
        }
        model.addAttribute("copyPolicy", copyPolicy);
        model.addAttribute("jsonCopyPolicySiteInfo", JsonUtils.toJson(siteViews));
        return "copyPolicy/modifyPolicy";
    }
    
    @RequestMapping(value = "update", method = {RequestMethod.POST})
    public ResponseEntity<String> updateCopyPolicy(String copyPolicy, String lstCopyPolicyDataSiteInfo,
        HttpServletRequest request, String token)
    {
        checkCopyPlocyIsOpen();
        UserLog userLog = userLogService.initUserLog(request, UserLogType.COPYPLICY_UPDATE, new String[]{""});
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
            userLog.setDetail(UserLogType.COPYPLICY_UPDATE.getCommonErrorParamDetails(new String[]{""}));
            userLog.setType(UserLogType.COPYPLICY_UPDATE.getValue());
            userLogService.update(userLog);
            throw e;
        }
        
        if (!checkCopyPolicy(policy))
        {
            return new ResponseEntity<String>("BadCopyPolicyInfoConflict", HttpStatus.BAD_REQUEST);
        }
        
        if (!mirrorManager.modifyCopyPolicy(policy))
        {
            return new ResponseEntity<String>("BadCopyPolicyInfo", HttpStatus.BAD_REQUEST);
        }
        userLog.setDetail(UserLogType.COPYPLICY_UPDATE.getDetails(new String[]{policy.getName()}));
        userLog.setLevel(UserLogService.SUCCESS_LEVEL);
        userLogService.update(userLog);
        return new ResponseEntity<String>(HttpStatus.OK);
    }
    
    @RequestMapping(value = "updateStatus", method = {RequestMethod.POST})
    public ResponseEntity<String> updateStatusCopyPolicy(String ids, int state, HttpServletRequest request,
        String token)
    {
        checkCopyPlocyIsOpen();
        String log = state + "";
        if (state == MirrorCommonStatic.POLICY_STATE_COMMON)
        {
            log = UserLogTypeCopyTask.COPYPOLICY_STATUS_START.getDetails(new String[]{});
        }
        else if (state == POLICY_STATE_PAUSE)
        {
            log = UserLogTypeCopyTask.COPYPOLICY_STATUS_STOP.getDetails(new String[]{});
        }
        UserLog userLog = userLogService.initUserLog(request,
            UserLogType.COPYPLICY_UPDATE_STATS,
            new String[]{ids, log});
        userLogService.saveUserLog(userLog);
        super.checkToken(token);
        if (state != MirrorCommonStatic.POLICY_STATE_COMMON && state != POLICY_STATE_PAUSE)
        {
            return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
        }
        String[] arrId = coventIds(ids);
        StringBuffer buffer = new StringBuffer("{");
        for (String id : arrId)
        {
            buffer.append(updateStatusCopyPolicyForOne(state, id));
            buffer.append(',');
        }
        if (buffer.length() > 2)
        {
            buffer.delete(buffer.length() - 1, buffer.length());
        }
        buffer.append('}');
        userLog.setDetail(UserLogType.COPYPLICY_UPDATE_STATS.getDetails(new String[]{buffer.toString(), log}));
        userLog.setLevel(UserLogService.SUCCESS_LEVEL);
        userLogService.update(userLog);
        return new ResponseEntity<String>(HttpStatus.OK);
    }
    
    private boolean checkCopyPolicy(CopyPolicy policy)
    {
        List<CopyPolicy> lstCopyPolicies = mirrorQueryManager.getListCopyPolicy();
        if (lstCopyPolicies == null)
        {
            return false;
        }
        List<CopyPolicySiteInfo> oldCopyPolicySiteInfos = null;
        List<CopyPolicySiteInfo> newCopyPolicySiteInfos = policy.getLstCopyPolicyDataSiteInfo();
        for (CopyPolicy copyPolicy2 : lstCopyPolicies)
        {
            if (copyPolicy2.getId() == policy.getId())
            {
                continue;
            }
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
        else
        {
            if (copyPolicy2.getUserConfig().getUserType() == policy.getState())
            {
                return false;
            }
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
    
    private String updateStatusCopyPolicyForOne(int state, String id)
    {
        int idStr = 0;
        try
        {
            idStr = Integer.parseInt(id);
        }
        catch (NumberFormatException e)
        {
            throw new InvalidParamException("The string:" + id + " to int err", e);
        }
        CopyPolicy policy = mirrorQueryManager.getCopyPolicy(idStr);
        StringBuffer sb = new StringBuffer();
        if (null != policy)
        {
            policy.setState(state);
            mirrorManager.modifyCopyPolicyState(policy);
            sb.append(policy.getName());
        }
        return sb.toString();
    }
    
    @RequestMapping(value = "speedProcess/{id}", method = {RequestMethod.GET})
    public String getSpeedProcess(Model model, @PathVariable int id)
    {
        checkCopyPlocyIsOpen();
        List<MigrationProcessInfo> infos = appDataMigrationManager.getMigrationProcessInfoByPolicyId(id);
        if(infos==null)
        {
            return "copyPolicy/MrigrationSpeedList";
        }
        int size=infos.size();
        List<MigrationSpeedView> list = new ArrayList<MigrationSpeedView>(size);
        MigrationSpeedView view;
        MigrationProcessInfo info;
        for(int i=0;i<size;i++){
            info=infos.get(i);
            view = new MigrationSpeedView(info);
            view.setTimes(size-i);
            list.add(view);
        }
        
        model.addAttribute("id", id);
        model.addAttribute("speedlist", list);
        model.addAttribute("isDetail", false);
        return "copyPolicy/MrigrationSpeedList";
    }
    
    @RequestMapping(value = "speedProcess/detail/{pr}/{id}", method = {RequestMethod.GET})
    public String getSpeedProcessDetail(Model model, @PathVariable int pr, @PathVariable String id)
    {
        checkCopyPlocyIsOpen();
        MigrationProcessInfo info = appDataMigrationManager.getMigrationProcessInfo(id);
        MigrationSpeedView view = new MigrationSpeedView(info);
        List<MigrationEverydayProcess> erEverydayProcesses = appDataMigrationManager.getMigrationEverydayProcessById(id);
        model.addAttribute("id", pr);
        model.addAttribute("view", view);
        model.addAttribute("Processes", erEverydayProcesses);
        model.addAttribute("isDetail", true);
        return "copyPolicy/MrigrationSpeedList";
    }
}
