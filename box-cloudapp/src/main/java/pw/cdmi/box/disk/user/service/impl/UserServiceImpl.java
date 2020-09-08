package pw.cdmi.box.disk.user.service.impl;

import java.util.Date;

import org.apache.shiro.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import pw.cdmi.box.disk.enterprise.dao.EnterpriseAccountDao;
import pw.cdmi.box.disk.event.domain.Event;
import pw.cdmi.box.disk.event.domain.EventType;
import pw.cdmi.box.disk.event.service.EventService;
import pw.cdmi.box.disk.oauth2.domain.UserToken;
import pw.cdmi.box.disk.user.dao.EnterpriseUserDao;
import pw.cdmi.box.disk.user.dao.UserAccountDao;
import pw.cdmi.box.disk.user.dao.WxEnterpriseUserDao;
import pw.cdmi.box.disk.user.domain.*;
import pw.cdmi.box.disk.user.service.UserService;
import pw.cdmi.common.domain.enterprise.EnterpriseAccount;
import pw.cdmi.core.exception.BusinessErrorCode;
import pw.cdmi.core.exception.BusinessException;
import pw.cdmi.core.exception.InvalidParamException;

@Component
public class UserServiceImpl implements UserService
{
    
    private static Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
    
    @Autowired
    private EventService eventService;
    
    @Autowired
    private EnterpriseUserDao enterpriseUserDao;
    
    @Autowired
    private UserAccountDao userAccountDao;
    
    @Autowired
    private WxEnterpriseUserDao wxEnterpriseUserDao;

    @Autowired
    private EnterpriseAccountDao enterpriseAccountDao;
    
    public static String transDeviceStrType(int deviceType)
    {
        if (deviceType == Terminal.CLIENT_TYPE_ANDROID)
        {
            return Terminal.CLIENT_TYPE_ANDROID_STR;
        }
        if (deviceType == Terminal.CLIENT_TYPE_IOS)
        {
            return Terminal.CLIENT_TYPE_IOS_STR;
        }
        if (deviceType == Terminal.CLIENT_TYPE_PC)
        {
            return Terminal.CLIENT_TYPE_PC_STR;
        }
        if (deviceType == Terminal.CLIENT_TYPE_WEB)
        {
            return Terminal.CLIENT_TYPE_WEB_STR;
        }
        throw new InvalidParamException();
    }
    
    public static int transDeviceType(String deviceType)
    {
        if (deviceType.equals(Terminal.CLIENT_TYPE_ANDROID_STR))
        {
            return Terminal.CLIENT_TYPE_ANDROID;
        }
        if (deviceType.equals(Terminal.CLIENT_TYPE_IOS_STR))
        {
            return Terminal.CLIENT_TYPE_IOS;
        }
        if (deviceType.equals(Terminal.CLIENT_TYPE_PC_STR))
        {
            return Terminal.CLIENT_TYPE_PC;
        }
        if (deviceType.equals(Terminal.CLIENT_TYPE_WEB_STR))
        {
            return Terminal.CLIENT_TYPE_WEB;
        }
        throw new InvalidParamException();
    }
    
    @Override
    public void getUserTokenBydb(UserToken userToken, String loginName, long enterpriseId, long accountId)
    {
        EnterpriseUser enterpriseUser = enterpriseUserDao.getByLoginname(loginName, enterpriseId);
        UserAccount userAccount = userAccountDao.getByUserId(accountId, enterpriseUser.getId());
        userToken.setLoginName(loginName);
        userToken.setCloudUserId(userAccount.getCloudUserId());
        userToken.setEnterpriseId(enterpriseId);
        userToken.setAccountId(accountId);
        userToken.setName(enterpriseUser.getAlias());
        userToken.setId(enterpriseUser.getId());
        userToken.setMobile(enterpriseUser.getMobile());
    }
    
    @Override
    public void createEvent(UserToken userToken, EventType type, long createdBy)
    {
        try
        {
            Event event = userToken.getEvent();
            event.setType(type);
            event.setCreatedAt(new Date());
            event.setCreatedBy(createdBy);
            eventService.fireEvent(event);
        }
        catch (Exception e)
        {
            logger.error(e.toString(), e);
        }
    }
    
