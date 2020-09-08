package com.huawei.sharedrive.uam.enterprise.manager.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.huawei.sharedrive.uam.accountuser.domain.UserAccount;
import com.huawei.sharedrive.uam.accountuser.service.UserAccountService;
import com.huawei.sharedrive.uam.enterprise.domain.AccessConfig;
import com.huawei.sharedrive.uam.enterprise.domain.AccessSpaceConfig;
import com.huawei.sharedrive.uam.enterprise.domain.AccountSecConfig;
import com.huawei.sharedrive.uam.enterprise.domain.FileCopySecurity;
import com.huawei.sharedrive.uam.enterprise.domain.NetRegionIp;
import com.huawei.sharedrive.uam.enterprise.domain.ResourceStrategy;
import com.huawei.sharedrive.uam.enterprise.domain.SecOperation;
import com.huawei.sharedrive.uam.enterprise.manager.SecMatrixManager;
import com.huawei.sharedrive.uam.enterprise.service.AccessConfigService;
import com.huawei.sharedrive.uam.enterprise.service.AccessSpaceConfigService;
import com.huawei.sharedrive.uam.enterprise.service.AccountSecConfigService;
import com.huawei.sharedrive.uam.enterprise.service.FileCopySecurityService;
import com.huawei.sharedrive.uam.enterprise.service.NetRegionService;
import com.huawei.sharedrive.uam.enterprise.service.SafeLevelService;
import com.huawei.sharedrive.uam.exception.NoSuchUserException;
import com.huawei.sharedrive.uam.openapi.domain.secmatrix.FileSecResponse;
import com.huawei.sharedrive.uam.openapi.domain.secmatrix.GetSecRoleResponse;
import com.huawei.sharedrive.uam.util.SecurityConfigConstants;

import pw.cdmi.core.utils.IpUtils;

@Component
public class SecMatrixManagerImpl implements SecMatrixManager
{
    
    @Autowired
    private AccessSpaceConfigService accessSpaceConfigService;
    
    @Autowired
    private AccountSecConfigService accountSecConfigService;
    
    @Autowired
    private NetRegionService netRegionService;
    
    @Autowired
    private UserAccountService userAccountService;
    
    @Autowired
    private SafeLevelService safeLevelService;
    
    @Autowired
    private AccessConfigService accessConfigService;
    
    @Autowired
    private FileCopySecurityService fileCopySecurityService;
    
    @SuppressWarnings("PMD.ExcessiveParameterList")
    @Override
    public boolean judgeFileMatrix(long accountId, Integer secRole, Integer spaceSecRole, String ip,
        Byte fileSecId, String operation, Integer deviceType)
    {
        if (!SecurityConfigConstants.OPERATION_DOWNLOAD.equals(operation)
            && !SecurityConfigConstants.OPERATION_PREVIEW.equals(operation))
        {
            return true;
        }
        
        boolean isAllowed = false;
        AccountSecConfig config = accountSecConfigService.get((int) accountId);
        if (null == config || config.getEnableFileSec() == AccountSecConfig.DISABLE)
        {
            return true;
        }
        Long netRegionId = getNetWorkId(ip, accountId);
        // 这里只取当前用户
        //List<AccessConfig> list = accessConfigService.getAccessConfigList(accountId);
        AccessConfig ac = new AccessConfig();
        ac.setAccountId(accountId);
        ac.setSafeRoleId(secRole.longValue());
        List<AccessConfig> list = accessConfigService.getFilterdList(ac);
        
        for (AccessConfig acc : list)
        {
            if (match(acc, secRole, spaceSecRole, netRegionId, deviceType))
            {
                if (checkOperationIsAllowed(fileSecId, operation, list, acc))
                {
                    isAllowed = true;
                    break;
                }
            }
        }
        
        return isAllowed;
        
    }
    
    @Override
    public boolean judgeFileCopyMatrix(long accountId, Integer sourceRole, Integer targetRole,
        String operation)
    {
        if (!StringUtils.equals(SecurityConfigConstants.OPERATION_COPY, operation))
        {
            return true;
        }
        boolean isAllowed = false;
        AccountSecConfig config = accountSecConfigService.get((int) accountId);
        if (null == config || config.getEnableFileCopySec() == AccountSecConfig.DISABLE)
        {
            return true;
        }
        // 这里只取当前用户 
        // List<FileCopySecurity> fileCopySecurityList = fileCopySecurityService.getByAccountId(accountId);
        FileCopySecurity fcsquery = new FileCopySecurity();
        fcsquery.setSrcSafeRoleId(sourceRole.longValue());
        fcsquery.setAccountId(accountId);
        List<FileCopySecurity> fileCopySecurityList =  fileCopySecurityService.query(null,null,fcsquery);
        
        for (FileCopySecurity fcs : fileCopySecurityList)
        {
            if (matchSecRole(fcs.getSrcSafeRoleId(), sourceRole)
                && matchSecRole(fcs.getTargetSafeRoleId(), targetRole))
            {
                isAllowed = true;
                break;
            }
        }
        return isAllowed;
    }
    
