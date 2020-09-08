package com.huawei.sharedrive.isystem.account.web;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.huawei.sharedrive.isystem.account.domain.Account;
import com.huawei.sharedrive.isystem.account.domain.AccountPageCondition;
import com.huawei.sharedrive.isystem.account.service.AccountService;
import com.huawei.sharedrive.isystem.common.web.AbstractCommonController;
import com.huawei.sharedrive.isystem.exception.BaseRunTimeException;
import com.huawei.sharedrive.isystem.exception.BusinessException;
import com.huawei.sharedrive.isystem.exception.InvalidParamException;
import com.huawei.sharedrive.isystem.util.CSRFTokenManager;

import pw.cdmi.box.domain.Order;
import pw.cdmi.box.domain.Page;
import pw.cdmi.box.domain.PageRequest;

@Controller
@RequestMapping(value = "accountManage/account")
public class AccountListController extends AbstractCommonController
{
    private static final int DEFAULT_PAGE_SIZE = 20;
    
    @Autowired
    private AccountService accountService;
    
    @RequestMapping(method = RequestMethod.GET)
    public String enter(Model model)
    {
        model.addAttribute("showCopyPlocy", showCopyPlocy);
        return "account/accountManageMain";
    }
    
    @RequestMapping(value = "list", method = RequestMethod.GET)
    public String enterList(Model model, HttpServletRequest request) throws BaseRunTimeException
    {
        AccountPageCondition condition = new AccountPageCondition();
        PageRequest pageRequest = new PageRequest();
        pageRequest.setSize(DEFAULT_PAGE_SIZE);
        pageRequest.setOrder(new Order("createdAt", true));
        condition.setPageRequest(pageRequest);
        Page<Account> accountList = accountService.queryPage(condition);
        model.addAttribute("accountList", accountList);
        model.addAttribute("accountPageCondition", condition);
        return "account/accountList";
    }
    
    @InitBinder
    public void initBinder(ServletRequestDataBinder binder)
    {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
    }
    
    @RequestMapping(value = "list", method = RequestMethod.POST)
    public String list(AccountPageCondition condition, Integer page, Model model, String token)
    {
        if (StringUtils.isBlank(token)
            || !token.equals(SecurityUtils.getSubject()
                .getSession()
                .getAttribute(CSRFTokenManager.CSRF_TOKEN_FOR_SESSION_ATTR_NAME)))
        {
            throw new BusinessException(401, "invalid token");
        }
        
        PageRequest request = new PageRequest();
        request.setSize(DEFAULT_PAGE_SIZE);
        if (page != null)
        {
            request.setPage(page.intValue());
        }
        if (condition != null)
        {
            condition.setPageRequest(request);
            condition.setName(condition.getName());
            condition.setAppId(condition.getAppId());
        }
        else
        {
            throw new BusinessException(400, "invalid condition");
        }
        Date startTime = condition.getStartTime();
        Date endTime = condition.getEndTime();
        if (null != startTime && null != endTime && startTime.after(endTime))
        {
            throw new InvalidParamException("start time cannot be lagger than end time");
        }
        
        Page<Account> accountList = accountService.queryPage(condition);
        model.addAttribute("accountList", accountList);
        model.addAttribute("accountPageCondition", condition);
        return "account/accountList";
    }
}
