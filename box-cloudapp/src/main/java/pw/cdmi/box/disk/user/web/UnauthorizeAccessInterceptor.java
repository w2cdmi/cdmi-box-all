package pw.cdmi.box.disk.user.web;

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

import pw.cdmi.box.disk.declare.manager.UserSignDeclareManager;
import pw.cdmi.box.disk.oauth2.domain.UserToken;
import pw.cdmi.box.disk.user.domain.Terminal;
import pw.cdmi.box.disk.user.service.AccountUserService;
import pw.cdmi.common.domain.UserSignDeclare;

public class UnauthorizeAccessInterceptor extends HandlerInterceptorAdapter
{
    @Autowired
    private AccountUserService accountUserService;
    
    @Autowired
    private UserSignDeclareManager userSignDeclareManager;
    
    private static final ThreadLocal<Boolean> EXCLUED = new ThreadLocal<Boolean>();
    
    private Set<Pattern> excludeUrl = new HashSet<Pattern>(10);
    
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    @Override
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
        Exception ex) throws Exception
    {
    }
    
    /**
     * @param request
     * @param response
     * @param handler
     * @return
     * @throws Exception
     */
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
        throws Exception
    {
        EXCLUED.remove();
        if (exclude(request))
        {
            return super.preHandle(request, response, handler);
        }
        UserToken user = (UserToken) SecurityUtils.getSubject().getPrincipal();
        
        if (user == null)
        {
            return super.preHandle(request, response, handler);
        }

        if(user.getAccountId()==0){
            SecurityUtils.getSubject().getSession().setAttribute("isInitPwd", false);
            return true;
        }

        isSginDeclaration(user);
        
        Boolean isInitPwd = (Boolean) SecurityUtils.getSubject().getSession().getAttribute("isInitPwd");
        
        if (null != isInitPwd && isInitPwd)
        {
            return false;
        }
        
        if (null != isInitPwd && !isInitPwd)
        {
            return super.preHandle(request, response, handler);
        }
        
        if (accountUserService.isLocalAndFirstLogin(user.getAccountId(), user.getId()))
        {
            SecurityUtils.getSubject().getSession().setAttribute("isInitPwd", true);
            return false;
        }
        SecurityUtils.getSubject().getSession().setAttribute("isInitPwd", false);
        return true;
        
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
        
        exclude = false;
        Matcher matcher = null;
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
    
    private void isSginDeclaration(UserToken user)
    {
        UserSignDeclare declare = new UserSignDeclare();
        declare.setAccountId(user.getAccountId());
        declare.setCloudUserId(user.getCloudUserId());
        declare.setClientType(Terminal.CLIENT_TYPE_WEB_STR);
        if (userSignDeclareManager.isNeedDeclaration(declare, user.getToken().split("/")[0]))
        {
            SecurityUtils.getSubject().getSession().setAttribute("needDeclaration", true);
        }
        else
        {
            SecurityUtils.getSubject().getSession().setAttribute("needDeclaration", false);
        }
    }
}
