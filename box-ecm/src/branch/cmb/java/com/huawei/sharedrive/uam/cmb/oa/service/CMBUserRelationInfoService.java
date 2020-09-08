package com.huawei.sharedrive.uam.cmb.oa.service;

import java.util.List;

import com.huawei.sharedrive.uam.cmb.oa.domain.CMBSapUser;
import com.huawei.sharedrive.uam.cmb.oa.domain.CMBUserRelationInfo;

public interface CMBUserRelationInfoService
{
    void insert(CMBUserRelationInfo cmbUserRelationInfo);
    
    void updateById(CMBUserRelationInfo cmbUserRelationInfo);
    
    void updateNormolStatus();
    
    void updateUnNormolStatus();
    
    CMBUserRelationInfo getById(Integer orgId, String userId);
    
    List<CMBUserRelationInfo> getByUserId(String userId);
    
    List<CMBUserRelationInfo> getByOrgId(int orgId);
    
    List<CMBSapUser> listCMBSapUserByGroupIds(List<String> list);
    
}
