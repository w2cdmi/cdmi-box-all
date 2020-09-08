package com.huawei.sharedrive.cloudapp.cmb.openapi.rest.domain;

import java.io.Serializable;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;

import com.huawei.sharedrive.cloudapp.client.exception.InvalidParamException;

public class CMBSsoAPIRequest implements Serializable
{
    private static final long serialVersionUID = -675606125557020921L;
    
    private static final int DATA_LENGTH = 512;
    
    private static final int APPID_LENGTH = 20;
    
    private String data;
    
    private String token;
    
    private String appId;
    
    public void checkParameter(HttpServletRequest request)
    {
        if (StringUtils.isBlank(data) || StringUtils.isBlank(token))
        {
            
            throw new InvalidParamException("invalidate parameter data or token:" + token);
        }
        if (data.length() > DATA_LENGTH || token.length() > DATA_LENGTH)
        {
            
            throw new InvalidParamException("The data or token is too long max length is 512");
        }
        if (StringUtils.isNotBlank(appId))
        {
            if (appId.length() > APPID_LENGTH)
            {
                throw new InvalidParamException("appId is too long appId:" + appId);
            }
        }
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
