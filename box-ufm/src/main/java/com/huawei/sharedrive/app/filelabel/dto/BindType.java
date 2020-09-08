package com.huawei.sharedrive.app.filelabel.dto;

import org.apache.commons.lang.StringUtils;

/**
 * 
 * Desc  : 標簽綁定類型
 * Author: 77235
 * Date	 : 2016年12月17日
 */
public enum BindType {
    OPERATIVE(1), /** 我的文件*/
    TEAMSPACE(2); /** 團隊空間*/
    
    private int bindingType;
    
    private BindType(int bindingType){
        this.bindingType = bindingType;
    }

    public int getBindingType() {
        return bindingType;
    }

    public static BindType get(String point){
        if (null != point){
            if (StringUtils.equalsIgnoreCase("teamspace", point.trim())){
                return TEAMSPACE;
            }
            
            if (StringUtils.equalsIgnoreCase("operative", point.trim())){
                return OPERATIVE;
            }
        }
        
        return null;
    }
}
