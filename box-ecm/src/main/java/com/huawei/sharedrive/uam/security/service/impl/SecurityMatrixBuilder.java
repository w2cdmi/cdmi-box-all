package com.huawei.sharedrive.uam.security.service.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.ServletRequest;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import pw.cdmi.common.cache.CacheClient;
import pw.cdmi.core.utils.IpUtils;

import com.huawei.it.usermanage.UserTypeUtils;
import com.huawei.sharedrive.integration.CloudDriveIntegrationService;
import com.huawei.sharedrive.integration.domain.EMPIPInfo;
import com.huawei.sharedrive.uam.exception.InvalidParamterException;
import com.huawei.sharedrive.uam.httpclient.rest.response.INodeResponse;
import com.huawei.sharedrive.uam.security.domain.NetworkRegion;
import com.huawei.sharedrive.uam.security.domain.NetworkType;
import com.huawei.sharedrive.uam.security.domain.ProxyNetwork;
import com.huawei.sharedrive.uam.security.domain.SecurityConstants;
import com.huawei.sharedrive.uam.security.domain.SecurityFactor;
import com.huawei.sharedrive.uam.security.domain.SecurityFactorType;
import com.huawei.sharedrive.uam.security.domain.SecurityMatrix;
import com.huawei.sharedrive.uam.security.domain.SecurityMatrixObject;
import com.huawei.sharedrive.uam.security.domain.SecurityPrincipal;
import com.huawei.sharedrive.uam.security.domain.UserSpecial;
import com.huawei.sharedrive.uam.security.domain.UserSpecialType;
import com.huawei.sharedrive.uam.security.domain.UserType;
import com.huawei.sharedrive.uam.security.service.MatrixContext;
import com.huawei.sharedrive.uam.security.service.NetworkRegionService;
import com.huawei.sharedrive.uam.security.service.ProxyNetworkService;
import com.huawei.sharedrive.uam.security.service.SecurityFactorService;
import com.huawei.sharedrive.uam.security.service.SecurityMatrixService;
import com.huawei.sharedrive.uam.security.service.UserSpecialService;

@Component
public class SecurityMatrixBuilder
{
    private static Logger logger = LoggerFactory.getLogger(SecurityMatrixBuilder.class);
    
    @Resource(name = "cacheClient")
    private CacheClient cacheClient;
    
    @Autowired
    private CloudDriveIntegrationService cloudDriveIntegrationService;
    
    @Autowired
    private MatrixContext matrixContext;
    
    @Autowired
    private NetworkRegionService networkRegionService;
    
    private SecurityPrincipal principal;
    
    @Autowired
    private ProxyNetworkService proxyNetworkService;
    
    @Autowired
    private SecurityFactorService securityFactorService;
    
    @Autowired
    private SecurityMatrixService securityMatrixService;
    
    @Autowired
    private UserSpecialService userSpecialService;
    
    /**
     * 
     * @param deviceType
     * @param networkType
     * @param userType
     * @param onwerUserType
     * @param resExtendType
     * 
     * @return
     */
    @SuppressWarnings("PMD.ExcessiveParameterList")
    public static String buildRoleName(String appType, String userType, String networkType,
        String deviceType, String onwerUserType, String resExtendType)
    {
        StringBuilder sb = new StringBuilder();
        sb.append(appType.toUpperCase(Locale.getDefault()));
        sb.append(SecurityConstants.ROLE_SPLIT);
        sb.append(userType.toUpperCase(Locale.getDefault()));
        sb.append(SecurityConstants.ROLE_SPLIT);
        sb.append(networkType.toUpperCase(Locale.getDefault()));
        sb.append(SecurityConstants.ROLE_SPLIT);
        sb.append(deviceType.toUpperCase(Locale.getDefault()));
        sb.append(SecurityConstants.ROLE_SPLIT);
        sb.append(onwerUserType.toUpperCase(Locale.getDefault()));
        sb.append(SecurityConstants.ROLE_SPLIT);
        sb.append(resExtendType.toUpperCase(Locale.getDefault()));
        return sb.toString().toUpperCase(Locale.ENGLISH);
    }
    
