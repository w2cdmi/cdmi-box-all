package com.huawei.sharedrive.app.mirror.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.huawei.sharedrive.app.mirror.dao.CopyPolicyDAO;
import com.huawei.sharedrive.app.mirror.dao.CopyPolicyUserConfigDAO;
import com.huawei.sharedrive.app.mirror.domain.CopyPolicy;
import com.huawei.sharedrive.app.mirror.domain.CopyPolicyUserConfig;
import com.huawei.sharedrive.app.mirror.service.CopyPolicyService;
import com.huawei.sharedrive.app.user.dao.UserDAOV2;
import com.huawei.sharedrive.app.user.domain.User;

@Service("copyPolicyService")
public class CopyPolicyServiceImpl implements CopyPolicyService
{
    @Autowired
    private UserDAOV2 userDAO;
    
    @Autowired
    private CopyPolicyUserConfigDAO copyPolicyUserConfigDAO;
    
    @Autowired
    private CopyPolicyDAO copyPolicyDAO;
    
    @Override
    public boolean checkUserCopyPolicy(int policy, long ownerId)
    {
        User user = userDAO.get(ownerId);
        CopyPolicyUserConfig config = null;
        if (null == user)
        {
            return false;
        }
        
        if (User.USER_TYPE_TEAMSPACE == user.getType())
        {
            /**
             * 是否存在批量策略
             */
            config = copyPolicyUserConfigDAO.getCopyPolicyUserConfig(policy,
                CopyPolicyUserConfig.USER_TYPE_TEAMSPACE,
                CopyPolicyUserConfig.DEFAULT_USER_ID);
        }
        else
        {
            // 首先查询全部
            config = copyPolicyUserConfigDAO.getCopyPolicyUserConfig(policy,
                CopyPolicyUserConfig.USER_TYPE_SINGLE,
                CopyPolicyUserConfig.DEFAULT_USER_ID);
            
            // 精确查找个人
            if (null == config)
            {
                config = copyPolicyUserConfigDAO.getCopyPolicyUserConfig(policy,
                    CopyPolicyUserConfig.USER_TYPE_SINGLE,
                    ownerId);
                
            }
        }
        
        return null != config;
    }
    
    @Override
    public List<CopyPolicy> listCopyPolicy()
    {
        
        return copyPolicyDAO.listCopyPolicy();
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
    
    public boolean isTeamSpaceType(int policy)
    {
        CopyPolicyUserConfig config = copyPolicyUserConfigDAO.getCopyPolicyUserConfig(policy,
            CopyPolicyUserConfig.USER_TYPE_TEAMSPACE,
            CopyPolicyUserConfig.DEFAULT_USER_ID);
        
        return null != config;
    }
    
    @Override
    public CopyPolicyUserConfig getOneUserConfigOrderByACS(long userId, int policy)
    {
        
        return null;
    }
}
