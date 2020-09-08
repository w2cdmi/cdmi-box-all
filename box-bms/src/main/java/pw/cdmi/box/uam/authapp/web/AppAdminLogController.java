package pw.cdmi.box.uam.authapp.web;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.support.RequestContextUtils;
import org.springframework.web.util.HtmlUtils;

import pw.cdmi.box.domain.Page;
import pw.cdmi.box.domain.PageImpl;
import pw.cdmi.box.domain.PageRequest;
import pw.cdmi.box.uam.authapp.manager.AppAdminLogManager;
import pw.cdmi.box.uam.authapp.manager.AuthAppManager;
import pw.cdmi.box.uam.exception.BusinessException;
import pw.cdmi.box.uam.exception.InvalidParamterException;
import pw.cdmi.box.uam.httpclient.rest.UserLogClient;
import pw.cdmi.box.uam.log.domain.OperateCategoryDomain;
import pw.cdmi.box.uam.log.domain.OperateType;
import pw.cdmi.box.uam.log.domain.OperateTypeDomain;
import pw.cdmi.box.uam.log.domain.QueryCondition;
import pw.cdmi.box.uam.log.domain.UserLogCategory;
import pw.cdmi.box.uam.log.domain.UserLogListReq;
import pw.cdmi.box.uam.log.domain.UserLogListRsp;
import pw.cdmi.box.uam.log.domain.UserLogQueryCondition;
import pw.cdmi.box.uam.log.domain.UserLogRes;
import pw.cdmi.box.uam.log.manager.SystemLogManager;
import pw.cdmi.box.uam.log.service.UserLogService;
import pw.cdmi.box.uam.user.domain.Admin;
import pw.cdmi.box.uam.util.CSRFTokenManager;
import pw.cdmi.box.uam.util.Constants;
import pw.cdmi.common.log.SystemLog;
import pw.cdmi.core.restrpc.RestClient;
import pw.cdmi.uam.domain.AuthApp;

@Controller
@RequestMapping(value = "/app/adminlog/log")
public class AppAdminLogController
{
    
    @Autowired
    private AuthAppManager authAppManager;
    
    @Autowired
    private UserLogService userLogService;
    
    @Resource
    private RestClient ufmClientService;
    
    @Autowired
    private SystemLogManager systemLogManager;
    
    @Autowired
    private AppAdminLogManager appAdminLogManager;
    
    private static final int INITIAL_CAPACITY = 16;
    
    private static final int SECLECT_ALL = -1;
    
    private static final int DEFAULT_CATEGORY_PID = 0;
    
    @RequestMapping(value = "manage", method = RequestMethod.GET)
    public String enter()
    {
        return "app/logManage/logManageMain";
    }
    
