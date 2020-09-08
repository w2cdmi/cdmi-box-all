package pw.cdmi.box.disk.group.service;

import pw.cdmi.core.exception.BaseRunException;
import pw.cdmi.core.restrpc.domain.TextResponse;
import pw.cdmi.box.disk.core.domain.PageV2;
import pw.cdmi.box.disk.core.domain.RestGroupMember;
import pw.cdmi.box.disk.httpclient.rest.request.GroupMemberOrderRequest;
import pw.cdmi.box.disk.httpclient.rest.request.RestAddGroupRequest;
import pw.cdmi.box.disk.httpclient.rest.response.GroupUserListResponse;
import pw.cdmi.box.disk.httpclient.rest.response.RestGroupResponse;
import pw.cdmi.box.disk.oauth2.domain.UserToken;

public interface GroupMembershipService
{
    String addMember(RestAddGroupRequest request, Long groupId);
    
    TextResponse deleteMember(Long groupId, Long userId);
    
    GroupUserListResponse listMember(Long id);
    
    PageV2<RestGroupMember> listPageMember(PageV2<RestGroupMember> pageRequest,
        GroupMemberOrderRequest request, Long id);
    
    void sendAddMemberMail(UserToken userToken, RestGroupResponse group, String email, String message)
        throws BaseRunException;
    
    void updateGroupRole(Long groupId, Long userId, String groupRole);
    
    void sendDeleteMemberMail(UserToken userToken, RestGroupResponse group, String email, String message)
        throws BaseRunException;
    
    void sendQuitGroupMail(UserToken userToken, RestGroupResponse group, String email, String message)
        throws BaseRunException;
}
