/* 
 * 版权声明(Copyright Notice)：
 *      Copyright(C) 2017-2017 聚数科技成都有限公司。保留所有权利。
 *      Copyright(C) 2017-2017 www.cdmi.pw Inc. All rights reserved. 
 * 
 *      警告：本内容仅限于聚数科技成都有限公司内部传阅，禁止外泄以及用于其他的商业目
 */ 
package pw.cdmi.file.engine.core.exception.errorcode;

/**
 * 
 * @author s90006125
 * 
 */
public enum ErrorCode
{
    /** 对象已经存在 */
    ObjectAlreadyExistException("ObjectAlreadyExist", "ObjectAlreadyExistException"),
    /** 对象不存在 */
    ObjectNotFoundException("ObjectNotFound", "ObjectNotFoundException"),
    /** 前置条件不满足 */
    PreconditionFailedException("PreconditionFailed", "PreconditionFailedException"),
    /** 服务端不支持的请求 */
    NotAcceptableException("NotAcceptable", "NotAcceptableException"),
    /** 服务端不支持的请求 */
    BadRequestException("BadRequest", "BadRequestException"),
    /** 参数错误 */
    InvalidParameterException("InvalidParameter", "InvalidParameterException"),
    /**
     * license无效
     */
    LicenseException("LicenseInvalid", "The license is invalid."),
    /** 参数缺失 */
    MissingParameterException("MissingParameter", "MissingParameterException");
    
    private String code;
    
    private String message;
    
    private ErrorCode(String code, String message)
    {
        this.code = code;
        this.message = message;
    }
    
    public String getCode()
    {
        return code;
    }
    
    public String getMessage()
    {
        return message;
    }
}
