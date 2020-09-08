/**
 * 
 */
package com.huawei.sharedrive.isystem.system.dao;

import java.util.List;

import pw.cdmi.common.domain.ClientManage;

/**
 * @author d00199602
 * 
 */
public interface ClientManageDAO
{
    List<ClientManage> getAll();
    
    void insert(ClientManage clientManage);
    
    void delete(String type);
    
}