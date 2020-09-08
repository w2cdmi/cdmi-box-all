package pw.cdmi.box.uam.declare.web;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.util.HtmlUtils;

import pw.cdmi.box.uam.authapp.manager.AuthAppManager;
import pw.cdmi.box.uam.authapp.service.AuthAppService;
import pw.cdmi.box.uam.common.web.AbstractCommonController;
import pw.cdmi.box.uam.declare.manager.ConcealDeclareManager;
import pw.cdmi.box.uam.exception.ErrorCode;
import pw.cdmi.box.uam.exception.InvalidParamterException;
import pw.cdmi.box.uam.log.domain.OperateDescription;
import pw.cdmi.box.uam.log.domain.OperateType;
import pw.cdmi.box.uam.log.manager.SystemLogManager;
import pw.cdmi.common.domain.ClientManage;
import pw.cdmi.common.domain.ConcealDeclare;
import pw.cdmi.common.domain.ClientManage.ClientType;
import pw.cdmi.uam.domain.AuthApp;

@Controller
@RequestMapping(value = "admin/declaration")
public class ConcealDeclareController extends AbstractCommonController
{
    private static final Logger LOGGER = LoggerFactory.getLogger(ConcealDeclareController.class);
    
    @Autowired
    private AuthAppManager authAppManager;
    
    @Autowired
    private AuthAppService authAppService;
    
    @Autowired
    private ConcealDeclareManager concealDeclareManager;
    
    @Autowired
    private SystemLogManager systemLogManager;
    
    private static final String SUCCESS_FLAG = "success";
    
    private static final int LENGTH_SIZE = 20000;
    
    @RequestMapping(value = "/config/{appId}", method = RequestMethod.GET)
    public String declarationList(@PathVariable(value = "appId") String appId, Model model)
    {
        model.addAttribute("appId", appId);
        model.addAttribute("appType", authAppService.getByAuthAppID(appId).getType());
        List<ConcealDeclare> list = concealDeclareManager.getConcealDeclaresByAppId(appId);
        String content;
        for (ConcealDeclare declare : list)
        {
            content = declare.getDeclaration();
            if (null == content)
            {
                continue;
            }
            declare.setDeclaration(HtmlUtils.htmlEscape(declare.getDeclaration()));
            // 前台列举隐私声明内容去掉<br>
            declare.setDeclaration(content);
        }
        model.addAttribute("declarationList", list);
        return "app/sysConfigManage/declarationList";
    }
    
    @RequestMapping(value = "/update/{type}/{appId}", method = RequestMethod.GET)
    public String updateDeclaration(@PathVariable(value = "appId") String appId,
        @PathVariable(value = "type") String type, Model model)
    {
        ConcealDeclare conceal = new ConcealDeclare();
        conceal.setAppId(appId);
        conceal.setClientType(type);
        ConcealDeclare declare = concealDeclareManager.getDeclaration(conceal);
        if (declare != null)
        {
            // 更新隐私声明内容时，前台<br>转换为换行符
            String declaration = declare.getDeclaration();
            declare.setDeclaration(HtmlUtils.htmlEscape(declaration));
        }

        model.addAttribute("appId", appId);
        model.addAttribute("type", type);
        model.addAttribute("declare", declare);
        return "app/sysConfigManage/updateDeclaration";
    }
    
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public String createDeclaration(ConcealDeclare declare, HttpServletRequest request, Model model,
        String token)
    {
        super.checkToken(token);
        if (declare == null || StringUtils.isEmpty(declare.getDeclaration()))
        {
            throw new InvalidParamterException(ErrorCode.INVALID_PARAMTER.getMessage());
        }
        String[] description = new String[]{declare.getClientType()};
        String logId = systemLogManager.saveFailLog(request,
            OperateType.SignDeclaration,
            OperateDescription.CREATE_SIGN_DECLARATION,
            null,
            description);
        String saveState = "success";
        try
        {
            checkParam(declare);
            concealDeclareManager.create(declare);
        }
        catch (InvalidParamterException e)
        {
            LOGGER.error("create declaration fail");
            saveState = "fail";
        }
        model.addAttribute("saveState", saveState);
        model.addAttribute("appId", declare.getAppId());
        if (SUCCESS_FLAG.equalsIgnoreCase(saveState))
        {
            systemLogManager.updateSuccess(logId);
        }
        return "app/sysConfigManage/updateDeclaration";
    }
    
    @RequestMapping(value = "/getLength", method = RequestMethod.POST)
    @ResponseBody
    public boolean getLength(ConcealDeclare declare, String token)
    {
        super.checkToken(token);
        boolean lengthFlag = false;
        if (declare.getDeclaration().length() > LENGTH_SIZE)
        {
            lengthFlag = true;
        }
        return lengthFlag;
    }
    
    private boolean checkAppId(String appId)
    {
        boolean temp = true;
        List<AuthApp> authAppList = authAppManager.getAuthAppList(null, null, null);
        if (StringUtils.isNotEmpty(appId) && authAppList != null && !authAppList.isEmpty())
        {
            temp = false;
            
            for (AuthApp app : authAppList)
            {
                if (StringUtils.equals(app.getAuthAppId(), appId))
                {
                    temp = true;
                    break;
                }
            }
        }
        return temp;
    }
    
    private void checkParam(ConcealDeclare declare)
    {
        boolean isAppId = checkAppId(declare.getAppId());
        if (!isAppId)
        {
            throw new InvalidParamterException(ErrorCode.INVALID_PARAMTER.getMessage());
        }
        if (StringUtils.isEmpty(declare.getClientType()) || declare.getDeclaration().length() > LENGTH_SIZE)
        {
            throw new InvalidParamterException(ErrorCode.INVALID_PARAMTER.getMessage());
        }
        ClientType[] clients = ClientManage.ClientType.values();
        boolean flag = true;
        for (ClientType type : clients)
        {
            if (declare.getClientType().trim().equalsIgnoreCase(type.toString()))
            {
                flag = false;
            }
        }
        if (flag)
        {
            throw new InvalidParamterException(ErrorCode.INVALID_PARAMTER.getMessage());
        }
    }
    
}
