package com.huawei.sharedrive.uam.user.service.impl;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.huawei.sharedrive.uam.accountuser.domain.UpdateUserAccountList;
import com.huawei.sharedrive.uam.exception.InvalidParamterException;
import com.huawei.sharedrive.uam.exception.InvalidVersionsException;
import com.huawei.sharedrive.uam.exception.LdapUserForbiddenException;
import com.huawei.sharedrive.uam.exception.NoSuchRegionException;
import com.huawei.sharedrive.uam.exception.ShaEncryptException;
import com.huawei.sharedrive.uam.openapi.domain.BasicUserUpdateRequest;
import com.huawei.sharedrive.uam.openapi.domain.RestUserUpdateRequest;
import com.huawei.sharedrive.uam.system.service.AppBasicConfigService;
import com.huawei.sharedrive.uam.user.domain.Tag;
import com.huawei.sharedrive.uam.user.domain.User;
import com.huawei.sharedrive.uam.user.service.TagService;
import com.huawei.sharedrive.uam.user.service.UserParameterCheck;
import com.huawei.sharedrive.uam.user.service.UserService;
import com.huawei.sharedrive.uam.util.PasswordValidateUtil;
import com.huawei.sharedrive.uam.util.PatternRegUtil;

import pw.cdmi.box.http.request.RestRegionInfo;
import pw.cdmi.common.domain.AppBasicConfig;
import pw.cdmi.core.encrypt.HashPassword;
import pw.cdmi.core.utils.HashPasswordUtil;

@SuppressWarnings("PMD.PreserveStackTrace")
@Component
public class UserParameterCheckImpl implements UserParameterCheck
{
    private static Logger logger = LoggerFactory.getLogger(UserParameterCheckImpl.class);
    
    @Autowired
    private AppBasicConfigService appBasicConfigService;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private TagService tagService;
    
    @Override
    public void preCheckImportUserParameter(User user)
    {
        String loginName = user.getLoginName();
        String name = user.getName();
        
        if (StringUtils.isBlank(loginName) || loginName.length() > 127)
        {
            throw new InvalidParamterException("invalid loginName");
        }
        if (StringUtils.isBlank(name) || name.length() > 127)
        {
            throw new InvalidParamterException("invalid name:" + name);
        }
        
        if (StringUtils.isNotBlank(user.getEmail()) && user.getEmail().length() > 255)
        {
            throw new InvalidParamterException("invalid email email");
        }
        
        if (StringUtils.isNotBlank(user.getDepartment()) && user.getDepartment().length() > 255)
        {
            throw new InvalidParamterException("invalid description:" + user.getDepartment());
        }
        
        try
        {
            if (StringUtils.isNotBlank(user.getEmail()))
            {
                PatternRegUtil.checkMailLegal(user.getEmail());
            }
        }
        catch (Exception e)
        {
            logger.error("invalid email email:" + user.getEmail());
            throw new InvalidParamterException("invalid email email.");
        }
        
        if (User.PARAMETER_UNDEFINED != user.getRegionId())
        {
            checkRegionId(user.getRegionId(), user.getAppId());
        }
        
        if (User.PARAMETER_UNDEFINED != user.getMaxVersions())
        {
            checkMaxVersions(user.getMaxVersions());
        }
        if (User.PARAMETER_UNDEFINED != user.getSpaceQuota())
        {
            checkSpaceQuota(user.getSpaceQuota());
        }
        
    }
    
    @Override
    public void checkUserParameter(User user)
    {
        RestUserUpdateRequest currentUser = new RestUserUpdateRequest();
        currentUser.setLoginName(user.getLoginName());
        currentUser.setName(user.getName());
        currentUser.setEmail(user.getEmail());
        currentUser.setDescription(user.getDepartment());
        currentUser.setRegionId(user.getRegionId());
        currentUser.setSpaceQuota(user.getSpaceQuota());
        currentUser.setMaxVersions(user.getMaxVersions());
        currentUser.setTeamSpaceFlag(user.getTeamSpaceFlag());
        
        checkCreateUserParament(currentUser, user.getAppId(), false);
    }
    
