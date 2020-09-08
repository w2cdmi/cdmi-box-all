package pw.cdmi.box.disk.share.domain;

import java.util.List;

public class ShareRole
{
    public static final String DOWNLOADER = "downloader";
    
    public static final String EDITOR = "editor";
    
    public static final String PREVIEWER = "previewer";
    
    public static final String SHARED = "shared";
    
    public static final String UPLOADER = "uploader";
    
    private String name;
    
    private List<String> permissionList;
    
    private int roleId;
    
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
