/*
 * 版权声明(Copyright Notice)：
 *      Copyright(C) 2017-2017 聚数科技成都有限公司。保留所有权利。
 *      Copyright(C) 2017-2017 www.cdmi.pw Inc. All rights reserved. 
 * 
 *      警告：本内容仅限于聚数科技成都有限公司内部传阅，禁止外泄以及用于其他的商业目
 */
package pw.cdmi.file.scan.domain;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.huawei.sharedrive.protobuf.scan.SecurityScanProtos.PSecurityScanTask;
import com.huawei.sharedrive.thrift.plugins.scan.TSecurityScanTask;

public final class SecurityScanTaskParser
{
    private SecurityScanTaskParser()
    {
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