    @Override
    public void checkUpdateUserListParament(UpdateUserAccountList updateUserList, String appId)
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
        checkTagId(updateUserList);
        checkTeamSpaceFlag(updateUserList);
        
    }
    
    private void checkTagId(UpdateUserAccountList updateUserList)
    {
        if (null != updateUserList.getIsTagId() && updateUserList.getIsTagId().booleanValue()
            && StringUtils.isNotBlank(updateUserList.getTagId()))
        {
            Tag tag = tagService.getTagByid(updateUserList.getTagId());
            if (null == tag)
            {
                throw new InvalidParamterException("tag is null");
            }
        }
    }
    
    private void checkTeamSpaceFlag(UpdateUserAccountList updateUserList)
    {
        if (null != updateUserList.getIsTeamSpaceFlag() && updateUserList.getIsTeamSpaceFlag()
            && null != updateUserList.getTeamSpaceFlag())
        {
            if (updateUserList.getTeamSpaceFlag() != User.TEAMSPACE_FLAG_SET
                && updateUserList.getTeamSpaceFlag() != User.TEAMSPACE_FLAG_UNSET)
            {
                throw new InvalidParamterException("invalid teamSpaceFlag teamSpaceFlag:"
                    + updateUserList.getTeamSpaceFlag());
            }
        }
    }
    
    @Override
    public void checkUpdateUserParament(BasicUserUpdateRequest user, String appId)
    {
        String name = user.getName();
        checkName(name);
        checkEmail(user.getEmail());
        checkDescription(user.getDescription());
        
        try
        {
            if (StringUtils.isNotBlank(user.getEmail()))
            {
                PatternRegUtil.checkMailLegal(user.getEmail());
            }
        }
        catch (Exception e)
        {
            logger.error("invalid email email:" + user.getEmail());
            throw new InvalidParamterException();
        }
        if (user.getMaxVersions() != null)
        {
            if (user.getMaxVersions() < 0 && user.getMaxVersions() != User.VERSION_NUM_UNLIMITED)
            {
                throw new InvalidVersionsException("maxVersions:" + user.getMaxVersions());
            }
        }
        checkRegionId(user.getRegionId(), appId);
    }
    
    private void checkDescription(String description)
    {
        if (StringUtils.isNotBlank(description) && description.length() > 255)
        {
            throw new InvalidParamterException("invalid description:" + description);
        }
    }
    
    private void checkEmail(String email)
    {
        if (StringUtils.isNotBlank(email) && email.length() > 255)
        {
            throw new InvalidParamterException("invalid email email");
        }
    }
    
    private void checkName(String name)
    {
        if (StringUtils.isNotBlank(name) && name.length() > 127)
        {
            throw new InvalidParamterException("invalid name name");
        }
    }
    
    @Override
    public void checkADUserParament(RestUserUpdateRequest user, String appId)
    {
        String loginName = user.getLoginName();
        String name = user.getName();
        if (StringUtils.isNotBlank(loginName) || StringUtils.isNotBlank(name))
        {
            throw new LdapUserForbiddenException();
        }
    }
    
    @Override
    public void checkBandWidthParament(Long uploadBandWidth, Long downloadBandWidth)
    {
        if (null != uploadBandWidth)
        {
            if (uploadBandWidth != User.PARAMETER_UNDEFINED && uploadBandWidth != User.SPACE_QUOTA_UNLIMITED
                && (uploadBandWidth <= 0 || uploadBandWidth > User.SPACE_QUOTA_MAX_G))
            {
                throw new InvalidParamterException("invalid uploadBandWidth");
            }
        }
        if (null != downloadBandWidth)
        {
            if (downloadBandWidth != User.PARAMETER_UNDEFINED
                && downloadBandWidth != User.SPACE_QUOTA_UNLIMITED
                && (downloadBandWidth <= 0 || downloadBandWidth > User.SPACE_QUOTA_MAX_G))
            {
                throw new InvalidParamterException("invalid downloadBandWidth");
            }
        }
    }
    
    @Override
    public void checkCreateUserParament(RestUserUpdateRequest user, String appId, boolean isRest)
    {
        String loginName = user.getLoginName();
        String name = user.getName();
        String password = user.getPassword();
        
        if (StringUtils.isBlank(loginName) || loginName.length() > 127)
        {
            throw new InvalidParamterException("invalid loginName");
        }
        if (StringUtils.isBlank(name) || name.length() > 127)
        {
            throw new InvalidParamterException("invalid name:" + name);
        }
        if (isRest)
        {
            if (!PasswordValidateUtil.isValidPassword(password, 127))
            {
                throw new InvalidParamterException("invalid password");
            }
            if (password.equals(user.getLoginName()))
            {
                throw new InvalidParamterException("loginName and password cannot be the same");
            }
        }
        if (StringUtils.isBlank(user.getEmail()) || user.getEmail().length() > 255)
        {
            throw new InvalidParamterException("invalid email email");
        }
        checkDescription(user.getDescription());
        try
        {
            PatternRegUtil.checkMailLegal(user.getEmail());
        }
        catch (Exception e)
        {
            logger.error("invalid email email:" + user.getEmail());
            throw new InvalidParamterException();
        }
        checkMaxVersions(user.getMaxVersions());
        checkSpaceQuota(user.getSpaceQuota());
        checkRegionId(user.getRegionId(), appId);
        user.checkAndSetStatus();
    }
    
    @Override
    public User fillCreateUser(RestUserUpdateRequest ruser, String appId)
    {
        User user = new User();
        AppBasicConfig appBasicConfig = appBasicConfigService.getAppBasicConfig(appId);
        boolean isEnableTeamSpace = appBasicConfig.isEnableTeamSpace();
        int teamSpaceMaxNumbers = appBasicConfig.getMaxTeamSpaces();
        Long spaceQuota = appBasicConfig.getUserSpaceQuota();
        if (spaceQuota == null || spaceQuota <= 0)
        {
            spaceQuota = User.SPACE_QUOTA_UNLIMITED;
        }
        else
        {
            spaceQuota = spaceQuota * User.SPACE_UNIT * User.SPACE_UNIT * User.SPACE_UNIT;
        }
        Integer maxFileVersions = appBasicConfig.getMaxFileVersions();
        if (maxFileVersions == null || maxFileVersions <= 0)
        {
            maxFileVersions = User.VERSION_NUM_UNLIMITED;
        }
        Date date = new Date();
        user.setAppId(appId);
        user.setCreatedAt(date);
        user.setEmail(ruser.getEmail());
        user.setLoginName(ruser.getLoginName());
        user.setName(ruser.getName());
        user.setObjectSid(ruser.getLoginName());
        user.setLastLoginAt(date);
        user.setMaxVersions(ruser.getMaxVersions() == null ? maxFileVersions : ruser.getMaxVersions());
        user.setModifiedAt(date);
        user.setPrincipalType(-1);
        user.setResourceType(-1);
        user.setRegionId(ruser.getRegionId() == null ? User.REGION_ID_UNDEFINED : ruser.getRegionId());
        user.setSpaceQuota(ruser.getSpaceQuota() == null ? spaceQuota : ruser.getSpaceQuota());
        if (StringUtils.isNotBlank(ruser.getDescription()))
        {
            user.setDepartment(ruser.getDescription());
        }
        user.setStatus(ruser.getStatus());
        if (ruser.getTeamSpaceFlag() == null)
        {
            if (isEnableTeamSpace)
            {
                user.setTeamSpaceFlag(User.TEAMSPACE_FLAG_SET);
            }
            else
            {
                user.setTeamSpaceFlag(User.TEAMSPACE_FLAG_UNSET);
            }
            user.setTeamSpaceMaxNum(teamSpaceMaxNumbers);
        }
        else
        {
            user.setTeamSpaceFlag(ruser.getTeamSpaceFlag());
            if (null == ruser.getTeamSpaceMaxNum())
            {
                user.setTeamSpaceMaxNum(teamSpaceMaxNumbers);
            }
            else
            {
                user.setTeamSpaceMaxNum(ruser.getTeamSpaceMaxNum());
            }
        }
        return user;
    }
    
    public static void transUser(RestUserUpdateRequest rUser, User user)
    {
        if (rUser.getEmail() != null)
        {
            user.setEmail(rUser.getEmail());
        }
        if (rUser.getLoginName() != null)
        {
            user.setLoginName(rUser.getLoginName());
        }
        if (rUser.getName() != null)
        {
            user.setName(rUser.getName());
        }
        if (rUser.getSpaceQuota() != null)
        {
            user.setSpaceQuota(rUser.getSpaceQuota());
        }
        if (rUser.getStatus() != null)
        {
            user.setStatus(String.valueOf(rUser.getStatus()));
        }
        if (rUser.getRegionId() != null)
        {
            user.setRegionId(rUser.getRegionId());
        }
        if (rUser.getDescription() != null)
        {
            user.setDepartment(rUser.getDescription());
        }
        if (rUser.getTeamSpaceFlag() != null)
        {
            user.setTeamSpaceFlag(rUser.getTeamSpaceFlag());
        }
        if (rUser.getMaxVersions() != null)
        {
            user.setMaxVersions(rUser.getMaxVersions());
        }
        if (rUser.getTeamSpaceMaxNum() != null)
        {
            user.setTeamSpaceMaxNum(rUser.getTeamSpaceMaxNum());
        }
    }
    
    public static void transADUser(RestUserUpdateRequest rUser, User user)
    {
        if (rUser.getSpaceQuota() != null)
        {
            user.setSpaceQuota(rUser.getSpaceQuota());
        }
        if (rUser.getRegionId() != null)
        {
            user.setRegionId(rUser.getRegionId());
        }
        if (rUser.getTeamSpaceFlag() != null)
        {
            user.setTeamSpaceFlag(rUser.getTeamSpaceFlag());
        }
        if (rUser.getTeamSpaceMaxNum() != null)
        {
            user.setTeamSpaceMaxNum(rUser.getTeamSpaceMaxNum());
        }
        if (rUser.getMaxVersions() != null)
        {
            user.setMaxVersions(rUser.getMaxVersions());
        }
    }
    
    @SuppressWarnings("PMD.PreserveStackTrace")
    public static void checkPassword(User user, String newPassword, String oldPassword)
    {
        if (!PasswordValidateUtil.isValidPassword(newPassword, 127))
        {
            throw new InvalidParamterException("newPassword is not correct");
        }
        if (!PasswordValidateUtil.isValidPassword(oldPassword, 127))
        {
            throw new InvalidParamterException("oldPassword is not correct");
        }
        
        HashPassword oldHashPassword = new HashPassword();
        oldHashPassword.setHashPassword(user.getPassword());
        oldHashPassword.setSalt(user.getSalt());
        oldHashPassword.setIterations(user.getIterations());
        if (!HashPasswordUtil.validatePassword(oldPassword, oldHashPassword))
        {
            throw new InvalidParamterException("oldpassword is not correct");
        }
        
        try
        {
            HashPassword hashPassword = HashPasswordUtil.generateHashPassword(newPassword);
            user.setPassword(hashPassword.getHashPassword());
            user.setIterations(hashPassword.getIterations());
            user.setSalt(hashPassword.getSalt());
        }
        catch (NoSuchAlgorithmException e)
        {
            logger.error("digest exception");
            throw new ShaEncryptException();
        }
        catch (InvalidKeySpecException e)
        {
            logger.error("digest exception");
            throw new ShaEncryptException();
        }
    }
    
    private void checkMaxVersions(Integer userMaxVersions)
    {
        if (userMaxVersions != null)
        {
            if (userMaxVersions <= 0 && userMaxVersions != User.VERSION_NUM_UNLIMITED)
            {
                throw new InvalidVersionsException("maxVersions:" + userMaxVersions);
            }
        }
    }
    
    private void checkRegionId(Integer regionId, String appId)
    {
        if (regionId != null)
        {
            if (regionId <= 0 && regionId != User.REGION_ID_UNDEFINED)
            {
                throw new NoSuchRegionException("invalid regionId:" + regionId);
            }
            
            List<RestRegionInfo> regionList = userService.getRegionInfo(appId);
            if (null == regionList)
            {
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
            if (spaceQuota <= 0 && spaceQuota != User.SPACE_QUOTA_UNLIMITED)
            {
                throw new InvalidParamterException("invalid spaceQuota:" + spaceQuota);
            }
        }
    }
}
