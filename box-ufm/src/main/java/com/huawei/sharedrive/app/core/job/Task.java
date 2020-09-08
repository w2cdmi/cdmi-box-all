package com.huawei.sharedrive.app.core.job;

import pw.cdmi.common.log.LoggerUtil;

public abstract class Task implements Runnable
{
    public abstract void execute();
    
    public abstract String getName();
    
    @Override
    public void run()
    {
        LoggerUtil.regiestThreadLocalLog();
        execute();
    }
}
