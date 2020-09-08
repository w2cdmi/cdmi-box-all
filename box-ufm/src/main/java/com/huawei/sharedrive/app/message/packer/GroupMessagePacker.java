package com.huawei.sharedrive.app.message.packer;

import java.util.HashMap;
import java.util.Map;

import com.huawei.sharedrive.app.group.domain.Group;
import com.huawei.sharedrive.app.message.domain.Message;
import com.huawei.sharedrive.app.message.domain.MessageType;
import com.huawei.sharedrive.app.openapi.domain.message.MessageParamName;
import com.huawei.sharedrive.app.system.service.SystemConfigService;
import com.huawei.sharedrive.app.user.domain.User;

import pw.cdmi.core.utils.JsonUtils;

public class GroupMessagePacker
{
    public Message generalGroupMessage(SystemConfigService systemConfigService, Group group, User provider,
        long receiverId, MessageType type)
    {
        int expiredDays = MessageHelper.getExpiredDays(systemConfigService);
        Map<String, Object> params = new HashMap<String, Object>(4);
        params.put(MessageParamName.PROVIDER_USERNAME, provider.getLoginName());
        params.put(MessageParamName.PROVIDER_NAME, provider.getName());
        params.put(MessageParamName.GROUP_ID, group.getId());
        params.put(MessageParamName.GROUP_NAME, group.getName());
        
        Message message = new Message(provider.getAppId(), provider.getId(), receiverId, type.getValue(),
            Message.STATUS_UNREAD, expiredDays, JsonUtils.toJsonExcludeNull(params));
        return message;
    }
    
    public Message generalGroupRoleUpdateMessage(SystemConfigService systemConfigService, Group group,
        String role, User provider, long receiverId)
    {
        int expiredDays = MessageHelper.getExpiredDays(systemConfigService);
        Map<String, Object> params = new HashMap<String, Object>(4);
        params.put(MessageParamName.PROVIDER_USERNAME, provider.getLoginName());
        params.put(MessageParamName.PROVIDER_NAME, provider.getName());
        params.put(MessageParamName.GROUP_ID, group.getId());
        params.put(MessageParamName.GROUP_NAME, group.getName());
        params.put(MessageParamName.CURRENT_ROLE, role);
        
        Message message = new Message(provider.getAppId(), provider.getId(), receiverId,
            MessageType.GROUP_ROLE_UPDATE.getValue(), Message.STATUS_UNREAD, expiredDays,
            JsonUtils.toJsonExcludeNull(params));
        return message;
    }
}