    public SecurityMatrixObject build()
    {
        if (!this.validation())
        {
            throw new InvalidParamterException("The Security Matrix Context Info can't be null.");
        }
        SecurityPrincipal principal = this.matrixContext.getSecurityPrincipal();
        
        int deviceType = this.getDeviceTypeByToken(principal);
        int userType = this.getUserTypeByToken(principal);
        int networkType = this.getNetworkTypeByToken(principal);
        int onwerUserType = this.getOnwerUserTypeByToken(principal);
        String resExtendType = this.getExtendType(principal.getiNode());
        
        SecurityFactor deviceFactor = securityFactorService.getSecurityFactorByCode(SecurityFactorType.DeviceType.getType(),
            deviceType);
        SecurityFactor userFactor = securityFactorService.getSecurityFactorByCode(SecurityFactorType.UserType.getType(),
            userType);
        SecurityFactor networkFactor = securityFactorService.getSecurityFactorByCode(SecurityFactorType.NetworkType.getType(),
            networkType);
        SecurityFactor onwerUserFactor = securityFactorService.getSecurityFactorByCode(SecurityFactorType.SrcResourceType.getType(),
            onwerUserType);
        SecurityFactor appFactor = securityFactorService.getSecurityFactorByName(SecurityFactorType.AppType.getType(),
            principal.getAppId());
        
        String roleName = SecurityMatrixBuilder.buildRoleName(principal.getAppId(),
            userFactor.getName(),
            networkFactor.getName(),
            deviceFactor.getName(),
            onwerUserFactor.getName(),
            resExtendType);
        SecurityMatrixObject securityMatrix = null;
        String key = SecurityMatrixBuilder.class.getSimpleName() + "_" + roleName;
        Object cacheMatrix = cacheClient.getCache(key);
        if (cacheMatrix != null)
        {
            securityMatrix = (SecurityMatrixObject) cacheMatrix;
        }
        else
        {
            SecurityMatrix queryObject = new SecurityMatrix();
            queryObject.setAppType(appFactor);
            queryObject.setDeviceType(deviceFactor);
            queryObject.setNetworkType(networkFactor);
            queryObject.setUserType(userFactor);
            queryObject.setSrcResourceType(onwerUserFactor);
            SecurityFactor factor = securityFactorService.getSecurityFactorByName(SecurityFactorType.ResExtendType.getType(),
                resExtendType);
            queryObject.setResExtendType(factor);
            
            List<SecurityMatrix> matrixList = securityMatrixService.queryMatrixByNFactor(queryObject);
            if (CollectionUtils.isEmpty(matrixList))
            {
                logger.info("Don't match one security matrix in appId [ " + appFactor.getName()
                    + "] usertype [" + userType + "],networkType [" + networkType + "],deviceType["
                    + deviceType + "],OnwerUserType[" + onwerUserType + "],resType[" + resExtendType + "]");
            }
            else
            {
                securityMatrix = new SecurityMatrixObject(userType, networkType, deviceType);
                securityMatrix.setRoleName(matrixList.get(0).getRoleName());
                securityMatrix.setPermissions(this.buildPermissions(matrixList));
                cacheClient.setCacheNoExpire(key, securityMatrix);
            }
        }
        // maybe null;
        return securityMatrix;
    }
    
    public SecurityMatrixBuilder setMatrixContext(MatrixContext matrixContext)
    {
        this.matrixContext = matrixContext;
        return this;
    }
    
    public SecurityMatrixBuilder setRequest(ServletRequest request)
    {
        this.matrixContext.setRequest(request);
        return this;
    }
    
    public SecurityMatrixBuilder setSecurityPrincipal(SecurityPrincipal principal)
    {
        if (principal == null)
        {
            throw new InvalidParamterException("The SecurityPrincipal can't be null when build security matrix");
        }
        this.matrixContext.setSecurityPrincipal(principal);
        this.principal = principal;
        return this;
    }
    
    private Set<String> buildPermissions(List<SecurityMatrix> matrixList)
    {
        Set<String> permissions = new HashSet<String>(10);
        String permissionValue;
        String[] pvs;
        for (SecurityMatrix sm : matrixList)
        {
            permissionValue = sm.getPermissionValue();
            pvs = permissionValue.split(SecurityConstants.PERMISSION_SPLIT);
            for (String p : pvs)
            {
                permissions.add(p);
            }
        }
        return permissions;
    }
    
    private int getDeviceTypeByToken(SecurityPrincipal principal)
    {
        return principal.getDeviceType();
    }
    
    private String getExtendType(INodeResponse inode)
    {
        String resExtendType = "NONE";
        if (inode == null)
        {
            return resExtendType;
        }
        if (inode.getType() == 0)
        {
            resExtendType = "FOLDER";
        }
        else
        {
            resExtendType = FilenameUtils.getExtension(inode.getName());
        }
        if (StringUtils.isEmpty(resExtendType))
        {
            resExtendType = "";
        }
        return resExtendType;
    }
    
