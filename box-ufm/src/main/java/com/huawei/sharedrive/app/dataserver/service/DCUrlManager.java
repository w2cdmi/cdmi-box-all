/**
 * 
 */
package com.huawei.sharedrive.app.dataserver.service;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import com.huawei.sharedrive.app.authapp.service.AuthAppService;
import com.huawei.sharedrive.app.dataserver.WebProtocol;
import com.huawei.sharedrive.app.dataserver.domain.DataAccessURLInfo;
import com.huawei.sharedrive.app.dataserver.domain.ResourceGroup;
import com.huawei.sharedrive.app.dns.service.DssDomainService;
import com.huawei.sharedrive.app.exception.BaseRunException;
import com.huawei.sharedrive.app.files.domain.INode;
import com.huawei.sharedrive.app.oauth2.domain.UserToken;
import com.huawei.sharedrive.app.system.service.SystemConfigService;
import com.huawei.sharedrive.app.teamspace.service.TeamSpaceService;

import pw.cdmi.common.domain.SystemConfig;
import pw.cdmi.core.exception.InnerException;
import pw.cdmi.uam.domain.AuthApp;


/**
 * 数据服务器管理接口，提供数据服务器的各种管理操作
 * 
 * @author s00108907
 * 
 */
@Service("dcUrlManager")
public class DCUrlManager
{
    
    private enum REQUEST_METHOD
    {
        GET, PUT;
    }
    
    private static Logger logger = LoggerFactory.getLogger(DCUrlManager.class);
    
    private static final String PORT_SPLIT = ":";
    
    
    private static final String URL_SPLIT = "/";
    
    private static final String URL_SUFFIX = "/api/";
    
    
    
    @Autowired
    private AuthAppService authAppService;
    
    
    @Autowired
    private DCManager dcManager;
    
    @Autowired
    private DcUrlHelper dcUrlHelper;
    
    @Autowired
    private SystemConfigService systemConfigService;
    
    @Autowired
    private DssDomainService dssDomainService;
    
    @Autowired
    private TeamSpaceService teamSpaceService;
    
    /**
     * 获取下载地址
     * 
     * @param groupid
     * @return
     * @throws BaseRunException
     */
    public DataAccessURLInfo getDownloadURL(int groupID, Integer qosPort) throws BaseRunException
    {
        ResourceGroup resourceGroup = dcManager.getCacheResourceGroup(groupID);
        
        if (null == resourceGroup)
        {
            String message = "No Such ResourceGroup [ " + groupID + " ]";
            logger.warn(message);
            throw new InnerException(message);
        }
        
        logger.info("id [" + resourceGroup.getDcId() + "] domian [" + resourceGroup.getDomainName()
            + "] get protocat [" + resourceGroup.getGetProtocol() + "]");
        
        logger.info("http port [" + resourceGroup.getServiceHttpPort() + "https port ["
            + resourceGroup.getServiceHttpsPort() + "]");
        
        String url = null;
        if (StringUtils.isBlank(resourceGroup.getDomainName()))
        {
            String domain = dssDomainService.getOuterDomainByDssId(resourceGroup);
            url = getAccessUrl(getWebProtocol(REQUEST_METHOD.GET, resourceGroup),
                domain,
                resourceGroup,
                qosPort);
        }
        else
        {
            url = getAccessUrl(getWebProtocol(REQUEST_METHOD.GET, resourceGroup),
                HtmlUtils.htmlEscape(resourceGroup.getDomainName()),
                resourceGroup,
                qosPort);
        }
        
        DataAccessURLInfo urlInfo = new DataAccessURLInfo(groupID, url, null);
        
        logger.info("url:" + url);
        return urlInfo;
    }
    
