package pw.cdmi.box.disk.teamspace.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import pw.cdmi.box.disk.client.api.TeamSpaceClient;
import pw.cdmi.box.disk.client.domain.node.INode;
import pw.cdmi.box.disk.group.domain.GroupConstants;
import pw.cdmi.box.disk.group.domain.GroupMembershipsInfo;
import pw.cdmi.box.disk.group.domain.GroupMembershipsList;
import pw.cdmi.box.disk.group.domain.RestGroupMemberOrderRequest;
import pw.cdmi.box.disk.group.service.GroupMemberService;
import pw.cdmi.box.disk.httpclient.rest.common.Constants;
import pw.cdmi.box.disk.oauth2.domain.UserToken;
import pw.cdmi.box.disk.share.domain.mail.RequestAttribute;
import pw.cdmi.box.disk.share.domain.mail.RequestMail;
import pw.cdmi.box.disk.share.domain.mail.RestMailSendRequest;
import pw.cdmi.box.disk.teamspace.domain.ListTeamSpaceMemberRequest;
import pw.cdmi.box.disk.teamspace.domain.RestTeamMemberInfo;
import pw.cdmi.box.disk.teamspace.domain.RestTeamSpaceInfo;
import pw.cdmi.box.disk.teamspace.service.TeamSpaceService;
import pw.cdmi.box.disk.user.service.UserTokenManager;
import pw.cdmi.box.disk.utils.PatternRegUtil;
import pw.cdmi.box.domain.Page;
import pw.cdmi.box.domain.PageRequest;
import pw.cdmi.core.exception.BaseRunException;
import pw.cdmi.core.exception.BusinessException;
import pw.cdmi.core.restrpc.RestClient;
import pw.cdmi.core.restrpc.domain.TextResponse;

@Component
public class TeamSpaceServiceImpl implements TeamSpaceService
{
    private static final int MEMBER_MAX_SIZE = 1000;
    
    private TeamSpaceClient teamSpaceHttpClient;
    
    @PostConstruct
    void init()
    {
        teamSpaceHttpClient = new TeamSpaceClient(ufmClientService);
    }
    
    @Resource
    private RestClient ufmClientService;
    
    @Resource
    private RestClient uamClientService;
    
    @Autowired
    private UserTokenManager userTokenManager;
    
    @Autowired
    private GroupMemberService groupMemberService;
    
    @Override
    public INode getNodeInfo(UserToken user, long ownerId, long nodeId) throws BaseRunException
    {
        return teamSpaceHttpClient.getNodeInfo(user, ownerId, nodeId);
    }
    
    @Override
    public RestTeamMemberInfo getMemberByLoginName(long teamId, String loginName, String teamRole,
        String keyWord) throws BaseRunException
    {
        if (StringUtils.isBlank(loginName))
        {
            return null;
        }
        Page<RestTeamMemberInfo> pages;
        int page = 1;
        RestTeamMemberInfo result = null;
        PageRequest pageRequest = null;
        ListTeamSpaceMemberRequest request = null;
        while (true)
        {
            pageRequest = new PageRequest(page, MEMBER_MAX_SIZE);
            request = new ListTeamSpaceMemberRequest(pageRequest.getLimit().getLength(),
                Long.valueOf(pageRequest.getLimit().getOffset()));
            request.setTeamRole(teamRole);
            pages = teamSpaceHttpClient.listTeamMembers(teamId, request, pageRequest);
            result = getResultFromOnePage(loginName, pages, result);
            
            if (pages.getTotalElements() < MEMBER_MAX_SIZE)
            {
                break;
            }
            page++;
        }
        return result;
    }
    
    private RestTeamMemberInfo getResultFromOnePage(String loginName, Page<RestTeamMemberInfo> pages,
        RestTeamMemberInfo result)
    {
        for (RestTeamMemberInfo restTeamMemberInfo : pages)
        {
            if (loginName.equals(restTeamMemberInfo.getMember().getLoginName()))
            {
                result = setMemberWithCheck(result, restTeamMemberInfo);
                continue;
            }
            if (StringUtils.equals(GroupConstants.USERTYPE_GROUP, restTeamMemberInfo.getMember().getType()))
            {
                try
                {
                    result = tryGetMemberFromGroup(loginName, result, restTeamMemberInfo);
                }
                catch (Exception e)
                {
                    continue;
                }
            }
            if (Constants.SPACE_TYPE_SYSTEM.equals(restTeamMemberInfo.getMember().getType()))
            {
                result = setMemberWithCheck(result, restTeamMemberInfo);
            }
            if (Constants.SPACE_TYPE_ORGANIZATION.equals(restTeamMemberInfo.getMember().getType()))
            {
                result = setMemberWithCheck(result, restTeamMemberInfo);
            }
        }
        return result;
    }
    
