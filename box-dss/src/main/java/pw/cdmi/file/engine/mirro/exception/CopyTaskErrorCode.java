/**
 * 
 */
package pw.cdmi.file.engine.mirro.exception;

/**
 * @author w00186884
 * 
 */
public enum CopyTaskErrorCode
{
    DEFAULT(0, "Default"),
    // 成功
    TASK_SUCCESS(200, "Success"),
    
    // taskID不存在
    TASK_ID_NOTFOUND(404, "TaskIDNotFound"),
    
    // 参数错误
    PARAMTER_INVALID(400, "paramterInvalid"),
    
    // 对象不存在
    OBJECT_NOTFOUND(404, "ObjectNotFound"),
    
    // DSS不可用
    DSS_UNAVAILABILITY(500, "DssUnavailability"),
    
    INTERNAL_SERVER_ERROR(500, "InternalServerError"),
    
    // 数据内容不一致
    CONTENT_ERROR(700, "ContentError");
    
    private int errCode;
    
    private String msg;
    
    private CopyTaskErrorCode(int errCode, String msg)
    {
        this.errCode = errCode;
        this.msg = msg;
    }
    
    public int getErrCode()
    {
        return errCode;
    }
    
    public String getMsg()
    {
        return msg;
    }
}
