package com.huawei.sharedrive.uam.cmb.openapi.domain;

import java.io.Serializable;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;

import com.huawei.sharedrive.uam.exception.InvalidParamterException;
import com.huawei.sharedrive.uam.terminal.service.impl.TerminalServiceImpl;

public class RequestSsoAPI implements Serializable
{
    private static final long serialVersionUID = 3603759127657383652L;
    
    private static final int DATA_LENGTH = 512;
    
    private static final int APPID_LENGTH = 20;
    
    private String data;
    
    private String token;
    
    private String appId;
    
    public void checkParameter(HttpServletRequest request)
    {
        if (StringUtils.isBlank(data) || StringUtils.isBlank(token))
        {
            String msg = "invalidate parameter data or token:" + token;
            throw new InvalidParamterException(msg);
        }
        if (data.length() > DATA_LENGTH || token.length() > DATA_LENGTH)
        {
            String msg = "The data or token is too long max length is 512";
            throw new InvalidParamterException(msg);
        }
        if (StringUtils.isNotBlank(appId))
        {
            if (appId.length() > APPID_LENGTH)
            {
                String msg = "appId is too long appId:" + appId;
                throw new InvalidParamterException(msg);
            }
        }
        TerminalServiceImpl.checkDeviceParamter(request);
    }
    
    public String getData()
    {
        return data;
    }
    
    public void setData(String data)
    {
        this.data = data;
    }
    
    public String getToken()
    {
        return token;
    }
    
    public void setToken(String token)
    {
        this.token = token;
    }
    
    public String getAppId()
    {
        return appId;
    }
    
    public void setAppId(String appId)
    {
        this.appId = appId;
    }
    
}
