/*
 * 版权声明(Copyright Notice)：
 *      Copyright(C) 2017-2017 聚数科技成都有限公司。保留所有权利。
 *      Copyright(C) 2017-2017 www.cdmi.pw Inc. All rights reserved. 
 * 
 *      警告：本内容仅限于聚数科技成都有限公司内部传阅，禁止外泄以及用于其他的商业目
 */
package pw.cdmi.file.engine.realtime.exception;

import pw.cdmi.core.exception.CustomException;

public class RealTimeCopyTaskException extends CustomException
{

    /**
     * 
     */
    private static final long serialVersionUID = -3602808332698123978L;

    public RealTimeCopyTaskException()
    {
        super();
    }

    public RealTimeCopyTaskException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace)
    {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public RealTimeCopyTaskException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public RealTimeCopyTaskException(String message)
    {
        super(message);
    }

    public RealTimeCopyTaskException(Throwable cause)
    {
        super(cause);
    }
    
}
