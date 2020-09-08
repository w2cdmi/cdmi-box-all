package com.huawei.sharedrive.uam.exception;

public enum BusinessErrorCode
{
    BadRequestException(400), MissingParameterException(400), NotFoundException(404), AlreadyExistException(
        409), NotAcceptableException(406), PreconditionFailedException(412),
    
    INTERNAL_SERVER_ERROR(500);
    
    private int code;
    
    private BusinessErrorCode(int code)
    {
        this.code = code;
    }
    
    public int getCode()
    {
        return code;
    }
}
