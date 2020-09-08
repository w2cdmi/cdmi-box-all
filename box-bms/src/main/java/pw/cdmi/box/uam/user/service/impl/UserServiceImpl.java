package pw.cdmi.box.uam.user.service.impl;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.shiro.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import pw.cdmi.box.domain.Page;
import pw.cdmi.box.domain.PageImpl;
import pw.cdmi.box.domain.PageRequest;
import pw.cdmi.box.uam.authapp.service.AuthAppService;
import pw.cdmi.box.uam.enterprise.dao.EnterpriseAccountDao;
import pw.cdmi.box.uam.exception.BusinessException;
import pw.cdmi.box.uam.httpclient.domain.RestAppStatisticsRequest;
import pw.cdmi.box.uam.httpclient.domain.RestAppStatisticsResponse;
import pw.cdmi.box.uam.httpclient.domain.RestRegionInfo;
import pw.cdmi.box.uam.httpclient.rest.RegionClient;
import pw.cdmi.box.uam.httpclient.rest.StatisticsHttpClient;
import pw.cdmi.box.uam.statistics.service.StatisticsAccesskeyService;
import pw.cdmi.box.uam.system.dao.SystemConfigDAO;
import pw.cdmi.box.uam.system.service.AppBasicConfigService;
import pw.cdmi.box.uam.user.dao.UserDAO;
import pw.cdmi.box.uam.user.domain.Admin;
import pw.cdmi.box.uam.user.domain.User;
import pw.cdmi.box.uam.user.domain.UserExtend;
import pw.cdmi.box.uam.user.domain.UserQos;
import pw.cdmi.box.uam.user.domain.UserTagExtend;
import pw.cdmi.box.uam.user.service.UserService;
import pw.cdmi.box.uam.user.service.UserTagService;
import pw.cdmi.box.uam.util.BusinessConstants;
import pw.cdmi.common.domain.AppBasicConfig;
import pw.cdmi.common.domain.SystemConfig;
import pw.cdmi.common.domain.enterprise.EnterpriseAccount;
import pw.cdmi.core.restrpc.RestClient;
import pw.cdmi.core.utils.DigestUtil;
import pw.cdmi.core.utils.HashPasswordUtil;
import pw.cdmi.uam.domain.AuthApp;

@Component
public class UserServiceImpl implements UserService
{
    
    public final static String DOWNLOAD_BANDWIDTH = "downloadBandWidth";
    
    public final static String UPLOAD_BANDWIDTH = "uploadBandWidth";
    
    public final static String USER_STATISTICS_PATH = "/UserStatistics";
    
    private static final Logger LOGGER = LoggerFactory.getLogger(UserServiceImpl.class);
    
    /**
     * 
     * @param destUserresourceType
     * @param toUser
     */
    public static void transUser(User destUser, User toUser)
    {
        toUser.setCreatedAt(destUser.getCreatedAt());
        toUser.setDepartment(destUser.getDepartment());
        toUser.setDomain(destUser.getDomain());
        toUser.setEmail(destUser.getEmail());
        toUser.setId(destUser.getId());
        toUser.setLoginName(destUser.getLoginName());
        toUser.setModifiedAt(destUser.getModifiedAt());
        toUser.setName(destUser.getName());
        toUser.setObjectSid(destUser.getObjectSid());
        toUser.setPassword(destUser.getPassword());
        toUser.setRegionId(destUser.getRegionId());
        toUser.setStatus(destUser.getStatus());
        toUser.setCloudUserId(destUser.getCloudUserId());
        toUser.setAppId(destUser.getAppId());
        toUser.setDepartmentCode(destUser.getDepartmentCode());
        toUser.setLastLoginAt(destUser.getLastLoginAt());
        toUser.setPrincipalType(destUser.getPrincipalType());
        toUser.setResourceType(destUser.getResourceType());
        toUser.setRegionId(destUser.getRegionId());
        toUser.setMaxVersions(destUser.getMaxVersions());
        toUser.setTeamSpaceFlag(destUser.getTeamSpaceFlag());
        toUser.setTeamSpaceMaxNum(destUser.getTeamSpaceMaxNum());
        toUser.setSpaceQuota(destUser.getSpaceQuota());
        toUser.setDownloadBandWidth(destUser.getDownloadBandWidth());
        toUser.setUploadBandWidth(destUser.getUploadBandWidth());
    }
    
