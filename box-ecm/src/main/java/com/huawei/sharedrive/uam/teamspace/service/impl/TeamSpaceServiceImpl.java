package com.huawei.sharedrive.uam.teamspace.service.impl;

import com.huawei.sharedrive.uam.enterprise.dao.EnterpriseAccountDao;
import com.huawei.sharedrive.uam.exception.BusinessException;
import com.huawei.sharedrive.uam.httpclient.rest.UserHttpClient;
import com.huawei.sharedrive.uam.oauth2.domain.UserToken;
import com.huawei.sharedrive.uam.teamspace.domain.*;
import com.huawei.sharedrive.uam.teamspace.service.TeamSpaceService;
import com.huawei.sharedrive.uam.user.domain.Admin;
import com.huawei.sharedrive.uam.util.BusinessConstants;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.shiro.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import pw.cdmi.box.domain.Page;
import pw.cdmi.box.domain.PageImpl;
import pw.cdmi.box.domain.PageRequest;
import pw.cdmi.common.domain.enterprise.EnterpriseAccount;
import pw.cdmi.core.restrpc.RestClient;
import pw.cdmi.core.restrpc.domain.TextResponse;
import pw.cdmi.core.utils.JsonUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class TeamSpaceServiceImpl implements TeamSpaceService
{
    private static final Logger LOGGER = LoggerFactory.getLogger(TeamSpaceServiceImpl.class);
    
    private static final String TEAM_SPACES_URL = "/api/v2/teamspaces";
    
    private static final String RESOURCE_ROLE_URL = "/api/v2/roles";
    
    @Resource
    private RestClient ufmClientService;
    
    @Autowired
    private EnterpriseAccountDao enterpriseAccountDao;
    
    @Autowired
    protected MessageSource messageSource;
    
    @Override
    public RestTeamSpaceInfo changeOwner(Long teamId, String appId, ChangeOwnerRequest request) {
        StringBuilder uri = new StringBuilder(TEAM_SPACES_URL);
        uri.append('/').append(teamId).append("/memberships/deptAdmin/").append(request.getNewOwnerId());

        Map<String, String> headerMap = assembleAccountToken(appId);

        TextResponse response = ufmClientService.performJsonPutTextResponse(uri.toString(), headerMap, null);
        String content = response.getResponseBody();

        if (response.getStatusCode() == HttpStatus.OK.value()) {
            return JsonUtils.stringToObject(content, RestTeamSpaceInfo.class);
        }

        LOGGER.info(content);
        throw new BusinessException(content);
    }
    
    @Override
    public RestTeamSpaceInfo changeOwner(Long teamId, UserToken userToken, ChangeOwnerRequest request) {
        StringBuilder uri = new StringBuilder(TEAM_SPACES_URL);
        uri.append('/').append(teamId).append("/memberships/deptAdmin/").append(request.getNewOwnerId());

        Map<String, String> headerMap = new HashMap<String, String>(1);
        headerMap.put("Authorization", userToken.getToken());

        TextResponse response = ufmClientService.performJsonPutTextResponse(uri.toString(), headerMap, null);
        String content = response.getResponseBody();

        if (response.getStatusCode() == HttpStatus.OK.value()) {
            return JsonUtils.stringToObject(content, RestTeamSpaceInfo.class);
        }

        LOGGER.info(content);
        throw new BusinessException(content);
    }
    
    
    @Override
    public Page<RestTeamSpaceInfo> getPagedTeamSpace(ListAllTeamSpaceRequest listRequest, String appId,
        PageRequest pageRequest)
    {
        Map<String, String> headerMap = assembleAccountToken(appId);
        
        TextResponse response = ufmClientService.performJsonPostTextResponse(TEAM_SPACES_URL + "/all",
            headerMap,
            listRequest);
        String content = response.getResponseBody();
        List<RestTeamSpaceInfo> list = new ArrayList<RestTeamSpaceInfo>(BusinessConstants.INITIAL_CAPACITIES);
        if (response.getStatusCode() == HttpStatus.OK.value())
        {
            if (StringUtils.isNotBlank(content.substring(content.indexOf("[") + 1, content.indexOf("]"))))
            {
                RestAllTeamSpaceList restTeamSpaceList = JsonUtils.stringToObject(content,
                    RestAllTeamSpaceList.class);
                Long total = restTeamSpaceList.getTotalCount();
                return new PageImpl<RestTeamSpaceInfo>(restTeamSpaceList.getTeamSpaces(), pageRequest,
                    total.intValue());
            }
            return new PageImpl<RestTeamSpaceInfo>(list, pageRequest, list.size());
        }
        
        LOGGER.info(content);
        return new PageImpl<RestTeamSpaceInfo>(list, pageRequest, list.size());
    }
    
    @Override
    public String[] getAllTeamSpaceIds(String appId, String keyword)
    {
        ArrayList<String> teamIds = new ArrayList<String>(BusinessConstants.INITIAL_CAPACITIES);
        Map<String, String> headerMap = assembleAccountToken(appId);
        
        PageRequest pageRequest = null;
        ListAllTeamSpaceRequest request;
        int page = 1;
        TextResponse response = null;
        String content = null;
        while (true)
        {
            pageRequest = new PageRequest(page, 1000);
            request = new ListAllTeamSpaceRequest(pageRequest.getLimit().getLength(),
                Long.valueOf(pageRequest.getLimit().getOffset()));
            response = ufmClientService.performJsonPostTextResponse(TEAM_SPACES_URL + "/all",
                headerMap,
                request);
            content = response.getResponseBody();
            
            if (response.getStatusCode() == HttpStatus.OK.value())
            {
                boolean isBreak = isBreak(content, teamIds);
                if(isBreak)
                {
                    break;
                }
            }
            else
            {
                break;
            }
            
            page++;
        }
        
        return teamIds.toArray(new String[teamIds.size()]);
    }
    
    @Override
    public RestTeamSpaceInfo getTeamSpaceInfo(Long teamId, String appId)
    {
        RestTeamSpaceInfo restTeamSpaceInfo;
        
        try
        {
            String uri = TEAM_SPACES_URL + "/" + teamId;
            
            Map<String, String> headerMap = assembleAccountToken(appId);
            TextResponse response = ufmClientService.performGetText(uri, headerMap);
            String content = response.getResponseBody();
            
            if (response.getStatusCode() == HttpStatus.OK.value())
            {
                restTeamSpaceInfo = JsonUtils.stringToObject(content, RestTeamSpaceInfo.class);
                
                LOGGER.info(ToStringBuilder.reflectionToString(restTeamSpaceInfo));
                return restTeamSpaceInfo;
            }
            LOGGER.error(response.getResponseBody());
            return null;
            
        }
        catch (BusinessException e)
        {
            LOGGER.error(e.getMessage(), e);
            return null;
        }
    }

    @Override
    public RestTeamSpaceInfo createTeamSpace(String appId, RestTeamSpaceCreateRequest request) {
        Map<String, String> headerMap = assembleAccountToken(appId);

        TextResponse response = ufmClientService.performJsonPostTextResponse(TEAM_SPACES_URL, headerMap, request);
        String content = response.getResponseBody();

        if (response.getStatusCode() == HttpStatus.OK.value() || response.getStatusCode() == HttpStatus.CREATED.value()) {
            return JsonUtils.stringToObject(content, RestTeamSpaceInfo.class);
        }

        LOGGER.info(content);
        throw new BusinessException(content);
    }

    @Override
    public RestTeamSpaceInfo createTeamSpace(Long enterpriseId, String appId, RestTeamSpaceCreateRequest request) {
        Map<String, String> headerMap = assembleAccountToken(enterpriseId, appId);

        TextResponse response = ufmClientService.performJsonPostTextResponse(TEAM_SPACES_URL, headerMap, request);
        String content = response.getResponseBody();

        if (response.getStatusCode() == HttpStatus.OK.value() || response.getStatusCode() == HttpStatus.CREATED.value()) {
            return JsonUtils.stringToObject(content, RestTeamSpaceInfo.class);
        }

        LOGGER.info(content);
        throw new BusinessException(content);
    }

    @Override
    public RestTeamSpaceInfo modifyTeamSpace(Long teamId, String appId, RestTeamSpaceModifyRequest spaceModifyRequest) {
        Map<String, String> headerMap = assembleAccountToken(appId);

        return modifyTeamSpace(teamId, spaceModifyRequest, headerMap);
    }

    @Override
    public RestTeamSpaceInfo modifyTeamSpace(Long enterpriseId, Long teamId, String appId, RestTeamSpaceModifyRequest spaceModifyRequest) {
        Map<String, String> headerMap = assembleAccountToken(enterpriseId, appId);

        return modifyTeamSpace(teamId, spaceModifyRequest, headerMap);
    }

    protected RestTeamSpaceInfo modifyTeamSpace(Long teamId, RestTeamSpaceModifyRequest spaceModifyRequest, Map<String, String> headerMap) {
        StringBuilder uri = new StringBuilder(TEAM_SPACES_URL);
        uri.append('/').append(teamId);

        TextResponse response = ufmClientService.performJsonPutTextResponse(uri.toString(),
                headerMap,
                spaceModifyRequest);
        String content = response.getResponseBody();

        if (response.getStatusCode() == HttpStatus.OK.value()) {
            return JsonUtils.stringToObject(content, RestTeamSpaceInfo.class);
        }

        LOGGER.info(content);
        throw new BusinessException(content);
    }

    /**
     */
    @Override
    public String modifyTeamSpaces(String teamIds, String appId, RestTeamSpaceModifyRequest spaceModifyRequest, String keyword)
    {
        String[] idArray = teamIds.split(",");
        if ("all".equalsIgnoreCase(teamIds))
        {
            idArray = getAllTeamSpaceIds(appId, keyword);
        }
        
        StringBuilder sb = new StringBuilder("");
        for (String teamId : idArray)
        {
            if (StringUtils.isNotBlank(teamId))
            {
                try
                {
                    modifyTeamSpace(Long.valueOf(teamId), appId, spaceModifyRequest);
                }
                catch (NumberFormatException e)
                {
                    LOGGER.warn(new StringBuilder("modifyTeamSpace fail, teamId:").append(teamId)
                        .append(",error:")
                        .append(e.getMessage())
                        .toString());
                    sb.append(',').append(teamId);
                }
            }
        }
        String idFails = sb.toString();
        return idFails;
    }

    @Override
    public void deleteTeamSpace(Long enterpriseId, String appId, Long teamId) {
        Map<String, String> headerMap = assembleAccountToken(enterpriseId, appId);

        TextResponse response = ufmClientService.performDelete(TEAM_SPACES_URL + "/" + teamId, headerMap);
        String content = response.getResponseBody();

        if (response.getStatusCode() != HttpStatus.OK.value() && response.getStatusCode() != HttpStatus.CREATED.value()) {
            LOGGER.info(content);
            throw new BusinessException(content);
        }
    }

    private Map<String, String> assembleAccountToken(String appId) {
        Admin sessAdmin = (Admin) SecurityUtils.getSubject().getPrincipal();
        long enterpriseId = sessAdmin.getEnterpriseId();
        EnterpriseAccount account = enterpriseAccountDao.getByEnterpriseApp(enterpriseId, appId);
        return UserHttpClient.assembleAccountToken(account);
    }

    private Map<String, String> assembleAccountToken(long enterpriseId, String appId) {
        EnterpriseAccount account = enterpriseAccountDao.getByEnterpriseApp(enterpriseId, appId);
        return UserHttpClient.assembleAccountToken(account);
    }

    private Map<String, String> assembleAccountToken(long accountId) {
        EnterpriseAccount enterpriseAccount = enterpriseAccountDao.getByAccountId(accountId);
        return UserHttpClient.assembleAccountToken(enterpriseAccount);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<RestNodeRoleInfo> getSystemRoles(String appId)
    {
        List<RestNodeRoleInfo> result;
        
        try
        {
            String uri = RESOURCE_ROLE_URL;
            Map<String, String> headerMap = assembleAccountToken(appId);
            TextResponse response = ufmClientService.performGetText(uri, headerMap);
            String content = response.getResponseBody();
            
            if (response.getStatusCode() == HttpStatus.OK.value())
            {
                result = (List<RestNodeRoleInfo>) JsonUtils.stringToList(content,
                    ArrayList.class,
                    RestNodeRoleInfo.class);
                
                LOGGER.info(ToStringBuilder.reflectionToString(result));
                return result;
            }
            LOGGER.error(response.getResponseBody());
            return null;
        }
        catch (BusinessException e)
        {
            LOGGER.error(e.getMessage(), e);
            return null;
        }
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public List<RestNodeRoleInfo> getSystemRoles(long accountId)
    {
        List<RestNodeRoleInfo> result;
        
        try
        {
            String uri = RESOURCE_ROLE_URL;
            
            Map<String, String> headerMap = assembleAccountToken(accountId);
            TextResponse response = ufmClientService.performGetText(uri, headerMap);
            String content = response.getResponseBody();
            
            if (response.getStatusCode() == HttpStatus.OK.value())
            {
                result = (List<RestNodeRoleInfo>) JsonUtils.stringToList(content,
                    ArrayList.class,
                    RestNodeRoleInfo.class);
                
                LOGGER.info(ToStringBuilder.reflectionToString(result));
                return result;
            }
            LOGGER.error(response.getResponseBody());
            return null;
            
        }
        catch (BusinessException e)
        {
            LOGGER.error(e.getMessage(), e);
            return null;
        }
    }
    
    private boolean isBreak(String content, List<String> teamIds)
    {
        if (StringUtils.isNotBlank(content.substring(content.indexOf("[") + 1, content.indexOf("]"))))
        {
            RestAllTeamSpaceList restTeamSpaceList = JsonUtils.stringToObject(content,
                RestAllTeamSpaceList.class);
            
            for (RestTeamSpaceInfo team : restTeamSpaceList.getTeamSpaces())
            {
                teamIds.add(team.getId() + "");
            }
            
            if (restTeamSpaceList.getTeamSpaces().size() < 1000)
            {
                return true;
            }
        }
        return false;
    }

    @Override
    public RestTeamMemberInfo addTeamSpaceMember(Long enterpriseId, String appId, Long teamId, RestTeamMemberCreateRequest request) {
        String url = "/api/v2/teamspaces/" + teamId + "/memberships";
        Map<String, String> headerMap = assembleAccountToken(enterpriseId, appId);

        TextResponse response = ufmClientService.performJsonPostTextResponse(url, headerMap, request);
        String content = response.getResponseBody();

        if (response.getStatusCode() == HttpStatus.OK.value() || response.getStatusCode() == HttpStatus.CREATED.value()) {
            return JsonUtils.stringToObject(content, RestTeamMemberInfo.class);
        }

        LOGGER.info(content);
        throw new BusinessException(content);
    }

//    @Override
//    public void deleteTeamSpaceMember(Long enterpriseId, String appId, Long teamId, Long memberId) {
//        String url = "/api/v2/teamspaces/" + teamId + "/memberships/" + memberId;
//        Map<String, String> headerMap = assembleAccountToken(enterpriseId, appId);
//
//        ufmClientService.performDelete(url, headerMap);
//    }
    
    @Override
    public void deleteTeamSpaceMemberByCloudUserId(Long enterpriseId, String appId, Long teamId, Long cloudUserId) {
        String url = "/api/v2/teamspaces/" + teamId + "/memberships/user/" + cloudUserId;
        Map<String, String> headerMap = assembleAccountToken(enterpriseId, appId);

        
        TextResponse response =  ufmClientService.performDelete(url, headerMap);
        String content = response.getResponseBody();
        if (response.getStatusCode() == HttpStatus.OK.value() ) {
        	return ;
        }
        LOGGER.info(content);
        throw new BusinessException(content);

       
    }
    
    @Override
    public List<RestTeamMemberInfo>  listUserTeamSpaces(Long enterpriseId, String appId, Long memberId, Integer spaceType) {
        String url = "/api/v2/teamspaces/items";
        Map<String, String> headerMap = assembleAccountToken(enterpriseId, appId);

        ListUserTeamSpaceRequest request = new ListUserTeamSpaceRequest();
        request.setUserId(memberId);
        request.setOffset(0L);
        request.setLimit(99);
        request.setType(spaceType);

        TextResponse response = ufmClientService.performJsonPostTextResponse(url, headerMap, request);
        String content = response.getResponseBody();
        List<RestTeamMemberInfo> list = new ArrayList<>(BusinessConstants.INITIAL_CAPACITIES);
        if (response.getStatusCode() == HttpStatus.OK.value()) {
            if (StringUtils.isNotBlank(content.substring(content.indexOf("[") + 1, content.indexOf("]")))) {
                RestUserTeamSpaceList restTeamSpaceList = JsonUtils.stringToObject(content, RestUserTeamSpaceList.class);
                return restTeamSpaceList.getMemberships();
            }
        }

        return list;
    }
}
