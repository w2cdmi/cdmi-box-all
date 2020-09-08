/**
 * 
 */
package com.huawei.sharedrive.isystem.system.service;

import java.util.List;

import pw.cdmi.common.domain.ClientManage;

/**
 * @author d00199602
 * 
 */
public interface ClientManageService
{
    
    /**
     * 获取所有客户端
     * @return
     */
    List<ClientManage> getAll();
    
    /**
     * 更新客户端(外部接口)
     * @param clientManage
     */
    void updateClient(ClientManage clientManage);
    
    /**
     * 执行客户端更新操作(提交至数据库)
     * @param clientManage
     */
    void doUpdate(ClientManage clientManage);
}
