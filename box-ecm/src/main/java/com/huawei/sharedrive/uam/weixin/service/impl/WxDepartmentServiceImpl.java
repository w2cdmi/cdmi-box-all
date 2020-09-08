/* 
 * 版权声明(Copyright Notice)：
 * Copyright(C) 2017-2017 聚数科技成都有限公司。保留所有权利。
 * Copyright(C) 2017-2017 www.cdmi.pw Inc. All rights reserved.
 * 警告：本内容仅限于聚数科技成都有限公司内部传阅，禁止外泄以及用于其他的商业项目
 */
package com.huawei.sharedrive.uam.weixin.service.impl;

import com.huawei.sharedrive.uam.weixin.dao.WxDepartmentDao;
import com.huawei.sharedrive.uam.weixin.domain.WxDepartment;
import com.huawei.sharedrive.uam.weixin.service.WxDepartmentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/************************************************************
 * @author Rox
 * @version 3.0.1
 * @Description: <pre>部门</pre>
 * @Project Alpha CDMI Service Platform, box-uam-web Component. 2017/7/31
 ************************************************************/
@Service
public class WxDepartmentServiceImpl implements WxDepartmentService {
    private static final Logger logger = LoggerFactory.getLogger(WxDepartmentServiceImpl.class);

    @Autowired
    private WxDepartmentDao wxDepartmentDao;

    @Override
    public WxDepartment get(String corpId, int deptId) {
        return wxDepartmentDao.get(corpId, deptId);
    }

    @Override
    public void create(WxDepartment wxDept) {
        try {
            if(wxDept.getState() == null) {
                wxDept.setState(WxDepartment.STATE_ENABLE);
            }
            if(wxDept.getCreatedAt() == null) {
                wxDept.setCreatedAt(new Date());
            }
            if(wxDept.getModifiedAt() == null) {
                wxDept.setModifiedAt(new Date());
            }
            wxDepartmentDao.create(wxDept);
        } catch (Exception e) {
            logger.error("Failed to create WxDepartment: corpId={}, name={}, error={}", wxDept.getCorpId(), wxDept.getName(), e.getMessage());
            logger.error("Failed to create WxDepartment: ", e);
        }
    }

    @Override
    public void update(WxDepartment wxDept) {
        try {
            if(wxDept.getModifiedAt() == null) {
                wxDept.setModifiedAt(new Date());
            }
            wxDepartmentDao.update(wxDept);
        } catch (Exception e) {
            logger.error("Failed to update WxDepartment: corpId={}, name={}, error={}", wxDept.getCorpId(), wxDept.getName(), e.getMessage());
            logger.error("Failed to update WxDepartment: ", e);
        }
    }

    @Override
    public void delete(WxDepartment wxDept) {
        try {
            //删除微信部门信息
            wxDepartmentDao.delete(wxDept);
        } catch (Exception e) {
            logger.error("Failed to delete WxDepartment: corpId={}, deptId={}, error={}", wxDept.getCorpId(), wxDept.getId(), e.getMessage());
            logger.debug("Failed to delete WxDepartment: ", e);
        }
    }

    @Override
    public void delete(String corpId, int deptId) {
        WxDepartment wxDept = wxDepartmentDao.get(corpId, deptId);
        if(wxDept != null) {
            delete(wxDept);
        }
    }

    @Override
    public List<WxDepartment> listByCorpId(String corpId) {
        return wxDepartmentDao.listByCorpId(corpId);
    }

    //批量修改某个企业下的所有部门状态
    public void changeState(String corpId, int state) {
        WxDepartment dept = new WxDepartment();
        dept.setCorpId(corpId);
        dept.setState(state);
        dept.setModifiedAt(new Date());

        wxDepartmentDao.changeState(dept);
    }

    //批量删除某个企业下特定状态的部门
    public void deleteByState(String corpId, int state) {
        wxDepartmentDao.deleteByState(corpId, state);
    }
}
