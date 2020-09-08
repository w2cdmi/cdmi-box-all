package com.huawei.sharedrive.uam.cmb.sso.web;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.huawei.sharedrive.uam.cmb.control.manager.Constants;
import com.huawei.sharedrive.uam.cmb.sso.util.CMBCertUtils;
import com.huawei.sharedrive.uam.common.web.AbstractCommonController;
import com.huawei.sharedrive.uam.sso.manager.SSOTmpTokenManager;

@Controller
@RequestMapping(value = "/sso/ssocmb")
public class SSOCmbController extends AbstractCommonController
{
    
    @Autowired
    private SSOTmpTokenManager sSOTmpTokenManager;
    
    @RequestMapping(value = "", method = RequestMethod.GET)
    public void enter(HttpServletRequest request, HttpServletResponse response) throws IOException
    {
        if (!Constants.isCMB)
        {
            return;
        }
        String userData = request.getParameter(Constants.USERKEY);
        String tokenData = request.getParameter(Constants.TOKENKEY);
        String netAction = request.getParameter(Constants.NETACTIONKEY);
        String userInfo = "";
        if (tokenData != null)
        {
            userInfo = CMBCertUtils.verifySign(userData, tokenData);
        }
        if (StringUtils.isBlank(userInfo))
        {
            if (Constants.IS_BUSINESS_BOX)
            {
                response.sendRedirect(Constants.CLOUDAPP_LOGIN_URL);
                return;
            }
            else
            {
                response.sendRedirect(Constants.CMB_URL);
                return;
            }
        }
        if (CMBCertUtils.isSSOTimeExpired(userInfo))
        {
            if (Constants.IS_BUSINESS_BOX)
            {
                response.sendRedirect(Constants.CLOUDAPP_LOGIN_URL);
                return;
            }
            else
            {
                response.sendRedirect(Constants.CMB_URL);
                return;
            }
        }
        String sapId = CMBCertUtils.phraseSSOResult(userInfo);
        if (StringUtils.isBlank(sapId))
        {
            if (Constants.IS_BUSINESS_BOX)
            {
                response.sendRedirect(Constants.CLOUDAPP_LOGIN_URL);
                return;
            }
            else
            {
                response.sendRedirect(Constants.CMB_URL);
                return;
            }
        }
        String tmpToken = sSOTmpTokenManager.saveSSOTmpToken(sapId);
        StringBuilder sb = new StringBuilder(Constants.CLOUDAPP_URL);
        sb.append("?ssotoken=").append(tmpToken);
        if (null != netAction)
        {
            sb.append("&nextAction=").append(toSafeValue(netAction));
        }
        response.sendRedirect(sb.toString());
    }
    
    private static final char[] UNSAFE_CHAR = new char[]{'\n', '\r', '=', ':'};
    
    public static final String toSafeValue(String value)
    {
        if (StringUtils.isEmpty(value))
        {
            return value;
        }
        String safeValue = value;
        for (char c : UNSAFE_CHAR)
        {
            safeValue = StringUtils.remove(safeValue, c);
        }
        return safeValue;
    }
    
}
