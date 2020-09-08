package com.huawei.sharedrive.app.mirror.domain;

/**
 * 復制類型
 * @author c00287749
 *
 */
public enum CopyType
{
    //異步復制
    COPY_TYPE_RECOVERY(1),
    
    //就近訪問
    COPY_TYPE_NEAR(2),
    
    //用户个人数据迁移
    COPY_TYPE_USER_DATA_MIGRATION(3),
    
    //应用垮DSS数据迁移，保留老数据，但是不保留老数据对应的镜像
    COPY_TYPE_APP_MIGRATION_PERSISTED_OLD_DATA(4),
    
    //应用垮DSS数据迁移，不保留老数据，同时不保留老数据对应的镜像
    COPY_TYPE_APP_MIGRATION_CLEAR_OLD_DATA(5);
    
    private int copyType;
    
    CopyType(int copyType)
    {
        this.copyType =copyType;
    }

    public int getCopyType()
    {
        return copyType;
    }
}
