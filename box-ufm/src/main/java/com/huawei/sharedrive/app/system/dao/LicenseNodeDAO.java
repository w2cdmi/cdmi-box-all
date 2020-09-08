package com.huawei.sharedrive.app.system.dao;

import java.util.List;

import com.huawei.sharedrive.common.license.LicenseNode;

public interface LicenseNodeDAO
{
    
    /**
     * 根据ESN和nodeType获取license节点信息
     * @param esn
     * @param nodeType
     * @return
     */
    LicenseNode getLicenseNode(String esn, byte nodeType);
    
    /**
     * 获取所有的license节点列表
     * @return
     */
    List<LicenseNode> getLicenseNodeList();
    
    
    LicenseNode getCurrentLicenseNode(String esn, String licenseId);
    
    /**
     * 根据节点类型获取不包含本节点的已启用节点数
     * 
     * @param nodeType
     * @param esn
     * @return
     */
    
    /**
     * 根据节点类型获取不包含本节点的已启用节点数
     * 
     * @param nodeType
     * @param esn
     * @return
     */
    int getNormalCountWithoutMe(byte nodeType, String licenseId, String esn);
    
    /**
     * 保存license节点信息
     * @param licenseNode
     */
    void save(LicenseNode licenseNode);
    
    /**
     * 更新license节点信息
     * @param licenseNode
     * @return
     */
    int update(LicenseNode licenseNode);
    
}
