package pw.cdmi.box.disk.user.shiro;

import org.apache.shiro.web.filter.authc.UserFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pw.cdmi.box.disk.share.domain.INodeLink;
import pw.cdmi.box.disk.share.domain.LinkAndNodeV2;
import pw.cdmi.box.disk.share.service.LinkService;
import pw.cdmi.core.exception.RestException;
import pw.cdmi.core.utils.SpringContextUtil;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

public class LinkCodeAuthFilter extends UserFilter {
    private static Logger logger = LoggerFactory.getLogger(LinkCodeAuthFilter.class);

    private String invalidLinkUrl = "/WEB-INF/views/share/linkInvalidRemind.jsp";

    public String getInvalidLinkUrl() {
        return invalidLinkUrl;
    }

    public void setInvalidLinkUrl(String invalidLinkUrl) {
        this.invalidLinkUrl = invalidLinkUrl;
    }

    @Override
    protected boolean isAccessAllowed(ServletRequest req, ServletResponse resp, Object mappedValue) {
        LinkService linkService = (LinkService) SpringContextUtil.getBean("linkService");
        HttpServletRequest httpRequest = (HttpServletRequest) req;
        String referer = httpRequest.getRequestURL().toString();
        String linkCode = referer.substring(referer.lastIndexOf('/') + 1);
        try {
            LinkAndNodeV2 linkAndNodeV2 = linkService.getLinkOnlyByLinkCode(linkCode);
            INodeLink iNodeLinkV2 = linkAndNodeV2.getLink();
            if (!iNodeLinkV2.isNeedLogin()) {
                return allowAnonymous(req, resp, mappedValue);
            }

            return denyAnonymous(req, resp, mappedValue);
        } catch (RestException e) {
            logger.warn("Failed to query the link info: error={}, message={}, code={}", e.getCode(), e.getMessage(), linkCode);
            try {
                httpRequest.getRequestDispatcher(invalidLinkUrl).forward(httpRequest, resp);
            } catch (Exception e1) {
                logger.warn("Failed to forward the response to location: {}, error={}", invalidLinkUrl, e.getMessage());
            }
        }

        return false;
    }

    /**
     */
    boolean allowAnonymous(ServletRequest req, ServletResponse resp, Object mappedValue) {
        return true;
    }

    /**
     */
    boolean denyAnonymous(ServletRequest req, ServletResponse resp, Object mappedValue) {
        AutoLoginAuthenticationFilter autoFilter = (AutoLoginAuthenticationFilter) SpringContextUtil.getBean("autoLoginFilter");
        return autoFilter.isAccessAllowed(req, resp, mappedValue);
    }

}
