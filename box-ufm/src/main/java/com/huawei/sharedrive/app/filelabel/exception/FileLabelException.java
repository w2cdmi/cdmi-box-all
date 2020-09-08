package com.huawei.sharedrive.app.filelabel.exception;

/**
 * 
 * Desc  : 文件标签异常类
 * Author: 77235
 * Date	 : 2016年11月28日
 */
public class FileLabelException extends RuntimeException {

    private static final long serialVersionUID = 1L;
    
    private String errorCode;

    public FileLabelException() {
        super();
    }

    public FileLabelException(String message, Throwable cause, boolean enableSuppression,
        boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public FileLabelException(String message, Throwable cause) {
        super(message, cause);
    }

    public FileLabelException(String message) {
        super(message);
    }

    public FileLabelException(Throwable cause) {
        super(cause);
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }
    
    public static void throwFilelabelException(String errMsg, Throwable th, String errorCode) {
        FileLabelException exception = new FileLabelException(errMsg, th);
        exception.setErrorCode(errorCode);
        
        throw exception;
    }
    
    public static void throwFilelabelException(Throwable th, String errorCode) {
        FileLabelException exception = new FileLabelException(th);
        exception.setErrorCode(errorCode);
        
        throw exception;
    }
    
    public static void throwFilelabelException(String errMsg, Throwable th) {
        throw new FileLabelException(errMsg, th);
    }
    
    public static void throwFilelabelException(Throwable th) {
        throw new FileLabelException(th);
    }
    
    public static void throwFilelabelException(String errMsg) {
        throw new FileLabelException(errMsg);
    }
}
