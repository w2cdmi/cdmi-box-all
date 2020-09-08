package pw.cdmi.box.disk.group.service;

import pw.cdmi.core.exception.RestException;
import pw.cdmi.box.disk.group.domain.GroupMembershipsList;
import pw.cdmi.box.disk.group.domain.RestGroupMemberOrderRequest;

public interface GroupMemberService
{
    GroupMembershipsList getUserList(Long groupId, RestGroupMemberOrderRequest groupOrder)
        throws RestException;
    
    void deleteMember(Long groupId, Long userId) throws RestException;
}
