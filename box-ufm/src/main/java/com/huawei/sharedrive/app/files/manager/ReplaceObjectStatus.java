package com.huawei.sharedrive.app.files.manager;

public enum ReplaceObjectStatus
{
    FINISH_STATUS(0), 
    
    SRC_NOT_EXIST_STATUS(1),
    
    INNER_OPER_FAILED_STATUS(2);
    
    private int status;
    
    ReplaceObjectStatus(int status)
    {
        this.status = status;
    }
}
