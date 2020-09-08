package com.huawei.sharedrive.app.message.manage;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

import javax.annotation.PostConstruct;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.huawei.sharedrive.app.event.domain.EventType;
import com.huawei.sharedrive.app.event.domain.PersistentEvent;
import com.huawei.sharedrive.app.event.manager.PersistentEventManager;
import com.huawei.sharedrive.app.event.service.PersistentEventConsumer;
import com.huawei.sharedrive.app.exception.NoSuchMessageException;
import com.huawei.sharedrive.app.files.domain.INode;
import com.huawei.sharedrive.app.files.service.FileBaseService;
import com.huawei.sharedrive.app.group.domain.Group;
import com.huawei.sharedrive.app.group.domain.GroupMemberships;
import com.huawei.sharedrive.app.group.service.GroupMembershipsService;
import com.huawei.sharedrive.app.group.service.GroupService;
import com.huawei.sharedrive.app.message.domain.Message;
import com.huawei.sharedrive.app.message.domain.MessageStatus;
import com.huawei.sharedrive.app.message.domain.MessageType;
import com.huawei.sharedrive.app.message.service.MessageService;
import com.huawei.sharedrive.app.oauth2.domain.UserToken;
import com.huawei.sharedrive.app.oauth2.service.Authorize.AuthorityMethod;
import com.huawei.sharedrive.app.oauth2.service.UserTokenHelper;
import com.huawei.sharedrive.app.openapi.domain.message.ListMessageRequest;
import com.huawei.sharedrive.app.openapi.domain.message.MessageList;
import com.huawei.sharedrive.app.openapi.domain.message.MessageListenUrl;
import com.huawei.sharedrive.app.openapi.domain.message.MessageParamName;
import com.huawei.sharedrive.app.openapi.domain.message.MessageResponse;
import com.huawei.sharedrive.app.system.service.SystemConfigService;
import com.huawei.sharedrive.app.teamspace.domain.TeamSpace;
import com.huawei.sharedrive.app.teamspace.domain.TeamSpaceMemberships;
import com.huawei.sharedrive.app.teamspace.service.TeamSpaceService;
import com.huawei.sharedrive.app.user.domain.User;
import com.huawei.sharedrive.app.user.service.UserService;
import com.huawei.sharedrive.app.utils.BusinessConstants;
import com.huawei.sharedrive.app.utils.PropertiesUtils;

import pw.cdmi.common.domain.DirectChainConfig;
import pw.cdmi.common.domain.SystemConfig;
import pw.cdmi.common.log.LoggerUtil;

@Component("messageManager")
public class MessageManager implements PersistentEventConsumer
{
    private static final Logger LOGGER = LoggerFactory.getLogger(MessageManager.class);
    
    @Autowired
    private FileBaseService fileBaseService;
    
    @Autowired
    private GroupMembershipsService groupMembershipsService;
    
    @Autowired
    private GroupService groupService;
    
    @Autowired
    private MessageSender messageSender;
    
    @Autowired
    private MessageService messageService;
    
    @Autowired
    private PersistentEventManager persistentEventManager;
    
    @Autowired
    private SystemConfigService systemConfigService;
    
    @Autowired
    private TeamSpaceService teamSpaceService;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private UserTokenHelper userTokenHelper;
    
    private HandleThread hThread = null;
    
    private static final int MAX_QUEUE_SIZE = 20000;
    
    private static LinkedBlockingQueue<PersistentEvent> eventQueue = new LinkedBlockingQueue<PersistentEvent>(
        MAX_QUEUE_SIZE);
    
    @Override
    public void consumeEvent(PersistentEvent event)
    {
        LoggerUtil.regiestThreadLocalLog();
        if (!eventQueue.offer(event))
        {
            LOGGER.info("add event failed, queue size:" + eventQueue.size() + ",event:" + event);
        }
    }
    
    /**
     * 删除消息
     * 
     * @param receiverId
     * @param id
     */
    public void deleteMessagae(long receiverId, long id)
    {
        Message message = messageService.get(receiverId, id);
        if (message == null)
        {
            throw new NoSuchMessageException("Message does not exist");
        }
        messageService.delete(receiverId, id);
    }
    
