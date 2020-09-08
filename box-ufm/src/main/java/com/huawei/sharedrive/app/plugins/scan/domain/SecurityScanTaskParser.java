package com.huawei.sharedrive.app.plugins.scan.domain;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.huawei.sharedrive.protobuf.scan.SecurityScanProtos.PSecurityScanTask;
import com.huawei.sharedrive.thrift.plugins.scan.TSecurityScanTask;

public final class SecurityScanTaskParser
{
    private SecurityScanTaskParser()
    {
    }
    
    public static SecurityScanTask bytesToSecurityScanTask(byte[] data, int offset, int length)
        throws InvalidProtocolBufferException
    {
        ByteString bs = ByteString.copyFrom(data, offset, length);
        PSecurityScanTask node = PSecurityScanTask.parseFrom(bs);
        SecurityScanTask task = new SecurityScanTask();
        task.setNodeId(node.getNodeId());
        task.setNodeName(node.getNodeName());
        task.setObjectId(node.getObjectId());
        task.setOwnedBy(node.getOwnedBy());
        task.setDssId(node.getDssId());
        task.setPriority(node.getPriority());
        return task;
    }
    
    public static TSecurityScanTask bytesToTSecurityScanTask(byte[] data, int offset, int length)
        throws InvalidProtocolBufferException
    {
        ByteString bs = ByteString.copyFrom(data, offset, length);
        PSecurityScanTask node = PSecurityScanTask.parseFrom(bs);
        TSecurityScanTask task = new TSecurityScanTask();
        task.setNodeId(node.getNodeId());
        task.setNodeName(node.getNodeName());
        task.setObjectId(node.getObjectId());
        task.setOwnedBy(node.getOwnedBy());
        task.setDssId(node.getDssId());
        task.setPriority(node.getPriority());
        return task;
    }
    
    public static byte[] convertTaskToBytes(SecurityScanTask task)
    {
        PSecurityScanTask.Builder nodeBuilder = PSecurityScanTask.newBuilder();
        nodeBuilder.setNodeId(task.getNodeId());
        nodeBuilder.setNodeName(task.getNodeName());
        nodeBuilder.setObjectId(task.getObjectId());
        nodeBuilder.setOwnedBy(task.getOwnedBy());
        nodeBuilder.setDssId(task.getDssId());
        nodeBuilder.setPriority(task.getPriority());
        try {
			PSecurityScanTask node = nodeBuilder.build();
			return node.toByteArray();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return null;
    }
    
    public static byte[] tSecurityScanTaskToBytes(TSecurityScanTask task)
    {
        PSecurityScanTask.Builder nodeBuilder = PSecurityScanTask.newBuilder();
        nodeBuilder.setNodeId(task.getNodeId());
        nodeBuilder.setNodeName(task.getNodeName());
        nodeBuilder.setObjectId(task.getObjectId());
        nodeBuilder.setOwnedBy(task.getOwnedBy());
        nodeBuilder.setDssId(task.getDssId());
        nodeBuilder.setPriority(task.getPriority());
        PSecurityScanTask node = nodeBuilder.build();
        return node.toByteArray();
    }
    
}
