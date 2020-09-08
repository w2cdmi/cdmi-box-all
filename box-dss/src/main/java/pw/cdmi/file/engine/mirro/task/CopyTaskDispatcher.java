/**
 * 
 */
package pw.cdmi.file.engine.mirro.task;

import pw.cdmi.file.engine.mirro.domain.CopyTask;

/**
 * @author w00186884
 * 
 */
public interface CopyTaskDispatcher extends Runnable
{
    /**
     * 任务队列
     * 
     * @param copyTask
     * @return
     */
    boolean addCopyTask(CopyTask copyTask);
    
    int getQueueSize();
    
    /**
     * 首次启动干点事情
     */
    void firstStartWork();
    
    /**
     * 任务放入失败后的处理
     */
    void taskStartFailedCallback(CopyTask copyTask);
    
    /**
     * 是否在启动状态
     * 
     * @return
     */
    boolean isStart();
    
}
