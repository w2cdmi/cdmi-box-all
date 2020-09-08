/**
 * 
 */
package com.huawei.sharedrive.app.system.service;


import com.huawei.sharedrive.common.license.CseLicenseInfo;
import com.huawei.sharedrive.common.license.LicenseNode;

/**
 * @author l90003768
 * 
 */
public interface LicenseService
{
    /**
     * 上报AC节点license状态
     * @param cseLicenseInfo
     */
    void reportAcLicenseNode(LicenseNode licenseNode, CseLicenseInfo licenseInfo);
    
    void loadLastestLicense();
    
}
