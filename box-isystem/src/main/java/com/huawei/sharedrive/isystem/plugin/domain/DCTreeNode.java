package com.huawei.sharedrive.isystem.plugin.domain;


public class DCTreeNode
{
    private int id;
    private String name;
    private boolean isParent =false;
    private boolean checked = false;
    public int getId()
    {
        return id;
    }
    public void setId(int id)
    {
        this.id = id;
    }
    public String getName()
    {
        return name;
    }
    public void setName(String name)
    {
        this.name = name;
    }
    public boolean isIsParent()
    {
        return isParent;
    }

    public void setIsParent(boolean isParent)
    {
        this.isParent = isParent;
    }
    public boolean isChecked()
    {
        return checked;
    }
    public void setChecked(boolean checked)
    {
        this.checked = checked;
    }
    public DCTreeNode()
    {
    }
    public DCTreeNode(int id, String name)
    {
        this.id = id;
        this.name = name;
    }
    
    
}
