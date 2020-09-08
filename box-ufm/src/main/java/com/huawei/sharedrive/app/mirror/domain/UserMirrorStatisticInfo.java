package com.huawei.sharedrive.app.mirror.domain;

import java.util.Date;
import java.util.List;

import com.huawei.sharedrive.app.files.domain.INode;

public class UserMirrorStatisticInfo
{
    
    // 用户类型
    private long accountId;
    
    // 应用ID
    private String appId;
    
    // 唯一主键
    private String id;
    
    private List<INode> mirrorFileInfo;
    
    private String mirrorFileInfoStr;
    
    // 有镜像文件信息
    private long mirrorFileNumber;
    
    // 没有镜像对象的文件信息
    private List<INode> notMirrorFileInfo;
    
    private String notMirrorFileInfoStr;
    
    // 没有镜像的对象数
    private long notMirrorFileNumber;
    
    // 复制策略ID
    private int policyId;
    
    // 统计时间
    private Date statistcDate;
    
    /** 分表信息 ,按account分表 */
    private int tableSuffix;
    
    // 用户ID，可以是user/account/appid
    private long userId;
    
    public long getAccountId()
    {
        return accountId;
    }
    
    public String getAppId()
    {
        return appId;
    }
    
    public String getId()
    {
        return id;
    }
    
    public List<INode> getMirrorFileInfo()
    {
        return mirrorFileInfo;
    }
    
    public String getMirrorFileInfoStr()
    {
        return mirrorFileInfoStr;
    }
    
    public long getMirrorFileNumber()
    {
        return mirrorFileNumber;
    }
    
    public List<INode> getNotMirrorFileInfo()
    {
        return notMirrorFileInfo;
    }
    
    public String getNotMirrorFileInfoStr()
    {
        return notMirrorFileInfoStr;
    }
    
    public long getNotMirrorFileNumber()
    {
        return notMirrorFileNumber;
    }
    
    public int getPolicyId()
    {
        return policyId;
    }
    
    public Date getStatistcDate()
    {
        if (statistcDate == null)
        {
            return null;
        }
        return (Date) statistcDate.clone();
    }
    
    public int getTableSuffix()
    {
        return tableSuffix;
    }
    
    public long getUserId()
    {
        return userId;
    }
    
    public void setAccountId(long accountId)
    {
        this.accountId = accountId;
    }
    
    public void setAppId(String appId)
    {
        this.appId = appId;
    }
    
    public void setId(String id)
    {
        this.id = id;
    }
    
    public void setMirrorFileInfo(List<INode> mirrorFileInfo)
    {
        this.mirrorFileInfo = mirrorFileInfo;
    }
    
    public void setMirrorFileInfoStr(String mirrorFileInfoStr)
    {
        this.mirrorFileInfoStr = mirrorFileInfoStr;
    }
    
    public void setMirrorFileNumber(long mirrorFileNumber)
    {
        this.mirrorFileNumber = mirrorFileNumber;
    }
    
    public void setNotMirrorFileInfo(List<INode> notMirrorFileInfo)
    {
        this.notMirrorFileInfo = notMirrorFileInfo;
    }
    
    public void setNotMirrorFileInfoStr(String notMirrorFileInfoStr)
    {
        this.notMirrorFileInfoStr = notMirrorFileInfoStr;
    }
    
    public void setNotMirrorFileNumber(long notMirrorFileNumber)
    {
        this.notMirrorFileNumber = notMirrorFileNumber;
    }
    
    public void setPolicyId(int policyId)
    {
        this.policyId = policyId;
    }
    
    public void setStatistcDate(Date statistcDate)
    {
        if (statistcDate == null)
        {
            this.statistcDate = null;
        }
        else
        {
            this.statistcDate = (Date) statistcDate.clone();
        }
    }
    
    public void setTableSuffix(int tableSuffix)
    {
        this.tableSuffix = tableSuffix;
    }
    
    public void setUserId(long userId)
    {
        this.userId = userId;
    }
    
}
