package com.huawei.sharedrive.uam.accountuser.manager.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.huawei.sharedrive.uam.accountuser.domain.UpdateUserAccountList;
import com.huawei.sharedrive.uam.accountuser.domain.UserAccount;
import com.huawei.sharedrive.uam.accountuser.manager.UserAccountCheckManager;
import com.huawei.sharedrive.uam.exception.InvalidParamterException;
import com.huawei.sharedrive.uam.exception.InvalidVersionsException;
import com.huawei.sharedrive.uam.exception.NoSuchRegionException;
import com.huawei.sharedrive.uam.openapi.domain.RestUserUpdateRequest;
import com.huawei.sharedrive.uam.user.domain.User;
import com.huawei.sharedrive.uam.user.service.UserService;

import pw.cdmi.box.http.request.RestRegionInfo;

@Component
public class UserAccountCheckManagerImpl implements UserAccountCheckManager
{
    private final static Logger LOGGER = LoggerFactory.getLogger(UserAccountCheckManager.class);
    
    @Autowired
    private UserService userService;
    
    public void checkBandWidthParament(Long uploadBandWidth, Long downloadBandWidth)
    {
        if (null != uploadBandWidth)
        {
            if (uploadBandWidth != User.PARAMETER_UNDEFINED && uploadBandWidth != User.SPACE_QUOTA_UNLIMITED
                && (uploadBandWidth < 100 || uploadBandWidth > User.SPACE_QUOTA_MAX_G))
            {
                throw new InvalidParamterException("invalid uploadBandWidth");
            }
        }
        if (null != downloadBandWidth)
        {
            if (downloadBandWidth != User.PARAMETER_UNDEFINED
                && downloadBandWidth != User.SPACE_QUOTA_UNLIMITED
                && (downloadBandWidth < 100 || downloadBandWidth > User.SPACE_QUOTA_MAX_G))
            {
                throw new InvalidParamterException("invalid downloadBandWidth");
            }
        }
    }

    @Override
    public void checkUpdateUserListPara(UpdateUserAccountList updateUserList, String appId)
    {
        if (null != updateUserList.getIsMaxVersions() && updateUserList.getIsMaxVersions().booleanValue())
        {
            if (null != updateUserList.getMaxVersions())
            {
                checkMaxVersions(updateUserList.getMaxVersions());
            }
        }
        if (null != updateUserList.getIsSpaceQuota() && updateUserList.getIsSpaceQuota().booleanValue())
        {
            if (null != updateUserList.getSpaceQuota())
            {
                checkSpaceQuota(updateUserList.getSpaceQuota());
            }
            
        }
        if (null != updateUserList.getIsRegionId() && updateUserList.getIsRegionId().booleanValue())
        {
            if (null != updateUserList.getRegionId())
            {
                checkRegionId(updateUserList.getRegionId(), appId);
            }
        }
        checkTeamspaceFlag(updateUserList);
        
        if (null != updateUserList.getIsTeamSpaceMaxNum() && updateUserList.getIsTeamSpaceMaxNum().booleanValue())
        {
            if (null != updateUserList.getTeamSpaceMaxNum())
            {
                checkMaxVersions(updateUserList.getTeamSpaceMaxNum());
            }
        }
        checkBandWidthParament(updateUserList.getUploadBandWidth(), updateUserList.getDownloadBandWidth());
    }
    
    @Override
    public void checkUserAccountStatus(int status)
    {
        if (status == UserAccount.INT_STATUS_DISABLE)
        {
            return;
        }
        if (status == UserAccount.INT_STATUS_ENABLE)
        {
            return;
        }
        throw new InvalidParamterException("invalid status status:" + status);
    }
    
    @Override
    public boolean isUpdateAndSetUserAccount(UserAccount selUser, RestUserUpdateRequest rUser)
    {
        boolean isUpdate = false;
        
        isUpdate = isUpdateReginId(selUser, rUser, isUpdate);
        
        isUpdate = isUpdateSpaceQuota(selUser, rUser, isUpdate);
        
        isUpdate = isUpdateMaxVersions(selUser, rUser, isUpdate);
        
        isUpdate = isUpdateTeamSpaceFlag(selUser, rUser, isUpdate);
        
        isUpdate = isUpdateTeamSpaceMaxNum(selUser, rUser, isUpdate);
        
        isUpdate = isUpdateStatus(selUser, rUser, isUpdate);
        
        return isUpdate;
    }
    
    private void checkMaxVersions(Integer userMaxVersions)
    {
        if (userMaxVersions != null)
        {
            if (userMaxVersions <= 0 && userMaxVersions != User.VERSION_NUM_UNLIMITED)
            {
                LOGGER.error("invalid userMaxVersions:" + userMaxVersions);
                throw new InvalidVersionsException("invalid userMaxVersions:" + userMaxVersions);
            }
            if (userMaxVersions > 10000)
            {
                LOGGER.error("invalid userMaxVersions:" + userMaxVersions);
                throw new InvalidVersionsException("invalid userMaxVersions:" + userMaxVersions);
            }
        }
    }
    
    private void checkRegionId(Integer regionId, String appId)
    {
        if (regionId != null)
        {
            if (regionId <= 0 && regionId != User.REGION_ID_UNDEFINED)
            {
                LOGGER.error("invalid regionId:" + regionId);
                throw new NoSuchRegionException("invalid regionId:" + regionId);
            }
            
            List<RestRegionInfo> regionList = userService.getRegionInfo(appId);
            if (null == regionList)
            {
                LOGGER.error("regionList is null:" + regionId);
                throw new NoSuchRegionException("regionList is null");
            }
            boolean isLegaRegion = false;
            for (RestRegionInfo regionInfo : regionList)
            {
                if (regionId == regionInfo.getRegionId())
                {
                    isLegaRegion = true;
                    break;
                }
            }
            if (!isLegaRegion)
            {
                LOGGER.error("uncorrect regionId:" + regionId);
                throw new NoSuchRegionException("uncorrect regionId");
            }
        }
    }
    
