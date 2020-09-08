package com.huawei.sharedrive.app.plugins.preview.dao;

import com.huawei.sharedrive.app.plugins.preview.domain.AccountWatermark;

public interface AccountWatermarkDao
{
    void replace(AccountWatermark watermark);
    
    AccountWatermark get(long accountId);
}
