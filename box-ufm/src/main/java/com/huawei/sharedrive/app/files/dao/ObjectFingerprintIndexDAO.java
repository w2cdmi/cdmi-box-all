package com.huawei.sharedrive.app.files.dao;

import java.util.List;

import com.huawei.sharedrive.app.files.domain.ObjectFingerprintIndex;

public interface ObjectFingerprintIndexDAO
{
    /**
     *创建指纹索引对象
     * 
     * @param objFpIndex
     */
    void create(ObjectFingerprintIndex objFpIndex);
    
    /**
     * 删除指纹索引对象
     * 
     * @param objFpIndex
     * @return
     */
    int delete(ObjectFingerprintIndex objFpIndex);
    
    /**
     * 根据sha1删除指纹索引对象
     * 
     * @param sha1
     * @return
     */
    int deleteBySha1(String sha1);
    
    /**
     * 根据sha1获取指纹索引对象
     * 
     * @param sha1
     * @return
     */
    List<ObjectFingerprintIndex> getBySha1(String sha1);
    
    /**
     * 根据sha1和regionId获取指纹索引对象
     * 
     * @param sha1
     * @param regionId
     * @return
     */
    List<ObjectFingerprintIndex> getBySha1AndRegionID(String sha1, int regionId);
}
