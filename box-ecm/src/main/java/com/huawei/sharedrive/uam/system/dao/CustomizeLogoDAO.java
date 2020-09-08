package com.huawei.sharedrive.uam.system.dao;

import pw.cdmi.common.domain.CustomizeLogo;

public interface CustomizeLogoDAO
{
    CustomizeLogo get(int id);
    
    void update(CustomizeLogo customizeLogo);
    
    void insert(CustomizeLogo customizeLogo);
    
}