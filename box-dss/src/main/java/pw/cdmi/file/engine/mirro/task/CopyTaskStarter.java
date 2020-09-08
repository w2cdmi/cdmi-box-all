/**
 * 
 */
package pw.cdmi.file.engine.mirro.task;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pw.cdmi.common.job.JobExecuteContext;
import pw.cdmi.common.job.JobExecuteRecord;
import pw.cdmi.common.job.quartz.QuartzJobTask;
import pw.cdmi.file.engine.mirro.printer.MirrorPrinter;

/**
 * @author w00186884
 * 
 */
@Service("copyTaskStarter")
public class CopyTaskStarter extends QuartzJobTask
{

    
    @Autowired
    private CopyTaskRecevier copyTaskRecevier;
    
    @Autowired
    private CopyTaskDispatcher copyTaskDispatcher;
    
    public synchronized void start()
    {
        // 接受任务线程
        if (!copyTaskRecevier.isStart())
        {
            new Thread(copyTaskRecevier).start();
            MirrorPrinter.info("CopyTaskRecevier Thread start by Me.");
        }
        // 启动任务处理线程
        if (!copyTaskDispatcher.isStart())
        {
            new Thread(copyTaskDispatcher).start();
            MirrorPrinter.info("CopyTaskDispatcher Thread start by Me.");
        }
    }
    
    @Override
    public void doTask(JobExecuteContext context, JobExecuteRecord record)
    {
        this.start();
        
        if (!copyTaskRecevier.isStart() || !copyTaskDispatcher.isStart())
        {
            record.setOutput("The copyTaskRecevier or copyTaskDispatcher start failed. ");
            record.setSuccess(false);
        }
    }
}