	  public DataAccessURLInfo getPreviewUrl(int groupID, Integer qosPort)
        throws BaseRunException {
        ResourceGroup resourceGroup = dcManager.getCacheResourceGroup(groupID);

        if (null == resourceGroup) {
            String message = "No Such ResourceGroup [ " + groupID + " ]";
            logger.warn(message);
            throw new InnerException(message);
        }

        logger.info("id [" + resourceGroup.getDcId() + "] domian [" +
            resourceGroup.getDomainName() + "] get protocat [" +
            resourceGroup.getGetProtocol() + "]");

        logger.info("http port [" + resourceGroup.getServiceHttpPort() +
            "https port [" + resourceGroup.getServiceHttpsPort() + "]");

        String url = null;

        if (StringUtils.isBlank(resourceGroup.getDomainName())) {
            String domain = dssDomainService.getOuterDomainByDssId(resourceGroup);
            url = getAccessUrl(getWebProtocol(REQUEST_METHOD.GET, resourceGroup),
                    domain, resourceGroup, qosPort);
        } else {
            url = getAccessUrl(getWebProtocol(REQUEST_METHOD.GET, resourceGroup),
                    HtmlUtils.htmlEscape(resourceGroup.getDomainName()),
                    resourceGroup, qosPort);
        }

        DataAccessURLInfo urlInfo = new DataAccessURLInfo(groupID, null, null,
                url);

        logger.info("url:" + url);

        return urlInfo;
    }
    public DataAccessURLInfo getUploadURL(int groupId, Integer qosPort) throws BaseRunException
    {
        ResourceGroup resourceGroup = dcManager.getCacheResourceGroup(groupId);
        if (null == resourceGroup)
        {
            String message = "No Such ResourceGroup [ " + groupId + " ]";
            logger.warn(message);
            throw new InnerException(message);
        }
        String url = null;
        if (StringUtils.isBlank(resourceGroup.getDomainName()))
        {
            String domain = dssDomainService.getOuterDomainByDssId(resourceGroup);
            url = getAccessUrl(getWebProtocol(REQUEST_METHOD.PUT, resourceGroup),
                domain,
                resourceGroup,
                qosPort);
        }
        else
        {
            url = getAccessUrl(getWebProtocol(REQUEST_METHOD.PUT, resourceGroup),
                resourceGroup.getDomainName(),
                resourceGroup,
                qosPort);
        }
        
        DataAccessURLInfo urlInfo = new DataAccessURLInfo(resourceGroup.getId(), null, url);
        return urlInfo;
    }
    
    public DataAccessURLInfo getUploadURL(UserToken userToken ,INode iNode, boolean enableUploadNearest) throws BaseRunException
    {
        boolean isTeamSpace = teamSpaceService.isTeamSpace(iNode.getOwnedBy());
        // 启用就近上传
        AuthApp authApp = authAppService.getByAuthAppID(userToken.getAppId());
        int region = getUploadRegionId(userToken, enableUploadNearest, isTeamSpace, authApp);
        
        ResourceGroup resourceGroup = dcUrlHelper.getAavailableResourceGroup(region);

        if (null == resourceGroup)
        {
            String message = "No Support ResourceGroup For User [ " + userToken.getId() + ',' + region + " ]";
            logger.warn(message);
            throw new InnerException(message);
        }
        // 获取QOS端口
        Integer qosPort = authApp != null ? authApp.getQosPort() : null;
        String url = null;
        if (StringUtils.isBlank(resourceGroup.getDomainName()))
        {
            String domain = dssDomainService.getOuterDomainByDssId(resourceGroup);
            url = getAccessUrl(getWebProtocol(REQUEST_METHOD.PUT, resourceGroup),
                domain,
                resourceGroup,
                qosPort);
        }
        else
        {
            url = getAccessUrl(getWebProtocol(REQUEST_METHOD.PUT, resourceGroup),
                resourceGroup.getDomainName(),
                resourceGroup,
                qosPort);
        }
        
        DataAccessURLInfo urlInfo = new DataAccessURLInfo(resourceGroup.getId(), null, url);
        return urlInfo;
    }

    private int getUploadRegionId(UserToken userToken, boolean enableUploadNearest,
        boolean isTeamSpace, AuthApp authApp)
    {
        int region = userToken.getRegionId();
        if (authApp != null && AuthApp.NEARESTSTORE_ENABLE == authApp.getNearestStore()
            && userToken.getLoginRegion() != null)
        {
            SystemConfig systemConfig = systemConfigService.getConfig("nearestStore.Status");
            int type = Integer.parseInt(systemConfig.getValue());
            if(isTeamSpace && type!=1)
            {
                region = userToken.getLoginRegion();
                logger.info("enable teamspace nearestStore");
            }
            else if(!isTeamSpace && type!=2)
            {
                region = userToken.getLoginRegion();
                logger.info("enable userspace nearestStore");
            }
            logger.info("User login region: {}", region);
        }
        if(enableUploadNearest)
        {
            region = userToken.getLoginRegion();
            logger.info("[uploadNearest]enable userspace nearestStore");
        }
        return region;
    }
    
    
    
    
    
    /**
     * 通过资源组信息，或者访问地址，如果资源组配置了域名，则该方法返回值不为空，如果未配置，则该方法返回Null
     * 
     * @param group
     * @return
     */
    private String getAccessUrl(WebProtocol webProtocol, String addr, ResourceGroup group, Integer qosPort)
    {
        StringBuilder url = new StringBuilder(webProtocol.getUrlPrefix()).append(StringUtils.trimToEmpty(addr));
        
        // 如果设置了QOS端口, 则使用QOS端口生成访问地址
        String port = qosPort == null ? getWebPort(webProtocol, group) : String.valueOf(qosPort);
        if (StringUtils.isNotBlank(port))
        {
            url.append(PORT_SPLIT).append(port);
        }
        
        if (StringUtils.isNotBlank(group.getServicePath()))
        {
            url.append(URL_SPLIT).append(StringUtils.trimToEmpty(group.getServicePath()));
        }
        
        url.append(URL_SUFFIX);
        
        return url.toString();
    }
    
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
