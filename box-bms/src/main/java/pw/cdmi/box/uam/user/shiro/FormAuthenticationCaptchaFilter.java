package pw.cdmi.box.uam.user.shiro;

import java.io.IOException;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.authc.FormAuthenticationFilter;
import org.apache.shiro.web.util.WebUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pw.cdmi.common.web.RefererMatchFilter;
import pw.cdmi.core.utils.IpUtils;

public class FormAuthenticationCaptchaFilter extends FormAuthenticationFilter
{
    private static final Logger LOGGER = LoggerFactory.getLogger(FormAuthenticationCaptchaFilter.class);
    
    private RefererMatchFilter refererMatchFilter;
    
    public void setRefererMatchFilter(RefererMatchFilter refererMatchFilter)
    {
        this.refererMatchFilter = refererMatchFilter;
    }
    
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    @Override
    protected boolean executeLogin(ServletRequest request, ServletResponse response) throws Exception
    {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        String referer = httpRequest.getHeader("REFERER");
        boolean matched = refererMatchFilter.isRefererMatched(referer);
        if (!matched)
        {
            LOGGER.warn("unknown referer " + referer + ", reject this request");
            httpResponse.sendError(403);
            return false;
        }
        return super.executeLogin(request, response);
    }
    
    @Override
    protected AuthenticationToken createToken(ServletRequest request, ServletResponse response)
    {
        String username = getUsername(request);
        String pwd = getPassword(request);
        String captcha = request.getParameter("captcha");
        if (pwd == null)
        {
            pwd = "";
        }
        boolean rememberMe = isRememberMe(request);
        String host = IpUtils.getClientAddress((HttpServletRequest) request);
        return new UsernamePasswordCaptchaToken(username, pwd.toCharArray(), rememberMe, host, false, "",
            captcha);
    }
    
    @Override
    protected void redirectToLogin(ServletRequest request, ServletResponse response) throws IOException
    {
        String loginUrl = getLoginUrl();
        HttpServletResponse rsp = (HttpServletResponse) response;
        rsp.sendRedirect(loginUrl);
    }
    
    @Override
    protected boolean onLoginSuccess(AuthenticationToken token, Subject subject, ServletRequest request,
        ServletResponse response) throws IllegalStateException, IOException
    {
        String successUrl = getSuccessUrl();
        WebUtils.getAndClearSavedRequest(request);
        if (successUrl == null)
        {
            throw new IllegalStateException("Success URL not available via saved request or via the "
                + "successUrlFallback method parameter. One of these must be non-null for "
                + "issueSuccessRedirect() to work.");
        }
        MyWebUtils.issueRedirect(request, response, successUrl, null, true);
        
        return false;
    }
}
