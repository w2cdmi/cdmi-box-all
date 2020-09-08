/**
 * 
 */
package com.huawei.sharedrive.isystem.system.dao;

import pw.cdmi.common.domain.CustomizeLogo;

/**
 * @author d00199602
 * 
 */
public interface CustomizeLogoDAO
{
    CustomizeLogo get(int id);
    
    void update(CustomizeLogo customizeLogo);
    
}