    @Override
    public User getUserByCloudUserId(long cloudUserId)
    {
        Long accountId = (Long) SecurityUtils.getSubject().getSession().getAttribute("accountId");
        if(accountId!=0){
            if (null == accountId)
            {
                logger.error("accountId is null");
                throw new BusinessException(BusinessErrorCode.INTERNAL_SERVER_ERROR, "accountId is null");
            }
            UserAccount userAccount = userAccountDao.getByCloudUserId(accountId, cloudUserId);
            if (null == userAccount)
            {
                logger.error("userAccount is null accountid:" + accountId + " cloudUserId:" + cloudUserId);
                throw new BusinessException(BusinessErrorCode.INTERNAL_SERVER_ERROR, "userAccount is null");
            }

            EnterpriseUser enterpriseUser = enterpriseUserDao.get(userAccount.getUserId(),
                    userAccount.getEnterpriseId());
            if (null == enterpriseUser)
            {
                logger.error("enterpriseUser is null userId:" + userAccount.getUserId() + " enterpriseId:"
                        + userAccount.getEnterpriseId());
                throw new BusinessException(BusinessErrorCode.INTERNAL_SERVER_ERROR, "enterpriseUser is null");
            }
            User user = EnterpriseUser.copyEnterpriseUser(enterpriseUser);
            user.setTeamSpaceFlag(userAccount.getTeamSpaceFlag());
            user.setTeamSpaceMaxNum(userAccount.getTeamSpaceMaxNum());
            user.setAccountId(accountId);
            user.setCloudUserId(cloudUserId);
            return user;
        }else{
            return  null;
        }

    }
    
    
    @Override
    public User getUserByCloudUserId(long accountId,long cloudUserId)
    {
        UserAccount userAccount = userAccountDao.getByCloudUserId(accountId, cloudUserId);
        if (null == userAccount)
        {
            logger.error("userAccount is null accountid:" + accountId + " cloudUserId:" + cloudUserId);
            throw new BusinessException(BusinessErrorCode.INTERNAL_SERVER_ERROR, "userAccount is null");
        }
        
        EnterpriseUser enterpriseUser = enterpriseUserDao.get(userAccount.getUserId(),
            userAccount.getEnterpriseId());
        if (null == enterpriseUser)
        {
            logger.error("enterpriseUser is null userId:" + userAccount.getUserId() + " enterpriseId:"
                + userAccount.getEnterpriseId());
            throw new BusinessException(BusinessErrorCode.INTERNAL_SERVER_ERROR, "enterpriseUser is null");
        }
        User user = EnterpriseUser.copyEnterpriseUser(enterpriseUser);
        user.setTeamSpaceFlag(userAccount.getTeamSpaceFlag());
        user.setTeamSpaceMaxNum(userAccount.getTeamSpaceMaxNum());
        user.setAccountId(accountId);
        user.setCloudUserId(cloudUserId);
        return user;
    }
    
    @Override
    public EnterpriseUser getEnterpriseUserByUserId(long accountId, long id)
    {
        UserAccount userAccount = userAccountDao.getByUserId(accountId, id);
        if (null == userAccount)
        {
            logger.error("userAccount is null accountid:" + accountId + " id:" + id);
            throw new BusinessException(BusinessErrorCode.INTERNAL_SERVER_ERROR, "userAccount is null");
        }
        
        EnterpriseUser enterpriseUser = enterpriseUserDao.get(userAccount.getUserId(),
            userAccount.getEnterpriseId());
        if (null == enterpriseUser)
        {
            logger.error("enterpriseUser is null userId:" + userAccount.getUserId() + " enterpriseId:"
                + userAccount.getEnterpriseId());
            throw new BusinessException(BusinessErrorCode.INTERNAL_SERVER_ERROR, "enterpriseUser is null");
        }
        return enterpriseUser;
    }
    
    @Override
    public EnterpriseAccount getEnterpriseAccountByCloudUserId(long cloudUserId)
    {
        Long accountId = (Long) SecurityUtils.getSubject().getSession().getAttribute("accountId");
        if (null == accountId)
        {
            logger.error("accountId is null");
            throw new BusinessException(BusinessErrorCode.INTERNAL_SERVER_ERROR, "accountId is null");
        }
        EnterpriseAccount userAccount = enterpriseAccountDao.getByAccountId(accountId);
        if (null == userAccount)
        {
            logger.error("userAccount is null accountid:" + accountId + " cloudUserId:" + cloudUserId);
            throw new BusinessException(BusinessErrorCode.INTERNAL_SERVER_ERROR, "userAccount is null");
        }
        
        return userAccount;
    }

    @Override
    public EnterpriseUser getEnterpriseUserByWeixinUserId(String corpId, String userId) {
        WxEnterpriseUser wxUser = wxEnterpriseUserDao.get(corpId, userId);
        if (wxUser == null)
        {
            logger.error("WxEnterpriseUser is null corpId:" + corpId + " userId:" + userId);
            throw new BusinessException(BusinessErrorCode.INTERNAL_SERVER_ERROR, "WxEnterpriseUser is null");
        }

        EnterpriseUser enterpriseUser = enterpriseUserDao.get(wxUser.getBoxEnterpriseUserId(), wxUser.getBoxEnterpriseId());
        if (null == enterpriseUser)
        {
            logger.error("enterpriseUser is null userId:" + wxUser.getUserId() + " enterpriseId:"
                    + wxUser.getEnterpriseId());
            throw new BusinessException(BusinessErrorCode.INTERNAL_SERVER_ERROR, "enterpriseUser is null");
        }
        return enterpriseUser;
    }
}
