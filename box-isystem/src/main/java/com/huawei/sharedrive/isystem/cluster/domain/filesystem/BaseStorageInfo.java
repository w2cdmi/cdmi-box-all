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
package com.huawei.sharedrive.isystem.cluster.domain.filesystem;

import java.io.Serializable;

import com.huawei.sharedrive.thrift.filesystem.StorageInfo;

/**
 * 
 * @author s90006125
 *
 */
public abstract class BaseStorageInfo implements Serializable
{
    private static final long serialVersionUID = 2371613889035197510L;
    
    /** 存储ID */
    private String fsId;
    
    /** dc id */
    private int dcId;

    private boolean writeAlbe = true;
    
    private boolean available = true;
    
    // 状态：未启用 -- 0 启用 --1 停用---2
    private int status;
    
    public BaseStorageInfo()
    {
    }
    
    public BaseStorageInfo(StorageInfo storageInfo)
    {
        this.fsId = storageInfo.getId();
        this.writeAlbe = storageInfo.isWriteAlbe();
        this.available = storageInfo.isAvailAble();
        this.status = storageInfo.getStatus();
    }
    
    public String getFsId()
    {
        return fsId;
    }

    public void setFsId(String fsId)
    {
        this.fsId = fsId;
    }

    public int getDcId()
    {
        return dcId;
    }

    public void setDcId(int dcId)
    {
        this.dcId = dcId;
    }

    public boolean isWriteAlbe()
    {
        return writeAlbe;
    }

    public void setWriteAlbe(boolean writeAlbe)
    {
        this.writeAlbe = writeAlbe;
    }

    public boolean isAvailAble()
    {
        return available;
    }

    public void setAvailAble(boolean isAvailAble)
    {
        this.available = isAvailAble;
    }

    public int getStatus()
    {
        return status;
    }

    public void setStatus(int status)
    {
        this.status = status;
    }

    public abstract String getFsType();
}
