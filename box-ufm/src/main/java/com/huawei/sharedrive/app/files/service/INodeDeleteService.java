package com.huawei.sharedrive.app.files.service;

import java.util.List;

import com.huawei.sharedrive.app.core.domain.OrderV1;
import com.huawei.sharedrive.app.files.domain.INode;

import pw.cdmi.box.domain.Limit;

public interface INodeDeleteService
{
    /**
     * 
     * @param node
     */
    void createINodeDeleteInfo(INode node);
    
    /**
     * 
     * @param node
     */
    void deleteINodeDeleteInfo(INode node);
    
    /**
     * 
     * @param node
     * @return
     */
    INode getINodeDeleteInfo(INode node);
    
    /**
     * 
     * @param filter
     * @param order
     * @param limit
     * @return
     */
    List<INode> getINodeDeleteInfoByStatus(INode filter, OrderV1 order, Limit limit);
    
    /**
     * 
     * @param node
     */
    void updateINodeDeleteInfo(INode node);
}
