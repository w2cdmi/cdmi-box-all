package com.huawei.sharedrive.app.mirror.domain;

public class DcTaskNumConfig
{
    private int dcId;
    private long maxTaskNum;
    public int getDcId()
    {
        return dcId;
    }
    public void setDcId(int dcId)
    {
        this.dcId = dcId;
    }
    public long getMaxTaskNum()
    {
        return maxTaskNum;
    }
    public void setMaxTaskNum(long maxTaskNum)
    {
        this.maxTaskNum = maxTaskNum;
    }
}