    private RestTeamMemberInfo tryGetMemberFromGroup(String loginName, RestTeamMemberInfo result,
        RestTeamMemberInfo restTeamMemberInfo)
    {
        RestGroupMemberOrderRequest groupOrder = new RestGroupMemberOrderRequest();
        GroupMembershipsList groupMembershipses = groupMemberService.getUserList(restTeamMemberInfo.getMember()
            .getId(),
            groupOrder);
        for (GroupMembershipsInfo g : groupMembershipses.getMemberships())
        {
            if (loginName.equals(g.getMember().getLoginName()))
            {
                result = setMemberWithCheck(result, restTeamMemberInfo);
                continue;
            }
        }
        return result;
    }
    
    private RestTeamMemberInfo setMemberWithCheck(RestTeamMemberInfo result, RestTeamMemberInfo input)
    {
        if (result == null)
        {
            return input;
        }
        if (Constants.SPACE_ROLE_ADMIN.equals(result.getTeamRole()))
        {
            return result;
        }
        if (Constants.SPACE_ROLE_ADMIN.equals(input.getTeamRole()))
        {
            return input;
        }
        
        if (Constants.SPACE_ROLE_MANAGER.equals(result.getTeamRole()))
        {
            return result;
        }
        return input;
        
    }
    
    @Override
    public void sendAddMemberMail(UserToken userToken, RestTeamSpaceInfo teamspace, String emails,
        String message) throws BaseRunException
    {
        String from = userToken.getName();
        List<RequestMail> mailTo = new ArrayList<RequestMail>(10);
        if(!PatternRegUtil.checkMailLegal(emails)){
        	return;
        }
        RequestMail rm = new RequestMail();
        rm.setEmail(emails);
        mailTo.add(rm);
        
        RestMailSendRequest rs = new RestMailSendRequest();
        rs.setType("addTeamMember");
        rs.setMailTo(mailTo);
        
        rs.setCopyTo(null);
        
        List<RequestAttribute> params = new ArrayList<RequestAttribute>(1);
        RequestAttribute ra = new RequestAttribute();
        ra.setName("message");
        if (StringUtils.isNotBlank(message))
        {
            ra.setValue(message);
        }
        
        params.add(ra);
        
        ra = new RequestAttribute();
        ra.setName("teamSpaceName");
        ra.setValue("");
        if (StringUtils.isNotBlank(teamspace.getName()))
        {
            ra.setValue(teamspace.getName());
        }
        
        params.add(ra);
        
        ra = new RequestAttribute();
        ra.setName("sender");
        if (StringUtils.isNotBlank(from))
        {
            ra.setValue(from);
        }
        
        params.add(ra);
        
        rs.setParams(params);
        TextResponse restResponse = uamClientService.performJsonPostTextResponse(Constants.API_MAILS,
            assembleToken(),
            rs);
        if (restResponse.getStatusCode() != HttpStatus.OK.value())
        {
            throw new BusinessException();
        }
    }
    
    @Override
    public void sendEmailAllMember(String emails, List<RequestAttribute> params)
    {
        List<RequestMail> mailTo = new ArrayList<RequestMail>(10);
        if(!PatternRegUtil.checkMailLegal(emails)){
        	return;
        }
        RequestMail rm = new RequestMail();
        rm.setEmail(emails);
        mailTo.add(rm);
        
        RestMailSendRequest rs = new RestMailSendRequest();
        rs.setType("spaceMember");
        rs.setMailTo(mailTo);
        
        rs.setParams(params);
        
        TextResponse restResponse = uamClientService.performJsonPostTextResponse(Constants.API_MAILS,
            assembleToken(),
            rs);
        if (restResponse.getStatusCode() != HttpStatus.OK.value())
        {
            throw new BusinessException();
        }
    }
    
    private Map<String, String> assembleToken()
    {
        Map<String, String> headers = new HashMap<String, String>(1);
        headers.put("Authorization", userTokenManager.getToken());
        return headers;
    }
    
    @Override
    public RestTeamSpaceInfo getTeamSpace(long teamId, String token)
    {
        return teamSpaceHttpClient.getTeamSpace(teamId, token);
    }
    
}
