package pw.cdmi.box.uam.statistics.web;

import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import pw.cdmi.box.uam.common.web.AbstractCommonController;
import pw.cdmi.box.uam.log.domain.OperateDescription;
import pw.cdmi.box.uam.log.domain.OperateType;
import pw.cdmi.box.uam.log.manager.SystemLogManager;
import pw.cdmi.box.uam.statistics.service.StatisticsAccesskeyService;
import pw.cdmi.box.uam.util.Constants;
import pw.cdmi.common.domain.StatisticsConfig;

@Controller
@RequestMapping(value = "/sys/sysconfig/statistics/accesskey")
public class StatisticsAccesskeyController extends AbstractCommonController
{
    @Autowired
    private StatisticsAccesskeyService statisticsAccesskeyService;
    
    @Autowired
    private SystemLogManager systemLogManager;
    
    @RequestMapping(value = "load", method = RequestMethod.GET)
    public String load(Model model)
    {
        StatisticsConfig config = statisticsAccesskeyService.getStatisticsConfig();
        config.setSecretKey(Constants.DISPLAY_STAR_VALUE);
        model.addAttribute("statisticsConfig", config);
        return "sys/sysConfigManage/statisticsConfig";
    }
    
    @SuppressWarnings({"rawtypes", "unchecked"})
    @RequestMapping(value = "save", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<?> save(StatisticsConfig config, HttpServletRequest request, String token)
    {
        super.checkToken(token);
        if(Constants.DISPLAY_STAR_VALUE.equals(config.getSecretKey())) 
        {
            StatisticsConfig configs = statisticsAccesskeyService.getStatisticsConfig();
            config.setSecretKey(configs.getSecretKey());
        }
        Set violations = validator.validate(config);
        if (!violations.isEmpty())
        {
            saveValidateLog(request, OperateType.ChangeStatisticsAccesskey);
            throw new ConstraintViolationException(violations);
        }
        String[] description = new String[]{config.getAccessKey()};
        String id = systemLogManager.saveFailLog(request,
            OperateType.ChangeStatisticsAccesskey,
            OperateDescription.ADMIN_SET_STATISTICS_ACCESSKEY,
            null,
            description);
        statisticsAccesskeyService.saveStatisticsConfig(config);
        systemLogManager.updateSuccess(id);
        return new ResponseEntity(HttpStatus.OK);
    }
    
}
