package pw.cdmi.file.engine.mirro.task;

import pw.cdmi.file.engine.mirro.domain.CopyTask;

public interface CopyTaskWorker extends Runnable
{
    CopyTask getCopyTask();
    
    void setCopyTask(CopyTask copyTask);
    
    void beforeCopyTask();
    
    void completeCopyTask();
    
    void successCopyTask();
    
    void failedCopyTask();
    
}