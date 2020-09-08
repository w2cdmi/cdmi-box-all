package com.huawei.sharedrive.isystem.license.dao;

import java.util.List;

import com.huawei.sharedrive.common.license.LicenseFile;


public interface LicenseFileDAO
{
    
    LicenseFile get(String id);
    
    void save(LicenseFile licenseFile);
    
    void updateStatusWithId(String id, byte status);
    
    void updateStatusWithoutId(String id, byte status);
    
    List<LicenseFile> getLicenseFileListByStatus(byte status);
    
    List<LicenseFile> getAllExceptCurrentFile(byte status);
    
    void deleteById(String id);
    
}
