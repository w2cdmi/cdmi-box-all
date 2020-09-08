package com.huawei.sharedrive.app.mirror.exception;

public enum CopyTaskErrorCode
{
    //taskID不存在
    TASK_ID_NOTFOUND(404,"TaskIDNotFound"),
    
    //参数错误
    PARAMTER_INVALID(400,"paramterInvalid"),
    
    
    //对象不存在
    OBJECT_NOTFOUND(404,"ObjectNotFound"),
    
    //DSS不可用
    DSS_UNAVAILABILITY(503,"DssUnavailability"),
    
    
    INTERNAL_SERVER_ERROR(500,"InternalServerError"),
    
    
    TASK_EXE_SUCCESSED(200,"ok"),
    
    
    //数据内容不一致,扩展的
    CONTENT_ERROR(700,"ContentError");
    
    private int errCode;
    private String msg;
    
  
    private CopyTaskErrorCode(int errCode,String msg)
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
