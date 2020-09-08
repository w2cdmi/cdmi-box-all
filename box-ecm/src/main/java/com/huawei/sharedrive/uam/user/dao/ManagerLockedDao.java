package com.huawei.sharedrive.uam.user.dao;

import pw.cdmi.common.domain.ManagerLocked;

public interface ManagerLockedDao
{
    String ANON_ADMIN_KEY = "uam_admin_lock_";
    
    ManagerLocked get(String loginName);
    
    void insert(ManagerLocked managerLocked);
    
    void addFailTime(ManagerLocked managerLocked);
    
    void delete(String loginName);
    
    void lock(ManagerLocked managerLocked);
    
}
