package com.huawei.sharedrive.app.exception;

import org.springframework.http.HttpStatus;

public class ExceedGroupMaxMemberNumException extends BaseRunException
{
    private static final long serialVersionUID = -2342873742789247529L;
    
    public ExceedGroupMaxMemberNumException()
    {
        super(HttpStatus.PRECONDITION_FAILED, ErrorCode.EXCEED_MAX_GROUP_MEMBER_NUM.getCode(), ErrorCode.EXCEED_MAX_GROUP_MEMBER_NUM.getMessage());
    }
    
    public ExceedGroupMaxMemberNumException(String excepMessage)
    {
        super(HttpStatus.PRECONDITION_FAILED, ErrorCode.EXCEED_MAX_GROUP_MEMBER_NUM.getCode(), ErrorCode.EXCEED_MAX_GROUP_MEMBER_NUM.getMessage(),
            excepMessage);
    }
}

