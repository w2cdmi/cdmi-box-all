package com.huawei.sharedrive.uam.security.dao;

import java.util.List;

import com.huawei.sharedrive.uam.security.domain.SecurityPermission;

import pw.cdmi.box.domain.Limit;
import pw.cdmi.box.domain.Order;

public interface SecurityPermissionDAO
{
    
    void insert(SecurityPermission permission);
    
    void delete(String permissionDesc);
    
    void update(SecurityPermission permission, SecurityPermission oldPermission);
    
    List<SecurityPermission> getAll(SecurityPermission permission, Order order, Limit limit);
    
    Integer getFilterdCount(SecurityPermission permission);
    
    Integer isExist(SecurityPermission permission);
    
    SecurityPermission getByPermissionDesc(String permissionDesc);
    
    SecurityPermission getByKeyName(String keyName);
}
