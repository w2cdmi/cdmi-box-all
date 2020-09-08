/**
 * 
 */
package com.huawei.sharedrive.app.user.service.impl;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.huawei.sharedrive.app.user.domain.UserSyncVersion;
import com.huawei.sharedrive.app.user.protobuf.UserSyncVersionProtos.SyncVersion;

/**
 * @author q90003805
 * 
 */
public final class UserSyncVersionParser
{
    private UserSyncVersionParser()
    {
        
    }
    
    public static UserSyncVersion parseFromBytes(byte[] data, int size) throws InvalidProtocolBufferException
    {
        ByteString bs = ByteString.copyFrom(data, 0, size);
        SyncVersion node = SyncVersion.parseFrom(bs);
        UserSyncVersion version = new UserSyncVersion();
        version.setUserId(node.getUserId());
        version.setSyncVersion(node.getSyncVersion());
        return version;
    }
    
    public static byte[] toBytes(UserSyncVersion version)
    {
        SyncVersion.Builder nodeBuilder = SyncVersion.newBuilder();
        nodeBuilder.setUserId(version.getUserId());
        nodeBuilder.setSyncVersion(version.getSyncVersion());
        SyncVersion node = nodeBuilder.build();
        return node.toByteArray();
    }
}