    private boolean checkOperationIsAllowed(Byte fileSecId, String operation, List<AccessConfig> list,
        AccessConfig acc)
    {
        if (list.isEmpty())
        {
            return false;
        }
        if (operation.equals(SecurityConfigConstants.OPERATION_DOWNLOAD))
        {
            return isDownloadAllowed(fileSecId, acc);
        }
        if (operation.equals(SecurityConfigConstants.OPERATION_PREVIEW))
        {
            return isPreviewDownload(fileSecId, acc);
        }
        return false;
    }
    
    private boolean isPreviewDownload(Byte fileSecId, AccessConfig acc)
    {
        boolean isAllowed = false;
        String previewResourceTypeIds = acc.getPreviewResourceTypeIds();
        if (StringUtils.isBlank(previewResourceTypeIds))
        {
            return false;
        }
        String[] str = previewResourceTypeIds.split(",");
        for (String s : str)
        {
            if (Long.parseLong(s) == SecurityConfigConstants.TYPE_OF_SELECT_ALL)
            {
                isAllowed = true;
                break;
            }
            if (Long.parseLong(s) == fileSecId.longValue())
            {
                isAllowed = true;
                break;
            }
        }
        
        return isAllowed;
    }
    
    private boolean isDownloadAllowed(Byte fileSecId, AccessConfig acc)
    {
        boolean isAllowed = false;
        String downLoadResrouceTypeIds = acc.getDownLoadResrouceTypeIds();
        
        if (StringUtils.isBlank(downLoadResrouceTypeIds))
        {
            return false;
        }
        
        String[] str = downLoadResrouceTypeIds.split(",");
        for (String s : str)
        {
            if (Long.parseLong(s) == SecurityConfigConstants.TYPE_OF_SELECT_ALL)
            {
                isAllowed = true;
                break;
            }
            if (Long.parseLong(s) == fileSecId.longValue())
            {
                isAllowed = true;
                break;
            }
        }
        return isAllowed;
        
    }
    
    private boolean match(AccessConfig accessConfig, Integer secRole, Integer spaceSecRole, Long netRegionId,
        Integer deviceType)
    {
        if (!matchRegion(accessConfig.getNetRegionId(), netRegionId))
        {
            return false;
        }
        if (!matchSecRole(accessConfig.getSafeRoleId(), secRole))
        {
            return false;
        }
        
        if (!matchSecRole(accessConfig.getSpaceRoleId(), spaceSecRole))
        {
            return false;
        }
        
        if (!matchDeviceType(accessConfig.getClientType(), deviceType))
        {
            return false;
        }
        return true;
        
    }
    
    private boolean match(ResourceStrategy resourceStrategy, Integer secRole, Long netRegionId)
    {
        if (!matchRegion(resourceStrategy.getNetRegionId(), netRegionId))
        {
            return false;
        }
        if (!matchSecRole(resourceStrategy.getSecurityRoleId(), secRole))
        {
            return false;
        }
        return true;
        
    }
    
    private boolean matchDeviceType(Long clientType, Integer deviceType)
    {
        if (null == clientType)
        {
            return false;
        }
        if (clientType.byteValue() == AccessSpaceConfig.ALL)
        {
            return true;
        }
        if (null == deviceType)
        {
            return false;
        }
        if (clientType.intValue() == deviceType.intValue())
        {
            return true;
        }
        return false;
    }
    
    private boolean matchSecRole(Long targetSafeRoleId, Integer spaceSecRole)
    {
        if (null == targetSafeRoleId)
        {
            return false;
        }
        if (targetSafeRoleId.byteValue() == AccessSpaceConfig.ALL)
        {
            return true;
        }
        if (null == spaceSecRole)
        {
            return false;
        }
        if (targetSafeRoleId.intValue() == spaceSecRole.intValue())
        {
            return true;
        }
        return false;
    }
    
    private boolean matchRegion(Long netRegionId, Long accessRegionId)
    {
        if (null == netRegionId)
        {
            return false;
        }
        if (netRegionId.byteValue() == AccessSpaceConfig.ALL)
        {
            return true;
        }
        if (null == accessRegionId)
        {
            return false;
        }
        if (netRegionId.intValue() == accessRegionId.intValue())
        {
            return true;
        }
        return false;
    }
    
    @SuppressWarnings("PMD.ExcessiveParameterList")
    @Override
    public boolean judgeSpaceMatrix(long accountId, Integer secRole, String ip, Integer spaceSecRole,
        String operation, Integer deviceType)
    {
        
        if (SecurityConfigConstants.OPERATION_COPY.equals(operation))
        {
            return true;
        }
        AccountSecConfig config = accountSecConfigService.get((int) accountId);
        if (null == config || config.getEnableSpaceSec() == AccountSecConfig.DISABLE)
        {
            return true;
        }
        Long netRegionId = getNetWorkId(ip, accountId);
        SecOperation secOperation = SecOperation.getSecOperation(operation);
        if (null != secOperation)
        {
            return accessSpaceConfigService.judgeSpaceMatrix(accountId,
                secRole,
                netRegionId,
                spaceSecRole,
                SecOperation.getSecOperation(operation),
                deviceType);
        }
        return true;
    }
    
