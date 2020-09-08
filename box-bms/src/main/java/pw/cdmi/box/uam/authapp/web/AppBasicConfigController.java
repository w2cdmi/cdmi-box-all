package pw.cdmi.box.uam.authapp.web;

import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import pw.cdmi.box.uam.authapp.service.AuthAppService;
import pw.cdmi.box.uam.common.web.AbstractCommonController;
import pw.cdmi.box.uam.httpclient.domain.RestRegionInfo;
import pw.cdmi.box.uam.log.domain.OperateDescription;
import pw.cdmi.box.uam.log.domain.OperateType;
import pw.cdmi.box.uam.log.manager.SystemLogManager;
import pw.cdmi.box.uam.system.service.AppBasicConfigService;
import pw.cdmi.box.uam.user.service.UserService;
import pw.cdmi.common.domain.AppBasicConfig;
import pw.cdmi.uam.domain.AuthApp;

@Controller
@RequestMapping(value = "/app/basicconfig")
public class AppBasicConfigController extends AbstractCommonController
{
    @Autowired
    private AppBasicConfigService appBasicConfigService;
    
    @Autowired
    private AuthAppService authAppService;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private SystemLogManager systemLogManager;
    
    @RequestMapping(value = "config/{appId}", method = RequestMethod.GET)
    public String load(@PathVariable(value = "appId") String appId, Model model)
    {
        model.addAttribute("appId", appId);
        model.addAttribute("appType", authAppService.getByAuthAppID(appId).getType());
        model.addAttribute("appBasicConfig", appBasicConfigService.getAppBasicConfig(appId));
        model.addAttribute("regionList", userService.getRegionInfo(appId));
        return "app/sysConfigManage/basicConfig";
    }
    
    @RequestMapping(value = "save", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<String> save(AppBasicConfig appBasicConfig, String token, HttpServletRequest request)
    {
        Set<ConstraintViolation<AppBasicConfig>> violations = validator.validate(appBasicConfig);
        if (!violations.isEmpty())
        {
            saveValidateLog(request, OperateType.ChangeBasicConfig);
            return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
        }
        String[] description = new String[]{appBasicConfig.getAppId(),
            String.valueOf(appBasicConfig.isEnableTeamSpace()),
            String.valueOf(appBasicConfig.getTeamSpaceQuota()),
            String.valueOf(appBasicConfig.getTeamSpaceMaxMembers()),
            String.valueOf(appBasicConfig.getMaxFileVersions()),
            String.valueOf(appBasicConfig.getUserSpaceQuota()),
            String.valueOf(appBasicConfig.getUploadBandWidth()),
            String.valueOf(appBasicConfig.getDownloadBandWidth()),
            String.valueOf(appBasicConfig.getUserDefaultRegion())};
        if (!checkAppBasicConfig(appBasicConfig))
        {
            systemLogManager.saveFailLog(request,
                OperateType.ChangeBasicConfig,
                OperateDescription.APP_BASIC_CONFIG,
                null,
                description);
            return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
        }
        if (!checkParam(appBasicConfig))
        {
            saveValidateLog(request, OperateType.ChangeBasicConfig);
            return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
        }
        String id = systemLogManager.saveFailLog(request,
            OperateType.ChangeBasicConfig,
            OperateDescription.APP_BASIC_CONFIG,
            null,
            description);
        super.checkToken(token);
        permissionCheck(appBasicConfig.getAppId());
        appBasicConfigService.saveAppBasicConfig(appBasicConfig);
        systemLogManager.updateSuccess(id);
        return new ResponseEntity<String>(HttpStatus.OK);
    }
    
    private boolean checkParam(AppBasicConfig appBasicConfig)
    {
        AuthApp authApp = authAppService.getByAuthAppID(appBasicConfig.getAppId());
        if (authApp == null)
        {
            return false;
        }
        boolean temp = true;
        List<RestRegionInfo> list = userService.getRegionInfo(appBasicConfig.getAppId());
        if (null != appBasicConfig.getUserDefaultRegion() && list != null && !list.isEmpty())
        {
            temp = false;
            for (RestRegionInfo info : list)
            {
                if (info.getId() == appBasicConfig.getUserDefaultRegion().intValue())
                {
                    temp = true;
                    break;
                }
            }
            
        }
        return temp;
    }
    
    private boolean checkAppBasicConfig(AppBasicConfig appBasicConfig)
    {
        
        if (!checkBandWidth(appBasicConfig))
        {
            return false;
        }
        if (appBasicConfig.getMaxFileVersions() == 0)
        {
            return false;
        }
        
        if (appBasicConfig.getUserSpaceQuota() == 0)
        {
            return false;
        }
        
        if (!checkTeamSpace(appBasicConfig))
        {
            return false;
        }
        if (appBasicConfig.getUserDefaultRegion() == null)
        {
            return false;
        }
        return true;
    }
    
    private boolean checkBandWidth(AppBasicConfig appBasicConfig)
    {
        int unlimited = -1;
        int minBandWidth = 100;
        if (appBasicConfig.getDownloadBandWidth() != unlimited
            && minBandWidth > appBasicConfig.getDownloadBandWidth())
        {
            return false;
        }
        if (appBasicConfig.getUploadBandWidth() != unlimited
            && minBandWidth > appBasicConfig.getUploadBandWidth())
        {
            return false;
        }
        return true;
    }
    
    private boolean checkTeamSpace(AppBasicConfig appBasicConfig)
    {
        if (appBasicConfig.isEnableTeamSpace())
        {
            if (appBasicConfig.getTeamSpaceMaxMembers() == null 
                || appBasicConfig.getMaxTeamSpaces() == null
                || appBasicConfig.getTeamSpaceMaxMembers() == null)
            {
                return false;
            }
            
            if (appBasicConfig.getTeamSpaceMaxMembers() == 0 
                || appBasicConfig.getMaxTeamSpaces() == 0
                || appBasicConfig.getTeamSpaceMaxMembers() == 0)
            {
                return false;
            }
        }
        return true;
    }
    
}
