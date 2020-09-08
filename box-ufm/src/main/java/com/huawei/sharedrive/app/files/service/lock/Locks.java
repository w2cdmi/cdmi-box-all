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
package com.huawei.sharedrive.app.files.service.lock;

import com.huawei.sharedrive.app.utils.PropertiesUtils;

/**
 * 
 * @author s90006125
 *
 */
public class Locks
{
    public static final HandleLock DELETE_LOCK =
        new HandleLock("DeleteLock", Integer.parseInt(PropertiesUtils.getProperty("lock.delete", "15")));
    
    public static final HandleLock COPY_LOCK =
        new HandleLock("CopyLock", Integer.parseInt(PropertiesUtils.getProperty("lock.copy", "15")));
    
    public static final HandleLock MOVE_LOCK =
        new HandleLock("MoveLock", Integer.parseInt(PropertiesUtils.getProperty("lock.move", "15")));
    
    public static final HandleLock SYNCMETADATA_LOCK =
        new HandleLock("SyncMetadataLock", Integer.parseInt(PropertiesUtils.getProperty("lock.syncmetadata", "15")));
}