    @SuppressWarnings("PMD.AvoidCatchingThrowable")
    private int getNetworkTypeByToken(SecurityPrincipal principal)
    {
        String proxyAddress = principal.getProxyAddress();
        if (IpUtils.isIPv4LiteralAddress(proxyAddress))
        {
            List<ProxyNetwork> proxys = proxyNetworkService.getTypeList(NetworkType.Internet.getType());
            for (ProxyNetwork p : proxys)
            {
                if (p.getAccessIp().equals(proxyAddress))
                {
                    return NetworkType.Internet.getType();
                }
            }
        }
        
        try
        {
            EMPIPInfo ipInfo = cloudDriveIntegrationService.queryNetworkType(principal.getDeviceAddress());
            if (ipInfo != null && ipInfo.isResult())
            {
                NetworkType networkType = NetworkType.parseType(ipInfo.getNetworkType());
                NetworkType nt = null;
                if (networkType != null)
                {
                    nt = networkType;
                    return nt.getType();
                }
            }
            else
            {
                long ipValue = IpUtils.toLong(principal.getDeviceAddress());
                NetworkRegion networkRegion = networkRegionService.getByIpValue(ipValue);
                if (networkRegion != null)
                {
                    return networkRegion.getNetworkType();
                }
            }
        }
        catch (Throwable e)
        {
            logger.warn("Obtain networktype from EMP occur error. try get from local", e);
            long ipValue = IpUtils.toLong(principal.getDeviceAddress());
            NetworkRegion networkRegion = networkRegionService.getByIpValue(ipValue);
            if (networkRegion != null)
            {
                return networkRegion.getNetworkType();
            }
        }
        return NetworkType.Others.getType();
    }
    
    private int getUserTypeByToken(SecurityPrincipal principal)
    {
        int userType = principal.getPrincipalType();
        UserSpecial userSpecial = new UserSpecial(principal.getLoginName(), UserSpecialType.BlackListUser);
        userSpecial = userSpecialService.getUserSpecial(userSpecial);
        if (userSpecial != null)
        {
            return UserType.BlackListUser.getType();
        }
        userSpecial = new UserSpecial(principal.getLoginName(), UserSpecialType.NORDConfidential);
        userSpecial = userSpecialService.getUserSpecial(userSpecial);
        if (userSpecial != null)
        {
            return UserType.NORDConfidential.getType();
        }
        
        userSpecial = new UserSpecial(principal.getLoginName(), UserSpecialType.RDConfidential);
        userSpecial = userSpecialService.getUserSpecial(userSpecial);
        if (userSpecial != null)
        {
            return UserType.RDConfidential.getType();
        }
        
        userSpecial = new UserSpecial(principal.getLoginName(), UserSpecialType.WhiteListUser);
        userSpecial = userSpecialService.getUserSpecial(userSpecial);
        if (userSpecial != null)
        {
            return UserType.WhiteListUser.getType();
        }
        
        boolean isInnerUser = UserTypeUtils.isHWUser(principal.getLoginName());
        if (!isInnerUser)
        {
            return UserType.OUTER.getType();
        }
        
        return userType;
    }
    
    private int getOnwerUserTypeByToken(SecurityPrincipal principal)
    {
        int userType = principal.getResourceType();
        UserSpecial userSpecial = new UserSpecial(principal.getOnwerUser().getLoginName(),
            UserSpecialType.BlackListUser);
        userSpecial = userSpecialService.getUserSpecial(userSpecial);
        if (userSpecial != null)
        {
            return UserType.BlackListUser.getType();
        }
        userSpecial = new UserSpecial(principal.getOnwerUser().getLoginName(),
            UserSpecialType.NORDConfidential);
        userSpecial = userSpecialService.getUserSpecial(userSpecial);
        if (userSpecial != null)
        {
            return UserType.NORDConfidential.getType();
        }
        
        userSpecial = new UserSpecial(principal.getOnwerUser().getLoginName(), UserSpecialType.RDConfidential);
        userSpecial = userSpecialService.getUserSpecial(userSpecial);
        if (userSpecial != null)
        {
            return UserType.RDConfidential.getType();
        }
        
        userSpecial = new UserSpecial(principal.getOnwerUser().getLoginName(), UserSpecialType.WhiteListUser);
        userSpecial = userSpecialService.getUserSpecial(userSpecial);
        if (userSpecial != null)
        {
            return UserType.WhiteListUser.getType();
        }
        
        boolean isInnerUser = UserTypeUtils.isHWUser(principal.getOnwerUser().getLoginName());
        if (!isInnerUser)
        {
            return UserType.OUTER.getType();
        }
        
        return userType;
    }
    
    private boolean validation()
    {
        if (this.principal == null)
        {
            return false;
        }
        if (!IpUtils.isIPv4LiteralAddress(this.principal.getDeviceAddress()))
        {
            logger.info("The Security Martix Device Address is not valid ,get ip is :"
                + this.principal.getDeviceAddress());
            return false;
        }
        return true;
    }
}
