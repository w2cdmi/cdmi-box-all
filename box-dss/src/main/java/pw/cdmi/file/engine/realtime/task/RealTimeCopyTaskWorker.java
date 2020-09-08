/*
 * 版权声明(Copyright Notice)：
 *      Copyright(C) 2017-2017 聚数科技成都有限公司。保留所有权利。
 *      Copyright(C) 2017-2017 www.cdmi.pw Inc. All rights reserved. 
 * 
 *      警告：本内容仅限于聚数科技成都有限公司内部传阅，禁止外泄以及用于其他的商业目
 */
package pw.cdmi.file.engine.realtime.task;


import pw.cdmi.file.engine.realtime.domain.RealTimeCopyTask;
import pw.cdmi.file.engine.realtime.exception.RealTimeCopyTaskException;

public interface RealTimeCopyTaskWorker extends Runnable
{
    RealTimeCopyTask getRealTimeCopyTask();
    
    void setRealTimeCopyTask(RealTimeCopyTask realTimeCopyTask);
    /**
     *先获取文件整体的下载地址
     * @throws RealTimeCopyTaskException 
     * 
     */
    void beforeRealTimeCopyTask() throws RealTimeCopyTaskException;
    
    void successRealTimeCopyTask();
    
    void failedRealTimeCopyTask();
}
