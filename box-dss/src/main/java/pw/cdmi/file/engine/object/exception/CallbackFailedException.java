/*
 * 版权声明(Copyright Notice)：
 *      Copyright(C) 2017-2017 聚数科技成都有限公司。保留所有权利。
 *      Copyright(C) 2017-2017 www.cdmi.pw Inc. All rights reserved. 
 * 
 *      警告：本内容仅限于聚数科技成都有限公司内部传阅，禁止外泄以及用于其他的商业目
 */ 
package pw.cdmi.file.engine.object.exception;

import org.springframework.http.HttpStatus;

import pw.cdmi.file.engine.core.exception.BusinessException;


/**
 * 回调失败异常
 * 
 * @author s90006125
 * 
 */
public class CallbackFailedException extends BusinessException
{
    private static final long serialVersionUID = -5042217836376030634L;
    
    private HttpStatus status;
    
    public CallbackFailedException(String message)
    {
        super("CallbackFailed", message);
    }
    
    public CallbackFailedException(HttpStatus status, String message)
    {
        super("CallbackFailed", message);
        this.status = status;
    }
    
    public HttpStatus getStatus()
    {
        return status;
    }
}
