/* 
 * 版权声明(Copyright Notice)：
 *      Copyright(C) 2017-2017 聚数科技成都有限公司。保留所有权利。
 *      Copyright(C) 2017-2017 www.cdmi.pw Inc. All rights reserved. 
 * 
 *      警告：本内容仅限于聚数科技成都有限公司内部传阅，禁止外泄以及用于其他的商业目
 */ 
package pw.cdmi.file.engine.core.exception;

import pw.cdmi.file.engine.core.exception.errorcode.ErrorCode;

/**
 * 
 * @author s90006125
 * 
 */
public abstract class BusinessException extends RuntimeException
{
    private static final long serialVersionUID = 2649215475695345446L;
    
    private String code;
    
    public BusinessException(ErrorCode errorCode)
    {
        this(errorCode.getCode(), errorCode.getMessage());
    }
    
    public BusinessException(String code, String message)
    {
        super(message);
        this.code = code;
    }
    
    public BusinessException(String code, String message, Throwable cause)
    {
        super(message, cause);
        this.code = code;
    }
    
    public String getCode()
    {
        return code;
    }
    
    public void setCode(String code)
    {
        this.code = code;
    }
}
