package com.huawei.sharedrive.uam.user.service.impl;

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
import org.springframework.web.util.HtmlUtils;

import com.huawei.sharedrive.uam.authapp.service.AuthAppService;
import com.huawei.sharedrive.uam.core.RankRequest;
import com.huawei.sharedrive.uam.core.RankResponse;
import com.huawei.sharedrive.uam.enterprise.dao.EnterpriseAccountDao;
import com.huawei.sharedrive.uam.event.domain.Event;
import com.huawei.sharedrive.uam.event.domain.EventType;
import com.huawei.sharedrive.uam.event.service.EventService;
import com.huawei.sharedrive.uam.exception.BusinessException;
import com.huawei.sharedrive.uam.httpclient.rest.StatisticsHttpClient;
import com.huawei.sharedrive.uam.httpclient.rest.UserHttpClient;
import com.huawei.sharedrive.uam.oauth2.domain.UserToken;
import com.huawei.sharedrive.uam.openapi.domain.RestAppStatisticsRequest;
import com.huawei.sharedrive.uam.openapi.domain.RestAppStatisticsResponse;
import com.huawei.sharedrive.uam.openapi.domain.UserOrder;
import com.huawei.sharedrive.uam.openapi.domain.user.ResponseSearchUser;
import com.huawei.sharedrive.uam.openapi.domain.user.ResponseUser;
import com.huawei.sharedrive.uam.statistics.service.StatisticsAccesskeyService;
import com.huawei.sharedrive.uam.system.service.AppBasicConfigService;
import com.huawei.sharedrive.uam.user.dao.UserDAO;
import com.huawei.sharedrive.uam.user.domain.Admin;
import com.huawei.sharedrive.uam.user.domain.User;
import com.huawei.sharedrive.uam.user.domain.UserExtend;
import com.huawei.sharedrive.uam.user.domain.UserTagExtend;
import com.huawei.sharedrive.uam.user.service.UserService;
import com.huawei.sharedrive.uam.user.service.UserTagService;
import com.huawei.sharedrive.uam.util.BusinessConstants;

import pw.cdmi.box.domain.Limit;
import pw.cdmi.box.domain.Page;
import pw.cdmi.box.domain.PageImpl;
import pw.cdmi.box.domain.PageRequest;
import pw.cdmi.box.domain.Pager;
import pw.cdmi.box.http.request.RestRegionInfo;
import pw.cdmi.common.domain.AppBasicConfig;
import pw.cdmi.common.domain.enterprise.EnterpriseAccount;
import pw.cdmi.core.restrpc.RestClient;
import pw.cdmi.core.utils.DigestUtil;
import pw.cdmi.core.utils.HashPasswordUtil;
import pw.cdmi.uam.domain.AuthApp;

@SuppressWarnings("PMD.PreserveStackTrace")
@Component
public class UserServiceImpl implements UserService
{
    
    public final static String USER_STATISTICS_PATH = "/UserStatistics";
    
    public final static String UPLOAD_BANDWIDTH = "uploadBandWidth";
    
    public final static String DOWNLOAD_BANDWIDTH = "downloadBandWidth";
    
    private static final Logger LOGGER = LoggerFactory.getLogger(UserServiceImpl.class);
    
    @Autowired
    private StatisticsAccesskeyService statisticsAccesskeyService;
    
    @Autowired
    private EnterpriseAccountDao enterpriseAccountDao;
    
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
    private AuthAppService authAppService;
    
    @Autowired
    private EventService eventService;
    
    @Resource
    private RestClient ufmClientService;
    
    @Autowired
    private UserDAO userDAO;
    
    @Autowired
    private AppBasicConfigService appBasicConfigService;
    
