package com.huawei.sharedrive.app.mirror.service;

import java.util.List;

import com.huawei.sharedrive.app.mirror.domain.UserMirrorStatisticInfo;

public interface UserMirrorStatisticInfoService
{
    /**
     * 
     * @param info
     */
    void create(UserMirrorStatisticInfo info);
    
    /**
     * 更新
     * @param info
     * @return
     */
    int update(UserMirrorStatisticInfo info);
    
    /**
     * 删除，通过ID
     * @param info
     * @return
     */
    int delete(UserMirrorStatisticInfo info);
    
    /**
     * 通过ID获取
     * @param info
     * @return
     */
    UserMirrorStatisticInfo get(UserMirrorStatisticInfo info);
    
    /**
     * 获取用户统计信息
     * @param info
     * @return
     */
    List<UserMirrorStatisticInfo> listStatisticByUserId(UserMirrorStatisticInfo info);
    
    /**
     * 获取最近一次的统计信息
     * @param info
     * @return
     */
    UserMirrorStatisticInfo getLastStatisticInfo(UserMirrorStatisticInfo info);
    
    /**
     * 获取任务总数
     * @return
     */
    long getCopyTaskTotal();
}
