/**
 * 
 */
package com.huawei.sharedrive.app.system.service;

import java.util.List;

import pw.cdmi.common.domain.AccessNetwork;

/**
 * @author d00199602
 * 
 */
public interface AccessNetworkService
{
    /**
     * 获取接入网络列表
     * 
     * @return
     */
    List<AccessNetwork> getAll();
    
}
