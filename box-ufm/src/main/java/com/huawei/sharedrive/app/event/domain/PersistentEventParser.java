package com.huawei.sharedrive.app.event.domain;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.huawei.sharedrive.protobuf.event.PersistentEventProtos.PEventType;
import com.huawei.sharedrive.protobuf.event.PersistentEventProtos.PParam;
import com.huawei.sharedrive.protobuf.event.PersistentEventProtos.PPersistentEvent;

public final class PersistentEventParser
{
    private PersistentEventParser()
    {
        
    }
    public static PersistentEvent bytesToPersistentEvent(byte[] data, int offset, int length)
        throws InvalidProtocolBufferException
    {
        ByteString bs = ByteString.copyFrom(data, offset, length);
        PPersistentEvent node = PPersistentEvent.parseFrom(bs);
        PersistentEvent event = new PersistentEvent();
        event.setEventType(EventType.valueOf(node.getEventType().name()));
        event.setNodeId(node.getNodeId());
        event.setNodeName(node.getNodeName());
        event.setNodeType((byte) node.getNodeType());
        event.setParentId(node.getParentId());
        event.setOwnedBy(node.getOwnedBy());
        event.setPriority(node.getPriority());
        event.setCreatedBy(node.getCreatedBy());
        
        List<PParam> list = node.getParamsList();
        Map<String, String> params = event.getParams();
        if (params == null)
        {
            params = new HashMap<String, String>(8);
        }
        for (PParam temp : list)
        {
            params.put(temp.getKey(), temp.getValue());
        }
        event.setParams(params);
        return event;
    }
    
    public static byte[] convertEventToBytes(PersistentEvent event)
    {
        PPersistentEvent.Builder nodeBuilder = PPersistentEvent.newBuilder();
        if (event.getEventType() != null)
        {
            nodeBuilder.setEventType(PEventType.valueOf(event.getEventType().name()));
        }
        if (event.getNodeId() != null)
        {
            nodeBuilder.setNodeId(event.getNodeId());
        }
        if (event.getNodeName() != null)
        {
            nodeBuilder.setNodeName(event.getNodeName());
        }
        if (event.getNodeType() != null)
        {
            nodeBuilder.setNodeType(event.getNodeType());
        }
        if (event.getOwnedBy() != null)
        {
            nodeBuilder.setOwnedBy(event.getOwnedBy());
        }
        if (event.getPriority() != null)
        {
            nodeBuilder.setPriority(event.getPriority());
        }
        if (event.getParams() != null)
        {
            PParam param = null;
            for (Entry<String, String> entry : event.getParams().entrySet())
            {
                param = bulidParam(entry.getKey(), entry.getValue());
                nodeBuilder.addParams(param);
            }
        }
        if (event.getCreatedBy() != null)
        {
            nodeBuilder.setCreatedBy(event.getCreatedBy());
        }
        PPersistentEvent node = nodeBuilder.build();
        return node.toByteArray();
    }
    
    private static PParam bulidParam(String key, String value)
    {
        PParam.Builder nodeBuilder = PParam.newBuilder();
        nodeBuilder.setKey(key);
        nodeBuilder.setValue(value);
        return nodeBuilder.build();
    }
    
}
