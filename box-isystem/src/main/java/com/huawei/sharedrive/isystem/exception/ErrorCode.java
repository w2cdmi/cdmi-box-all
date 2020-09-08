package com.huawei.sharedrive.isystem.exception;

/**
 * 
 * @author c00110381
 * 
 */
public enum ErrorCode
{
    /************************** 公共部分 start **************************/
    /** 签名错误 */
    LOGINUNAUTHORIZED("unauthorized", "Authentication fails, the user name or password is incorrect."), TOKENUNAUTHORIZED(
        "unauthorized", "Authentication fails, the token illegal or invalid."), BAD_REQUEST("bad_request",
        "The requested resource or the request parameter error."), FILES_CONFLICT("conflict",
        "This folder already exists with the same name file or folder."), USERLOCKED("UserLocked",
        "forbidden, the user is locked."),NOSUCHUSER("NoSuchUser", "This user does not exist."),
    
    MissingParameter("missing_parameter", "The request missing required parameters"), FORBIDDEN_OPER(
        "forbidden", "."), NO_SUCH_USER("no_such_user", "."), NO_SUCH_FOLDER("no_such_folder", "."), NO_SUCH_FILE(
        "no_such_file", "."), NO_SUCH_VERSION("no_such_version", "."), INVALID_PARAMTER("invalid_paramter",
        "."), TOO_MANY_REQUESTS("too_many_requests", "."), INTERNAL_SERVER_ERROR("internal_server_error", "."), METHOD_NOT_ALLOWED(
        "method_not_allowed", "."),
    
    /**
     * AES加解密错误
     */
    AES_ENCRYPT_ERROR("aes_error", ""),
    
    /**
     * SHA加解密错误
     */
    SHA_ENCRYPT_ERROR("sha_error", ""),
    
    /** 从网络读取数据失败（比如客户端上传文件时，服务端不能获取到整个文件内容） */
    NetworkException("Network exception",
        "Network exception, please try again this operation when the network is not so busy.");
    
    private String code;
    
    private String message;
    
    private ErrorCode(String code, String message)
    {
        this.code = code;
        this.message = message;
    }
    
    public String getCode()
    {
        return code;
    }
    
    public String getMessage()
    {
        return message;
    }
}
