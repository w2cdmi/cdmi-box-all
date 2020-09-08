package com.huawei.sharedrive.uam.cmb.oa.domain;

import java.io.Serializable;

public class CMBSapUser implements Serializable
{
    
    private static final long serialVersionUID = 5675092672220348593L;
    
    public final static String STATUS_NORMAL = "A";
    
    public final static String STATUS_DELETE_B = "B";
    
    public final static String STATUS_DELETE_D = "D";
    
    private String userId;
    
    private String name;
    
    private Integer gender;
    
    private String officeTel;
    
    private String sapId;
    
    private String position;
    
    private String status;
    
    private Integer type;
    
    private String email;
    
    private String firstSpell;
    
    private String spell;
    
    public String getUserId()
    {
        return userId;
    }
    
    public void setUserId(String userId)
    {
        this.userId = userId;
    }
    
    public String getName()
    {
        return name;
    }
    
    public void setName(String name)
    {
        this.name = name;
    }
    
    public Integer getGender()
    {
        return gender;
    }
    
    public void setGender(Integer gender)
    {
        this.gender = gender;
    }
    
    public String getOfficeTel()
    {
        return officeTel;
    }
    
    public void setOfficeTel(String officeTel)
    {
        this.officeTel = officeTel;
    }
    
    public String getSapId()
    {
        return sapId;
    }
    
    public void setSapId(String sapId)
    {
        this.sapId = sapId;
    }
    
    public String getPosition()
    {
        return position;
    }
    
    public void setPosition(String position)
    {
        this.position = position;
    }
    
    public String getStatus()
    {
        return status;
    }
    
    public void setStatus(String status)
    {
        this.status = status;
    }
    
    public String getEmail()
    {
        return email;
    }
    
    public void setEmail(String email)
    {
        this.email = email;
    }
    
    public String getFirstSpell()
    {
        return firstSpell;
    }
    
    public void setFirstSpell(String firstSpell)
    {
        this.firstSpell = firstSpell;
    }
    
    public String getSpell()
    {
        return spell;
    }
    
    public void setSpell(String spell)
    {
        this.spell = spell;
    }
    
    public Integer getType()
    {
        return type;
    }
    
    public void setType(Integer type)
    {
        this.type = type;
    }
    
}