    @Autowired
    private AppBasicConfigService appBasicConfigService;
    
    @Autowired
    private AuthAppService authAppService;
    
    @Autowired
    private EnterpriseAccountDao enterpriseAccountDao;
    
    @Autowired
    private StatisticsAccesskeyService statisticsAccesskeyService;
    
    @Autowired
    private SystemConfigDAO systemConfigDAO;
    
    @Resource
    private RestClient ufmClientService;
    
    @Autowired
    private UserDAO userDAO;
    
    @Autowired
    private UserTagService userTagService;
    
    @Override
    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    public void addUserList(List<User> userlist)
    {
        userDAO.addUserList(userlist);
    }
    
    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    @Override
    public void delete(long id)
    {
        userDAO.delete(id);
    }
    
    // @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    // @Override
    // public void deleteUserByid(User user, AuthApp authApp)
    // {
    // new UserHttpClient(ufmClientService).deleteUserInfo(user.getCloudUserId(),
    // authApp);
    // delete(user.getId());
    // userTagService.deleteByUserId(user.getId());
    // }
    
    @Override
    public Map<String, Long> fillBandWidth(Long donwloadBandWidthPare, Long uploadBandWidthPare, String appId)
    {
        Map<String, Long> map = new HashMap<String, Long>(BusinessConstants.INITIAL_CAPACITIES);
        long uploadBandWidth = -1;
        long downloadBandWidth = -1;
        AppBasicConfig appBasicConfig = appBasicConfigService.getAppBasicConfig(appId);
        if (null == uploadBandWidthPare || User.PARAMETER_UNDEFINED == uploadBandWidthPare)
        {
            if (null == appBasicConfig)
            {
                map.put(UPLOAD_BANDWIDTH, uploadBandWidth);
            }
            else
            {
                map.put(UPLOAD_BANDWIDTH,
                    null == appBasicConfig.getUploadBandWidth() ? Long.valueOf(uploadBandWidth)
                        : appBasicConfig.getUploadBandWidth().equals(Long.valueOf(-1)) ? Long.valueOf(-1)
                            : appBasicConfig.getUploadBandWidth());
            }
        }
        else
        {
            map.put(UPLOAD_BANDWIDTH, uploadBandWidthPare);
        }
        if (null == donwloadBandWidthPare || User.PARAMETER_UNDEFINED == donwloadBandWidthPare)
        {
            if (null == appBasicConfig)
            {
                map.put(DOWNLOAD_BANDWIDTH, downloadBandWidth);
            }
            else
            {
                map.put(DOWNLOAD_BANDWIDTH,
                    null == appBasicConfig.getDownloadBandWidth() ? Long.valueOf(downloadBandWidth)
                        : appBasicConfig.getDownloadBandWidth().equals(Long.valueOf(-1)) ? Long.valueOf(-1)
                            : appBasicConfig.getDownloadBandWidth());
            }
        }
        else
        {
            map.put(DOWNLOAD_BANDWIDTH, donwloadBandWidthPare);
        }
        return map;
    }
    
    @Override
    @RequestMapping(method = RequestMethod.POST)
    public void fillStatistics(User user, RestAppStatisticsRequest request, String appId)
    {
        AuthApp authApp = authAppService.getByAuthAppID(appId);
        if (null == authApp)
        {
            user.setSpaceUsed(-1);
            user.setFileCount(-1);
            user.setSpaceCount(-1);
            return;
        }
        
        Admin sessAdmin = (Admin) SecurityUtils.getSubject().getPrincipal();
        long enterpriseId = sessAdmin.getEnterpriseId();
        EnterpriseAccount enterprise = enterpriseAccountDao.getByEnterpriseApp(enterpriseId, appId);
        if (null == enterprise)
        {
            user.setSpaceUsed(-1);
            user.setFileCount(-1);
            user.setSpaceCount(-1);
            return;
        }
        StatisticsHttpClient userHttpClient = new StatisticsHttpClient(ufmClientService,
            statisticsAccesskeyService);
        RestAppStatisticsResponse response = userHttpClient.getStatisticsInfo(appId, enterprise, request);
        if (response == null)
        {
            user.setSpaceUsed(-1);
            user.setFileCount(-1);
            user.setSpaceQuota(-1);
            return;
        }
        user.setSpaceUsed(response.getSpaceUsed() == null ? 0 : response.getSpaceUsed());
        user.setFileCount(response.getFileCount() == null ? 0 : response.getFileCount());
        user.setSpaceCount(response.getSpaceCount() == null ? 0 : response.getSpaceCount());
    }
    
