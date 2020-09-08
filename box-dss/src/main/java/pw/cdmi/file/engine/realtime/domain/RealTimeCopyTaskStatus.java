/*
 * 版权声明(Copyright Notice)：
 *      Copyright(C) 2017-2017 聚数科技成都有限公司。保留所有权利。
 *      Copyright(C) 2017-2017 www.cdmi.pw Inc. All rights reserved. 
 * 
 *      警告：本内容仅限于聚数科技成都有限公司内部传阅，禁止外泄以及用于其他的商业目
 */
package pw.cdmi.file.engine.realtime.domain;

public enum RealTimeCopyTaskStatus
{
    /**
     * 待復制
     */
    WAITING(0),
    
    /**
     * 正在復制
     */
    RUNNING(1),
    
    /**
     * 復制成功
     */
    SUCCESS(2),
    
    /**
     * 復制失败
     */
    FAILED(3),
    
    /**
     * 回调成功
     */
    CALLBACK_SUCCESSED(4),
    
    /**
     * 回调失败
     */
    CALLBACK_FAILED(5);
    
    private int code = 0;
    
    private RealTimeCopyTaskStatus(int code)
    {
        this.code = code;
    }
    
    public int getCode()
    {
        return code;
    }
    
    public static RealTimeCopyTaskStatus parseStatus(int code)
    {
        for (RealTimeCopyTaskStatus status : RealTimeCopyTaskStatus.values())
        {
            if (code == status.getCode())
            {
                return status;
            }
        }
        
        return null;
    }
}
