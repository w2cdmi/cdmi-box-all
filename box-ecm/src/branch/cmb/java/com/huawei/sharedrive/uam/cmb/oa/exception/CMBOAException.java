package com.huawei.sharedrive.uam.cmb.oa.exception;

import org.springframework.http.HttpStatus;

import com.huawei.sharedrive.uam.exception.BaseRunException;
import com.huawei.sharedrive.uam.exception.ErrorCode;

public class CMBOAException extends BaseRunException
{
    private static final long serialVersionUID = 7713835782306654989L;
    
    public CMBOAException()
    {
        super(HttpStatus.INTERNAL_SERVER_ERROR, ErrorCode.INTERNAL_SERVER_ERROR.getCode(),
            ErrorCode.INTERNAL_SERVER_ERROR.getMessage());
    }
    
    public CMBOAException(String excepMessage)
    {
        super(HttpStatus.INTERNAL_SERVER_ERROR, ErrorCode.INTERNAL_SERVER_ERROR.getCode(),
            ErrorCode.INTERNAL_SERVER_ERROR.getMessage(), excepMessage);
    }
}
