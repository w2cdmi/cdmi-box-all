package com.huawei.sharedrive.app.filelabel.domain;

import java.io.Serializable;
/**
 * 
 * Desc  : 基本信息
 * Author: 77235
 * Date	 : 2016年12月2日
 */
public class BaseFileLabelInfo implements Serializable {
    private static final long serialVersionUID = 1L;
    /** 主键 */
    private long id;
    /** 标签名称 */
    private String labelName;
    
    public long getId() {
        return id;
    }
    
    public void setId(long id) {
        this.id = id;
    }
    
    public String getLabelName() {
        return labelName;
    }
    
    public void setLabelName(String labelName) {
        this.labelName = labelName;
    }

    public BaseFileLabelInfo(long id, String labelName) {
        super();
        this.id = id;
        this.labelName = labelName;
    }

    public BaseFileLabelInfo() {
        super();
    }
}
