package com.huawei.sharedrive.isystem.mirror.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.huawei.sharedrive.isystem.cluster.dao.DCDao;
import com.huawei.sharedrive.isystem.cluster.dao.RegionDao;
import com.huawei.sharedrive.isystem.cluster.domain.DataCenter;
import com.huawei.sharedrive.isystem.cluster.domain.Region;
import com.huawei.sharedrive.isystem.exception.BusinessException;
import com.huawei.sharedrive.isystem.mirror.dao.CopyPolicyDAO;
import com.huawei.sharedrive.isystem.mirror.dao.CopyPolicyUserConfigDAO;
import com.huawei.sharedrive.isystem.mirror.domain.CopyPolicy;
import com.huawei.sharedrive.isystem.mirror.domain.CopyPolicySiteInfo;
import com.huawei.sharedrive.isystem.mirror.domain.CopyPolicyUserConfig;
import com.huawei.sharedrive.isystem.mirror.service.CopyPolicyService;
import com.huawei.sharedrive.isystem.plugin.domain.DCTreeNode;

@Service("copyPolicyService")
public class CopyPolicyServiceImpl implements CopyPolicyService
{
    
    @Autowired
    private CopyPolicyUserConfigDAO copyPolicyUserConfigDAO;
    
    @Autowired
    private CopyPolicyDAO copyPolicyDAO;
    
    @Autowired
    private DCDao dcDao;
    
    @Autowired
    private RegionDao regionDao;
    
    @Override
    public List<CopyPolicy> listCopyPolicy()
    {
        
        List<CopyPolicy> lstPolicy = copyPolicyDAO.listCopyPolicy();
        
        if (null == lstPolicy || lstPolicy.isEmpty())
        {
            return lstPolicy;
        }
        
        CopyPolicyUserConfig userConfig = null;
        for (CopyPolicy policy : lstPolicy)
        {
            userConfig = copyPolicyUserConfigDAO.getCopyPolicyUserConfig(policy.getId(),
                CopyPolicyUserConfig.USER_TYPE_TEAMSPACE,
                CopyPolicyUserConfig.DEFAULT_USER_ID);
            if (null == userConfig)
            {
                userConfig = copyPolicyUserConfigDAO.getCopyPolicyUserConfig(policy.getId(),
                    CopyPolicyUserConfig.USER_TYPE_SINGLE,
                    CopyPolicyUserConfig.DEFAULT_USER_ID);
            }
            
            policy.setUserConfig(userConfig);
        }
        
        return lstPolicy;
    }
    
