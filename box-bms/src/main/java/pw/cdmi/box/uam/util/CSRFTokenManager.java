package pw.cdmi.box.uam.util;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public final class CSRFTokenManager
{
    
    /**
     * The token parameter name
     */
    static final String CSRF_PARAM_NAME = "CSRFToken";
    
    /**
     * The location on the session which stores the token
     */
    public static final String CSRF_TOKEN_FOR_SESSION_ATTR_NAME = CSRFTokenManager.class.getName()
        + ".tokenval";
    
    public static String getTokenForSession(HttpSession session)
    {
        String token = (String) session.getAttribute(CSRF_TOKEN_FOR_SESSION_ATTR_NAME);
        if (null == token)
        {
            token = RandomKeyGUID.getSecureRandomGUID();
            session.setAttribute(CSRF_TOKEN_FOR_SESSION_ATTR_NAME, token);
        }
        
        return token;
    }
    
    /**
     * Extracts the token value from the session
     * 
     * @param request
     * @return
     */
    public static String getTokenFromRequest(HttpServletRequest request)
    {
        return request.getParameter(CSRF_PARAM_NAME);
    }
    
    private CSRFTokenManager()
    {
    }
    
}
