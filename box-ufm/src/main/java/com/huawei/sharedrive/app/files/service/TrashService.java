/**
 * 
 */
package com.huawei.sharedrive.app.files.service;

import com.huawei.sharedrive.app.core.domain.OrderV1;
import com.huawei.sharedrive.app.exception.BaseRunException;
import com.huawei.sharedrive.app.files.domain.FileINodesList;
import com.huawei.sharedrive.app.oauth2.domain.UserToken;

import pw.cdmi.box.domain.Limit;

public interface TrashService
{
    /**
     * 列举回收站，支持分页
     * 
     * @param user
     * @param ownerId
     * @param order
     * @param limit
     * @return
     * @throws BaseRunException
     */
    FileINodesList listTrashItems(UserToken user, long ownerId, OrderV1 order, Limit limit)
        throws BaseRunException;
}
