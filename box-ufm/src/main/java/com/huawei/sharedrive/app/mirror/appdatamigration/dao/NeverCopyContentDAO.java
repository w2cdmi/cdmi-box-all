package com.huawei.sharedrive.app.mirror.appdatamigration.dao;

import java.util.List;

import com.huawei.sharedrive.app.mirror.appdatamigration.domain.NeverCopyContent;

public interface NeverCopyContentDAO
{
    /**
     *创建一个无法复制的对象 
     * @param object
     */
    void create(NeverCopyContent object);
    
    /**
     * 获取该对象所有无法复制的对象信息
     * @param objectId
     * @return
     */
    List<NeverCopyContent> get(String  objectId);
    
    /**
     * 获取该对象所有无法复制的对象信息
     * @param md5
     * @param blockMD5
     * @param size
     * @return
     */
    List<NeverCopyContent> getByMD5(String  md5,String blockMD5,long size);
    
    /**
     * 删除对象
     * @param object
     */
    void delete(NeverCopyContent object);
    
    
}
