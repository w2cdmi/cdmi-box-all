package pw.cdmi.box.uam.core.interceptor;

import java.io.IOException;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import pw.cdmi.box.uam.system.service.AccessWhiteListService;

public class CSRFHandler implements Filter
{
    private final static String PREFIX_HTTP = "http://";
    
    private final static String PREFIX_HTTPS = "https://";
    
    private static Logger logger = LoggerFactory.getLogger(CSRFHandler.class);
    
    /**
     * 
     * @param req request
     * @param resp response
     * @param chain
     * @throws IOException IO
     * @throws ServletException Servlet
     */
    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException,
        ServletException
    {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) resp;
        
        String referer = request.getHeader("Referer");
        
        WebApplicationContext applicationContext = WebApplicationContextUtils.getWebApplicationContext(req.getServletContext());
        AccessWhiteListService accessWhiteListService;
        List<String> listWhite = null;
        if (applicationContext != null)
        {
            accessWhiteListService = (AccessWhiteListService) applicationContext.getBean("accessWhiteListService");
            listWhite = accessWhiteListService.getWhiteList();
        }
        
        if (CollectionUtils.isNotEmpty(listWhite) && StringUtils.isNotBlank(referer))
        {
            String refererPrefix = checkRefererRule(referer, response);
            boolean endFlag = false;
            
            for (String str : listWhite)
            {
                if (refererPrefix.indexOf(str) == 0)
                {
                    logger.info("valid rule -----------" + str);
                    endFlag = true;
                    break;
                }
            }
            if (!endFlag)
            {
                logger.error("invalid referer", refererPrefix);
                response.sendError(404);
            }
            chain.doFilter(request, response);
        }
        if (CollectionUtils.isEmpty(listWhite) || referer == null)
        {
            chain.doFilter(request, response);
        }
    }
    
    private String checkRefererRule(String referer, HttpServletResponse response)
    {
        String refererPrefix = null;
        if (referer.startsWith(PREFIX_HTTP))
        {
            refererPrefix = referer.substring(PREFIX_HTTP.length());
        }
        else if (referer.startsWith(PREFIX_HTTPS))
        {
            refererPrefix = referer.substring(PREFIX_HTTPS.length());
        }
        else
        {
            try
            {
                response.sendError(401);
            }
            catch (IOException e)
            {
                logger.error("", e);
            }
        }
        return refererPrefix;
    }
    
    /**
     * 
     * @param filterConfig
     * @throws ServletException servlet
     */
    @Override
    public void init(FilterConfig filterConfig) throws ServletException
    {
    }
    
    @Override
    public void destroy()
    {
    }
}
