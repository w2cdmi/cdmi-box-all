/* 
 * 版权声明(Copyright Notice)：
 * Copyright(C) 2017-2017 聚数科技成都有限公司。保留所有权利。
 * Copyright(C) 2017-2017 www.cdmi.pw Inc. All rights reserved.
 * 警告：本内容仅限于聚数科技成都有限公司内部传阅，禁止外泄以及用于其他的商业项目
 */
package com.huawei.sharedrive.uam.weixin.service.impl;

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.huawei.sharedrive.uam.weixin.dao.WxUserEnterpriseDao;
import com.huawei.sharedrive.uam.weixin.domain.WxUserEnterprise;
import com.huawei.sharedrive.uam.weixin.service.WxUserEnterpriseService;

/************************************************************
 * @author Rox
 * @version 3.0.1
 * @Description: <pre>微信用户账户管理</pre>
 * @Project Alpha CDMI Service Platform, box-uam-web Component. 2017/7/31
 ************************************************************/
@Service
public class WxUserEnterpriseServiceImpl implements WxUserEnterpriseService {
    private static final Logger logger = LoggerFactory.getLogger(WxUserEnterpriseServiceImpl.class);

    @Autowired
    private WxUserEnterpriseDao wxUserEnterpriseDao;

    @Override
    public void create(WxUserEnterprise wxUserEnterprise) {
        if(wxUserEnterprise.getCreatedAt() == null) {
            wxUserEnterprise.setCreatedAt(new Date());
        }
        if(wxUserEnterprise.getModifiedAt() == null) {
            wxUserEnterprise.setModifiedAt(new Date());
        }

        wxUserEnterpriseDao.create(wxUserEnterprise);
    }

    @Override
    public void deleteByUnionId(String unionId) {
        wxUserEnterpriseDao.deleteByUnionId(unionId);
    }

    @Override
    public WxUserEnterprise getByUnionIdAndEnterpriseId(String unionId, long enterpriseId) {
        return wxUserEnterpriseDao.getByUnionIdAndEnterpriseId(unionId, enterpriseId);
    }
    
    @Override
    public WxUserEnterprise getByEnterpriseUser(long userId, long enterpriseId) {
        return wxUserEnterpriseDao.getByEnterpriseUser(userId, enterpriseId);
    }

    @Override
    public void deleteByEnterpriseUserIdAndEnterpriseId(long employeeid, long enterpriseId) {
        wxUserEnterpriseDao.deleteByEnterpriseUserIdAndEnterpriseId(employeeid, enterpriseId);
    }

    @Override
    public List<WxUserEnterprise> listByUnionId(String unionId) {
        return wxUserEnterpriseDao.listByUnionId(unionId);
    }

	@Override
	public void update(WxUserEnterprise wxUserEnterprise) {
        if(wxUserEnterprise.getModifiedAt() == null) {
            wxUserEnterprise.setModifiedAt(new Date());
        }

        wxUserEnterpriseDao.update(wxUserEnterprise);
	}
}
