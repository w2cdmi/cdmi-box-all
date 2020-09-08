/**
 * 
 */
package pw.cdmi.file.engine.mirro.domain;

import pw.cdmi.file.engine.mirro.exception.CopyTaskErrorCode;

/**
 * @author w00186884
 * 
 */
public enum CopyTaskStatus
{
    /**
     * 初始入库
     */
    INPUT(0),
    
    /**
     * 正在执行
     */
    RUNNING(1),
    
    /**
     * 执行成功，但未回调
     */
    SUCCESS(2),
    
    /**
     * 执行失败
     */
    FAILED(3),
    
    /**
     * 暂停状态
     */
    PAUSE(4),
    
    /**
     * 未执行成功，任务已经不存在了
     */
    NOTEXIST(5),
    
    /**
     * 回调成功
     */
    CALLBACKSUCCESS(8),
    
    /**
     * 回调失败
     */
    CALLBACKFAILED(9),

    /**
     * 回调失败,内容错误，需要重置任务状态
     */
    CALLBACKCONTENTFAILED(CopyTaskErrorCode.CONTENT_ERROR.getErrCode());
    
    private int code = 0;
    
    private CopyTaskStatus(int code)
    {
        this.code = code;
    }
    
    public int getCode()
    {
        return code;
    }
    
    public static CopyTaskStatus parseStatus(int code)
    {
        for (CopyTaskStatus status : CopyTaskStatus.values())
        {
            if (code == status.getCode())
            {
                return status;
            }
        }
        
        return null;
    }
}
