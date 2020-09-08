package com.huawei.sharedrive.app.files.dao;

import java.util.List;

import com.huawei.sharedrive.app.files.domain.WaitingDeleteObject;

import pw.cdmi.box.domain.Limit;

public interface WaitingDeleteObjectDAO
{
    /**
     * 创建对象删除任务
     * 
     * @param waitingDeleteObject
     */
    void create(WaitingDeleteObject waitingDeleteObject);
    
    /**
     * 删除对象删除任务
     * 
     * @param waitingDeleteObject
     * @return
     */
    int delete(WaitingDeleteObject waitingDeleteObject);
    
    /**
     * 获取对象删除任务
     * 
     * @param objectId
     * @return
     */
    WaitingDeleteObject get(String objectId);
    
    /**
     * 列举对象删除任务
     * 
     * @param limit
     * @return
     */
    List<WaitingDeleteObject> listWaitingDeleteObject(Limit limit);
    
}
