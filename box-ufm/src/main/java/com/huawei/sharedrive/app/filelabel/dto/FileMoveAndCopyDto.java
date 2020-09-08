package com.huawei.sharedrive.app.filelabel.dto;

/**
 * 
 * Desc  : 文件拷貝與移動參數封裝
 * Author: 77235
 * Date	 : 2016年12月17日
 */
public class FileMoveAndCopyDto {    
    private FileOperationType optType;
    
    private long optUserId;
    
    private long fromOwnerId;
    
    private long toOwnerId;
    
    private long formNodeId;
    
    private long destNodeId;
    
    private long enterpriseId;
    
    private BindType bindType;

    public long getOptUserId() {
        return optUserId;
    }

    public void setOptUserId(long optUserId) {
        this.optUserId = optUserId;
    }

    public long getFromOwnerId() {
        return fromOwnerId;
    }

    public void setFromOwnerId(long fromOwnerId) {
        this.fromOwnerId = fromOwnerId;
    }

    public long getToOwnerId() {
        return toOwnerId;
    }

    public void setToOwnerId(long toOwnerId) {
        this.toOwnerId = toOwnerId;
    }

    public long getFormNodeId() {
        return formNodeId;
    }

    public void setFormNodeId(long formNodeId) {
        this.formNodeId = formNodeId;
    }

    public long getDestNodeId() {
        return destNodeId;
    }

    public void setDestNodeId(long destNodeId) {
        this.destNodeId = destNodeId;
    }

    public FileOperationType getOptType() {
        return optType;
    }

    public void setOptType(FileOperationType optType) {
        this.optType = optType;
    }
    
    public long getEnterpriseId() {
        return enterpriseId;
    }

    public void setEnterpriseId(long enterpriseId) {
        this.enterpriseId = enterpriseId;
    }

    public BindType getBindType() {
        return bindType;
    }

    public void setBindType(BindType bindType) {
        this.bindType = bindType;
    }

    public enum FileOperationType{
        COPY, /** 拷貝*/
        MOVE; /** 移動*/
    }
}


