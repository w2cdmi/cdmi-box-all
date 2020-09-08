/**
 * 
 */
package com.huawei.sharedrive.app.system.dao;

import java.util.List;

import com.huawei.sharedrive.app.system.domain.RegistApp;

import pw.cdmi.box.dao.BaseDAO;

/**
 * @author s00108907
 * 
 */
public interface AppManageDAO extends BaseDAO<RegistApp, String>
{
    
    /**
     * 获取所有对象列表
     * 
     * @return
     */
    List<RegistApp> getAll();
}