    @Override
    public CopyPolicy getCopyPolicy(int policyId)
    {
        CopyPolicy policy = copyPolicyDAO.get(policyId);
        if (null != policy)
        {
            CopyPolicyUserConfig userConfig = copyPolicyUserConfigDAO.getCopyPolicyUserConfig(policyId,
                CopyPolicyUserConfig.USER_TYPE_TEAMSPACE,
                CopyPolicyUserConfig.DEFAULT_USER_ID);
            if (null == userConfig)
            {
                userConfig = copyPolicyUserConfigDAO.getCopyPolicyUserConfig(policyId,
                    CopyPolicyUserConfig.USER_TYPE_SINGLE,
                    CopyPolicyUserConfig.DEFAULT_USER_ID);
            }
            
            policy.setUserConfig(userConfig);
        }
        
        
        return policy;
    }
    
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    public void createCopyPolicy(CopyPolicy policy)
    {
        int id = copyPolicyDAO.getNextAvailableId();
        policy.setId(id);
        copyPolicyDAO.create(policy);
        if (null != policy.getUserConfig())
        {
            CopyPolicyUserConfig userConfig = policy.getUserConfig();
            userConfig.setPolicyId(id);
            copyPolicyUserConfigDAO.create(userConfig);
        }
        
    }
    
    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void modifyCopyPolicy(CopyPolicy policy)
    {
        copyPolicyDAO.update(policy);
        if (null != policy.getUserConfig())
        {
            copyPolicyUserConfigDAO.deleteByPolicy(policy.getId());
            copyPolicyUserConfigDAO.create(policy.getUserConfig());
        }
        
    }
    
    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void modifyCopyPolicySiteInfo(CopyPolicy policy, List<CopyPolicySiteInfo> lstCopyPolicyDataSiteInfo)
    {
        CopyPolicy tmp = copyPolicyDAO.get(policy.getId());
        if (null == tmp)
        {
            throw new BusinessException("not found policy id:" + policy.getId());
        }
        tmp.setLstCopyPolicyDataSiteInfo(lstCopyPolicyDataSiteInfo);
        copyPolicyDAO.update(tmp);
    }
    
    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void modifyCopyPolicyExeTime(CopyPolicy policy)
    {
        CopyPolicy tmp = copyPolicyDAO.get(policy.getId());
        if (null == tmp)
        {
            throw new BusinessException("not found policy id:" + policy.getId());
        }
        
        tmp.setExeType(policy.getExeType());
        tmp.setExeStartAt(policy.getExeStartAt());
        tmp.setExeEndAt(policy.getExeEndAt());
        copyPolicyDAO.update(tmp);
        
        // 是否需要已经形成的任务时间,如果需要启动异步任务来做。
        
    }
    
    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void modifyCopyPolicyState(CopyPolicy policy)
    {
        CopyPolicy tmp = copyPolicyDAO.get(policy.getId());
        if (null == tmp)
        {
            throw new BusinessException("not found policy id:" + policy.getId());
        }
        
        tmp.setState(policy.getState());
        
        copyPolicyDAO.update(tmp);
    }
    
    @Override
    public void deleteCopyPolicy(CopyPolicy policy)
    {
        copyPolicyDAO.delete(policy.getId());
        copyPolicyUserConfigDAO.deleteByPolicy(policy.getId());
//        copyTaskDAO.deleteCopyTaskByPolicy(policy.getId());
    }
    
    @Override
    public List<CopyPolicy> getAppCopyPolicy(String appId)
    {
        
        List<CopyPolicy> lstPolicy = copyPolicyDAO.getByApp(appId);
        
        if (null == lstPolicy || lstPolicy.isEmpty())
        {
            return lstPolicy;
        }
        CopyPolicyUserConfig userConfig = null;
        for (CopyPolicy policy : lstPolicy)
        {
            userConfig = copyPolicyUserConfigDAO.getCopyPolicyUserConfig(policy.getId(),
                CopyPolicyUserConfig.USER_TYPE_TEAMSPACE,
                CopyPolicyUserConfig.DEFAULT_USER_ID);
            if (null == userConfig)
            {
                userConfig = copyPolicyUserConfigDAO.getCopyPolicyUserConfig(policy.getId(),
                    CopyPolicyUserConfig.USER_TYPE_SINGLE,
                    CopyPolicyUserConfig.DEFAULT_USER_ID);
            }
            
            policy.setUserConfig(userConfig);
        }
        
        return lstPolicy;
    }
    
    @Override
    public List<DCTreeNode> getTreeNode(Integer id)
    {
        List<DCTreeNode> nodes = new ArrayList<DCTreeNode>(10);
        DCTreeNode dcnode = null;
        if (null != id)
        {
            List<DataCenter> dcs = dcDao.getAllByRegion(id);
            for (DataCenter dc : dcs)
            {
                dcnode = new DCTreeNode(dc.getId(), dc.getName());
                nodes.add(dcnode);
            }
            
        }
        else
        {
            List<Region> resgions = regionDao.getAll();
            for (Region region : resgions)
            {
                dcnode = new DCTreeNode(region.getId(), region.getCode());
                dcnode.setIsParent(true);
                nodes.add(dcnode);
            }
        }
        return nodes;
    }
    
}
