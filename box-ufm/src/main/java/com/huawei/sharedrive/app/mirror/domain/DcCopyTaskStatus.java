package com.huawei.sharedrive.app.mirror.domain;

import java.io.Serializable;

public class DcCopyTaskStatus implements Serializable
{

    /**
     * 
     */
    private static final long serialVersionUID = 5873581925542108764L;
    
    private int resourceGroup;
    private int totalTask;
    private int input;
    private int runingTask;
    private int failedTask;
    private int notexist;
    private int callbacksuccess;
    private int callbackfailed;
    private int others;
    public int getTotalTask()
    {
        return totalTask;
    }
    public void setTotalTask(int totalTask)
    {
        this.totalTask = totalTask;
    }
    public int getRuningTask()
    {
        return runingTask;
    }
    public void setRuningTask(int runingTask)
    {
        this.runingTask = runingTask;
    }
    public int getFailedTask()
    {
        return failedTask;
    }
    public void setFailedTask(int failedTask)
    {
        this.failedTask = failedTask;
    }
    public int getOthers()
    {
        return others;
    }
    public void setOthers(int others)
    {
        this.others = others;
    }
    public int getResourceGroup()
    {
        return resourceGroup;
    }
    public void setResourceGroup(int resourceGroup)
    {
        this.resourceGroup = resourceGroup;
    }
    public int getInput()
    {
        return input;
    }
    public void setInput(int input)
    {
        this.input = input;
    }
    public int getNotexist()
    {
        return notexist;
    }
    public void setNotexist(int notexist)
    {
        this.notexist = notexist;
    }
    public int getCallbacksuccess()
    {
        return callbacksuccess;
    }
    public void setCallbacksuccess(int callbacksuccess)
    {
        this.callbacksuccess = callbacksuccess;
    }
    public int getCallbackfailed()
    {
        return callbackfailed;
    }
    public void setCallbackfailed(int callbackfailed)
    {
        this.callbackfailed = callbackfailed;
    }
}
