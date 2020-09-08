package pw.cdmi.box.disk.group.service;

import java.util.List;

import pw.cdmi.box.disk.core.domain.PageV2;
import pw.cdmi.box.disk.group.domain.GroupList;
import pw.cdmi.box.disk.group.domain.GroupMembershipsInfoV2;
import pw.cdmi.box.disk.group.domain.RestGroup;
import pw.cdmi.box.disk.httpclient.rest.request.GroupOrderUserRequest;
import pw.cdmi.box.disk.httpclient.rest.request.RestGroupRequest;
import pw.cdmi.box.disk.httpclient.rest.response.RestGroupResponse;
import pw.cdmi.box.domain.Order;
import pw.cdmi.core.restrpc.domain.TextResponse;

public interface GroupService
{
    RestGroupResponse createGroup(RestGroupRequest groupRequest);
    
    TextResponse deleteGroup(long id);
    
    RestGroup getGroupInfo(Long groupId);
    
    RestGroupResponse getGroupInfoV2(long id);
    
    GroupList getGroupList(String keyword, Long offset, Integer limit, String type, List<Order> order);
    
    PageV2<GroupMembershipsInfoV2> listUserGroups(PageV2<GroupMembershipsInfoV2> pageGroup,
        GroupOrderUserRequest request);
    
    RestGroupResponse modifyGroup(long id, RestGroupRequest groupRequest);

	boolean isNameExist(RestGroupRequest groupRequest);
}
