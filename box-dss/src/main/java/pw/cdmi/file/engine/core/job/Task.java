package pw.cdmi.file.engine.core.job;

import pw.cdmi.common.log.LoggerUtil;

public abstract class Task implements Runnable
{
    @Override
    public void run()
    {
        LoggerUtil.regiestThreadLocalLog();
        execute();
    }
    
    public abstract void execute();
    
    public abstract String getName();
}
