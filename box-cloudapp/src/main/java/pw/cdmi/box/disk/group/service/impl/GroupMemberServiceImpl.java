package pw.cdmi.box.disk.group.service.impl;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import pw.cdmi.core.exception.RestException;
import pw.cdmi.core.restrpc.RestClient;
import pw.cdmi.box.disk.client.api.GroupMemberClient;
import pw.cdmi.box.disk.group.domain.GroupMembershipsList;
import pw.cdmi.box.disk.group.domain.RestGroupMemberOrderRequest;
import pw.cdmi.box.disk.group.service.GroupMemberService;

@Component
public class GroupMemberServiceImpl implements GroupMemberService
{
    private GroupMemberClient groupMemberClient;
    
    @Resource
    private RestClient ufmClientService;
    
    @Override
    public GroupMembershipsList getUserList(Long groupId, RestGroupMemberOrderRequest groupOrder)
        throws RestException
    {
        return groupMemberClient.getUserList(groupId, groupOrder);
    }
    
    @Override
    public void deleteMember(Long groupId, Long userId) throws RestException
    {
        groupMemberClient.deleteMemberships(groupId, userId);
    }
    
    @PostConstruct
    void init()
    {
        this.groupMemberClient = new GroupMemberClient(ufmClientService);
    }
    
}