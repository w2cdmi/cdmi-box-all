/*
 * Copyright Notice:
 *      Copyright  1998-2009, Huawei Technologies Co., Ltd.  ALL Rights Reserved.
 *
 *      Warning: This computer software sourcecode is protected by copyright law
 *      and international treaties. Unauthorized reproduction or distribution
 *      of this sourcecode, or any portion of it, may result in severe civil and
 *      criminal penalties, and will be prosecuted to the maximum extent
 *      possible under the law.
 */
package com.huawei.sharedrive.isystem.license.web;

import com.huawei.sharedrive.common.license.CseLicenseInfo;

/**
 * 
 * @author s90006125
 *
 */
public class LicenseImportResult
{
    private boolean success = false;
    private CseLicenseInfo licenseInfo;
    private String message;
    
    public boolean isSuccess()
    {
        return success;
    }
    public void setSuccess(boolean success)
    {
        this.success = success;
    }
    public CseLicenseInfo getLicenseInfo()
    {
        return licenseInfo;
    }
    public void setLicenseInfo(CseLicenseInfo licenseInfo)
    {
        this.licenseInfo = licenseInfo;
    }
    public String getMessage()
    {
        return message;
    }
    public void setMessage(String message)
    {
        this.message = message;
    }
}
