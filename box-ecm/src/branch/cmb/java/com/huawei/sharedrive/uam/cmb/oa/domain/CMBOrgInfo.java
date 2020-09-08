package com.huawei.sharedrive.uam.cmb.oa.domain;

import java.io.Serializable;

public class CMBOrgInfo implements Serializable
{
    private static final long serialVersionUID = 2452635192223555593L;
    
    private Integer orgId;
    
    private String groupId;
    
    private String groupName;
    
    private String fatherGroupId;
    
    private Integer fatherOrgId;
    
    private Integer leaf;
    
    private Integer unitType;
    
    private Integer hierarchyflag;
    
    private String emailId;
    
    private String headerId;
    
    private Integer display;
    
    private Integer point;
    
    private String pathName;
    
    private Integer oneclassOrgId;
    
    private Integer twoClassOrgId;
    
    private Integer threeClassOrgId;
    
    private Integer fourClassOrgId;
    
    private Integer fiveClassOrgId;
    
    private Integer usePlace;
    
    private Integer groupProperty;
    
    private Integer location;
    
    private Integer sortId;
    
    private String exInfo;
    
    public Integer getDisplay()
    {
        return display;
    }
    
    public void setDisplay(Integer display)
    {
        this.display = display;
    }
    
    public String getEmailId()
    {
        return emailId;
    }
    
    public void setEmailId(String emailId)
    {
        this.emailId = emailId;
    }
    
    public String getExInfo()
    {
        return exInfo;
    }
    
    public void setExInfo(String exInfo)
    {
        this.exInfo = exInfo;
    }
    
    public String getFatherGroupId()
    {
        return fatherGroupId;
    }
    
    public void setFatherGroupId(String fatherGroupId)
    {
        this.fatherGroupId = fatherGroupId;
    }
    
    public Integer getFatherOrgId()
    {
        return fatherOrgId;
    }
    
    public void setFatherOrgId(Integer fatherOrgId)
    {
        this.fatherOrgId = fatherOrgId;
    }
    
    public Integer getFiveClassOrgId()
    {
        return fiveClassOrgId;
    }
    
    public void setFiveClassOrgId(Integer fiveClassOrgId)
    {
        this.fiveClassOrgId = fiveClassOrgId;
    }
    
    public Integer getFourClassOrgId()
    {
        return fourClassOrgId;
    }
    
    public void setFourClassOrgId(Integer fourClassOrgId)
    {
        this.fourClassOrgId = fourClassOrgId;
    }
    
    public Integer getGroupProperty()
    {
        return groupProperty;
    }
    
    public void setGroupProperty(Integer groupProperty)
    {
        this.groupProperty = groupProperty;
    }
    
    public String getGroupId()
    {
        return groupId;
    }
    
    public void setGroupId(String groupId)
    {
        this.groupId = groupId;
    }
    
    public String getGroupName()
    {
        return groupName;
    }
    
    public void setGroupName(String groupName)
    {
        this.groupName = groupName;
    }
    
    public String getHeaderId()
    {
        return headerId;
    }
    
    public void setHeaderId(String headerId)
    {
        this.headerId = headerId;
    }
    
    public Integer getHierarchyflag()
    {
        return hierarchyflag;
    }
    
    public void setHierarchyflag(Integer hierarchyflag)
    {
        this.hierarchyflag = hierarchyflag;
    }
    
    public Integer getLeaf()
    {
        return leaf;
    }
    
    public void setLeaf(Integer leaf)
    {
        this.leaf = leaf;
    }
    
    public Integer getLocation()
    {
        return location;
    }
    
    public void setLocation(Integer location)
    {
        this.location = location;
    }
    
    public Integer getOneclassOrgId()
    {
        return oneclassOrgId;
    }
    
    public void setOneclassOrgId(Integer oneclassOrgId)
    {
        this.oneclassOrgId = oneclassOrgId;
    }
    
    public Integer getOrgId()
    {
        return orgId;
    }
    
    public void setOrgId(Integer orgId)
    {
        this.orgId = orgId;
    }
    
    public String getPathName()
    {
        return pathName;
    }
    
    public void setPathName(String pathName)
    {
        this.pathName = pathName;
    }
    
    public Integer getPoint()
    {
        return point;
    }
    
    public void setPoint(Integer point)
    {
        this.point = point;
    }
    
    public Integer getSortId()
    {
        return sortId;
    }
    
    public void setSortId(Integer sortId)
    {
        this.sortId = sortId;
    }
    
    public Integer getThreeClassOrgId()
    {
        return threeClassOrgId;
    }
    
    public void setThreeClassOrgId(Integer threeClassOrgId)
    {
        this.threeClassOrgId = threeClassOrgId;
    }
    
    public Integer getTwoClassOrgId()
    {
        return twoClassOrgId;
    }
    
    public void setTwoClassOrgId(Integer twoClassOrgId)
    {
        this.twoClassOrgId = twoClassOrgId;
    }
    
    public Integer getUnitType()
    {
        return unitType;
    }
    
    public void setUnitType(Integer unitType)
    {
        this.unitType = unitType;
    }
    
    public Integer getUsePlace()
    {
        return usePlace;
    }
    
    public void setUsePlace(Integer usePlace)
    {
        this.usePlace = usePlace;
    }
    
}
