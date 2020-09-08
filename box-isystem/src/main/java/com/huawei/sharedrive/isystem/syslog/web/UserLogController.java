package com.huawei.sharedrive.isystem.syslog.web;

import java.security.InvalidParameterException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.huawei.sharedrive.isystem.adminlog.domain.QueryCondition;
import com.huawei.sharedrive.isystem.common.web.AbstractCommonController;
import com.huawei.sharedrive.isystem.exception.BusinessException;
import com.huawei.sharedrive.isystem.syslog.domain.UserLogType;
import com.huawei.sharedrive.isystem.syslog.domain.UserLogTypeDomain;
import com.huawei.sharedrive.isystem.syslog.service.UserLogService;
import com.huawei.sharedrive.isystem.util.CSRFTokenManager;

import pw.cdmi.box.domain.Page;
import pw.cdmi.box.domain.PageRequest;
import pw.cdmi.common.log.UserLog;

@Controller
@RequestMapping(value = "/userlog/log")
public class UserLogController extends AbstractCommonController
{
    public static final Logger LOGGER = LoggerFactory.getLogger(UserLogController.class);
    
    private static final int DEFAULT_PAGE_SIZE = 20;
    
    @Autowired
    private UserLogService userLogService;
    
    @RequestMapping(value = "manage", method = RequestMethod.GET)
    public String enter(Model model)
    {
        return "logManage/logManageMain";
    }
    
    /**
     * 进入列表页面
     * 
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "list", method = RequestMethod.GET)
    public String enterList(Model model, HttpServletRequest request)
    {
        QueryCondition condition = new QueryCondition();
        PageRequest pageRequest = new PageRequest();
        pageRequest.setSize(DEFAULT_PAGE_SIZE);
        condition.setPageRequest(pageRequest);
        Page<UserLog> adminLogList = userLogService.queryPage(condition);
        model.addAttribute("adminLogList", adminLogList);
        model.addAttribute("queryCondition", condition);
        model.addAttribute("operateTypeList", getOperateList());
        return "logManage/adminLogList";
    }
    
    @InitBinder
    public void initBinder(WebDataBinder binder)
    {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        dateFormat.setLenient(true);
        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
    }
    
    @RequestMapping(value = "list", method = RequestMethod.POST)
    public String list(QueryCondition condition, Integer page, Model model, String token)
    {
        if (StringUtils.isBlank(token)
            || !token.equals(SecurityUtils.getSubject()
                .getSession()
                .getAttribute(CSRFTokenManager.CSRF_TOKEN_FOR_SESSION_ATTR_NAME)))
        {
            throw new BusinessException(401, "invalid token");
        }
        
        int operatype = condition.getOperateType();
        UserLogType[] userTypes = UserLogType.values();
        boolean temp = false;
        for (int i = 0; i < userTypes.length; i++)
        {
            if (operatype == userTypes[i].getValue())
            {
                temp = true;
                break;
            }
            
        }
        if (!temp)
        {
            throw new InvalidParameterException(" condition.getOperateType exception");
        }
        if (condition.getStartTime() != null && condition.getEndTime() != null
            && condition.getStartTime().after(condition.getEndTime()))
        {
            throw new InvalidParameterException("Start EndTime Exception");
        }
        PageRequest request = new PageRequest();
        request.setSize(DEFAULT_PAGE_SIZE);
        if (page != null)
        {
            request.setPage(page.intValue());
        }
        condition.setPageRequest(request);
        condition.setAdmin(condition.getAdmin());
        Page<UserLog> adminLogList = userLogService.queryPage(condition);
        model.addAttribute("adminLogList", adminLogList);
        model.addAttribute("queryCondition", condition);
        model.addAttribute("operateTypeList", getOperateList());
        return "logManage/adminLogList";
    }
    
    private List<UserLogTypeDomain> getOperateList()
    {
        UserLogType[] userTypes = UserLogType.values();
        int len = userTypes.length;
        List<UserLogTypeDomain> listDomain = new ArrayList<UserLogTypeDomain>(len);
        UserLogType userLogType;
        UserLogTypeDomain userLogTypeDomain;
        for (int i = 0; i < len; i++)
        {
            userLogType = userTypes[i];
            if (UserLogType.LOGIN_EMPTY_ROLE.name().equals(userLogType.name()))
            {
                continue;
            }
            userLogTypeDomain = new UserLogTypeDomain();
            userLogTypeDomain.setUserLogType(userLogType);
            setOperatrDetails(userLogType, userLogTypeDomain);
            listDomain.add(userLogTypeDomain);
        }
        return listDomain;
    }
    
    private void setOperatrDetails(UserLogType userLogType, UserLogTypeDomain userLogTypeDomain)
    {
        try
        {
            userLogTypeDomain.setOperatrDetails(userLogType.getType(null));
        }
        catch (Exception e)
        {
            LOGGER.error(e.toString(), e);
            userLogTypeDomain.setOperatrDetails(userLogType.name());
        }
    }
    
}
