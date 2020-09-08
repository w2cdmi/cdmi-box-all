package com.huawei.sharedrive.app.filelabel.dto;

import org.springframework.http.HttpStatus;

import com.huawei.sharedrive.app.filelabel.domain.BaseFileLabelInfo;

/**
 * 
 * Desc  : 单实例响应
 * Author: 77235
 * Date	 : 2016年12月7日
 */
public class EntityFilelabelResponseDto extends BaseFilelabelResponseDto {
    private static final long serialVersionUID = 1L;
    
    private BaseFileLabelInfo filelabelInfo;

    public BaseFileLabelInfo getFilelabelInfo() {
        return filelabelInfo;
    }

    public void setFilelabelInfo(BaseFileLabelInfo filelabelInfo) {
        this.filelabelInfo = filelabelInfo;
    }

    public EntityFilelabelResponseDto() {
        super();
    }
    
    public EntityFilelabelResponseDto(HttpStatus status) {
        super(status);
    }
}
