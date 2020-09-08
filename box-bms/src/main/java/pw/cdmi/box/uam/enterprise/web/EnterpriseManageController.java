package pw.cdmi.box.uam.enterprise.web;

import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.List;
import java.util.Locale;
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
import org.springframework.web.servlet.support.RequestContextUtils;

import pw.cdmi.box.domain.Order;
import pw.cdmi.box.domain.Page;
import pw.cdmi.box.domain.PageRequest;
import pw.cdmi.box.uam.authapp.manager.AuthAppManager;
import pw.cdmi.box.uam.common.web.AbstractCommonController;
import pw.cdmi.box.uam.enterprise.manager.EnterpriseManager;
import pw.cdmi.box.uam.exception.BadRquestException;
import pw.cdmi.box.uam.httpclient.domain.RestEnterpriseAccountRequest;
import pw.cdmi.box.uam.log.domain.OperateDescription;
import pw.cdmi.box.uam.log.domain.OperateType;
import pw.cdmi.box.uam.log.manager.SystemLogManager;
import pw.cdmi.box.uam.util.Constants;
import pw.cdmi.common.domain.enterprise.Enterprise;
import pw.cdmi.uam.domain.AuthApp;

@Controller
@RequestMapping(value = "/enterprise/manager")
public class EnterpriseManageController extends AbstractCommonController
{
    private static final String DEFAULT_ORDERBY = "name";
    
    @Autowired
    private EnterpriseManager enterpriseManager;
    
    @Autowired
    private AuthAppManager authAppManager;
    
    @Autowired
    private SystemLogManager systemLogManager;
    
    @RequestMapping(method = RequestMethod.GET)
    public String enter(Model model)
    {
        List<AuthApp> authAppList = authAppManager.getAuthAppList(null, null, null);
        AuthApp.htmlEscape(authAppList);
        model.addAttribute("authAppListTabs", authAppList);
        return "enterprise/manager/managerMain";
    }
    
    @RequestMapping(value = "list", method = {RequestMethod.GET})
    public String list(Model model)
    {
        List<AuthApp> authAppList = authAppManager.getAuthAppList(null, null, null);
        model.addAttribute("authAppList", authAppList);
        AuthApp.htmlEscape(authAppList);
        return "enterprise/manager/enterpriseList";
    }
    
    @SuppressWarnings("PMD.ExcessiveParameterList")
    @RequestMapping(value = "list", method = RequestMethod.POST)
    public ResponseEntity<Page<Enterprise>> list(Integer page, String appId, String filter,
        String newHeadItem, boolean newFlag, String token)
    {
        return listEnterprise(page, appId, filter, newHeadItem, newFlag, token);
    }
    
    @SuppressWarnings("PMD.ExcessiveParameterList")
    @RequestMapping(value = "query", method = RequestMethod.POST)
    public ResponseEntity<Page<Enterprise>> query(Integer page, String appId, String filter,
        String newHeadItem, boolean newFlag, String token)
    {
        return listEnterprise(page, appId, filter, newHeadItem, newFlag, token);
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
    
    /**
     * 
     * @param model
     * @return
     */
    @RequestMapping(value = "createEnterprise", method = RequestMethod.GET)
    public String enterCreateLocal(Model model)
    {
        return "enterprise/manager/createEnterprise";
    }
    
    /**
     * 
     * @param admin
     * @return
     * @throws IOException
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    @RequestMapping(value = "createLocal", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<String> createLocal(Enterprise enterprise, HttpServletRequest request, String token)
        throws IOException
    {
        Set violations = validator.validate(enterprise);
        if (!violations.isEmpty())
        {
            saveValidateLog(request, OperateType.CreateEnterprise);
            throw new ConstraintViolationException(violations);
        }
        
        String byteStr = String.valueOf(enterprise.getStatus());
        if ("0".equals(byteStr))
        {
            byteStr = "true";
        }
        else
        {
            byteStr = "false";
        }
        String[] description = new String[]{enterprise.getName(), enterprise.getDomainName(),
            enterprise.getContactEmail(), enterprise.getContactPerson(), enterprise.getContactPhone(),
            byteStr};
        String id = systemLogManager.saveFailLog(request,
            OperateType.CreateEnterprise,
            OperateDescription.CREATE_ENTERPRISE,
            null,
            description);
        
        super.checkToken(token);
        Locale locale = RequestContextUtils.getLocaleResolver(request).resolveLocale(request);
        String language = locale.getLanguage();
        if ("zh_CN".equals(language) || "zh".equals(language))
        {
            locale = Locale.CHINA;
        }
        else
        {
            locale = Locale.ENGLISH;
        }
        
        enterpriseManager.create(enterprise, locale);
        systemLogManager.updateSuccess(id);
        return new ResponseEntity<String>(HttpStatus.OK);
    }
    
    @SuppressWarnings("PMD.ExcessiveParameterList")
    public ResponseEntity<Page<Enterprise>> listEnterprise(Integer page, String appId, String filter,
        String newHeadItem, boolean newFlag, String token)
    {
        Integer tempPage = page;
        super.checkToken(token);
        if (null == tempPage || tempPage < 1)
        {
            tempPage = 1;
        }
        PageRequest request = new PageRequest();
        request.setSize(Constants.DEFAULT_PAGE_SIZE);
        request.setPage(tempPage);
        Order order = new Order();
        if (StringUtils.isNotBlank(newHeadItem))
        {
            order.setField(newHeadItem);
        }
        else
        {
            order.setField(DEFAULT_ORDERBY);
        }
        order.setField(newHeadItem);
        order.setDesc(newFlag);
        request.setOrder(order);
        boolean check = checkAppId(appId);
        if (!check)
        {
            return new ResponseEntity<Page<Enterprise>>(HttpStatus.BAD_REQUEST);
        }
        Page<Enterprise> enterprisePage = enterpriseManager.getFilterd(filter, null, appId, request);
        Enterprise.htmlEscape(enterprisePage.getContent());
        return new ResponseEntity<Page<Enterprise>>(enterprisePage, HttpStatus.OK);
        
    }
    
    @RequestMapping(value = "openOrganization", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<?> openOrganization(long id, String token){
    	  super.checkToken(token);
          Enterprise enterprise = enterpriseManager.getById(id);
          if (null == enterprise)
          {
        	  throw new InvalidParameterException();
          }
    	enterprise.setIsdepartment(true);
		try {
			enterpriseManager.updateEnterprise(enterprise);
		} catch (IOException e) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<>(HttpStatus.OK);
    }
}
