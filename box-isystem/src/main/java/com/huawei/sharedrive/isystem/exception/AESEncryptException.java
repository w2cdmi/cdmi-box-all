package com.huawei.sharedrive.isystem.exception;

import org.springframework.http.HttpStatus;

public class AESEncryptException extends BaseRunTimeException
{
    
    /**
     * 
     */
    private static final long serialVersionUID = -8385462547381801475L;
    
    public AESEncryptException()
    {
        super(HttpStatus.OK, ErrorCode.AES_ENCRYPT_ERROR.getCode(), ErrorCode.AES_ENCRYPT_ERROR.getMessage());
    }
    
    public AESEncryptException(Throwable e)
    {
        super(e, HttpStatus.OK, ErrorCode.AES_ENCRYPT_ERROR.getCode(),
            ErrorCode.AES_ENCRYPT_ERROR.getMessage());
    }
}
