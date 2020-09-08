package com.huawei.sharedrive.app.message.packer;

import java.util.HashMap;
import java.util.Map;

import com.huawei.sharedrive.app.files.domain.INode;
import com.huawei.sharedrive.app.message.domain.Message;
import com.huawei.sharedrive.app.message.domain.MessageType;
import com.huawei.sharedrive.app.openapi.domain.message.MessageParamName;
import com.huawei.sharedrive.app.system.service.SystemConfigService;
import com.huawei.sharedrive.app.user.domain.User;

import pw.cdmi.core.utils.JsonUtils;

public class ShareMessagePacker
{
    public Message generalShareMessage(SystemConfigService systemConfigService, INode node, User provider,
        long receiverId, MessageType type)
    {
        int expiredDays = MessageHelper.getExpiredDays(systemConfigService);
        Map<String, Object> params = new HashMap<String, Object>(5);
        params.put(MessageParamName.PROVIDER_USERNAME, provider.getLoginName());
        params.put(MessageParamName.PROVIDER_NAME, provider.getName());
        params.put(MessageParamName.NODE_ID, node.getId());
        params.put(MessageParamName.NODE_NAME, node.getName());
        params.put(MessageParamName.NODE_TYPE, node.getType());
        if (null != node && null != node.getPrimaryNodeType()){
        	params.put(MessageParamName.PRIMARY_NODE_TYPE, node.getPrimaryNodeType());
        }
        
        Message message = new Message(provider.getAppId(), provider.getId(), receiverId, type.getValue(),
            Message.STATUS_UNREAD, expiredDays, JsonUtils.toJsonExcludeNull(params));
        return message;
    }
}
