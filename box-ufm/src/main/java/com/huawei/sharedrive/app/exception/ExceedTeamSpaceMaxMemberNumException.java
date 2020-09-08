package com.huawei.sharedrive.app.exception;

import org.springframework.http.HttpStatus;

public class ExceedTeamSpaceMaxMemberNumException extends BaseRunException
{
    private static final long serialVersionUID = -2342873742789247523L;
    
    public ExceedTeamSpaceMaxMemberNumException()
    {
        super(HttpStatus.PRECONDITION_FAILED, ErrorCode.EXCEED_MAX_TEAMSPACE_MEMBER_NUM.getCode(), ErrorCode.EXCEED_MAX_TEAMSPACE_MEMBER_NUM.getMessage());
    }
    
    public ExceedTeamSpaceMaxMemberNumException(String excepMessage)
    {
        super(HttpStatus.PRECONDITION_FAILED, ErrorCode.EXCEED_MAX_TEAMSPACE_MEMBER_NUM.getCode(), ErrorCode.EXCEED_MAX_TEAMSPACE_MEMBER_NUM.getMessage(),
            excepMessage);
    }
}
