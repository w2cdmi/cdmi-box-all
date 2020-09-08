/**
 * 
 */
package com.huawei.sharedrive.isystem.system.service;

import pw.cdmi.common.domain.CustomizeLogo;

/**
 * @author s00108907
 * 
 */
public interface CustomizeLogoService
{
    
    /**
     * 获取当前的自定义设置
     * 
     * @return
     */
    CustomizeLogo getCustomize();
    
    /**
     * 更新自定义设置
     * 
     * @param customize
     */
    void updateCustomize(CustomizeLogo customize);
}
