package com.huawei.sharedrive.app.message.domain;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.huawei.sharedrive.app.utils.BusinessConstants;
import com.huawei.sharedrive.protobuf.message.MessageProtos.PMessage;
import com.huawei.sharedrive.protobuf.message.MessageProtos.PParam;

import pw.cdmi.core.utils.DateUtils;
import pw.cdmi.core.utils.JsonUtils;

public final class MessageParser
{
    private MessageParser()
    {
        
    }
    
    public static Message bytesToMessage(byte[] data, int offset, int length)
        throws InvalidProtocolBufferException
    {
        ByteString bs = ByteString.copyFrom(data, offset, length);
        PMessage node = PMessage.parseFrom(bs);
        Message message = new Message();
        message.setId(node.getId());
        message.setProviderId(node.getProviderId());
        message.setReceiverId(node.getReceiverId());
        message.setAppId(node.getAppId());
        message.setType((byte) node.getType());
        message.setStatus((byte) node.getStatus());
        message.setCreatedAt(new Date(node.getCreatedAt()));
        message.setExpiredAt(new Date(node.getExpiredAt()));
        
        List<PParam> list = node.getParamsList();
        Map<String, Object> params = new HashMap<String, Object>(BusinessConstants.INITIAL_CAPACITIES);
        for (PParam temp : list)
        {
            params.put(temp.getName(), temp.getValue());
        }
        message.setParams(JsonUtils.toJson(params));
        return message;
    }
    
    public static byte[] convertMessageToBytes(Message message)
    {
        PMessage.Builder nodeBuilder = PMessage.newBuilder();
        nodeBuilder.setId(message.getId());
        nodeBuilder.setProviderId(message.getProviderId());
        nodeBuilder.setReceiverId(message.getReceiverId());
        nodeBuilder.setAppId(message.getAppId() == null ? "" : message.getAppId());
        nodeBuilder.setType(message.getType());
        nodeBuilder.setStatus(message.getStatus());
        nodeBuilder.setCreatedAt(DateUtils.getDateTime(message.getCreatedAt()));
        nodeBuilder.setExpiredAt(DateUtils.getDateTime(message.getExpiredAt()));
        if (StringUtils.isNotBlank(message.getParams()))
        {
            Map<String, Object> params = JsonUtils.stringToMap(message.getParams());
            
            if (params != null)
            {
                PParam param = null;
                for (Entry<String, Object> entry : params.entrySet())
                {
                    param = bulidParam(entry.getKey(), entry.getValue());
                    nodeBuilder.addParams(param);
                }
            }
        }
        PMessage node = nodeBuilder.build();
        return node.toByteArray();
    }
    
    private static PParam bulidParam(String key, Object value)
    {
        PParam.Builder nodeBuilder = PParam.newBuilder();
        nodeBuilder.setName(key);
        nodeBuilder.setValue(String.valueOf(value));
        return nodeBuilder.build();
    }
    
}
