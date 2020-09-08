/**
 * 
 */
package com.huawei.sharedrive.app.system.dao;

import java.util.List;

import pw.cdmi.common.domain.ClientManage;

/**
 * @author d00199602
 * 
 */
public interface ClientManageDAO
{
    ClientManage getClient(String type);
    
    List<ClientManage> getAll();
    
}