package com.huawei.sharedrive.app.message.manage;

import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.huawei.sharedrive.app.files.domain.INode;
import com.huawei.sharedrive.app.group.domain.Group;
import com.huawei.sharedrive.app.group.domain.GroupMemberships;
import com.huawei.sharedrive.app.group.service.GroupMembershipsService;
import com.huawei.sharedrive.app.group.service.GroupService;
import com.huawei.sharedrive.app.message.domain.Message;
import com.huawei.sharedrive.app.message.domain.MessageType;
import com.huawei.sharedrive.app.message.mq.producer.TopicProducer;
import com.huawei.sharedrive.app.message.packer.GroupMessagePacker;
import com.huawei.sharedrive.app.message.packer.ShareMessagePacker;
import com.huawei.sharedrive.app.message.packer.TeamspaceMessagePacker;
import com.huawei.sharedrive.app.message.service.MessageService;
import com.huawei.sharedrive.app.system.service.SystemConfigService;
import com.huawei.sharedrive.app.teamspace.domain.TeamMemberList;
import com.huawei.sharedrive.app.teamspace.domain.TeamSpace;
import com.huawei.sharedrive.app.teamspace.domain.TeamSpaceMemberships;
import com.huawei.sharedrive.app.teamspace.service.TeamSpaceMembershipService;
import com.huawei.sharedrive.app.user.domain.User;

import pw.cdmi.box.domain.Limit;

@Component("messageSender")
public class MessageSender
{
    private static final int LIST_TEAMSPACE_MEMBER_LIMIT = 1000;
    
    private static final Logger LOGGER = LoggerFactory.getLogger(MessageSender.class);
    
    @Autowired
    private GroupMembershipsService groupMembershipsService;
    
    @Autowired
    private GroupService groupService;
    
    @Autowired
    private MessageService messageService;
    
    @Resource(name = "noticeProducer")
    private TopicProducer noticeProducer;
    
    @Autowired
    private TeamSpaceMembershipService teamSpaceMembershipService;
    
    @Autowired
    private SystemConfigService systemConfigService;
    
    /**
     * 发送通知
     * 
     * @param message
     */
    public void sendMessage(Message message)
    {
        noticeProducer.send(message);
    }
    
    /**
     * 发送消息
     * 
     * @param messageType
     * @param provider
     * @param receiverId
     * @param node
     * @param teamSpace
     * @param group
     */
    @SuppressWarnings("PMD.ExcessiveParameterList")
    public void sendMessageEvent(MessageType messageType, User provider, Long receiverId, INode node,
        TeamSpace teamSpace, Group group, String role)
    {
        ShareMessagePacker sharemessage = new ShareMessagePacker();
        TeamspaceMessagePacker teamspace = new TeamspaceMessagePacker();
        GroupMessagePacker groupmessage = new GroupMessagePacker();
        try
        {
            // 消息是否允许发送
            if (!isAllowedToSend(messageType, teamSpace))
            {
                return;
            }
            
            Message message = null;
            switch (messageType)
            {
                case SHARE:
                case DELETE_SHARE:
                    message = sharemessage.generalShareMessage(systemConfigService,
                        node,
                        provider,
                        receiverId,
                        messageType);
                    saveAndSend(message);
                    break;
                case TEAMSPACE_ADD_MEMBER:
                case LEAVE_TEAMSPACE:
                case TEAMSPACE_DELETE_MEMBER:
                    message = teamspace.generalTeamSpaceMessage(systemConfigService,
                        teamSpace,
                        provider,
                        receiverId,
                        messageType);
                    saveAndSend(message);
                    break;
                case TEAMSPACE_UPLOAD:
                    sendFileUploadedMsg(teamSpace, node, provider);
                    break;
                case GROUP_ADD_MEMBER:
                case GROUP_DELETE_MEMBER:
                case LEAVE_GROUP:
                    message = groupmessage.generalGroupMessage(systemConfigService,
                        group,
                        provider,
                        receiverId,
                        messageType);
                    saveAndSend(message);
                    break;
                case TEAMSPACE_ROLE_UPDATE:
                    message = teamspace.generalTeamSpaceRoleUpdateMessage(systemConfigService,
                        teamSpace,
                        role,
                        provider,
                        receiverId);
                    saveAndSend(message);
                    break;
                case GROUP_ROLE_UPDATE:
                    message = groupmessage.generalGroupRoleUpdateMessage(systemConfigService,
                        group,
                        role,
                        provider,
                        receiverId);
                    saveAndSend(message);
                    break;
                default:
                    break;
            }
        }
        catch (Exception e)
        {
            LOGGER.error("Send message failed!", e);
        }
    }
    