    // @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    // @Override
    // public void disableUser(long id, String appId)
    // {
    // AuthApp authApp = authAppService.getByAuthAppID(appId);
    // User user = get(id);
    // if (null == authApp || null == user)
    // {
    // throw new InvalidParamterException("authApp or user is null,update user failed");
    // }
    // userDAO.updateStatus(id, User.STATUS_DISABLE);
    // UserToken userToken = userTokenHelper.getUserTokenById(id);
    // if (userToken != null)
    // {
    // userToken.setStatus(User.STATUS_DISABLE);
    // userTokenHelper.saveUserInfo(userToken);
    // }
    // UserHttpClient userHttpClient = new UserHttpClient(ufmClientService);
    // user.setStatus(User.STATUS_DISABLE);
    // userHttpClient.updateUser(user, authApp);
    // }
    //
    // @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    // @Override
    // public void enableUser(long id, String appId)
    // {
    // AuthApp authApp = authAppService.getByAuthAppID(appId);
    // User user = get(id);
    // if (null == authApp || null == user)
    // {
    // throw new InvalidParamterException("authApp or user is null,update user failed");
    // }
    // userDAO.updateStatus(id, User.STATUS_ENABLE);
    // UserHttpClient userHttpClient = new UserHttpClient(ufmClientService);
    // user.setStatus(User.STATUS_ENABLE);
    // userHttpClient.updateUser(user, authApp);
    // }
    //
    @Override
    public Long findUserById()
    {
        return userDAO.findUserById();
    }
    
    @Override
    public User get(Long id)
    {
        return userDAO.get(id);
    }
    
    @Override
    public List<User> getAll()
    {
        return userDAO.getAll();
    }
    
    @Override
    public long getAppUsedSpace(String appId)
    {
        return 0L;
    }
    
    @Override
    public long getAppUserLoginTotal(String appId, Date beginDate, Date endDate)
    {
        return 0;
    }
    
    @Override
    public long getAppUserTotal(String appId)
    {
        User filter = new User();
        filter.setAppId(appId);
        return userDAO.getFilterdCount(filter);
    }
    
    @Override
    public List<User> getExportUser(String appId, int offset, int length)
    {
        List<User> content = userDAO.listUserByAppid(appId, offset, length);
        return content;
    }
    
    @Override
    public List<String> getFilterdId(User filter)
    {
        return userDAO.getFilterdId(filter);
    }
    
    @Override
    public Page<User> getPagedUser(User filter, PageRequest pageRequest)
    {
        int total = userDAO.getFilterdCount(filter);
        List<User> content = userDAO.getFilterd(filter, pageRequest.getOrder(), pageRequest.getLimit());
        // for (User user : content)
        // {
        // fillUsedSpaceAndFileCount(user);
        // }
        Page<User> page = new PageImpl<User>(content, pageRequest, total);
        return page;
    }
    
    @Override
    public Page<UserExtend> getPagedUserExtend(User filter, PageRequest pageRequest)
    {
        int total = userDAO.getFilterdCount(filter);
        List<User> content = userDAO.getFilterd(filter, pageRequest.getOrder(), pageRequest.getLimit());
        List<UserExtend> userExtendList = new ArrayList<UserExtend>(content.size());
        UserExtend userExtend = null;
        List<UserTagExtend> userTagExtendList = null;
        for (User user : content)
        {
            // fillUsedSpaceAndFileCount(user);
            userExtend = new UserExtend();
            BeanUtils.copyProperties(user, userExtend);
            userTagExtendList = userTagService.selectUserTagByUserId(user.getId());
            if (null != userTagExtendList && !userTagExtendList.isEmpty())
            {
                UserTagExtend userTagExtend = userTagExtendList.get(0);
                userExtend.setTag(userTagExtend.getTag());
                userExtend.setTagId(userTagExtend.getTagId());
            }
            userExtendList.add(userExtend);
        }
        Page<UserExtend> page = new PageImpl<UserExtend>(userExtendList, pageRequest, total);
        return page;
    }
    
