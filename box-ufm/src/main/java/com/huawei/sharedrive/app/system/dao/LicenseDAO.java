package com.huawei.sharedrive.app.system.dao;

import java.util.List;

import com.huawei.sharedrive.common.license.LicenseFile;

public interface LicenseDAO
{
    LicenseFile get(String id);
    
    List<LicenseFile> getLicenseFileListByStatus(byte status);
    
    LicenseFile getLastest();
}
