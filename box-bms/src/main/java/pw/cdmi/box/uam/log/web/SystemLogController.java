package pw.cdmi.box.uam.log.web;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.support.RequestContextUtils;
import org.springframework.web.util.HtmlUtils;

import pw.cdmi.box.domain.Page;
import pw.cdmi.box.domain.PageRequest;
import pw.cdmi.box.uam.common.web.AbstractCommonController;
import pw.cdmi.box.uam.exception.BusinessException;
import pw.cdmi.box.uam.exception.InvalidParamterException;
import pw.cdmi.box.uam.log.domain.OperateType;
import pw.cdmi.box.uam.log.domain.OperateTypeDomain;
import pw.cdmi.box.uam.log.domain.QueryCondition;
import pw.cdmi.box.uam.log.manager.SystemLogManager;
import pw.cdmi.box.uam.util.CSRFTokenManager;
import pw.cdmi.box.uam.util.Constants;
import pw.cdmi.common.log.SystemLog;

@Controller
@RequestMapping(value = "/sys/systemlog/log")
public class SystemLogController extends AbstractCommonController
{
    
    private static Logger logger = LoggerFactory.getLogger(SystemLogController.class);
    
    @Autowired
    private SystemLogManager systemLogManager;
    
    @RequestMapping(value = "manage", method = RequestMethod.GET)
    public String enter(Model model)
    {
        return "sys/logManage/logManageMain";
    }
    
    @RequestMapping(value = "list", method = {RequestMethod.GET})
    public String list(Model model, HttpServletRequest request)
    {
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
        QueryCondition condition = new QueryCondition();
        PageRequest pageRequest = new PageRequest();
        pageRequest.setSize(Constants.DEFAULT_PAGE_SIZE);
        condition.setPageRequest(pageRequest);
        Page<SystemLog> adminLogList = systemLogManager.getFilterd(condition, locale);
        model.addAttribute("adminLogList", adminLogList);
        model.addAttribute("queryCondition", condition);
        model.addAttribute("operateTypeList", getOperateList(locale));
        return "sys/logManage/logList";
    }
    
    @RequestMapping(value = "list", method = RequestMethod.POST)
    public String list(QueryCondition condition, Integer page, Model model, String token,
        HttpServletRequest httpServletRequest)
    {
        Locale locale = RequestContextUtils.getLocaleResolver(httpServletRequest)
            .resolveLocale(httpServletRequest);
        String language = locale.getLanguage();
        if ("zh_CN".equals(language) || "zh".equals(language))
        {
            locale = Locale.CHINA;
        }
        else
        {
            locale = Locale.ENGLISH;
        }
        if (StringUtils.isBlank(token) || !token.equals(SecurityUtils.getSubject()
            .getSession()
            .getAttribute(CSRFTokenManager.CSRF_TOKEN_FOR_SESSION_ATTR_NAME)))
        {
            throw new BusinessException(401, "invalid token");
        }
        Date startTime = condition.getStartTime();
        Date endTime = condition.getEndTime();
        if (null != startTime && null != endTime && startTime.after(endTime))
        {
            throw new InvalidParamterException("start time cannot be lagger than end time");
        }
        checkOperateType(condition);
        
        PageRequest request = new PageRequest();
        request.setSize(Constants.DEFAULT_PAGE_SIZE);
        if (page != null)
        {
            request.setPage(page.intValue());
        }
        condition.setPageRequest(request);
        condition.setAdmin(HtmlUtils.htmlEscape(condition.getAdmin()));
        Page<SystemLog> adminLogList = systemLogManager.getFilterd(condition, locale);
        model.addAttribute("adminLogList", adminLogList);
        model.addAttribute("queryCondition", condition);
        model.addAttribute("operateTypeList", getOperateList(locale));
        return "sys/logManage/logList";
    }
    
    private List<OperateTypeDomain> getOperateList(Locale locale)
    {
        OperateType[] operateTypes = OperateType.values();
        List<OperateTypeDomain> listDomain = new ArrayList<OperateTypeDomain>(operateTypes.length);
        if (operateTypes.length <= 0)
        {
            return null;
        }
        OperateType operateType = null;
        OperateTypeDomain operateTypeDomain = null;
        for (int i = 0; i < operateTypes.length; i++)
        {
            operateType = operateTypes[i];
            operateTypeDomain = new OperateTypeDomain();
            operateTypeDomain.setOperateType(operateType);
            try
            {
                operateTypeDomain.setOperatrDetails(operateType.getDetails(locale, null));
            }
            catch (RuntimeException e)
            {
                logger.error(e.toString());
                operateTypeDomain.setOperatrDetails(operateType.name());
            }
            listDomain.add(operateTypeDomain);
        }
        return listDomain;
    }
    
    private void checkOperateType(QueryCondition condition)
    {
        boolean isAllowed = false;
        OperateType[] operateTypes = OperateType.values();
        OperateType operateType = null;
        int type = 0;
        int originalType = 0;
        for (int i = 0; i < operateTypes.length; i++)
        {
            operateType = operateTypes[i];
            type = condition.getOperateType();
            originalType = operateType.getCode();
            if (originalType == type)
            {
                isAllowed = true;
                break;
            }
        }
        if (!isAllowed)
        {
            throw new InvalidParamterException("invalid type ");
        }
    }
    
}
