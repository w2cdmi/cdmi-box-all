package com.huawei.sharedrive.app.dataserver.service;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import com.huawei.sharedrive.app.dataserver.WebProtocol;
import com.huawei.sharedrive.app.dataserver.domain.DataAccessURLInfo;
import com.huawei.sharedrive.app.dataserver.domain.ResourceGroup;
import com.huawei.sharedrive.app.exception.BaseRunException;
import com.huawei.sharedrive.app.files.domain.INode;
import com.huawei.sharedrive.app.oauth2.domain.UserToken;

@Service("dcSimpleUrlManager")
public class DCSimpleUrlManager
{
    public DataAccessURLInfo getUploadURL(UserToken userToken ,INode iNode, Integer qosPort, ResourceGroup resourceGroup, String domain) throws BaseRunException
    {
        String url = packAccessUrl(getWebProtocol(REQUEST_METHOD.PUT, resourceGroup),
            domain,
            resourceGroup,
            qosPort);
        DataAccessURLInfo urlInfo = new DataAccessURLInfo(resourceGroup.getId(), null, url);
        return urlInfo;
    }
    
    private enum REQUEST_METHOD
    {
        GET, PUT;
    }
    
    /**
     * 通过资源组信息，或者访问地址，如果资源组配置了域名，则该方法返回值不为空，如果未配置，则该方法返回Null
     * <br/>
     * 不涉及数据库操作和外部交互
     * @param group
     * @return
     */
    private String packAccessUrl(WebProtocol webProtocol, String addr, ResourceGroup group, Integer qosPort)
    {
        StringBuilder url = new StringBuilder(webProtocol.getUrlPrefix()).append(StringUtils.trimToEmpty(addr));
        String port;
        if(qosPort == null)
        {
            port = getWebPort(webProtocol, group);
        }
        else
        {
            port = String.valueOf(qosPort);
        }
        if (StringUtils.isNotBlank(port))
        {
            url.append(PORT_SPLIT).append(port);
        }
        if (StringUtils.isNotBlank(group.getServicePath()))
        {
            url.append(URL_SPLIT).append(StringUtils.trimToEmpty(group.getServicePath()));
        }
        url.append("/api/");
        return url.toString();
    }
    
    private static final String PORT_SPLIT = ":";
    
    
    private static final String URL_SPLIT = "/";
    
    
    private String getWebPort(WebProtocol webProtocol, ResourceGroup group)
    {
        int port = 8443;
        if (WebProtocol.HTTP == webProtocol)
        {
            port = group.getServiceHttpPort();
        }
        else
        {
            port = group.getServiceHttpsPort();
        }
        
        if (port <= 0 || 80 == port || 443 == port)
        {
            return "";
        }
        
        return String.valueOf(port);
    }
    
    /**
     * 获取到默认的访问协议
     * 
     * @return
     */
    private WebProtocol getWebProtocol(REQUEST_METHOD requestMethod, ResourceGroup resourceGroup)
    {
        if (REQUEST_METHOD.GET == requestMethod)
        {
            return WebProtocol.parseByScheme(resourceGroup.getGetProtocol());
        }
        return WebProtocol.parseByScheme(resourceGroup.getPutProtocol());
    }
    
}