    @RequestMapping(value = "list", method = RequestMethod.GET)
    public String enterList(Model model, HttpServletRequest request)
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
        Admin sessAdmin = (Admin) SecurityUtils.getSubject().getPrincipal();
        condition.setOperater(sessAdmin.getLoginName());
        Page<SystemLog> adminLogList = systemLogManager.getFilterd(condition, locale);
        model.addAttribute("adminLogList", adminLogList);
        model.addAttribute("queryCondition", condition);
        model.addAttribute("operateTypeList", getOperateList(locale));
        return "app/logManage/logList";
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
        if (StringUtils.isBlank(token)
            || !token.equals(SecurityUtils.getSubject()
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
        Admin sessAdmin = (Admin) SecurityUtils.getSubject().getPrincipal();
        condition.setOperater(sessAdmin.getLoginName());
        condition.setAdmin(HtmlUtils.htmlEscape(condition.getAdmin()));
        Page<SystemLog> adminLogList = systemLogManager.getFilterd(condition, locale);
        model.addAttribute("adminLogList", adminLogList);
        model.addAttribute("queryCondition", condition);
        model.addAttribute("operateTypeList", getOperateList(locale));
        return "app/logManage/logList";
    }
    
    @RequestMapping(value = "user", method = RequestMethod.GET)
    public String enterUserLogList(Model model, HttpServletRequest httpServletRequest)
    {
        
        String curAppId = null;
        List<AuthApp> authAppList = authAppManager.getAuthAppList(null, null, null);
        int initialCapacity = INITIAL_CAPACITY;
        if (null != authAppList)
        {
            initialCapacity = authAppList.size();
        }
        List<String> ownedAppList = new ArrayList<String>(initialCapacity);
        if (null != authAppList && !authAppList.isEmpty())
        {
            for (AuthApp authApp : authAppList)
            {
                ownedAppList.add(authApp.getAuthAppId());
            }
        }
        if (!ownedAppList.isEmpty())
        {
            curAppId = ownedAppList.get(0);
        }
        AuthApp authApp = authAppManager.getByAuthAppID(curAppId);
        
        UserLogListReq userLogReq = new UserLogListReq();
        userLogReq.setAppId(curAppId);
        userLogReq.setOffset(0L);
        userLogReq.setLimit(Constants.DEFAULT_PAGE_SIZE);
        
        UserLogListRsp logResponse = null;
        if (null != authApp)
        {
            logResponse = new UserLogClient(ufmClientService).getUserLog(userLogReq, authApp);
        }
        PageRequest page = new PageRequest();
        page.setPage(0);
        page.setSize(Constants.DEFAULT_PAGE_SIZE);
        Page<UserLogRes> pagedUserLogList;
        if (logResponse != null)
        {
            pagedUserLogList = new PageImpl<UserLogRes>(logResponse.getUserLogs(), page,
                (int) logResponse.getTotalCount());
        }
        else
        {
            pagedUserLogList = new PageImpl<UserLogRes>(new ArrayList<UserLogRes>(1), page, 0);
        }
        model.addAttribute("userLogList", pagedUserLogList);
        model.addAttribute("ownedAppList", ownedAppList);
        model.addAttribute("selectedApp", curAppId);
        Locale locale = getLocale(httpServletRequest);
        model.addAttribute("categoryList", getCategoryList(locale, DEFAULT_CATEGORY_PID));
        model.addAttribute("kidCategoryList", getCategoryList(locale, SECLECT_ALL));
        return "app/logManage/userLogList";
    }
    
    @RequestMapping(value = "category/{name}", method = RequestMethod.GET)
    @ResponseBody
    public List<OperateCategoryDomain> querySecondCategory(@PathVariable("name") String name,
        HttpServletRequest httpServletRequest)
    {
        UserLogCategory logCategory = UserLogCategory.valueOf(name);
        int selfId = logCategory.getSelfId();
        Locale locale = getLocale(httpServletRequest);
        List<OperateCategoryDomain> categoryList = getCategoryList(locale, selfId);
        return categoryList;
    }
    
    @RequestMapping(value = "accountlog", method = RequestMethod.GET)
    public String enterEventLogList(Model model)
    {
        
        String curAppId = null;
        List<AuthApp> authAppList = authAppManager.getAuthAppList(null, null, null);
        int initialCapacity = INITIAL_CAPACITY;
        if (null != authAppList)
        {
            initialCapacity = authAppList.size();
        }
        List<String> ownedAppList = new ArrayList<String>(initialCapacity);
        if (null != authAppList && !authAppList.isEmpty())
        {
            for (AuthApp authApp : authAppList)
            {
                ownedAppList.add(authApp.getAuthAppId());
            }
        }
        if (!ownedAppList.isEmpty())
        {
            curAppId = ownedAppList.get(0);
        }
        
        UserLogListReq userLogReq = new UserLogListReq();
        userLogReq.setAppId(curAppId);
        userLogReq.setOffset(0L);
        userLogReq.setLimit(Constants.DEFAULT_PAGE_SIZE);
        UserLogListRsp logResponse = userLogService.queryLogs(userLogReq);
        if (logResponse != null)
        {
            PageRequest page = new PageRequest();
            page.setPage(0);
            page.setSize(Constants.DEFAULT_PAGE_SIZE);
            Page<UserLogRes> pagedUserLogList = new PageImpl<UserLogRes>(logResponse.getUserLogs(), page,
                (int) logResponse.getTotalCount());
            model.addAttribute("userLogList", pagedUserLogList);
        }
        model.addAttribute("ownedAppList", ownedAppList);
        model.addAttribute("selectedApp", curAppId);
        return "app/logManage/userAccountLogList";
    }
    
    @RequestMapping(value = "user", method = RequestMethod.POST)
    public String queryUserLoglist(UserLogQueryCondition condition, Integer page, Model model, String token,
        HttpServletRequest request)
    {
        if (StringUtils.isBlank(token)
            || !token.equals(SecurityUtils.getSubject()
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
        
        List<AuthApp> authAppList = authAppManager.getAuthAppList(null, null, null);
        List<String> ownedAppList = new ArrayList<String>(authAppList.size());
        for (AuthApp authApp : authAppList)
        {
            ownedAppList.add(authApp.getAuthAppId());
        }
        if (!ownedAppList.contains(condition.getAppId()))
        {
            throw new BusinessException(401, "invalid appId");
        }
        String curAppId = condition.getAppId();
        AuthApp authApp = authAppManager.getByAuthAppID(curAppId);
        
        PageRequest pageRequest = new PageRequest();
        pageRequest.setSize(Constants.DEFAULT_PAGE_SIZE);
        pageRequest.setPage(page);
        
        UserLogListReq userLogReq = new UserLogListReq();
        userLogReq.setOffset(Long.valueOf(pageRequest.getLimit().getOffset()));
        userLogReq.setLimit(pageRequest.getLimit().getLength());
        userLogReq.setAppId(curAppId);
        userLogReq.setBeginTime(condition.getStartTime() == null ? null : condition.getStartTime().getTime());
        userLogReq.setEndTime(condition.getEndTime() == null ? null : condition.getEndTime().getTime());
        userLogReq.setDetail(condition.getDetail().trim());
        userLogReq.setType(appAdminLogManager.getOperationType(condition));
        userLogReq.setLevel(appAdminLogManager.getOperationLevel(condition));
        boolean vaildConditon = appAdminLogManager.isVaildConditon(condition, userLogReq);
        UserLogListRsp logResponse = null;
        if (vaildConditon)
        {
            logResponse = new UserLogClient(ufmClientService).getUserLog(userLogReq, authApp);
        }
        Page<UserLogRes> pagedUserLogList;
        if (logResponse != null)
        {
            pagedUserLogList = new PageImpl<UserLogRes>(logResponse.getUserLogs(), pageRequest,
                (int) logResponse.getTotalCount());
        }
        else
        {
            pagedUserLogList = new PageImpl<UserLogRes>(new ArrayList<UserLogRes>(1), pageRequest, 0);
        }
        model.addAttribute("userLogList", pagedUserLogList);
        model.addAttribute("ownedAppList", ownedAppList);
        model.addAttribute("selectedApp", curAppId);
        condition.resetTypeValue();
        model.addAttribute("queryCondition", condition);
        Locale locale = getLocale(request);
        model.addAttribute("categoryList", getCategoryList(locale, DEFAULT_CATEGORY_PID));
        UserLogCategory logCategory = UserLogCategory.valueOf(condition.getFirstCategory());
        model.addAttribute("kidCategoryList", getCategoryList(locale, logCategory.getSelfId()));
        return "app/logManage/userLogList";
    }
    
    @RequestMapping(value = "accountlog", method = RequestMethod.POST)
    public String listEventLog(QueryCondition condition, Integer page, Model model, String token)
    {
        if (StringUtils.isBlank(token)
            || !token.equals(SecurityUtils.getSubject()
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
        String curAppId = condition.getAppId();
        
        List<AuthApp> authAppList = authAppManager.getAuthAppList(null, null, null);
        List<String> ownedAppList = new ArrayList<String>(authAppList.size());
        for (AuthApp authApp : authAppList)
        {
            ownedAppList.add(authApp.getAuthAppId());
        }
        
        if (!ownedAppList.contains(condition.getAppId()))
        {
            throw new BusinessException(401, "invalid appId");
        }
        
        PageRequest pageRequest = new PageRequest();
        pageRequest.setSize(Constants.DEFAULT_PAGE_SIZE);
        pageRequest.setPage(page);
        
        UserLogListReq userLogReq = new UserLogListReq();
        userLogReq.setOffset(Long.valueOf(pageRequest.getLimit().getOffset()));
        userLogReq.setLimit(pageRequest.getLimit().getLength());
        userLogReq.setAppId(curAppId);
        userLogReq.setBeginTime(condition.getStartTime() == null ? null : condition.getStartTime().getTime());
        userLogReq.setEndTime(condition.getEndTime() == null ? null : condition.getEndTime().getTime());
        userLogReq.setLoginName(condition.getOperater());
        UserLogListRsp logResponse = userLogService.queryLogs(userLogReq);
        if (logResponse != null)
        {
            Page<UserLogRes> pagedUserLogList = new PageImpl<UserLogRes>(logResponse.getUserLogs(),
                pageRequest, (int) logResponse.getTotalCount());
            model.addAttribute("userLogList", pagedUserLogList);
        }
        
        model.addAttribute("ownedAppList", ownedAppList);
        model.addAttribute("selectedApp", curAppId);
        return "app/logManage/userAccountLogList";
    }
    
    private List<OperateTypeDomain> getOperateList(Locale locale)
    {
        OperateType[] operateTypes = OperateType.values();
        List<OperateTypeDomain> listDomain = new ArrayList<OperateTypeDomain>(operateTypes.length);
        if (operateTypes.length <= 0)
        {
            return null;
        }
        OperateType operateType;
        OperateTypeDomain operateTypeDomain;
        for (int i = 0; i < operateTypes.length; i++)
        {
            operateType = operateTypes[i];
            operateTypeDomain = new OperateTypeDomain();
            operateTypeDomain.setOperateType(operateType);
            if (null == locale)
            {
                operateTypeDomain.setOperatrDetails(operateType.name());
            }
            else
            {
                operateTypeDomain.setOperatrDetails(operateType.getDetails(locale, null));
            }
            listDomain.add(operateTypeDomain);
        }
        return listDomain;
    }
    
    private List<OperateCategoryDomain> getCategoryList(Locale locale, int id)
    {
        UserLogCategory[] operateTypes = UserLogCategory.values();
        List<OperateCategoryDomain> listDomain = new ArrayList<OperateCategoryDomain>(operateTypes.length);
        if (operateTypes.length <= 0)
        {
            return null;
        }
        UserLogCategory logCategory;
        OperateCategoryDomain categoryDomain;
        for (int i = 0; i < operateTypes.length; i++)
        {
            logCategory = operateTypes[i];
            if (id == SECLECT_ALL && logCategory.getpId() > DEFAULT_CATEGORY_PID)
            {
                categoryDomain = new OperateCategoryDomain();
                categoryDomain.setLogCategory(logCategory);
                if (null == locale)
                {
                    categoryDomain.setOperatrDetail(logCategory.name());
                }
                else
                {
                    categoryDomain.setOperatrDetail(logCategory.getDetails(locale));
                }
                listDomain.add(categoryDomain);
            }
            if (id > SECLECT_ALL && logCategory.getpId() == id)
            {
                categoryDomain = new OperateCategoryDomain();
                categoryDomain.setLogCategory(logCategory);
                categoryDomain.setSelfId(logCategory.getSelfId());
                if (null == locale)
                {
                    categoryDomain.setOperatrDetail(logCategory.name());
                }
                else
                {
                    categoryDomain.setOperatrDetail(logCategory.getDetails(locale));
                }
                listDomain.add(categoryDomain);
            }
            
        }
        return listDomain;
    }
    
    private void checkOperateType(QueryCondition condition)
    {
        boolean isAllowed = false;
        OperateType[] operateTypes = OperateType.values();
        int length = operateTypes.length;
        OperateType operateType;
        int type = 0;
        int originalType = 0;
        for (int i = 0; i < length; i++)
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
    
    private Locale getLocale(HttpServletRequest httpServletRequest)
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
        return locale;
    }
}
