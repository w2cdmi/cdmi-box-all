package com.huawei.sharedrive.uam.cmb.oa.manager;

import java.util.List;

import com.huawei.sharedrive.uam.cmb.oa.domain.CMBSapUser;
import com.huawei.sharedrive.uam.cmb.oa.domain.CMBUserRelationInfo;

public interface CMBUserRelationInfoManager
{
    void insertCMBUserRelationInfo(List<CMBUserRelationInfo> list);
    
    List<CMBUserRelationInfo> getByUserId(String userId);
    
    List<CMBSapUser> listCMBSapUserByGroupIds(List<String> list);
}
