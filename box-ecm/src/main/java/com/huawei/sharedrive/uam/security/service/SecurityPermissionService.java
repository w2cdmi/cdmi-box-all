package com.huawei.sharedrive.uam.security.service;

import com.huawei.sharedrive.uam.security.domain.SecurityPermission;

import pw.cdmi.box.domain.Page;
import pw.cdmi.box.domain.PageRequest;

public interface SecurityPermissionService
{
    
    String RETURN_SUCCESS = "SUCCESS";
    
    String RETURN_EXIST = "EXIST";
    
    String RETURN_QUOTE = "QUOTE";
    
    String insert(SecurityPermission permission);
    
    String delete(String permissionDesc);
    
    String update(SecurityPermission permission, SecurityPermission oldPermission);
    
    Page<SecurityPermission> queryPage(SecurityPermission permission, PageRequest pageRequest);
    
    Integer isExist(SecurityPermission permission);
    
    SecurityPermission getByPermissionDesc(String permissionDesc);
    
    SecurityPermission getByKeyName(String keyName);
}
