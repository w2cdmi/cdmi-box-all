package com.huawei.sharedrive.app.files.dto;

import java.io.Serializable;

import org.springframework.http.HttpStatus;

/**
 * 
 * Desc  : 传输数据信息 
 * Author: 77235
 * Date	 : 2016年12月23日
 */
public class DataMigrationResponseDto implements Serializable{
    private static final long serialVersionUID = 1L;
    
    private String retCode = HttpStatus.INTERNAL_SERVER_ERROR.name();
    
    private String retMsg;
    
    private String errorCode;

    private long inodeId;
    
    private String fileName;

    public long getInodeId() {
        return inodeId;
    }

    public void setInodeId(long inodeId) {
        this.inodeId = inodeId;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getRetCode() {
        return retCode;
    }

    public void setRetCode(String retCode) {
        this.retCode = retCode;
    }

    public String getRetMsg() {
        return retMsg;
    }

    public void setRetMsg(String retMsg) {
        this.retMsg = retMsg;
    }

	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}
}
