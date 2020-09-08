package com.huawei.sharedrive.app.plugins.preview.service;

import com.huawei.sharedrive.app.plugins.preview.domain.AccountWatermark;

public interface AccountWatermarkService
{
    
    AccountWatermark getWatermarkByAccountId(long accountId);
    
    void setWatermarkByAccountId(long accountId, byte[] data);
    
}