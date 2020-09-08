/*
 * 版权声明(Copyright Notice)：
 *      Copyright(C) 2017-2017 聚数科技成都有限公司。保留所有权利。
 *      Copyright(C) 2017-2017 www.cdmi.pw Inc. All rights reserved. 
 * 
 *      警告：本内容仅限于聚数科技成都有限公司内部传阅，禁止外泄以及用于其他的商业目
 */
package pw.cdmi.file.engine.realtime.exception;

public enum RealTimeCopyTaskError
{
DEFAULT(0, "Default"),
    
    TASK_SUCCESS(200, "Success"),
    
    PARAMTER_INVALID(400, "ParamterInvalid"),
    
    TASK_ID_NOT_FOUND(404, "Task Id Not Found"),
    
    SOURCE_OBJECT_NOT_FOUND(406, "Object Not Found"),
    
    INTERNAL_SERVER_ERROR(500, "InternalServerError"),
    
    DSS_UNAVAILABILITY(501, "DssUnavailability"),
    
    FAIL_MANY_TIME(502, "task failed many times"),
    
    RE_CAL_MD5_FAILED(600, "Re Cal MD5 Failed"),
    
    CONTENT_ERROR(700, "Content Error");
    
    private int errorCode;
    
    public static final int MD5_LENGTH = 32;
    
    private String msg;
    
    private RealTimeCopyTaskError(int errorCode, String msg)
    {
        this.errorCode = errorCode;
        this.msg = msg;
    }
    
    public int getErrorCode()
    {
        return errorCode;
    }
    
    public String getMsg()
    {
        return msg;
    }
}
