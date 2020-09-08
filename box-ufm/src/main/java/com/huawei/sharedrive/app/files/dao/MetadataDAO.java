package com.huawei.sharedrive.app.files.dao;

import com.huawei.sharedrive.app.files.domain.INode;

public interface MetadataDAO
{
    void exportINodesToLocal(INode srcNode, String localFile);
}
