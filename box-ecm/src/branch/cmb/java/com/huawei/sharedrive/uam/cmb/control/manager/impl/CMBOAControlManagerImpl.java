package com.huawei.sharedrive.uam.cmb.control.manager.impl;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.huawei.sharedrive.common.domain.AuthServer;
import com.huawei.sharedrive.common.domain.enterprise.Enterprise;
import com.huawei.sharedrive.uam.authserver.manager.AuthServerManager;
import com.huawei.sharedrive.uam.cmb.control.manager.CMBOAControlManager;
import com.huawei.sharedrive.uam.core.domain.Page;
import com.huawei.sharedrive.uam.core.domain.PageRequest;
import com.huawei.sharedrive.uam.enterprise.manager.EnterpriseManager;
import com.huawei.sharedrive.uam.util.Constants;

@Component
public class CMBOAControlManagerImpl implements CMBOAControlManager
{
    
    @Autowired
    private EnterpriseManager enterpriseManager;
    
    @Autowired
    private AuthServerManager authServerManager;
    
    @Override
    public Enterprise getEnterprise()
    {
        PageRequest request = new PageRequest();
        request.setSize(Constants.DEFAULT_PAGE_SIZE);
        Page<Enterprise> enterprisePage = enterpriseManager.getFilterd(null, null, request);
        List<Enterprise> enterpriseList = enterprisePage.getContent();
        Enterprise enterprise = null;
        if (null != enterpriseList && enterpriseList.size() > 0)
        {
            enterprise = enterpriseList.get(0);
        }
        return enterprise;
    }
    
    @Override
    public AuthServer getAuthServer(long enterpriseId)
    {
        List<AuthServer> list = authServerManager.getByEnterpriseId(enterpriseId);
        if (CollectionUtils.isEmpty(list))
        {
            return null;
        }
        for (AuthServer authServer : list)
        {
            if (StringUtils.equals(AuthServer.AUTH_TYPE_LOCAL, authServer.getType()))
            {
                return authServer;
            }
        }
        return null;
    }
}
