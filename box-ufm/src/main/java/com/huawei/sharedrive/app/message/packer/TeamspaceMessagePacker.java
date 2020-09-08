package com.huawei.sharedrive.app.message.packer;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.huawei.sharedrive.app.files.domain.INode;
import com.huawei.sharedrive.app.message.domain.Message;
import com.huawei.sharedrive.app.message.domain.MessageType;
import com.huawei.sharedrive.app.openapi.domain.message.MessageParamName;
import com.huawei.sharedrive.app.system.service.SystemConfigService;
import com.huawei.sharedrive.app.teamspace.domain.TeamSpace;
import com.huawei.sharedrive.app.user.domain.User;

import pw.cdmi.core.utils.JsonUtils;

@Service
public class TeamspaceMessagePacker
{
    public Message generalTeamSpaceMessage(SystemConfigService systemConfigService, TeamSpace teamSpace,
        User provider, long receiverId, MessageType messageType)
    {
        int expiredDays = MessageHelper.getExpiredDays(systemConfigService);
        Map<String, Object> params = new HashMap<String, Object>(4);
        params.put(MessageParamName.PROVIDER_USERNAME, provider.getLoginName());
        params.put(MessageParamName.PROVIDER_NAME, provider.getName());
        params.put(MessageParamName.TEAMSPACE_ID, teamSpace.getCloudUserId());
        params.put(MessageParamName.TEAMSPACE_NAME, teamSpace.getName());
        
        Message message = new Message(provider.getAppId(), provider.getId(), receiverId,
            messageType.getValue(), Message.STATUS_UNREAD, expiredDays, JsonUtils.toJsonExcludeNull(params));
        return message;
    }
    
    public Message generalTeamSpaceNewFileMessage(SystemConfigService systemConfigService,
        TeamSpace teamSpace, INode node, User provider, long receiverId)
    {
        int expiredDays = MessageHelper.getExpiredDays(systemConfigService);
        Map<String, Object> params = new HashMap<String, Object>(7);
        params.put(MessageParamName.PROVIDER_USERNAME, provider.getLoginName());
        params.put(MessageParamName.PROVIDER_NAME, provider.getName());
        params.put(MessageParamName.TEAMSPACE_ID, teamSpace.getCloudUserId());
        params.put(MessageParamName.TEAMSPACE_NAME, teamSpace.getName());
        params.put(MessageParamName.NODE_ID, node.getId());
        params.put(MessageParamName.NODE_NAME, node.getName());
        params.put(MessageParamName.NODE_TYPE, node.getType());
        
        Message message = new Message(provider.getAppId(), provider.getId(), receiverId,
            MessageType.TEAMSPACE_UPLOAD.getValue(), Message.STATUS_UNREAD, expiredDays,
            JsonUtils.toJsonExcludeNull(params));
        return message;
    }
    
    public Message generalTeamSpaceRoleUpdateMessage(SystemConfigService systemConfigService,
        TeamSpace teamSpace, String role, User provider, long receiverId)
    {
        int expiredDays = MessageHelper.getExpiredDays(systemConfigService);
        Map<String, Object> params = new HashMap<String, Object>(4);
        params.put(MessageParamName.PROVIDER_USERNAME, provider.getLoginName());
        params.put(MessageParamName.PROVIDER_NAME, provider.getName());
        params.put(MessageParamName.TEAMSPACE_ID, teamSpace.getCloudUserId());
        params.put(MessageParamName.TEAMSPACE_NAME, teamSpace.getName());
        params.put(MessageParamName.CURRENT_ROLE, role);
        
        Message message = new Message(provider.getAppId(), provider.getId(), receiverId,
            MessageType.TEAMSPACE_ROLE_UPDATE.getValue(), Message.STATUS_UNREAD, expiredDays,
            JsonUtils.toJsonExcludeNull(params));
        return message;
    }
}