    @Override
    public List<RestRegionInfo> getRegionInfo(String appId)
    {
        AuthApp authApp = authAppService.getByAuthAppID(appId);
        if (authApp == null)
        {
            throw new BusinessException("AuthApp not config");
            
        }
        RegionClient userHttpClient = new RegionClient(ufmClientService);
        return userHttpClient.getRegionInfo(authApp);
    }
    
    @Override
    public User getUserByCloudUserId(long cloudUserId)
    {
        return userDAO.getUserByCloudUserId(cloudUserId);
    }
    
    // @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    // @Override
    // public void saveUser(User user, EnterpriseAccount enterpriseAccount, boolean
    // isThrowLicense) throws BaseRunException
    // {
    // userParameterCheck.checkLoginNameByPattern(user.getLoginName());
    // UserHttpClient userHttpClient = new UserHttpClient(ufmClientService);
    // long cloudUserId = userHttpClient.createUser(user, enterpriseAccount,
    // isThrowLicense);
    // user.setCloudUserId(cloudUserId);
    // createUser(user);
    // }
    
    @Override
    public User getUserByLoginNameAppId(String username, String appId)
    {
        return userDAO.getUserByLoginNameAppId(username, appId);
    }
    
    @Override
    public User getUserByObjectSidAppId(String objectSid, String appId)
    {
        return userDAO.getUserByObjectSidAppId(objectSid, appId);
    }
    
    @Override
    public UserQos getUserQos(long id)
    {
        List<SystemConfig> itemList = systemConfigDAO.getByPrefix(null, null, UserQos.USER_QOS_CONFIG_PREFIX);
        return UserQos.buildUserQos(itemList);
    }
    
    @Override
    public List<User> getUsersByAppId(String appId)
    {
        return userDAO.getUsersByAppId(appId);
    }
    
    @Override
    public List<User> listStatisticsByAppId(String appId)
    {
        User userSpace = new User();
        User teamSpace = new User();
        User totalSpace = new User();
        
        RestAppStatisticsRequest userRequest = new RestAppStatisticsRequest();
        RestAppStatisticsRequest teamRequest = new RestAppStatisticsRequest();
        RestAppStatisticsRequest totalRequest = new RestAppStatisticsRequest();
        
        userRequest.setType(RestAppStatisticsRequest.StatisticsType.user.getType());
        
        fillStatistics(userSpace, userRequest, appId);
        userSpace.setId(0);
        teamRequest.setType(RestAppStatisticsRequest.StatisticsType.teamSpace.getType());
        fillStatistics(teamSpace, teamRequest, appId);
        teamSpace.setId(1);
        totalRequest.setType(RestAppStatisticsRequest.StatisticsType.total.getType());
        fillStatistics(totalSpace, totalRequest, appId);
        totalSpace.setId(2);
        List<User> users = new ArrayList<User>(3);
        users.add(userSpace);
        users.add(teamSpace);
        users.add(totalSpace);
        return users;
    }
    
    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    @Override
    public void resetPassword(long id, String newPsw)
    {
        try
        {
            userDAO.updatePassword(id, HashPasswordUtil.generateHashPassword(newPsw));
            userDAO.updateValidateKey(id, null);
        }
        catch (Exception e)
        {
            LOGGER.error("digest newPsw exception");
            throw new BusinessException(e);
        }
    }
    
    @Override
    public void sacleUser(long id, long spaceQuota)
    {
        userDAO.sacleUser(id, spaceQuota);
    }
    
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void setUserQos(long userID, long uploadTraffic, long downloadTraffic, int concurrent)
    {
        UserQos userQos = new UserQos(uploadTraffic, downloadTraffic, concurrent);
        List<SystemConfig> itemList = userQos.toConfigItem();
        for (SystemConfig systemConfig : itemList)
        {
            if (systemConfigDAO.get(systemConfig.getId()) == null)
            {
                systemConfigDAO.create(systemConfig);
            }
            else
            {
                systemConfigDAO.update(systemConfig);
            }
        }
    }
    
    @Override
    public void update(User user)
    {
        userDAO.update(user);
    }
    
    @Override
    public void updateValidateKey(long id, String validateKey)
    {
        userDAO.updateValidateKey(id, DigestUtil.digestPassword(validateKey));
    }
    
}
