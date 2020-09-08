package com.huawei.sharedrive.isystem.exception;

import org.springframework.http.HttpStatus;

public class ShaEncryptException extends BaseRunTimeException
{
    
    /**
     * 
     */
    private static final long serialVersionUID = -8385462547381801475L;
    
    public ShaEncryptException()
    {
        super(HttpStatus.OK, ErrorCode.SHA_ENCRYPT_ERROR.getCode(), ErrorCode.SHA_ENCRYPT_ERROR.getMessage());
    }
    
    public ShaEncryptException(Throwable e)
    {
        super(e, HttpStatus.OK, ErrorCode.SHA_ENCRYPT_ERROR.getCode(),
            ErrorCode.SHA_ENCRYPT_ERROR.getMessage());
    }
}
