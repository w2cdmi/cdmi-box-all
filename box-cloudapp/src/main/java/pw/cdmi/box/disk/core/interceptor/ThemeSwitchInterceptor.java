package pw.cdmi.box.disk.core.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

public class ThemeSwitchInterceptor extends HandlerInterceptorAdapter
{
    public static final String THEME_KEY = "THEME_KEY";
    
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
        ModelAndView modelAndView)
    {
        mergeContollerMappingToView(modelAndView, handler, request);
    }
    
    protected void mergeContollerMappingToView(ModelAndView modelAndView, Object handler,
        HttpServletRequest request)
    {
        if (modelAndView == null)
        {
            return;
        }
        String view = modelAndView.getViewName();
        if (view == null)
        {
            return;
        }
        HttpSession session = request.getSession();
        String templet = (String) session.getAttribute(THEME_KEY);
        if (StringUtils.isNotEmpty(templet))
        {
            String url = templet + "/" + view;
            modelAndView.setViewName(url);
        }
    }
}
