package pw.cdmi.box.disk.core.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import pw.cdmi.box.disk.security.service.SecurityMatrixHelper;
import pw.cdmi.box.disk.utils.BasicConstants;

public class RealIpInterceptor extends HandlerInterceptorAdapter
{
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
        throws Exception
    {
        SecurityMatrixHelper.clear();
        String realIp = request.getHeader(BasicConstants.HTTP_X_REAL_IP);
        if (StringUtils.isNotBlank(realIp))
        {
            SecurityMatrixHelper.setRealIP(realIp);
        }
        return super.preHandle(request, response, handler);
    }
}