    @Override
    public EventType[] getInterestedEvent()
    {
        return new EventType[]{EventType.INODE_PRELOAD_END, EventType.TEAMSPACE_MEMBER_CREATE,
            EventType.TEAMSPACE_MEMBER_DELETE, EventType.TEAMSPACE_MEMBER_UPDATE, EventType.SHARE_CREATE,
            EventType.SHARE_DELETE, EventType.GROUP_MEMBER_CREATE, EventType.GROUP_MEMBER_DELETE,
            EventType.GROUP_MEMBER_UPDATE};
    }
    
    /**
     * 获取消息监听地址
     * 
     * @param receiverId
     * @return
     */
    public MessageListenUrl getListener(long receiverId)
    {
        String serverDomain = null;
        SystemConfig systemConfig = systemConfigService.getConfig(DirectChainConfig.DIRECT_CHAIN_CONFIG);
        if (null != systemConfig)
        {
            serverDomain = systemConfig.getValue();
        }
        if (StringUtils.isEmpty(serverDomain))
        {
            serverDomain = PropertiesUtils.getProperty("ufm.server.domain");
        }
        String newServerDomain = replaceSchema(serverDomain);
        
        UserToken token = userTokenHelper.createTokenForMessageListener(receiverId,
            AuthorityMethod.MESSAGE_LISTEN);
        StringBuffer url = new StringBuffer(newServerDomain).append("/message/listen/")
            .append(receiverId)
            .append("/")
            .append(token.getToken());
        MessageListenUrl listener = new MessageListenUrl();
        listener.setUrl(url.toString());
        return listener;
    }
    
    @PostConstruct
    public void init()
    {
        persistentEventManager.registerConsumer(this);
        eventQueue.clear();
        if (hThread == null)
        {
            hThread = new HandleThread();
            hThread.start();
        }
    }
    
    /**
     * 列举消息
     * 
     * @param receiverId
     * @param status
     * @param startId
     * @param offset
     * @param length
     * @return
     */
    public MessageList listMessage(long receiverId, ListMessageRequest listMessageRequest)
    {
        List<Message> list = messageService.listMessage(receiverId,
            MessageStatus.getValue(listMessageRequest.getStatus()),
            listMessageRequest.getStartId(),
            listMessageRequest.getOffset(),
            listMessageRequest.getLimit());
        long total = messageService.getTotalMessages(receiverId,
            MessageStatus.getValue(listMessageRequest.getStatus()),
            listMessageRequest.getStartId());
        MessageList messageList = new MessageList(listMessageRequest.getOffset(),
            listMessageRequest.getLimit(), total, transform(list));
        return messageList;
    }
    
    /**
     * 更新消息状态
     * 
     * @param receiverId
     * @param id
     * @param status
     * @return
     */
    public Message updateMessage(long receiverId, long id, byte status)
    {
        Message message = messageService.get(receiverId, id);
        if (message == null)
        {
            throw new NoSuchMessageException("Message does not exist");
        }
        message.setStatus(status);
        messageService.updateStatus(receiverId, id, status);
        return message;
    }
    
    private String replaceSchema(String serverDomain)
    {
        return serverDomain.replaceAll("http", "ws").replaceAll("https", "wss");
    }
    
    /**
     * 发送群组类消息
     * 
     * @param event
     * @param messageType
     */
    private void sendGroupMessageEvent(PersistentEvent event, MessageType messageType)
    {
        MessageType type = messageType;
        long providerId = Long.parseLong(event.getParameter(MessageParamName.PROVIDER_ID));
        long receiverId = Long.parseLong(event.getParameter(MessageParamName.RECEIVER_ID));
        long groupId = Long.parseLong(event.getParameter(MessageParamName.GROUP_ID));
        User provider = userService.get(providerId);
        Group group = groupService.get(groupId);
        
        if (provider == null || group == null)
        {
            LOGGER.warn("Send message failed. provider:{}, group:{}, type:{}",
                provider,
                group,
                event.getEventType());
            return;
        }
        
        // 自己退出群组
        if (MessageType.GROUP_DELETE_MEMBER.getType().equals(type.getType()) && providerId == receiverId)
        {
            type = MessageType.LEAVE_GROUP;
            receiverId = group.getOwnedBy();
        }
        messageSender.sendMessageEvent(type, provider, receiverId, null, null, group, null);
        
    }
    
