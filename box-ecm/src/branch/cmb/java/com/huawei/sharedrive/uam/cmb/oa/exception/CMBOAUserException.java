package com.huawei.sharedrive.uam.cmb.oa.exception;

import org.springframework.http.HttpStatus;

import com.huawei.sharedrive.uam.exception.BaseRunException;
import com.huawei.sharedrive.uam.exception.ErrorCode;

public class CMBOAUserException extends BaseRunException
{
    
    private static final long serialVersionUID = 7232270481835043949L;
    
    public CMBOAUserException()
    {
        super(HttpStatus.INTERNAL_SERVER_ERROR, ErrorCode.INTERNAL_SERVER_ERROR.getCode(),
            ErrorCode.INTERNAL_SERVER_ERROR.getMessage());
    }
    
    public CMBOAUserException(String excepMessage)
    {
        super(HttpStatus.INTERNAL_SERVER_ERROR, ErrorCode.INTERNAL_SERVER_ERROR.getCode(),
            ErrorCode.INTERNAL_SERVER_ERROR.getMessage(), excepMessage);
    }
}
