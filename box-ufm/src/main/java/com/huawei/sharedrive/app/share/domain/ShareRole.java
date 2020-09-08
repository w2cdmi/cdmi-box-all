/*
 * Copyright Huawei Technologies Co.,Ltd. 2013-2014. All rights reserved.
 */
package com.huawei.sharedrive.app.share.domain;

import java.util.List;

/**
 * 共享角色
 * 
 * @author l90003768
 * 
 */
public class ShareRole
{
    /** 下载者 */
    public static final String DOWNLOADER = "downloader";
    
    /** 编辑者 */
    public static final String EDITOR = "editor";
    
    /** 预览者 */
    public static final String PREVIEWER = "previewer";
    
    /** 被共享者 */
    public static final String SHARED = "shared";
    
    /** 上传者 */
    public static final String UPLOADER = "uploader";
    
    /** 角色名称 */
    private String name;
    
    /** 角色权限 */
    private List<String> permissionList;
    
    /** 角色ID */
    private int roleId;
    
    /** 状态 */
    private byte status;
    
    public String getName()
    {
        return name;
    }
    
    public List<String> getPermissionList()
    {
        return permissionList;
    }
    
    public int getRoleId()
    {
        return roleId;
    }
    
    public byte getStatus()
    {
        return status;
    }
    
    public void setName(String name)
    {
        this.name = name;
    }
    
    public void setPermissionList(List<String> permissionList)
    {
        this.permissionList = permissionList;
    }
    
    public void setRoleId(int roleId)
    {
        this.roleId = roleId;
    }
    
    public void setStatus(byte status)
    {
        this.status = status;
    }
}
