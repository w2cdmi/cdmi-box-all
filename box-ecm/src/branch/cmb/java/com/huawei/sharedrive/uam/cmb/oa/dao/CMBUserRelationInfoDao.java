package com.huawei.sharedrive.uam.cmb.oa.dao;

import java.util.List;

import com.huawei.sharedrive.uam.cmb.oa.domain.CMBSapUser;
import com.huawei.sharedrive.uam.cmb.oa.domain.CMBUserRelationInfo;

public interface CMBUserRelationInfoDao
{
    void insert(CMBUserRelationInfo cmbUserRelationInfo);
    
    void updateById(CMBUserRelationInfo cmbUserRelationInfo);
    
    CMBUserRelationInfo getById(Integer orgId, String userId);
    
    List<CMBUserRelationInfo> getByUserId(String userId);
    
    List<CMBUserRelationInfo> getByOrgId(int orgId);
    
    void updateNormolStatus();
    
    void updateUnNormolStatus();
    
    List<CMBSapUser> listCMBSapUserByGroupIds(List<String> list);
}
