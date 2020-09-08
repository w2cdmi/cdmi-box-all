package pw.cdmi.box.disk.group.service.impl;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import pw.cdmi.box.disk.client.api.GroupClient;
import pw.cdmi.box.disk.core.domain.PageV2;
import pw.cdmi.box.disk.group.domain.GroupList;
import pw.cdmi.box.disk.group.domain.GroupMembershipsInfoV2;
import pw.cdmi.box.disk.group.domain.RestGroup;
import pw.cdmi.box.disk.group.service.GroupService;
import pw.cdmi.box.disk.httpclient.rest.GroupHttpClient;
import pw.cdmi.box.disk.httpclient.rest.GroupMembershipsHttpClient;
import pw.cdmi.box.disk.httpclient.rest.request.GroupMemberOrderRequest;
import pw.cdmi.box.disk.httpclient.rest.request.GroupOrderUserRequest;
import pw.cdmi.box.disk.httpclient.rest.request.RestGroupRequest;
import pw.cdmi.box.disk.httpclient.rest.response.GroupUserListResponse;
import pw.cdmi.box.disk.httpclient.rest.response.RestGroupResponse;
import pw.cdmi.box.disk.user.service.UserService;
import pw.cdmi.box.domain.Order;
import pw.cdmi.core.restrpc.RestClient;
import pw.cdmi.core.restrpc.domain.TextResponse;

@Component
public class GroupServiceImpl implements GroupService
{
    private static final Integer MAX_LIMIT = 1000;
    
    private GroupClient groupClient;
    
    @Resource
    private RestClient ufmClientService;
    
    @Autowired
    private UserService userService;
    
    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public RestGroupResponse createGroup(RestGroupRequest groupRequest)
    {
        GroupHttpClient groupHttpClient = new GroupHttpClient(ufmClientService);
        RestGroupResponse response = groupHttpClient.createGroup(groupRequest);
        return response;
    }
    
    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public TextResponse deleteGroup(long id)
    {
        GroupHttpClient groupHttpClient = new GroupHttpClient(ufmClientService);
        return groupHttpClient.deleteGroup(id);
    }
    
    @Override
    public RestGroup getGroupInfo(Long groupId)
    {
        return groupClient.getGroupInfo(groupId);
    }
    
    @Override
    public RestGroupResponse getGroupInfoV2(long id)
    {
        GroupHttpClient groupHttpClient = new GroupHttpClient(ufmClientService);
        RestGroupResponse response = groupHttpClient.getGroupInfo(id);
        return response;
    }
    
    @Override
    public GroupList getGroupList(String keyword, Long offset, Integer limit, String type, List<Order> order)
    {
        GroupList groupList = groupClient.getGroupList(keyword, offset, limit, type, order);
        return groupList;
    }
    
    @Override
    public PageV2<GroupMembershipsInfoV2> listUserGroups(PageV2<GroupMembershipsInfoV2> pageGroup,
        GroupOrderUserRequest request)
    {
        GroupHttpClient groupHttpClient = new GroupHttpClient(ufmClientService);
        GroupUserListResponse response = groupHttpClient.listUserGroups(request);
        
        Long totalNums;
        Integer numOfPage;
        Integer currentNum;
        List<GroupMembershipsInfoV2> data;
        if (null == response)
        {
            totalNums = 0L;
            data = null;
            numOfPage = 1;
            currentNum = 0;
        }
        else
        {
            data = response.getMemberships();
            RestGroupResponse group;
            for (GroupMembershipsInfoV2 membershipInfo : data)
            {
                group = membershipInfo.getGroup();
                fillResponseData(group);
            }
            totalNums = response.getTotalCount();
            numOfPage = response.getLimit();
            currentNum = data.size();
        }
        
        pageGroup.setData(data);
        pageGroup.setTotalNums(totalNums);
        pageGroup.setNumOfPage(numOfPage);
        pageGroup.setCurrentNum(currentNum);
        pageGroup.setTotalPage(totalNums % numOfPage == 0 ? totalNums / numOfPage : totalNums / numOfPage + 1);// ?????????
        return pageGroup;
    }
    
    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public RestGroupResponse modifyGroup(long id, RestGroupRequest groupRequest)
    {
        GroupHttpClient groupHttpClient = new GroupHttpClient(ufmClientService);
        RestGroupResponse response = groupHttpClient.modifyGroup(groupRequest, id);
        return response;
    }
    
    @PostConstruct
    void init()
    {
        this.groupClient = new GroupClient(ufmClientService);
    }
    
    private void fillResponseData(RestGroupResponse group)
    {
        
        GroupMemberOrderRequest allMemberRequest = new GroupMemberOrderRequest();
        allMemberRequest.setLimit(MAX_LIMIT);
        allMemberRequest.setOffset(0L);
        
        group.setMemberNums((long) new GroupMembershipsHttpClient(ufmClientService).listMember(allMemberRequest,
            group.getId())
            .getMemberships()
            .size());
        group.setOwnedByUserName(userService.getUserByCloudUserId(group.getOwnedBy()).getName());
    }

	@Override
	public boolean isNameExist(RestGroupRequest groupRequest) {
		GroupHttpClient groupHttpClient = new GroupHttpClient(ufmClientService);
		return groupHttpClient.isGroupNameExist(groupRequest);
	}
    
}