    /**
     * 
     * @param spaceQuota
     * @param appBasicConfig
     */
    private void checkSpaceQuota(Long spaceQuota)
    {
        if (spaceQuota != null)
        {
            if (spaceQuota <= 0 && spaceQuota != User.VERSION_NUM_UNLIMITED)
            {
                LOGGER.error("invalid spaceQuota:" + spaceQuota);
                throw new InvalidVersionsException("invalid spaceQuota:" + spaceQuota);
            }
        }
    }
    
    private void checkTeamspaceFlag(UpdateUserAccountList updateUserList)
    {
        if (null == updateUserList.getIsTeamSpaceFlag())
        {
            return;
        }
        if (!updateUserList.getIsTeamSpaceFlag().booleanValue())
        {
            return;
        }
        if (null == updateUserList.getTeamSpaceFlag())
        {
            return;
        }
        if (updateUserList.getTeamSpaceFlag() != User.TEAMSPACE_FLAG_SET
            && updateUserList.getTeamSpaceFlag() != User.TEAMSPACE_FLAG_UNSET)
        {
            throw new InvalidParamterException("invalid teamSpaceFlag teamSpaceFlag:"
                + updateUserList.getTeamSpaceFlag());
        }
        if (updateUserList.getTeamSpaceFlag() == User.TEAMSPACE_FLAG_SET)
        {
            return;
        }
        if (updateUserList.getTeamSpaceFlag() == User.TEAMSPACE_FLAG_UNSET)
        {
            return;
        }
        throw new InvalidParamterException("invalid teamSpaceFlag teamSpaceFlag:"
            + updateUserList.getTeamSpaceFlag());
    }
    
    private boolean isUpdateMaxVersions(UserAccount selUser, RestUserUpdateRequest rUser, boolean isUpdate)
    {
        if (rUser.getMaxVersions() != null)
        {
            if (selUser.getMaxVersions().intValue() != rUser.getMaxVersions().intValue())
            {
                isUpdate = true;
                selUser.setMaxVersions(rUser.getMaxVersions());
            }
        }
        return isUpdate;
    }
    
    private boolean isUpdateReginId(UserAccount selUser, RestUserUpdateRequest rUser, boolean isUpdate)
    {
        if (selUser.getRegionId() > 0)
        {
            if (rUser.getRegionId() != null && rUser.getRegionId() > 0
                && selUser.getRegionId() != rUser.getRegionId())
            {
                isUpdate = true;
                selUser.setRegionId(rUser.getRegionId());
            }
        }
        else
        {
            if (rUser.getRegionId() != null && rUser.getRegionId() > 0)
            {
                isUpdate = true;
                selUser.setRegionId(rUser.getRegionId());
            }
        }
        return isUpdate;
    }
    
    private boolean isUpdateSpaceQuota(UserAccount selUser, RestUserUpdateRequest rUser, boolean isUpdate)
    {
        if (rUser.getSpaceQuota() == null)
        {
            return isUpdate;
        }
        if (selUser.getSpaceQuota().intValue() != rUser.getSpaceQuota().intValue())
        {
            isUpdate = true;
            selUser.setSpaceQuota(rUser.getSpaceQuota());
        }
        return isUpdate;
    }
    
    private boolean isUpdateStatus(UserAccount selUser, RestUserUpdateRequest rUser, boolean isUpdate)
    {
        if (selUser.getStatus() >= 0)
        {
            if (rUser.getStatus() != null)
            {
                if (selUser.getStatus() != rUser.getStatusInt())
                {
                    isUpdate = true;
                    selUser.setStatus(rUser.getStatusInt());
                }
            }
        }
        else
        {
            if (rUser.getStatus() != null)
            {
                isUpdate = true;
                selUser.setStatus(rUser.getStatusInt());
            }
        }
        return isUpdate;
    }
    
    private boolean isUpdateTeamSpaceFlag(UserAccount selUser, RestUserUpdateRequest rUser, boolean isUpdate)
    {
        if (selUser.getTeamSpaceFlag() > 0)
        {
            if (rUser.getTeamSpaceFlag() != null && rUser.getTeamSpaceFlag() > 0
                && selUser.getTeamSpaceFlag() != (int) rUser.getTeamSpaceFlag())
            {
                selUser.setTeamSpaceFlag((int) rUser.getTeamSpaceFlag());
                return true;
            }
        }
        else if (rUser.getTeamSpaceFlag() != null && rUser.getTeamSpaceFlag() > 0)
        {
            selUser.setTeamSpaceFlag((int) rUser.getTeamSpaceFlag());
            return true;
        }
        return isUpdate;
    }
    
    private boolean isUpdateTeamSpaceMaxNum(UserAccount selUser, RestUserUpdateRequest rUser, boolean isUpdate)
    {
        if (rUser.getTeamSpaceMaxNum() == null)
        {
            return isUpdate;
        }
        if (selUser.getTeamSpaceMaxNum().intValue() != rUser.getTeamSpaceMaxNum().intValue())
        {
            selUser.setTeamSpaceMaxNum(rUser.getTeamSpaceMaxNum());
            return true;
        }
        return isUpdate;
    }
}
