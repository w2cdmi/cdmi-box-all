package com.huawei.sharedrive.uam.cmb.oa.service;

import java.util.List;

import org.datacontract.schemas._2004._07.cmbchina_yst.OrgInfo;
import org.datacontract.schemas._2004._07.cmbchina_yst.OrgUserRelationInfo;
import org.datacontract.schemas._2004._07.cmbchina_yst.SapUser;

import com.huawei.sharedrive.uam.cmb.oa.domain.CMBOrgInfo;
import com.huawei.sharedrive.uam.cmb.oa.domain.CMBSapUser;
import com.huawei.sharedrive.uam.cmb.oa.domain.CMBUserRelationInfo;

public interface ConvertFromWsdl
{
    List<CMBOrgInfo> convertToCMBOrgInfo(List<OrgInfo> infos);
    
    List<CMBSapUser> convertToCMBSapUser(List<SapUser> users);
    
    List<CMBUserRelationInfo> convertToCMBUserRelationInfo(List<OrgUserRelationInfo> orgUserRelationInfos);
}
