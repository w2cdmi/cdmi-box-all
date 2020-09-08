package pw.cdmi.box.uam.enterpriseradminlog.web;

import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.support.RequestContextUtils;

import pw.cdmi.box.domain.Page;
import pw.cdmi.box.domain.PageRequest;
import pw.cdmi.box.uam.authapp.manager.AuthAppManager;
import pw.cdmi.box.uam.common.web.AbstractCommonController;
import pw.cdmi.box.uam.enterprise.manager.EnterpriseManager;
import pw.cdmi.box.uam.enterpriseradminlog.domain.EnterpriseAdminLog;
import pw.cdmi.box.uam.enterpriseradminlog.domain.QueryCondition;
import pw.cdmi.box.uam.enterpriseradminlog.manager.EnterpriseAdminLogManager;
import pw.cdmi.box.uam.util.Constants;
import pw.cdmi.common.domain.enterprise.Enterprise;
import pw.cdmi.uam.domain.AuthApp;

@Controller
@RequestMapping(value = "/enterprise/adminstratorlog")
public class EnterpriseAdminstratorLogController extends AbstractCommonController
{
    @Autowired
    private AuthAppManager authAppManager;
    
    @Autowired
    private EnterpriseAdminLogManager enterpriseAdminManager;
    
    @Autowired
    private EnterpriseManager emanager;
    
    @RequestMapping(value = "enterprisrlist", method = RequestMethod.GET)
    public String getEnterpriselist(Model model)
    {
        List<AuthApp> authAppList = authAppManager.getAuthAppList(null, null, null);
        model.addAttribute("authAppList", authAppList);
        AuthApp.htmlEscape(authAppList);
        return "app/logManage/enterpriseList";
    }
    
    @RequestMapping(value = "logview", method = RequestMethod.GET)
    public String logView(Model model, HttpServletRequest request, String token)
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
        Long enterpriseId = Long.parseLong(request.getParameter("enterpriseId"));
        Enterprise enterprise = emanager.getById(enterpriseId);
        QueryCondition qc = new QueryCondition();
        PageRequest pageRequest = new PageRequest();
        pageRequest.setSize(Constants.DEFAULT_PAGE_SIZE);
        qc.setPageRequest(pageRequest);
        qc.setEnterpriseId(enterpriseId);
        Page<EnterpriseAdminLog> pageList = enterpriseAdminManager.queryByPage(qc, locale);
        model.addAttribute("pageList", pageList);
        model.addAttribute("qc", qc);
        model.addAttribute("enterprise", enterprise);
        return "app/logManage/enterpriselogList";
    }
    
    @RequestMapping(value = "logview", method = RequestMethod.POST)
    public String logViewPost(Model model, QueryCondition qc, Integer page,
        HttpServletRequest httpServletrequest, String token)
    {
        super.checkToken(token);
        Locale locale = RequestContextUtils.getLocaleResolver(httpServletrequest)
            .resolveLocale(httpServletrequest);
        String language = locale.getLanguage();
        if ("zh_CN".equals(language) || "zh".equals(language))
        {
            locale = Locale.CHINA;
        }
        else
        {
            locale = Locale.ENGLISH;
        }
        PageRequest request = new PageRequest();
        request.setSize(Constants.DEFAULT_PAGE_SIZE);
        if (page != null)
        {
            request.setPage(page.intValue());
        }
        qc.setPageRequest(request);
        Enterprise enterprise = emanager.getById(qc.getEnterpriseId());
        Page<EnterpriseAdminLog> pageList = enterpriseAdminManager.queryByPage(qc, locale);
        model.addAttribute("pageList", pageList);
        model.addAttribute("qc", qc);
        model.addAttribute("enterprise", enterprise);
        return "app/logManage/enterpriselogList";
    }
    
}
