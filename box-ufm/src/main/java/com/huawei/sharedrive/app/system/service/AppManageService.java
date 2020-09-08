/**
 * 
 */
package com.huawei.sharedrive.app.system.service;

import java.util.List;

import com.huawei.sharedrive.app.system.domain.RegistApp;

/**
 * @author s00108907
 * 
 */
public interface AppManageService
{
    
    /**
     * 创建对象
     * 
     * @param t
     */
    void create(RegistApp registApp);
    
    /**
     * 删除对象
     * 
     * @param id 对象id
     */
    void delete(String id);
    
    /**
     * 获取指定对象
     * 
     * @param id 对象id
     * @return
     */
    RegistApp get(String id);
    
    /**
     * 获取所有对象列表
     * 
     * @return
     */
    List<RegistApp> getAll();
    
    /**
     * 将应用程序信息发送给收件人
     * 
     * @param registApp
     * @param reciver
     */
    void sendMail(RegistApp registApp, String reciver);
    
    /**
     * 更新应用的KEY值
     * 
     * @param t
     */
    void updateKey(String id);
}
