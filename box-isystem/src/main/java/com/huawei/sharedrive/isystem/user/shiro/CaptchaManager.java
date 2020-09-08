package com.huawei.sharedrive.isystem.user.shiro;

import org.apache.commons.lang.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.springframework.stereotype.Service;

import com.huawei.sharedrive.isystem.exception.IncorrectCaptchaException;
import com.huawei.sharedrive.isystem.util.Constants;

@Service("captchaManager")
public class CaptchaManager
{
    public void validateCaptcha(String realValue) throws IncorrectCaptchaException
    {
        Session session = SecurityUtils.getSubject().getSession();
        Object captchaInSession = session.getAttribute(Constants.HW_VERIFY_CODE_CONST);
        session.setAttribute(Constants.HW_VERIFY_CODE_CONST, "");
        String value = captchaInSession == null ? "" : captchaInSession.toString();
        if (!StringUtils.equalsIgnoreCase(value, realValue))
        {
            throw new IncorrectCaptchaException("");
        }
    }
}
