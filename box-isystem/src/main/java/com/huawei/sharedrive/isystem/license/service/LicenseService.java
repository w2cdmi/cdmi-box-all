package com.huawei.sharedrive.isystem.license.service;

import java.io.IOException;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.huawei.sharedrive.common.license.CseLicenseException;
import com.huawei.sharedrive.common.license.CseLicenseInfo;
import com.huawei.sharedrive.common.license.LicenseFile;
import com.huawei.sharedrive.common.license.LicenseNode;
import com.huawei.sharedrive.isystem.license.LicenseCompareException;

public interface LicenseService
{
    byte STATUS_WAIT_CONFIRM = 1;
    
    byte STATUS_CURRENT = 2;
    
    byte STATUS_PAST = 3;
    
    int MAX_FILES = 5;
    
    void checkDifferenceWithCurrent(CseLicenseInfo newLicenseInfo) throws LicenseCompareException;
    
    LicenseFile getCurrentLicenseFile();
    
    CseLicenseInfo getCseLicenseInfo(MultipartFile mulFile, String fileName) throws CseLicenseException,
        IOException;
    
    void saveToDb(CseLicenseInfo licenseInfo, String uuid, long optId, byte licenseStatus) throws IOException;
    
    void confirmLicense(String licenseUuid);
    
    CseLicenseInfo getCurrentLicenseInfo() throws IOException, CseLicenseException;
    
    List<LicenseNode> getLienseNode();
    
}
