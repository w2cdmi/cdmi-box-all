package pw.cdmi.box.uam.authorize.web;

import java.nio.charset.Charset;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolationException;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import pw.cdmi.box.uam.common.web.AbstractCommonController;
import pw.cdmi.box.uam.exception.InvalidParamterException;
import pw.cdmi.box.uam.log.domain.OperateDescription;
import pw.cdmi.box.uam.log.domain.OperateType;
import pw.cdmi.box.uam.log.manager.SystemLogManager;
import pw.cdmi.box.uam.system.service.SyslogServerService;
import pw.cdmi.common.domain.SysLogServer;
import pw.cdmi.core.utils.NetCheckUtils;

@Controller
@RequestMapping(value = "/sys/sysconfig/syslog")
public class SysSysLogController extends AbstractCommonController
{
    @Autowired
    private SyslogServerService syslogService;
    
    @Autowired
    private SystemLogManager systemLogManager;
    
    private List<String> charsets = new ArrayList<String>(10);
    
    public SysSysLogController()
    {
        Map<String, Charset> all = Charset.availableCharsets();
        for (Map.Entry<String, Charset> entry : all.entrySet())
        {
            charsets.add(entry.getValue().name());
        }
    }
    
    @RequestMapping(method = RequestMethod.GET)
    public String enter()
    {
        return "sys/sysConfigManage/sysConfigMain";
    }
    
    @RequestMapping(value = "load", method = RequestMethod.GET)
    public String load(Model model)
    {
        SysLogServer syslogConfig = syslogService.getSyslogServer();
        model.addAttribute("sysLogServer", syslogConfig);
        if (syslogConfig != null && StringUtils.isNotEmpty(syslogConfig.getServer()))
        {
            model.addAttribute("server", syslogConfig.getServer());
        }
        else
        {
            model.addAttribute("server", "");
        }
        model.addAttribute("charsets", charsets);
        return "sys/sysConfigManage/syslogSetting";
    }
    
    @SuppressWarnings({"rawtypes", "unchecked"})
    @RequestMapping(value = "save", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<?> save(SysLogServer sysLogServer, HttpServletRequest request, String token)
    {
        super.checkToken(token);
        int protocolType = sysLogServer.getProtocolType();
        String protocolTypeStr;
        if (0 == protocolType)
        {
            protocolTypeStr = "TCP";
        }
        else if (1 == protocolType)
        {
            protocolTypeStr = "UDP";
        }
        else
        {
            saveValidateLog(request, OperateType.ChangeSyslog);
            throw new InvalidParamterException("bad protocolType: " + protocolType);
        }
        
        Set violations = validator.validate(sysLogServer);
        if (!violations.isEmpty())
        {
            saveValidateLog(request, OperateType.ChangeSyslog);
            throw new ConstraintViolationException(violations);
        }
        if (sysLogServer.getProtocolType() != SysLogServer.PROTOCOL_TYPE_TCP
            && sysLogServer.getProtocolType() != SysLogServer.PROTOCOL_TYPE_UDP)
        {
            saveValidateLog(request, OperateType.ChangeSyslog);
            throw new ConstraintViolationException(violations);
        }
        
        boolean temp = false;
        for (String charset : charsets)
        {
            if (StringUtils.equals(charset, sysLogServer.getCharset()))
            {
                temp = true;
                break;
            }
        }
        if (!temp)
        {
            saveValidateLog(request, OperateType.ChangeSyslog);
            throw new InvalidParameterException("sysLogServer.getCharset() Exception"
                + sysLogServer.getCharset());
        }
        
        String[] description = new String[]{sysLogServer.getServer(), String.valueOf(sysLogServer.getPort()),
            protocolTypeStr, sysLogServer.getCharset(), String.valueOf(sysLogServer.isSendLocalTimestamp()),
            String.valueOf(sysLogServer.isSendLocalName())};
        
        String id = systemLogManager.saveFailLog(request,
            OperateType.ChangeSyslog,
            OperateDescription.ADMIN_SYSLOG,
            null,
            description);
        syslogService.saveSysLogServer(sysLogServer);
        systemLogManager.updateSuccess(id);
        return new ResponseEntity(HttpStatus.OK);
    }
    
    @SuppressWarnings({"rawtypes", "unchecked"})
    @RequestMapping(value = "test", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<?> test(SysLogServer sysLogServer, String token, HttpServletRequest request)
    {
        super.checkToken(token);
        Set violations = validator.validate(sysLogServer);
        if (!violations.isEmpty())
        {
            saveValidateLog(request, OperateType.TestSyslog);
            throw new ConstraintViolationException(violations);
        }
        if (sysLogServer.getProtocolType() != SysLogServer.PROTOCOL_TYPE_TCP
            && sysLogServer.getProtocolType() != SysLogServer.PROTOCOL_TYPE_UDP)
        {
            saveValidateLog(request, OperateType.TestSyslog);
            throw new ConstraintViolationException(violations);
        }
        int protocolType = sysLogServer.getProtocolType();
        String protocolTypeStr = "";
        if (0 == protocolType)
        {
            protocolTypeStr = "TCP";
        }
        else if (1 == protocolType)
        {
            protocolTypeStr = "UDP";
        }
        
        String[] description = new String[]{sysLogServer.getServer(), String.valueOf(sysLogServer.getPort()),
            protocolTypeStr, sysLogServer.getCharset(), String.valueOf(sysLogServer.isSendLocalTimestamp()),
            String.valueOf(sysLogServer.isSendLocalName())};
        
        String id = systemLogManager.saveFailLog(request,
            OperateType.TestSyslog,
            OperateDescription.ADMIN_SYSLOG,
            null,
            description);
        
        if (checkSyslogConfig(sysLogServer))
        {
            systemLogManager.updateSuccess(id);
            return new ResponseEntity(HttpStatus.OK);
        }
        return new ResponseEntity(HttpStatus.BAD_REQUEST);
    }
    
    private boolean checkSyslogConfig(SysLogServer sysLogServer)
    {
        if (SysLogServer.PROTOCOL_TYPE_TCP == sysLogServer.getProtocolType())
        {
            return NetCheckUtils.isReachable(sysLogServer.getServer(), sysLogServer.getPort(), 5000);
        }
        return NetCheckUtils.isReachable(sysLogServer.getServer(), 5000);
    }
}
