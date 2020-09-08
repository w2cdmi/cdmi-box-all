package com.huawei.sharedrive.app.files.dao;

import java.util.List;
import java.util.Map;

import com.huawei.sharedrive.app.files.domain.INode;
import com.huawei.sharedrive.app.spacestatistics.domain.UserStatisticsInfo;

import pw.cdmi.box.domain.Limit;

public interface INodeDAOSlaveDB
{
    /**
     * 顺序列举节点
     * 
     * @param userdbNumber
     * @param tableNumber
     * @param limit
     * @return
     */
    List<INode> lstContentNode(int userdbNumber, int tableNumber, Limit limit);
    
    UserStatisticsInfo getUserInfoById(long ownerId);
    
    /**
     * 使用备库统计迁移数据量
     * @param userdbNumber
     * @param tableNumber
     * @param resourceGroupId
     * @return
     */
    Map<Long, Long> lstFilesNumAndSizesByResourceGroup(int userdbNumber, int tableNumber,int resourceGroupId);
}
