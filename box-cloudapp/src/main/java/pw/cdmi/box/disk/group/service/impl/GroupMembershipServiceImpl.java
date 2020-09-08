package pw.cdmi.box.disk.group.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.HtmlUtils;

import pw.cdmi.core.exception.BaseRunException;
import pw.cdmi.core.exception.BusinessException;
import pw.cdmi.core.restrpc.RestClient;
import pw.cdmi.core.restrpc.domain.TextResponse;
import pw.cdmi.box.disk.core.domain.PageV2;
import pw.cdmi.box.disk.core.domain.RestGroupMember;
import pw.cdmi.box.disk.group.domain.GroupMembershipsInfoV2;
import pw.cdmi.box.disk.group.service.GroupMembershipService;
import pw.cdmi.box.disk.httpclient.rest.GroupMembershipsHttpClient;
import pw.cdmi.box.disk.httpclient.rest.common.Constants;
import pw.cdmi.box.disk.httpclient.rest.request.GroupMemberOrderRequest;
import pw.cdmi.box.disk.httpclient.rest.request.RestAddGroupRequest;
import pw.cdmi.box.disk.httpclient.rest.request.RestModifyMemberRequest;
import pw.cdmi.box.disk.httpclient.rest.response.GroupUserListResponse;
import pw.cdmi.box.disk.httpclient.rest.response.RestGroupResponse;
import pw.cdmi.box.disk.oauth2.domain.UserToken;
import pw.cdmi.box.disk.share.domain.mail.RequestAttribute;
import pw.cdmi.box.disk.share.domain.mail.RequestMail;
import pw.cdmi.box.disk.share.domain.mail.RestMailSendRequest;
import pw.cdmi.box.disk.user.service.UserTokenManager;
import pw.cdmi.box.disk.utils.PatternRegUtil;

@Service
public class GroupMembershipServiceImpl implements GroupMembershipService
{
    
    @Resource
    private RestClient uamClientService;
    
    @Autowired
    private RestClient ufmClientService;
    
    @Autowired
    private UserTokenManager userTokenManager;
    
    private static final int INITIAL_SIZE = 10;
    
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
    public String addMember(RestAddGroupRequest request, Long groupId)
    {
        return new GroupMembershipsHttpClient(ufmClientService).addMemberships(groupId, request);
    }
    
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
    public TextResponse deleteMember(Long groupId, Long userId)
    {
        return new GroupMembershipsHttpClient(ufmClientService).deleteMember(groupId, userId);
    }
    
    @Override
    public GroupUserListResponse listMember(Long groupId)
    {
        GroupMemberOrderRequest allMemberRequest = new GroupMemberOrderRequest();
        allMemberRequest.setOffset(0L);
        allMemberRequest.setLimit(1000);
        GroupUserListResponse response = new GroupMembershipsHttpClient(ufmClientService).listMember(allMemberRequest,
            groupId);
        return response;
    }
    
    @Override
    public PageV2<RestGroupMember> listPageMember(PageV2<RestGroupMember> pageMember,
        GroupMemberOrderRequest request, Long groupId)
    {
        GroupUserListResponse response = new GroupMembershipsHttpClient(ufmClientService).listMember(request,
            groupId);
        Long totalNums;
        Integer numOfPage;
        Integer currentNum;
        List<RestGroupMember> data = new ArrayList<RestGroupMember>(INITIAL_SIZE);
        if (null == response)
        {
            totalNums = 0L;
            data = null;
            numOfPage = 1;
            currentNum = 0;
        }
        else
        {
            RestGroupMember member;
            for (GroupMembershipsInfoV2 membershipInfo : response.getMemberships())
            {
                member = membershipInfo.getMember();
                
                member.setDescription(HtmlUtils.htmlEscape(member.getDescription()));
                member.setGroupRole(HtmlUtils.htmlEscape(member.getGroupRole()));
                member.setLoginName(HtmlUtils.htmlEscape(member.getLoginName()));
                member.setName(HtmlUtils.htmlEscape(member.getName()));
                member.setUsername(HtmlUtils.htmlEscape(member.getUsername()));
                member.setUserType(HtmlUtils.htmlEscape(member.getUserType()));
                data.add(member);
            }
            totalNums = response.getTotalCount();
            numOfPage = response.getLimit();
            currentNum = data.size();
        }
        
        pageMember.setData(data);
        pageMember.setTotalNums(totalNums);
        pageMember.setNumOfPage(numOfPage);
        pageMember.setCurrentNum(currentNum);
        pageMember.setTotalPage(totalNums % numOfPage == 0 ? (totalNums / numOfPage)
            : (totalNums / numOfPage + 1));
        return pageMember;
    }
    
    @Override
    public void sendAddMemberMail(UserToken userToken, RestGroupResponse group, String email, String message)
        throws BaseRunException
    {
        String from = userToken.getName();
        List<RequestMail> mailTo = new ArrayList<RequestMail>(10);
        if(!PatternRegUtil.checkMailLegal(email)){
        	return;
        }
        RequestMail rm = new RequestMail();
        rm.setEmail(email);
        mailTo.add(rm);
        
        RestMailSendRequest rs = new RestMailSendRequest();
        rs.setType("addGroupMember");
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
        ra.setName("groupName");
        ra.setValue("");
        if (StringUtils.isNotBlank(group.getName()))
        {
            ra.setValue(group.getName());
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
    public void sendDeleteMemberMail(UserToken userToken, RestGroupResponse group, String email,
        String message) throws BaseRunException
    {
        String from = userToken.getName();
        List<RequestMail> mailTo = new ArrayList<RequestMail>(10);
        if(!PatternRegUtil.checkMailLegal(email)){
        	return;
        }
        RequestMail rm = new RequestMail();
        rm.setEmail(email);
        mailTo.add(rm);
        
        RestMailSendRequest rs = new RestMailSendRequest();
        rs.setType("deleteGroupMember");
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
        ra.setName("groupName");
        ra.setValue("");
        if (StringUtils.isNotBlank(group.getName()))
        {
            ra.setValue(group.getName());
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
    public void sendQuitGroupMail(UserToken userToken, RestGroupResponse group, String email, String message)
        throws BaseRunException
    {
        String from = userToken.getName();
        List<RequestMail> mailTo = new ArrayList<RequestMail>(10);
        if(!PatternRegUtil.checkMailLegal(email)){
        	return;
        }
        RequestMail rm = new RequestMail();
        rm.setEmail(email);
        mailTo.add(rm);
        
        RestMailSendRequest rs = new RestMailSendRequest();
        rs.setType("quitGroup");
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
        ra.setName("groupName");
        ra.setValue("");
        if (StringUtils.isNotBlank(group.getName()))
        {
            ra.setValue(group.getName());
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
    
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
    public void updateGroupRole(Long groupId, Long userId, String groupRole)
    {
        
        RestModifyMemberRequest request = new RestModifyMemberRequest();
        request.setGroupRole(groupRole);
        new GroupMembershipsHttpClient(ufmClientService).updateMemberGroupRole(userId, request, groupId);
    }
    
    private Map<String, String> assembleToken()
    {
        Map<String, String> headers = new HashMap<String, String>(1);
        headers.put("Authorization", userTokenManager.getToken());
        return headers;
    }
    
}
