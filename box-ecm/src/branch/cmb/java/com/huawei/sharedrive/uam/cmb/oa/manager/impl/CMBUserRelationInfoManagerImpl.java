package com.huawei.sharedrive.uam.cmb.oa.manager.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.huawei.sharedrive.uam.cmb.oa.domain.CMBSapUser;
import com.huawei.sharedrive.uam.cmb.oa.domain.CMBUserRelationInfo;
import com.huawei.sharedrive.uam.cmb.oa.manager.CMBUserRelationInfoManager;
import com.huawei.sharedrive.uam.cmb.oa.service.CMBUserRelationInfoService;
import com.huawei.sharedrive.uam.cmb.oa.service.CMBUserRelationInfoTmpService;
import com.huawei.sharedrive.uam.cmb.oa.util.CMBOAUtil;

@Component
public class CMBUserRelationInfoManagerImpl implements CMBUserRelationInfoManager
{
    private static final Logger LOGGER = LoggerFactory.getLogger(CMBUserRelationInfoManagerImpl.class);
    
    @Autowired
    private CMBUserRelationInfoTmpService cMBUserRelationInfoTmpService;
    
    @Autowired
    private CMBUserRelationInfoService cMBUserRelationInfoService;
    
    @Override
    public void insertCMBUserRelationInfo(List<CMBUserRelationInfo> list)
    {
        cMBUserRelationInfoTmpService.deleteAll();
        if (null == list || list.size() < 1)
        {
            return;
        }
        for (CMBUserRelationInfo cmbUserRelationInfo : list)
        {
            try
            {
                cMBUserRelationInfoTmpService.insert(cmbUserRelationInfo);
            }
            catch (Exception e)
            {
                LOGGER.warn("insert tmpCmbUserRelationInfo failed orgId:" + cmbUserRelationInfo.getOrgId()
                    + " groupId:" + cmbUserRelationInfo.getGroupId() + " userId:"
                    + cmbUserRelationInfo.getUserId() + " name:" + cmbUserRelationInfo.getName(),
                    e);
            }
        }
        cMBUserRelationInfoService.updateUnNormolStatus();
        cMBUserRelationInfoService.updateNormolStatus();
        insertCMBUserRelationInfoList(list);
    }
    
    @Override
    public List<CMBUserRelationInfo> getByUserId(String userId)
    {
        return cMBUserRelationInfoService.getByUserId(userId);
    }
    
    private void insertCMBUserRelationInfoList(List<CMBUserRelationInfo> cmblist)
    {
        List<CMBUserRelationInfo> tmpList = cMBUserRelationInfoTmpService.getAll();
        if (null == tmpList || tmpList.size() < 1)
        {
            return;
        }
        for (CMBUserRelationInfo cmbUserRelationInfo : tmpList)
        {
            try
            {
                CMBUserRelationInfo selCMBUserRelationInfo = cMBUserRelationInfoService.getById(cmbUserRelationInfo.getOrgId(),
                    cmbUserRelationInfo.getUserId());
                if (null != selCMBUserRelationInfo)
                {
                    boolean isMatchedValue = CMBOAUtil.compareUserRelation(selCMBUserRelationInfo,
                        cmbUserRelationInfo);
                    if (!isMatchedValue)
                    {
                        cMBUserRelationInfoService.updateById(cmbUserRelationInfo);
                    }
                }
                else
                {
                    cMBUserRelationInfoService.insert(cmbUserRelationInfo);
                }
            }
            catch (Exception e)
            {
                LOGGER.warn("insert cmbUserRelationInfo failed orgId:" + cmbUserRelationInfo.getOrgId()
                    + " groupId:" + cmbUserRelationInfo.getGroupId() + " userId:"
                    + cmbUserRelationInfo.getUserId() + " name:" + cmbUserRelationInfo.getName(),
                    e);
            }
        }
        
    }
    
    @Override
    public List<CMBSapUser> listCMBSapUserByGroupIds(List<String> list)
    {
        return cMBUserRelationInfoService.listCMBSapUserByGroupIds(list);
    }
}
