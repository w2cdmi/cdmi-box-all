package com.huawei.sharedrive.app.files.dao;

import java.util.List;

import com.huawei.sharedrive.app.files.domain.ObjectReference;

import pw.cdmi.box.domain.Limit;

public interface ObjectReferenceDAO
{
    /**
     * 创建对象引用
     * 
     * @param objRef
     */
    void create(ObjectReference objRef);
    
    /**
     * 减少一个引用计数
     * 
     * @param objRef
     */
    int decreaseRefCount(ObjectReference objRef);
    
    /**
     * 删除引用计数
     * 
     * @param objRef
     * @return
     */
    int deleteCheckRef(ObjectReference objRef);
    
    /**
     * 获取对象引用
     * 
     * @param objectId
     * @return
     */
    ObjectReference get(String objectId);
    
    /**
     * 获取对象当前的安全标识
     * 
     * @param objectId
     * @return
     */
    Integer getSecurityLabelForUpdate(String objectId);
    
    /**
     * 新增一个引用计数
     * 
     * @param objRef
     */
    int increaseRefCount(ObjectReference objRef);
    
    /**
     * 列举需要彻底删除的对象，只有对象的引用计数为0时才能被删除
     * 
     * @param userdbNumber
     * @param tableNumber
     * @param limit
     * @return
     */
    List<ObjectReference> lstNeedDeleteObjects(int userdbNumber, int tableNumber, Limit limit);
    
    /**
     * 更新对象指纹和大小
     * 
     * @param id
     * @param size
     * @param sha1
     * @param blockMD5
     * @return
     */
    int updateFingerprintAndSize(String id, long size, String sha1, String blockMD5);
    
    /**
     * 更新删除时间
     * 
     * @param objRef
     */
    void updateLastDeleteTime(ObjectReference objRef);
    
    /**
     * 更新文件安全标识
     * 
     * @param securityLabel
     * @param securityVersion
     * @param objectId
     * @return
     */
    int updateSecurityLabel(int securityLabel, String securityVersion, String objectId);
    
}
