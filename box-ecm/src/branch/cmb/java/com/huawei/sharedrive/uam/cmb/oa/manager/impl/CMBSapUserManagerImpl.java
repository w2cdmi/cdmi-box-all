package com.huawei.sharedrive.uam.cmb.oa.manager.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.huawei.sharedrive.uam.cmb.oa.domain.CMBSapUser;
import com.huawei.sharedrive.uam.cmb.oa.manager.CMBSapUserManager;
import com.huawei.sharedrive.uam.cmb.oa.service.CMBSapUserService;
import com.huawei.sharedrive.uam.cmb.oa.service.CMBSapUserTmpService;
import com.huawei.sharedrive.uam.cmb.oa.util.CMBOAUtil;

@Component
public class CMBSapUserManagerImpl implements CMBSapUserManager
{
    private static final Logger LOGGER = LoggerFactory.getLogger(CMBSapUserManagerImpl.class);
    
    @Autowired
    private CMBSapUserTmpService cMBSapUserTmpService;
    
    @Autowired
    private CMBSapUserService cMBSapUserService;
    
    @Override
    public void insertCMBSapUser(List<CMBSapUser> list)
    {
        cMBSapUserTmpService.deleteAll();
        if (null == list || list.size() < 1)
        {
            return;
        }
        for (CMBSapUser sapUser : list)
        {
            try
            {
                cMBSapUserTmpService.insert(sapUser);
            }
            catch (Exception e)
            {
                LOGGER.warn("insert tmpSapUser failed sapId:" + sapUser.getSapId() + " name:"
                    + sapUser.getName() + " email:" + sapUser.getEmail(),
                    e);
            }
        }
        insertCMBSapUserList();
    }
    
    @Override
    public List<CMBSapUser> getAll()
    {
        return cMBSapUserService.getAll();
    }
    
    private void insertCMBSapUserList()
    {
        List<CMBSapUser> tmpList = cMBSapUserTmpService.getAll();
        if (null == tmpList || tmpList.size() < 1)
        {
            return;
        }
        for (CMBSapUser cmbSapUser : tmpList)
        {
            try
            {
                CMBSapUser selCMBSapUser = cMBSapUserService.getById(cmbSapUser.getSapId());
                if (null != selCMBSapUser)
                {
                    boolean isMatchedValue = CMBOAUtil.compareCMBSapUser(cmbSapUser, selCMBSapUser);
                    if (!isMatchedValue)
                    {
                        cMBSapUserService.updateById(cmbSapUser);
                    }
                }
                else
                {
                    cMBSapUserService.insert(cmbSapUser);
                }
            }
            catch (Exception e)
            {
                LOGGER.warn("insert sapUser failed sapId:" + cmbSapUser.getSapId() + " name:"
                    + cmbSapUser.getName() + " email:" + cmbSapUser.getEmail(),
                    e);
            }
        }
    }
}
