package com.huawei.sharedrive.isystem.user.web;

import java.text.Normalizer;
import java.text.Normalizer.Form;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.huawei.sharedrive.isystem.user.domain.Admin;
import com.huawei.sharedrive.isystem.user.service.AdminService;

public class UnauthorizeAccessInterceptor extends HandlerInterceptorAdapter
{
    @Autowired
    private AdminService adminService;
    
    private static final ThreadLocal<Boolean> EXCLUED = new ThreadLocal<Boolean>();
    
    private Set<Pattern> excludeUrl = new HashSet<Pattern>(10);
    
    @Override
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler,
        Exception ex) throws Exception
    {
        super.afterCompletion(request, response, handler, ex);
        
        if (!exclude(request))
        {
            doAfterCompletion(request, response, handler, ex);
        }
    }
    
    /**
     * @param request
     * @param response
     * @param handler
     * @param ex
     * @throws Exception
     */
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    public void doAfterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler,
        Exception ex)
    {
        // TODO:
    }
    
    /**
     * @param request
     * @param response
     * @param handler
     * @return
     * @throws Exception
     */
    
    @Override
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
        throws Exception
    {
        EXCLUED.remove();
        if (exclude(request))
        {
            return super.preHandle(request, response, handler);
        }
        Admin sessAdmin = (Admin) SecurityUtils.getSubject().getPrincipal();
        
        if (sessAdmin == null)
        {
            return super.preHandle(request, response, handler);
        }
        
        Boolean isInitPwd = (Boolean) SecurityUtils.getSubject().getSession().getAttribute("isInitPwd");
        
        if (null != isInitPwd && isInitPwd)
        {
            return false;
        }
        
        if (null != isInitPwd && !isInitPwd)
        {
            return super.preHandle(request, response, handler);
        }
        Admin localAdmin = adminService.get(sessAdmin.getId());
        if (null == localAdmin.getLastLoginTime())
        {
            SecurityUtils.getSubject().getSession().setAttribute("isInitPwd", true);
            return false;
        }
        else
        {
            SecurityUtils.getSubject().getSession().setAttribute("isInitPwd", false);
            return true;
        }
        
    }
    
    public void setExcludeUrl(Set<String> excludeUrlStr)
    {
        for (String str : excludeUrlStr)
        {
            excludeUrl.add(Pattern.compile(str));
        }
    }
    
    private boolean exclude(HttpServletRequest request)
    {
        Boolean exclude = EXCLUED.get();
        if (null != exclude)
        {
            return exclude;
        }
        
        String requestPath = StringUtils.trimToEmpty(request.getRequestURI());
        requestPath = Normalizer.normalize(requestPath, Form.NFKC);
        exclude = false;
        Matcher matcher;
        for (Pattern pattern : excludeUrl)
        {
            matcher = pattern.matcher(requestPath);
            if (matcher.matches())
            {
                exclude = true;
                break;
            }
        }
        
        EXCLUED.set(exclude);
        
        return exclude;
    }
    
}