    @Autowired
    private UserTagService userTagService;
    
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
                        : appBasicConfig.getUploadBandWidth() == -1 ? Long.valueOf(-1)
                            : appBasicConfig.getUploadBandWidth());
            }
        }
        else
        {
            map.put(UPLOAD_BANDWIDTH, uploadBandWidthPare == -1 ? Long.valueOf(-1) : uploadBandWidthPare);
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
                        : appBasicConfig.getDownloadBandWidth() == -1 ? Long.valueOf(-1)
                            : appBasicConfig.getDownloadBandWidth());
            }
        }
        else
        {
            map.put(DOWNLOAD_BANDWIDTH, donwloadBandWidthPare == -1 ? Long.valueOf(-1)
                : donwloadBandWidthPare);
        }
        return map;
    }
    
    @Override
    public void createEvent(UserToken userToken, EventType type, long createdBy)
    {
        try
        {
            Event event = new Event();
            event.setDeviceAddress(userToken.getDeviceAddress());
            event.setDeviceAgent(userToken.getDeviceAgent());
            event.setDeviceSN(userToken.getDeviceSN());
            event.setDeviceType(userToken.getDeviceType());
            event.setType(type);
            event.setCreatedAt(new Date());
            event.setLoginName(userToken.getLoginName());
            eventService.fireEvent(event);
        }
        catch (BusinessException e)
        {
            LOGGER.error(e.toString(), e);
        }
    }
    
    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    @Override
    public void delete(long id)
    {
        userDAO.delete(id);
    }
    
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
    public ResponseSearchUser listUser(User filter, Limit limit, List<UserOrder> orderList)
    {
        
        int total = userDAO.getFilterdCount(filter);
        List<User> content = userDAO.getFilterdOrderList(filter, orderList, limit);
        List<ResponseUser> responseUserList = new ArrayList<ResponseUser>(10);
        ResponseUser responseUser;
        for (User user : content)
        {
            responseUser = ResponseUser.convetToResponseUser(user);
            responseUser.setSpaceUsed(null);
            responseUser.setFileCount(null);
            responseUserList.add(responseUser);
        }
        ResponseSearchUser responseSearchUser = new ResponseSearchUser();
        responseSearchUser.setLimit(limit.getLength());
        responseSearchUser.setOffset(limit.getOffset());
        responseSearchUser.setTotalCount((long) total);
        responseSearchUser.setUsers(responseUserList);
        return responseSearchUser;
    }
    
    @Override
    public Page<User> getPagedUser(User filter, PageRequest pageRequest)
    {
        int total = userDAO.getFilterdCount(filter);
        List<User> content = userDAO.getFilterd(filter, pageRequest.getOrder(), pageRequest.getLimit());
        Page<User> page = new PageImpl<User>(content, pageRequest, total);
        return page;
    }
    
    @Override
    public Pager<User> getRankedUser(Pager<User> pager, User user, RankRequest rankRequest)
    {
        Admin sessAdmin = (Admin) SecurityUtils.getSubject().getPrincipal();
        long enterpriseId = sessAdmin.getEnterpriseId();
        EnterpriseAccount enterprise = enterpriseAccountDao.getByEnterpriseApp(enterpriseId, user.getAppId());
        if (enterprise == null)
        {
            throw new BusinessException("enterprise not config");
        }
        UserHttpClient userHttpClient = new UserHttpClient(ufmClientService);
        RankResponse response = userHttpClient.rankUsers(enterprise, rankRequest);
        int totalNums = 0;
        List<User> data;
        int numOfPage = 0;
        int currentNum = 0;
        if (null == response)
        {
            totalNums = 0;
            data = null;
            numOfPage = 1;
            currentNum = 0;
        }
        else
        {
            totalNums = response.getTotalCount();
            data = response.getUsers();
            for (User userData : data)
            {
                userData.setAppId(HtmlUtils.htmlEscape(userData.getAppId()));
                userData.setDepartment(HtmlUtils.htmlEscape(userData.getDepartment()));
                userData.setDepartmentCode(HtmlUtils.htmlEscape(userData.getDepartmentCode()));
                userData.setDescription(HtmlUtils.htmlEscape(userData.getDescription()));
                userData.setDomain(HtmlUtils.htmlEscape(userData.getDomain()));
                userData.setEmail(HtmlUtils.htmlEscape(userData.getEmail()));
                userData.setLabel(HtmlUtils.htmlEscape(userData.getLabel()));
                userData.setLoginName(HtmlUtils.htmlEscape(userData.getLoginName()));
                userData.setName(HtmlUtils.htmlEscape(userData.getName()));
                userData.setStatus(HtmlUtils.htmlEscape(userData.getStatus()));
                userData.setValidateKey(HtmlUtils.htmlEscape(userData.getValidateKey()));
            }
            numOfPage = rankRequest.getLimit();
            currentNum = data.size();
        }
        pager.setData(data);
        pager.setTotalNums(totalNums);
        pager.setNumOfPage(rankRequest.getLimit());
        pager.setCurrentNum(currentNum);
        pager.setTotalPage(totalNums % numOfPage == 0 ? totalNums / numOfPage : totalNums / numOfPage + 1);
        return pager;
    }
    
    @Override
    public List<User> getExportUser(String appId, int offset, int length)
    {
        List<User> content = userDAO.listUserByAppid(appId, offset, length);
        return content;
    }
    
    @Override
    public Page<UserExtend> getPagedUserExtend(User filter, PageRequest pageRequest)
    {
        int total = userDAO.getFilterdCount(filter);
        List<User> content = userDAO.getFilterd(filter, pageRequest.getOrder(), pageRequest.getLimit());
        List<UserExtend> userExtendList = new ArrayList<UserExtend>(10);
        UserExtend userExtend;
        List<UserTagExtend> userTagExtendList;
        UserTagExtend userTagExtend;
        for (User user : content)
        {
            userExtend = new UserExtend();
            BeanUtils.copyProperties(user, userExtend);
            userTagExtendList = userTagService.selectUserTagByUserId(user.getId());
            if (null != userTagExtendList && !userTagExtendList.isEmpty())
            {
                userTagExtend = userTagExtendList.get(0);
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
        UserHttpClient userHttpClient = new UserHttpClient(ufmClientService);
        return userHttpClient.getRegionInfo(authApp);
    }
    
    @Override
    public User getUserByCloudUserId(long cloudUserId)
    {
        return userDAO.getUserByCloudUserId(cloudUserId);
    }
    
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
    public List<User> getUsersByAppId(String appId)
    {
        return userDAO.getUsersByAppId(appId);
    }
    
    @Override
    public void sacleUser(long id, long spaceQuota)
    {
        userDAO.sacleUser(id, spaceQuota);
    }
    
    @Override
    public void update(User user)
    {
        userDAO.update(user);
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
            throw new BusinessException();
        }
    }
    
    @Override
    public void updateValidateKey(long id, String validateKey)
    {
        userDAO.updateValidateKey(id, DigestUtil.digestPassword(validateKey));
    }
    
    @Override
    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    public void addUserList(List<User> userlist)
    {
        userDAO.addUserList(userlist);
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
        RestAppStatisticsResponse response;
        response = userHttpClient.getStatisticsInfo(appId, enterprise, request);
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
    
    @Override
    public long getAppUserTotal(String appId)
    {
        User filter = new User();
        filter.setAppId(appId);
        return userDAO.getFilterdCount(filter);
    }
    
    @Override
    public long getAppUserLoginTotal(String appId, Date beginDate, Date endDate)
    {
        return 0;
    }
    
    @Override
    public List<String> getFilterdId(User filter)
    {
        return userDAO.getFilterdId(filter);
    }
    
}
