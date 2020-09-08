package pw.cdmi.box.uam.httpclient.domain;

import java.io.Serializable;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;

import pw.cdmi.box.uam.exception.InvalidParamterException;
import pw.cdmi.box.uam.terminal.service.impl.TerminalServiceImpl;
import pw.cdmi.box.uam.util.PatternRegUtil;

public class RestUserLoginCreateRequest implements Serializable
{
    private static final int APPID_LENGTH = 20;
    
    private static final int LOGINNAME_LENGTH = 255;
    
    private static final long serialVersionUID = 7876808931188377625L;
    
    private String appId;
    
    private String domain;
    
    private String loginName;
    
    private String password;
    
    public void checkParameter(HttpServletRequest request)
    {
        if (StringUtils.isBlank(appId) || StringUtils.isBlank(loginName) || StringUtils.isBlank(password))
        {
            String msg = "invalidate parameter loginName or appId:" + appId;
            throw new InvalidParamterException(msg);
        }
        if (appId.length() > APPID_LENGTH || loginName.length() > LOGINNAME_LENGTH)
        {
            String msg = "The login name or appId is too long loginName:" + loginName + " appId:" + appId;
            throw new InvalidParamterException(msg);
        }
        if (StringUtils.isNotBlank(domain))
        {
            if (!PatternRegUtil.checkDomainRegex(domain))
            {
                String msg = "domainName is invalid:" + domain;
                throw new InvalidParamterException(msg);
            }
        }
        TerminalServiceImpl.checkDeviceParamter(request);
    }
    
    public String getAppId()
    {
        return appId;
    }
    
    public String getDomain()
    {
        return domain;
    }
    
    public String getLoginName()
    {
        return loginName;
    }
    
    public String getPassword()
    {
        return password;
    }
    
    public void setAppId(String appId)
    {
        this.appId = appId;
    }
    
    public void setDomain(String domain)
    {
        this.domain = domain;
    }
    
    public void setLoginName(String loginName)
    {
        this.loginName = loginName;
    }
    
    public void setPassword(String password)
    {
        this.password = password;
    }
    
}