    /**
     * 发送群组角色变更消息
     * 
     * @param event
     * @param messageType
     */
    private void sendGroupRoleUpdateEvent(PersistentEvent event)
    {
        long providerId = Long.parseLong(event.getParameter(MessageParamName.PROVIDER_ID));
        long receiverId = Long.parseLong(event.getParameter(MessageParamName.RECEIVER_ID));
        long groupId = Long.parseLong(event.getParameter(MessageParamName.GROUP_ID));
        String role = event.getParameter(MessageParamName.CURRENT_ROLE);
        User provider = userService.get(providerId);
        Group group = groupService.get(groupId);
        
        if (provider == null || group == null)
        {
            LOGGER.warn("Send message failed. provider:{}, group:{}, type:{}",
                provider,
                group,
                event.getEventType());
            return;
        }
        
        messageSender.sendMessageEvent(MessageType.GROUP_ROLE_UPDATE,
            provider,
            receiverId,
            null,
            null,
            group,
            role);
        
    }
    
    /**
     * 发送共享类消息
     * 
     * @param event
     * @param messageType
     */
    private void sendShareMessageEvent(PersistentEvent event, MessageType messageType)
    {
        long providerId = Long.parseLong(event.getParameter(MessageParamName.PROVIDER_ID));
        long receiverId = Long.parseLong(event.getParameter(MessageParamName.RECEIVER_ID));
        Long primaryNodeType = null == event.getParameter(MessageParamName.PRIMARY_NODE_TYPE) ? null :
    		Long.valueOf(event.getParameter(MessageParamName.PRIMARY_NODE_TYPE)); 
        INode tempNode = event.getBaseNodeInfo();
	    if (null != primaryNodeType){
	    	tempNode.setPrimaryNodeType(primaryNodeType);
	    }
	    
        User provider = userService.get(providerId);
        if (provider == null)
        {
            LOGGER.warn("Send message failed. provider is null, type:{}", event.getEventType());
            return;
        }
        messageSender.sendMessageEvent(messageType,
            provider,
            receiverId,
            tempNode,
            null,
            null,
            null);
    }
    
    /**
     * 发送群组成员类消息
     * 
     * @param event
     * @param messageType
     */
    private void sendTeamMemberMsg(PersistentEvent event, MessageType messageType)
    {
        long providerId = Long.parseLong(event.getParameter(MessageParamName.PROVIDER_ID));
        long receiverId = Long.parseLong(event.getParameter(MessageParamName.RECEIVER_ID));
        long teamSpaceId = Long.parseLong(event.getParameter(MessageParamName.TEAMSPACE_ID));
        String memberType = event.getParameter(MessageParamName.MEMBER_TYPE);
        
        User provider = userService.get(providerId);
        TeamSpace teamSpace = teamSpaceService.getTeamSpaceNoCheck(teamSpaceId);
        
        if (provider == null || teamSpace == null)
        {
            LOGGER.warn("Send message failed. provider:{}, teamspace:{}, type:{}",
                provider,
                teamSpace,
                event.getEventType());
            return;
        }
        
        // 用户离开团队空间
        if (messageType.getType().equals(MessageType.TEAMSPACE_DELETE_MEMBER.getType())
            && (providerId == receiverId && TeamSpaceMemberships.TYPE_USER.equals(memberType)))
        {
            messageSender.sendMessageEvent(MessageType.LEAVE_TEAMSPACE,
                provider,
                teamSpace.getOwnerBy(),
                null,
                teamSpace,
                null,
                null);
        }
        else
        {
            String role = MessageType.TEAMSPACE_ROLE_UPDATE.getType().equals(messageType.getType()) ? event.getParameter(MessageParamName.CURRENT_ROLE)
                : null;
            if (TeamSpaceMemberships.TYPE_GROUP.equals(memberType))
            {
                Group group = groupService.get(receiverId);
                if (group == null)
                {
                    LOGGER.warn("Send message failed, group is null, type:{}", event.getEventType());
                    return;
                }
                GroupMemberships groupMemberships = new GroupMemberships();
                groupMemberships.setGroupId(group.getId());
                List<GroupMemberships> list = groupMembershipsService.getMemberList(null,
                    null,
                    groupMemberships,
                    null,
                    null);
                for (GroupMemberships memberShip : list)
                {
                    if (providerId == memberShip.getUserId())
                    {
                        continue;
                    }
                    messageSender.sendMessageEvent(messageType,
                        provider,
                        memberShip.getUserId(),
                        null,
                        teamSpace,
                        null,
                        role);
                }
            }
            else
            {
                messageSender.sendMessageEvent(messageType, provider, receiverId, null, teamSpace, null, role);
            }
            
        }
    }
    
