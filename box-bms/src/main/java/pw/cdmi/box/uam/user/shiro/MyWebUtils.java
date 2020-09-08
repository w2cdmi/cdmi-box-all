package pw.cdmi.box.uam.user.shiro;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.apache.shiro.web.util.WebUtils;

@SuppressWarnings("rawtypes")
public final class MyWebUtils extends WebUtils
{
    private MyWebUtils()
    {
    }
    
    public static void issueRedirect(ServletRequest request, ServletResponse response, String url)
        throws IOException
    {
        issueRedirect(request, response, url, null, true, true);
    }
    
    public static void issueRedirect(ServletRequest request, ServletResponse response, String url,
        Map queryParams) throws IOException
    {
        issueRedirect(request, response, url, queryParams, true, true);
    }
    
    public static void issueRedirect(ServletRequest request, ServletResponse response, String url,
        Map queryParams, boolean contextRelative) throws IOException
    {
        issueRedirect(request, response, url, queryParams, contextRelative, true);
    }
    
    @SuppressWarnings("PMD.ExcessiveParameterList")
    public static void issueRedirect(ServletRequest request, ServletResponse response, String url,
        Map queryParams, boolean contextRelative, boolean http10Compatible) throws IOException
    {
        MyRedirectView view = new MyRedirectView(url, contextRelative, http10Compatible);
        view.renderMergedOutputModel(queryParams, toHttp(request), toHttp(response));
    }
    
}
