package com.huawei.sharedrive.app.dataserver.dao;

import java.util.List;

import com.huawei.sharedrive.app.dataserver.domain.RegionNetworkDistance;

public interface RegionNetworkDistanceDAO
{
    
    /**
     * 创建
     * @param regionNetworkDistance
     */
    void create(RegionNetworkDistance regionNetworkDistance);
    
    /**
     * 更新
     * @param regionNetworkDistance
     */
    void update(RegionNetworkDistance regionNetworkDistance);
    
    /**
     * 删除
     * @param regionNetworkDistance
     */
    void delete(RegionNetworkDistance regionNetworkDistance);
    
    /**
     * 通过存储区域获取
     * @param regionNetworkDistance
     * @return
     */
    List<RegionNetworkDistance> getByRegion(RegionNetworkDistance regionNetworkDistance);
    
    /**
     * 通过名称获取
     * @param name
     * @return
     */
    RegionNetworkDistance get(String name);
    
    /**
     * 列举
     * @return
     */
    List<RegionNetworkDistance> lstRegionNetworkDistance();
    
}
