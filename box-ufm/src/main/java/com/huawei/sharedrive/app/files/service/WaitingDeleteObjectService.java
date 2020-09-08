package com.huawei.sharedrive.app.files.service;

import com.huawei.sharedrive.app.files.domain.WaitingDeleteObject;

public interface WaitingDeleteObjectService
{
    /**
     * @param waitingDeleteObject
     */
    void deleteObject(WaitingDeleteObject waitingDeleteObject);
}
