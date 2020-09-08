package com.huawei.sharedrive.app.filelabel.dto;

import java.io.Serializable;

import org.springframework.http.HttpStatus;

/**
 * 
 * Desc  : 标签操作返回基本信息
 * Author: 77235
 * Date	 : 2016年12月7日
 */
public class BaseFilelabelResponseDto implements Serializable{
    
    private static final long serialVersionUID = 1L;

    private transient HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
    
    private String errorCode;

    public BaseFilelabelResponseDto() {
        super();
    }

    public BaseFilelabelResponseDto(HttpStatus status) {
        super();
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public void setStatus(HttpStatus status) {
        this.status = status;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }    

}