    private Long getNetWorkId(String ipAdress, long accountId)
    {
        long ipLong = IpUtils.toLong(ipAdress);
        NetRegionIp region = this.netRegionService.getByIp(accountId, ipLong);
        if (null == region)
        {
            return null;
        }
        return region.getNetRegionId();
    }
    
    @Override
    public GetSecRoleResponse getSecRole(int accountId, long cloudUserId)
    {
        GetSecRoleResponse getSecRoleResponse = new GetSecRoleResponse();
        UserAccount userAccount = new UserAccount();
        userAccount.setAccountId(accountId);
        userAccount.setCloudUserId(cloudUserId);
        
        UserAccount daoUserAccount = userAccountService.getBycloudUserAccountId(userAccount);
        if (null != daoUserAccount)
        {
            getSecRoleResponse.setSecurityRoleId(daoUserAccount.getRoleId());
        }
        else
        {
            throw new NoSuchUserException();
        }
        return getSecRoleResponse;
    }
    
    @Override
    public FileSecResponse getFileSecId(long accountId, Integer secRole, String realIp, int deviceType)
    {
        boolean isMatch = false;
        Long netRegionId = getNetWorkId(realIp, accountId);
        
        ResourceStrategy resourceStrategy = new ResourceStrategy();
        resourceStrategy.setAccountId(accountId);
        
        List<ResourceStrategy> listResourceStrategy = safeLevelService.getFilterdList(resourceStrategy);
        
        FileSecResponse returnResourceStrategy = new FileSecResponse();
        // find the match rule
        List<ResourceStrategy> listTemp = new ArrayList<ResourceStrategy>(10);
        for (ResourceStrategy acc : listResourceStrategy)
        {
            if (match(acc, secRole, netRegionId))
            {
                isMatch = true;
                listTemp.add(acc);
            }
        }
        if (!isMatch)
        {
            returnResourceStrategy.setSecurityId((byte) 0);
            return returnResourceStrategy;
        }
        // sort the match rule,must be follow the rules,poriority:
        // assigned security+assigned netregion>any security+assigned
        // netrgion>assigned security +any netregion
        Map<String, ResourceStrategy> map = handleMap(listTemp);
        // get the poritiy location and return the matched security value
        return getSecurityIdFromMap(returnResourceStrategy, map);
    }
    
    private Map<String, ResourceStrategy> handleMap(List<ResourceStrategy> listTemp)
    {
        Map<String, ResourceStrategy> map = new HashMap<String, ResourceStrategy>(10);
        long sortSecurityRoleId;
        long sortNetRegionId;
        for (ResourceStrategy acc : listTemp)
        {
            sortSecurityRoleId = acc.getSecurityRoleId();
            sortNetRegionId = acc.getNetRegionId();
            if (sortSecurityRoleId != SecurityConfigConstants.OBJECT_ID
                && sortNetRegionId != SecurityConfigConstants.OBJECT_ID)
            {
                map.put(SecurityConfigConstants.PRIORITY_FIRST, acc);
            }
            if (sortSecurityRoleId == SecurityConfigConstants.OBJECT_ID
                && sortNetRegionId != SecurityConfigConstants.OBJECT_ID)
            {
                map.put(SecurityConfigConstants.PRIORITY_SECOND, acc);
            }
            if (sortSecurityRoleId != SecurityConfigConstants.OBJECT_ID
                && sortNetRegionId == SecurityConfigConstants.OBJECT_ID)
            {
                map.put(SecurityConfigConstants.PRIORITY_THIRD, acc);
            }
        }
        return map;
    }
    
    private FileSecResponse getSecurityIdFromMap(FileSecResponse returnResourceStrategy,
        Map<String, ResourceStrategy> map)
    {
        if (map.containsKey(SecurityConfigConstants.PRIORITY_FIRST))
        {
            byte returnLevelId = map.get(SecurityConfigConstants.PRIORITY_FIRST)
                .getResourceSecurityLevelId()
                .byteValue();
            if (returnLevelId == SecurityConfigConstants.OBJECT_ID)
            {
                returnLevelId = 0;
            }
            returnResourceStrategy.setSecurityId(returnLevelId);
            return returnResourceStrategy;
        }
        else if (map.containsKey(SecurityConfigConstants.PRIORITY_SECOND))
        {
            byte returnLevelId = map.get(SecurityConfigConstants.PRIORITY_SECOND)
                .getResourceSecurityLevelId()
                .byteValue();
            if (returnLevelId == SecurityConfigConstants.OBJECT_ID)
            {
                returnLevelId = 0;
            }
            returnResourceStrategy.setSecurityId(returnLevelId);
            return returnResourceStrategy;
        }
        else
        {
            byte returnLevelId = map.get(SecurityConfigConstants.PRIORITY_THIRD)
                .getResourceSecurityLevelId()
                .byteValue();
            if (returnLevelId == SecurityConfigConstants.OBJECT_ID)
            {
                returnLevelId = 0;
            }
            returnResourceStrategy.setSecurityId(returnLevelId);
            return returnResourceStrategy;
        }
    }
    
}
