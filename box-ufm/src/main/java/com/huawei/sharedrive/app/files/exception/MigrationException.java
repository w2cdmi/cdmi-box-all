package com.huawei.sharedrive.app.files.exception;

public class MigrationException extends RuntimeException{

    private static final long serialVersionUID = 1L;
    
    private String errorCode;

    public MigrationException() {
        super();
    }

    public MigrationException(String message, Throwable cause, boolean enableSuppression,
        boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public MigrationException(String message, Throwable cause) {
        super(message, cause);
    }

    public MigrationException(String message) {
        super(message);
    }

    public MigrationException(Throwable cause) {
        super(cause);
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }
    
    public static void throwMigrationException(String errMsg, Throwable th, String errorCode) {
        MigrationException exception = new MigrationException(errMsg, th);
        exception.setErrorCode(errorCode);
        
        throw exception;
    }
    
    public static void throwMigrationException(Throwable th, String errorCode) {
        MigrationException exception = new MigrationException(th);
        exception.setErrorCode(errorCode);
        
        throw exception;
    }
    
    public static void throwMigrationException(String errMsg, Throwable th) {
        throw new MigrationException(errMsg, th);
    }
    
    public static void throwMigrationException(Throwable th) {
        throw new MigrationException(th);
    }
    
    public static void throwMigrationException(String errMsg) {
        throw new MigrationException(errMsg);
    }
}
