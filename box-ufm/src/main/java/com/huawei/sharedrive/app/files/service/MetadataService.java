package com.huawei.sharedrive.app.files.service;

import java.io.File;

import com.huawei.sharedrive.app.files.domain.INode;
import com.huawei.sharedrive.app.files.service.impl.metadata.MetadataTempFile;

public interface MetadataService
{
    boolean supportBridge();
    
    MetadataTempFile exportFileToLocal(long userId, INode syncFolder);
    
    File pullFileFromDbServer(long userId, MetadataTempFile tempDbFileObj);
    
    String generateSqliteFile(File file, long ownerId, long nodeId);
}
