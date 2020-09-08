package com.huawei.sharedrive.uam.ldap;

public final class Employee
{
    private String employeenumber;
    
    private String uid;
    
    private String userCN;
    
    private String mail;
    
    private String displayNameCN;
    
    private String displayNameEN;
    
    private String deptNameCN;
    
    private String deptNameEN;
    
    private String isAccountEnabled = "false";
    
    private int result = 0;
    
    private String resultDesc;
    
    public String getEmployeenumber()
    {
        return employeenumber;
    }
    
    public void setEmployeenumber(String employeenumber)
    {
        this.employeenumber = employeenumber;
    }
    
    public String getUid()
    {
        return uid;
    }
    
    public void setUid(String uid)
    {
        this.uid = uid;
    }
    
    public String getMail()
    {
        return mail;
    }
    
    public void setMail(String mail)
    {
        this.mail = mail;
    }
    
    public String getDisplayNameCN()
    {
        return displayNameCN;
    }
    
    public void setDisplayNameCN(String displayNameCN)
    {
        this.displayNameCN = displayNameCN;
    }
    
    public String getDisplayNameEN()
    {
        return displayNameEN;
    }
    
    public void setDisplayNameEN(String displayNameEN)
    {
        this.displayNameEN = displayNameEN;
    }
    
    public String getDeptNameCN()
    {
        return deptNameCN;
    }
    
    public void setDeptNameCN(String deptNameCN)
    {
        this.deptNameCN = deptNameCN;
    }
    
    public String getDeptNameEN()
    {
        return deptNameEN;
    }
    
    public void setDeptNameEN(String deptNameEN)
    {
        this.deptNameEN = deptNameEN;
    }
    
    public String getUserCN()
    {
        return userCN;
    }
    
    public void setUserCN(String userCN)
    {
        this.userCN = userCN;
    }
    
    public int getResult()
    {
        return result;
    }
    
    public void setResult(int result)
    {
        this.result = result;
    }
    
    public String getResultDesc()
    {
        return resultDesc;
    }
    
    public void setResultDesc(String resultDesc)
    {
        this.resultDesc = resultDesc;
    }
    
    public String getIsAccountEnabled()
    {
        return isAccountEnabled;
    }
    
    public void setIsAccountEnabled(String isAccountEnabled)
    {
        this.isAccountEnabled = isAccountEnabled;
    }
    
    public String toString()
    {
        StringBuilder buff = new StringBuilder();
        buff.append("result=").append(result).append(";resultDesc=").append(resultDesc);
        buff.append("employeenumber=").append(employeenumber).append('\n');
        buff.append("uid=").append(uid).append('\n');
        buff.append("cn=").append(userCN).append('\n');
        buff.append("mail=").append(mail).append('\n');
        buff.append("displayNameCN=").append(displayNameCN).append('\n');
        buff.append("displayNameEN=").append(displayNameEN).append('\n');
        buff.append("deptNameCN=").append(deptNameCN).append('\n');
        buff.append("deptNameEN=").append(deptNameEN).append('\n');
        buff.append("isAccountEnabled=").append(isAccountEnabled).append('\n');
        return buff.toString();
    }
}
