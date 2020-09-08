package com.huawei.sharedrive.app.teamspace.domain;
/**
 * 
 *           个    十    百      千      万     十万           百万         千万
 角色                上传  下载  预览  删除  编辑  获取链接    权限变更 是否拥有添加管理者权限
拥有者                1   1   1   1    1    1           1       1
管理者                1   1   1   1    1    1           1       0
编辑者                1   1   1   1    1    1           0       0
查看者、上传者  1   1   1   0    0    1           0       0
预览者、上传者  1   0   1   0    0    0           0       0
查看者                0   1   1   0    0    0           0       0
预览者                0   0   1   0    0    0           0       0
上传者                1   0   0   0    0    0           0       0
禁止访问者         0   0   0   0    0    0           0       0
 */

public enum DefaultResourceRole
{
    OWNER("owner", 11111111),
    ADMIN("admin", 1111111),
    EDITER("editer", 111111),
    UPLAODER_VIEWER("uploader_viewer",111),
    VIEWER("viewer",110),
    PREVIEWER("preview",100),
    UPLAODER_PREVIEWER("uploader_preview",101),
    PROHIBIT_VISITORS("ProhibitVisitors",0);   
    
    private String role;
    private long priviledge;
    private DefaultResourceRole(String role, long priviledge)
    {
        this.role = role;
        this.priviledge = priviledge;
    }
    
    public String getRole()
    {
        return role;
    }
    public long getPriviledge()
    {
        return priviledge;
    }
}
