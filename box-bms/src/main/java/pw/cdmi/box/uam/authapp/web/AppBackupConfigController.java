package pw.cdmi.box.uam.authapp.web;


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
import pw.cdmi.box.uam.system.service.BackupConfigService;
import pw.cdmi.common.domain.SystemConfig;

@Controller
@RequestMapping(value = "/app/backup")
public class AppBackupConfigController extends AbstractCommonController
{
    @Autowired
    private BackupConfigService backupConfigService;
    
    @Autowired
    private AuthAppService authAppService;
    
    @RequestMapping(value = "config/{appId}", method = RequestMethod.GET)
    public String load(@PathVariable(value = "appId") String appId, Model model)
    {
        SystemConfig configWhite = backupConfigService.getConfigById(appId, BackupConfigService.BACKUP_RULE_CONFIG_WHITE);
        SystemConfig configBlack = backupConfigService.getConfigById(appId, BackupConfigService.BACKUP_RULE_CONFIG_BLACK);
        model.addAttribute("appId", appId);
        model.addAttribute("appType", authAppService.getByAuthAppID(appId).getType());
        model.addAttribute("whiteRule",configWhite != null ? configWhite.getValue() : null);
        model.addAttribute("blackRule",configBlack != null ? configBlack.getValue() : null);
        return "app/sysConfigManage/backupManage";
    }
    
    @RequestMapping(value = "white/save", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<String> saveWhite(String appId, String whiteRule, String token)
    {
        super.checkToken(token);
        if (authAppService.getByAuthAppID(appId) == null)
        {
            return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
        }
        if (whiteRule == null || whiteRule.length() > 2048)
        {
            return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
        }
        String configId = BackupConfigService.BACKUP_RULE_CONFIG_WHITE;
        doSave(appId, whiteRule, configId);
        return new ResponseEntity<String>(HttpStatus.OK);
    }
    
    private void doSave(String appId, String value, String configId)
    {
        SystemConfig config = new SystemConfig();
        config.setAppId(appId);
        config.setId(configId);
        config.setValue(value);
        backupConfigService.saveConfig(config);
    }
    
    @RequestMapping(value = "black/save", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<String> saveBlack(String appId, String blackRule, String token)
    {
        super.checkToken(token);
        if (authAppService.getByAuthAppID(appId) == null)
        {
            return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
        }
        if (blackRule == null || blackRule.length() > 2048)
        {
            return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
        }
        
        String configId = BackupConfigService.BACKUP_RULE_CONFIG_BLACK;
        doSave(appId, blackRule, configId);
        return new ResponseEntity<String>(HttpStatus.OK);
    }
    
}
