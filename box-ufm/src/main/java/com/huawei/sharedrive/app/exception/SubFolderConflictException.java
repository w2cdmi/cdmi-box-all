package com.huawei.sharedrive.app.exception;

import org.springframework.http.HttpStatus;

/**
 * 子文件夹冲突异常
 * 
 * @author l90003768
 *
 */
public class SubFolderConflictException extends BaseRunException
{
    /**
     * 序列化号
     */
    private static final long serialVersionUID = 7976173906202442667L;

    public SubFolderConflictException()
    {
        super(HttpStatus.CONFLICT, ErrorCode.SUB_FOLDER_CONFILICT.getCode(), ErrorCode.SUB_FOLDER_CONFILICT.getMessage());
    }
    
    public SubFolderConflictException(String excepMessage)
    {
        super(HttpStatus.FORBIDDEN, ErrorCode.SUB_FOLDER_CONFILICT.getCode(), ErrorCode.SUB_FOLDER_CONFILICT.getMessage(), excepMessage);
    }
    
    public SubFolderConflictException(Throwable e)
    {
        super(e, HttpStatus.CONFLICT, ErrorCode.SUB_FOLDER_CONFILICT.getCode(),
            ErrorCode.SUB_FOLDER_CONFILICT.getMessage());
        
    }
    
}
