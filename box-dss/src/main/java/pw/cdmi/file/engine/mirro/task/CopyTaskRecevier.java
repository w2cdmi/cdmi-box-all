package pw.cdmi.file.engine.mirro.task;

import java.util.List;

import org.springframework.beans.factory.DisposableBean;

import pw.cdmi.file.engine.mirro.domain.CopyTask;

public interface CopyTaskRecevier extends Runnable,DisposableBean
{
    
    boolean isStart();
    
    boolean addCopyTask(CopyTask copyTask);
    
    void batchInsert(List<CopyTask> copyTaskList);
    
}