    private List<MessageResponse> transform(List<Message> messageList)
    {
        List<MessageResponse> list = new ArrayList<MessageResponse>(BusinessConstants.INITIAL_CAPACITIES);
        for (Message message : messageList)
        {
            list.add(message.toMessageResponse());
        }
        return list;
    }
    
    private class HandleThread extends Thread
    {
        @Override
        public void run()
        {
            while (true)
            {
                try
                {
                    long time = System.currentTimeMillis();
                    LOGGER.info("--------------------------:size:" + eventQueue.size());
                    handleEvent(eventQueue.take());
                    LOGGER.info("--------------------------:size:" + eventQueue.size() + ",time:"
                        + (System.currentTimeMillis() - time));
                    
                }
                catch (Exception e)
                {
                    LOGGER.warn(e.getMessage(), e);
                }
            }
            
        }
    }
    
    public void handleEvent(PersistentEvent event)
    {
        switch (event.getEventType())
        {
            case INODE_PRELOAD_END: // 文件上传完成
                if (event.getOwnedBy().longValue() == event.getCreatedBy().longValue())
                {
                    return;
                }
                INode node = fileBaseService.getINodeInfo(event.getOwnedBy(), event.getNodeId());
                if (node == null)
                {
                    return;
                }
                TeamSpace teamSpace = teamSpaceService.getTeamSpaceNoCheck(event.getOwnedBy());
                if (teamSpace == null)
                {
                    return;
                }
                long createdBy = node.getCreatedBy();
                User provider = userService.get(createdBy);
                if (provider == null)
                {
                    LOGGER.warn("Send message failed. provider is null");
                    return;
                }
                messageSender.sendMessageEvent(MessageType.TEAMSPACE_UPLOAD,
                    provider,
                    null,
                    node,
                    teamSpace,
                    null,
                    null);
                break;
            case TEAMSPACE_MEMBER_CREATE: // 团队空间加入成员
                sendTeamMemberMsg(event, MessageType.TEAMSPACE_ADD_MEMBER);
                break;
            case TEAMSPACE_MEMBER_DELETE: // 删除团队空间成员
                sendTeamMemberMsg(event, MessageType.TEAMSPACE_DELETE_MEMBER);
                break;
            case TEAMSPACE_MEMBER_UPDATE: // 团队空间成员角色变更
                sendTeamMemberMsg(event, MessageType.TEAMSPACE_ROLE_UPDATE);
                break;
            case SHARE_CREATE: // 共享
                sendShareMessageEvent(event, MessageType.SHARE);
                break;
            case SHARE_DELETE: // 取消共享
                sendShareMessageEvent(event, MessageType.DELETE_SHARE);
                break;
            case GROUP_MEMBER_CREATE: // 群组添加成员
                sendGroupMessageEvent(event, MessageType.GROUP_ADD_MEMBER);
                break;
            case GROUP_MEMBER_DELETE: // 群组删除成员
                sendGroupMessageEvent(event, MessageType.GROUP_DELETE_MEMBER);
                break;
            case GROUP_MEMBER_UPDATE: // 群组成员角色变更
                sendGroupRoleUpdateEvent(event);
                break;
            default:
                break;
        }
    }
    
}
