/* 
 * 版权声明(Copyright Notice)：
 * Copyright(C) 2017-2017 聚数科技成都有限公司。保留所有权利。
 * Copyright(C) 2017-2017 www.cdmi.pw Inc. All rights reserved.
 * 警告：本内容仅限于聚数科技成都有限公司内部传阅，禁止外泄以及用于其他的商业项目
 */
package com.huawei.sharedrive.uam.weixin.service.impl;

import java.io.IOException;
import java.util.Date;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.stereotype.Service;

import com.huawei.sharedrive.uam.anon.service.EnterpriseBySelfService;
import com.huawei.sharedrive.uam.openapi.domain.RestEnterpriseAccountRequest;
import com.huawei.sharedrive.uam.weixin.dao.WxEnterpriseDao;
import com.huawei.sharedrive.uam.weixin.domain.WxEnterprise;
import com.huawei.sharedrive.uam.weixin.service.WxEnterpriseService;

import pw.cdmi.common.domain.enterprise.Enterprise;

/************************************************************
 * @author Rox
 * @version 3.0.1
 * @Description: <pre>微信企业Service</pre>
 * @Project Alpha CDMI Service Platform, box-uam-web Component. 2017/7/31
 ************************************************************/
@Service
public class WxEnterpriseServiceImpl implements WxEnterpriseService {
    private static Logger logger = LoggerFactory.getLogger(WxEnterpriseService.class);

    @Autowired
    WxEnterpriseDao wxEnterpriseDao;

    @Override
    public void create(WxEnterprise wxEnterprise) {
        if(wxEnterprise.getCreatedAt() == null) {
            wxEnterprise.setCreatedAt(new Date());
        }
        if(wxEnterprise.getModifiedAt() == null) {
            wxEnterprise.setModifiedAt(new Date());
        }

        wxEnterprise.setState(WxEnterprise.STATE_INITIAL); //设置为初始状态
        wxEnterpriseDao.create(wxEnterprise);
    }

    @Override
    public void update(WxEnterprise wxEnterprise) {
        if(wxEnterprise.getModifiedAt() == null) {
            wxEnterprise.setModifiedAt(new Date());
        }

        wxEnterpriseDao.update(wxEnterprise);
    }

    @Override
    public void updateState(String corpId, Byte state) {
        WxEnterprise wxEnterprise = new WxEnterprise();
        wxEnterprise.setId(corpId);
        wxEnterprise.setState(state);
        wxEnterprise.setModifiedAt(new Date());

        wxEnterpriseDao.updateState(wxEnterprise);
    }

    @Override
    public WxEnterprise get(String corpId) {
        return wxEnterpriseDao.get(corpId);
    }
}
