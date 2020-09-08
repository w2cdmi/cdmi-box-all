package com.huawei.sharedrive.app.mirror.exception;

import com.huawei.sharedrive.app.dataserver.exception.BusinessException;

public class CopyTaskException extends BusinessException
{
    private static final long serialVersionUID = -1378947239455641149L;
    
    public CopyTaskException(CopyTaskErrorCode code)
    {
        super(code.getErrCode(), code.getMsg());
        
    }
}
