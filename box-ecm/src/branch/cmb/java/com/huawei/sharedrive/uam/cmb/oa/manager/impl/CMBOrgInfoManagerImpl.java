package com.huawei.sharedrive.uam.cmb.oa.manager.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.huawei.sharedrive.uam.cmb.oa.domain.CMBOrgInfo;
import com.huawei.sharedrive.uam.cmb.oa.manager.CMBOrgInfoManager;
import com.huawei.sharedrive.uam.cmb.oa.service.CMBOrgInfoService;
import com.huawei.sharedrive.uam.cmb.oa.service.CMBOrgInfoTmpService;
import com.huawei.sharedrive.uam.cmb.oa.util.CMBOAUtil;

@Component
public class CMBOrgInfoManagerImpl implements CMBOrgInfoManager
{
    
    private static final Logger LOGGER = LoggerFactory.getLogger(CMBOrgInfoManagerImpl.class);
    
    @Autowired
    private CMBOrgInfoTmpService cMBOrgInfoTmpService;
    
    @Autowired
    private CMBOrgInfoService cMBOrgInfoService;
    
    @Override
    public void insertCMBOrgInfo(List<CMBOrgInfo> list)
    {
        cMBOrgInfoTmpService.deleteAll();
        if (null == list || list.size() < 1)
        {
            return;
        }
        for (CMBOrgInfo cmbOrgInfo : list)
        {
            try
            {
                cMBOrgInfoTmpService.insert(cmbOrgInfo);
            }
            catch (Exception e)
            {
                LOGGER.warn("insert tmpOrgInfo failed orgId:" + cmbOrgInfo.getOrgId() + " groupId:"
                    + cmbOrgInfo.getGroupId() + " groupName:" + cmbOrgInfo.getGroupName(), e);
            }
        }
        insertCMBOrgInfoList();
    }
    
    @Override
    public CMBOrgInfo getCMBOrgInfo(Integer orgId)
    {
        return cMBOrgInfoService.getById(orgId);
    }
    
    @Override
    public void getGroupIds(List<String> fatherGroupId, List<String> groupIds)
    {
        if (null == fatherGroupId || fatherGroupId.size() < 1)
        {
            return;
        }
        for (String groupId : fatherGroupId)
        {
            groupIds.add(groupId);
            List<String> groupIdList = cMBOrgInfoService.getByExistsFatherGroupId(groupId);
            if (null == groupIdList || groupIdList.size() < 1)
            {
                continue;
            }
            getGroupIds(groupIdList, groupIds);
        }
    }
    
    private void insertCMBOrgInfoList()
    {
        List<CMBOrgInfo> tmpList = cMBOrgInfoTmpService.getAll();
        if (null == tmpList || tmpList.size() < 1)
        {
            return;
        }
        for (CMBOrgInfo cmbOrgInfo : tmpList)
        {
            try
            {
                CMBOrgInfo selCMBOrgInfo = cMBOrgInfoService.getById(cmbOrgInfo.getOrgId());
                if (null != selCMBOrgInfo)
                {
                    boolean isMatchedValue = CMBOAUtil.compareCMBOrgInfo(cmbOrgInfo, selCMBOrgInfo);
                    if (!isMatchedValue)
                    {
                        cMBOrgInfoService.updateById(cmbOrgInfo);
                    }
                }
                else
                {
                    cMBOrgInfoService.insert(cmbOrgInfo);
                }
            }
            catch (Exception e)
            {
                LOGGER.warn("insert orgInfo failed orgId:" + cmbOrgInfo.getOrgId() + " groupId:"
                    + cmbOrgInfo.getGroupId() + " groupName:" + cmbOrgInfo.getGroupName(),
                    e);
            }
        }
    }
}
