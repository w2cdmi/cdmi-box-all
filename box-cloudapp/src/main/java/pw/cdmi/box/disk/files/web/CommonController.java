package pw.cdmi.box.disk.files.web;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.util.HtmlUtils;

import pw.cdmi.box.disk.client.domain.node.Thumbnail;
import pw.cdmi.box.disk.client.utils.RestConstants;
import pw.cdmi.box.disk.oauth2.domain.UserToken;
import pw.cdmi.box.disk.user.service.UserTokenManager;
import pw.cdmi.box.disk.utils.BusinessConstants;
import pw.cdmi.box.disk.utils.CSRFTokenManager;
import pw.cdmi.box.disk.utils.CommonTools;
import pw.cdmi.common.util.signature.SignatureUtils;
import pw.cdmi.core.exception.BadRquestException;
import pw.cdmi.core.utils.DateUtils;

@Controller
public class CommonController
{
    private static final List<Thumbnail> DEFAULT_THUMBNAILS = new ArrayList<Thumbnail>(2);
    static
    {
        Thumbnail smallThumb = new Thumbnail(Thumbnail.DEFAULT_SMALL_WIDTH, Thumbnail.DEFAULT_SMALL_HEIGHT);
        Thumbnail bigThumb = new Thumbnail(Thumbnail.DEFAULT_BIG_WIDTH, Thumbnail.DEFAULT_BIG_HEIGHT);
        
        DEFAULT_THUMBNAILS.add(smallThumb);
        DEFAULT_THUMBNAILS.add(bigThumb);
    }
    
    @Autowired
    private UserTokenManager userTokenManager;
    
    /**
     * 
     * @param request
     * @throws BadRquestException
     */
    protected void checkToken(HttpServletRequest request) throws BadRquestException
    {
        String sToken = (String) request.getSession()
            .getAttribute(CSRFTokenManager.CSRF_TOKEN_FOR_SESSION_ATTR_NAME);
        
        if (StringUtils.isBlank(sToken))
        {
            CSRFTokenManager.getTokenForSession(request.getSession());
        }
        else
        {
            String pToken = request.getParameter("token");
            if (pToken == null || !pToken.equals(sToken))
            {
                throw new BadRquestException();
            }
        }
    }
    
    protected UserToken getCurrentUser()
    {
        return (UserToken) SecurityUtils.getSubject().getPrincipal();
    }
    
    protected List<Thumbnail> getDefultThumbnails()
    {
        return DEFAULT_THUMBNAILS;
    }
    
    protected String getToken()
    {
        return userTokenManager.getToken();
    }
    
    protected String htmlEscapeThumbnail(String thumbnailUrl)
    {
        if (thumbnailUrl == null)
        {
            return "";
        }
        int pos = thumbnailUrl.lastIndexOf("&");
        
        if (pos == -1)
        {
            return "";
        }
        return HtmlUtils.htmlEscape(thumbnailUrl.substring(0, pos)) + thumbnailUrl.substring(pos);
    }
    
    protected Map<String, String> assembleLink(String linkCode)
    {
        Map<String, String> headers = new HashMap<String, String>(BusinessConstants.INITIAL_CAPACITIES);
        String accessCode = CommonTools.getAccessCode(linkCode);
        
        String dateStr = DateUtils.dataToString(DateUtils.RFC822_DATE_FORMAT, new Date(), null);
        String authStr = "link," + linkCode + ',' + SignatureUtils.getSignature(accessCode, dateStr);
        headers.put(RestConstants.HEADER_AUTHORIZATION, authStr);
        headers.put(RestConstants.HEADER_DATE, dateStr);
        return headers;
    }
}