    /**
     * 判断某种类型的消息是否允许发送
     * 
     * @param messageType
     * @param teamSpace
     * @return
     */
    private boolean isAllowedToSend(MessageType messageType, TeamSpace teamSpace)
    {
        // 团队空间新增文件是否发送消息
        if (messageType == MessageType.TEAMSPACE_UPLOAD)
        {
            if (teamSpace != null && TeamSpace.UPLOAD_NOTICE_ENABLE == teamSpace.getUploadNotice())
            {
                return true;
            }
            return false;
        }
        return true;
        
    }
    
    private void saveAndSend(Message message)
    {
        messageService.save(message);
        noticeProducer.send(message);
    }
    
    /**
     * 发送团队空间文件上传消息
     * 
     * @param teamSpace
     * @param node
     * @param provider
     */
    private void sendFileUploadedMsg(TeamSpace teamSpace, INode node, User provider)
    {
        long offset = 0;
        Limit limit = new Limit();
        TeamMemberList memberList = null;
        while (true)
        {
            limit.setOffset(offset);
            limit.setLength(LIST_TEAMSPACE_MEMBER_LIMIT);
            memberList = teamSpaceMembershipService.listTeamSpaceMemberships(teamSpace.getCloudUserId(),
                null,
                limit,
                null,
                null);
            if (CollectionUtils.isEmpty(memberList.getTeamMemberList()))
            {
                break;
            }
            
            // 发送消息给团队空间成员
            sendToTeamMember(teamSpace, node, provider, memberList);
            
            offset += LIST_TEAMSPACE_MEMBER_LIMIT;
        }
    }
    
    private Message sendToTeamMember(TeamSpace teamSpace, INode node, User provider, TeamMemberList memberList)
    {
        TeamspaceMessagePacker teamspace = new TeamspaceMessagePacker();
        Message message = null;
        for (TeamSpaceMemberships memberShip : memberList.getTeamMemberList())
        {
            // 成员类型为群组
            if (TeamSpaceMemberships.TYPE_GROUP.equals(memberShip.getUserType()))
            {
                Group group = groupService.get(memberShip.getUserId());
                GroupMemberships groupMemberships = new GroupMemberships();
                groupMemberships.setGroupId(group.getId());
                List<GroupMemberships> list = groupMembershipsService.getMemberList(null,
                    null,
                    groupMemberships,
                    null,
                    null);
                // 发送群组成员
                for (GroupMemberships groupMember : list)
                {
                    // 不发送给群组所有者
                    if (provider.getId() == groupMember.getUserId())
                    {
                        continue;
                    }
                    message = teamspace.generalTeamSpaceNewFileMessage(systemConfigService,
                        teamSpace,
                        node,
                        provider,
                        groupMember.getUserId());
                    saveAndSend(message);
                }
            }
            else
            // 成员类型为个人
            {
                // 不发送给自己
                if (provider.getId() == memberShip.getUserId())
                {
                    continue;
                }
                message = teamspace.generalTeamSpaceNewFileMessage(systemConfigService,
                    teamSpace,
                    node,
                    provider,
                    memberShip.getUserId());
                saveAndSend(message);
            }
        }
        return message;
    }
    
}
