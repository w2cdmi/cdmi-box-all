package pw.cdmi.box.disk.core.interceptor;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

public class PageLayoutInterceptor extends HandlerInterceptorAdapter
{
    private static final String KEY_PAGE_LAYOUT = "pageLayoutType";
    
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse httpservletresponse, Object obj,
        ModelAndView modelAndView)
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
        Cookie[] cookies = request.getCookies();
        
        String retCssFile = PageLayoutType.menuT.getCssValue();
        
        if (cookies == null)
        {
            modelAndView.addObject(KEY_PAGE_LAYOUT, retCssFile);
            return;
        }
        
        for (Cookie item : cookies)
        {
            if (KEY_PAGE_LAYOUT.equalsIgnoreCase(item.getName()))
            {
                PageLayoutType layoutType = PageLayoutType.parseType(item.getValue());
                if (layoutType != null)
                {
                    retCssFile = layoutType.getCssValue();
                    break;
                }
            }
        }
        modelAndView.addObject(KEY_PAGE_LAYOUT, retCssFile);
    }
}

enum PageLayoutType
{
    menuT("menuT", "topNavigation"), menuB("menuB", "bottomNavigation"), menuL("menuL", "leftNavigation"), menuR(
        "menuR", "rightNavigation");
    
    private String type;
    
    private String cssValue;
    
    PageLayoutType(String type, String cssValue)
    {
        this.type = type;
        this.cssValue = cssValue;
    }
    
    public String getType()
    {
        return type;
    }
    
    public String getCssValue()
    {
        return cssValue;
    }
    
    public static PageLayoutType parseType(String input)
    {
        for (PageLayoutType item : PageLayoutType.values())
        {
            if (item.getType().equalsIgnoreCase(input))
            {
                return item;
            }
        }
        return null;
    }
}
