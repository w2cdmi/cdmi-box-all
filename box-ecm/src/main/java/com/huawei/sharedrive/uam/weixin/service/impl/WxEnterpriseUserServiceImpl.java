/* 
 * 版权声明(Copyright Notice)：
 * Copyright(C) 2017-2017 聚数科技成都有限公司。保留所有权利。
 * Copyright(C) 2017-2017 www.cdmi.pw Inc. All rights reserved.
 * 警告：本内容仅限于聚数科技成都有限公司内部传阅，禁止外泄以及用于其他的商业项目
 */
package com.huawei.sharedrive.uam.weixin.service.impl;

import com.huawei.sharedrive.uam.weixin.dao.WxEnterpriseUserDao;
import com.huawei.sharedrive.uam.weixin.domain.WxEnterpriseUser;
import com.huawei.sharedrive.uam.weixin.service.WxEnterpriseUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/************************************************************
 * @author Rox
 * @version 3.0.1
 * @Description: <pre>部门员工管理</pre>
 * @Project Alpha CDMI Service Platform, box-uam-web Component. 2017/7/31
 ************************************************************/
@Service
public class WxEnterpriseUserServiceImpl implements WxEnterpriseUserService {
    private static final Logger logger = LoggerFactory.getLogger(WxEnterpriseUserServiceImpl.class);

    @Autowired
    private WxEnterpriseUserDao wxEnterpriseUserDao;

    @Override
    public WxEnterpriseUser get(String corpId, String userId) {
        return wxEnterpriseUserDao.get(corpId, userId);
    }

    @Override
    public void create(WxEnterpriseUser wxUser) {
        try {
            if(wxUser.getCreatedAt() == null) {
                wxUser.setCreatedAt(new Date());
            }
            if(wxUser.getModifiedAt() == null) {
                wxUser.setModifiedAt(new Date());
            }
            if(wxUser.getStatus() == null) {
                wxUser.setStatus(WxEnterpriseUser.STATUS_ENABLE);
            }
            wxEnterpriseUserDao.create(wxUser);
        } catch (Exception e) {
            logger.error("Failed to create WxEnterpriseUser: corpId={}, userId={}, error={}", wxUser.getCorpId(), wxUser.getUserId(), e.getMessage());
            logger.error("Failed to create WxEnterpriseUser:", e);
        }
    }

    @Override
    public void update(WxEnterpriseUser wxUser) {
        try {
            if(wxUser.getModifiedAt() == null) {
                wxUser.setModifiedAt(new Date());
            }

            wxEnterpriseUserDao.update(wxUser);
        } catch (Exception e) {
            logger.error("Failed to update WxEnterpriseUser: corpId={}, userId={}, error={}", wxUser.getCorpId(), wxUser.getUserId(), e.getMessage());
            logger.error("Failed to update WxEnterpriseUser:", e);
        }
    }

    @Override
    public void delete(WxEnterpriseUser wxUser) {
        try {
            //删除微信用户信息
            wxEnterpriseUserDao.delete(wxUser);
        } catch (Exception e) {
            logger.error("Failed to delete WxEnterpriseUser: corpId={}, userId={}, error={}", wxUser.getCorpId(), wxUser.getUserId(), e.getMessage());
            logger.error("Failed to delete WxEnterpriseUser:", e);
        }
    }

    @Override
    public void delete(String corpId, String userId) {
        WxEnterpriseUser wxUser = wxEnterpriseUserDao.get(corpId, userId);
        if(wxUser != null) {
            delete(wxUser);
        }
    }

    @Override
    public List<WxEnterpriseUser> getByCorpId(String corpId) {
        return wxEnterpriseUserDao.getByCorpId(corpId);
    }

    @Override
    public List<WxEnterpriseUser> getByEnterpriseIdAndUserId(long enterpriseId, long enterpriseUserId) {
        return wxEnterpriseUserDao.getByEnterpriseIdAndUserId(enterpriseId, enterpriseUserId);
    }

    //批量修改某个企业下的所有部门状态
    public void changeStatus(String corpId, int status) {
        WxEnterpriseUser wxUser = new WxEnterpriseUser();
        wxUser.setCorpId(corpId);
        wxUser.setStatus(status);
        wxUser.setModifiedAt(new Date());

        wxEnterpriseUserDao.changeStatus(wxUser);
    }

    //批量删除某个企业下特定状态的部门
    public void deleteByStatus(String corpId, int status) {
        wxEnterpriseUserDao.deleteByStatus(corpId, status);
    }
}
