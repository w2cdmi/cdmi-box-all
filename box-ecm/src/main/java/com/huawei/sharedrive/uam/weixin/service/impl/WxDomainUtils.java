/* 
 * 版权声明(Copyright Notice)：
 * Copyright(C) 2017-2017 聚数科技成都有限公司。保留所有权利。
 * Copyright(C) 2017-2017 www.cdmi.pw Inc. All rights reserved.
 * 警告：本内容仅限于聚数科技成都有限公司内部传阅，禁止外泄以及用于其他的商业项目
 */
package com.huawei.sharedrive.uam.weixin.service.impl;

import com.huawei.sharedrive.uam.enterpriseuser.domain.EnterpriseUser;
import com.huawei.sharedrive.uam.util.PasswordGenerateUtil;
import com.huawei.sharedrive.uam.weixin.domain.WxDepartment;
import com.huawei.sharedrive.uam.weixin.domain.WxEnterprise;
import com.huawei.sharedrive.uam.weixin.domain.WxEnterpriseUser;
import com.huawei.sharedrive.uam.weixin.event.*;
import com.huawei.sharedrive.uam.weixin.rest.Department;
import com.huawei.sharedrive.uam.weixin.rest.PermanentCodeInfo;
import com.huawei.sharedrive.uam.weixin.rest.User;

import java.util.Date;

/************************************************************
 * @author Rox
 * @version 3.0.1
 * @Description: <pre>将微信事件或查询结果转换为Domain的工具类</pre>
 * @Project Alpha CDMI Service Platform, box-uam-web Component. 2017/7/31
 ************************************************************/
public class WxDomainUtils {
    public static WxEnterprise toWxEnterprise(PermanentCodeInfo info) {
        WxEnterprise enterprise = new WxEnterprise();
        enterprise.setId(info.getAuthCorpInfo().getCorpid());
        enterprise.setName(info.getAuthCorpInfo().getCorpName());
        enterprise.setType(info.getAuthCorpInfo().getCorpType());
        enterprise.setSquareLogoUrl(info.getAuthCorpInfo().getCorpSquareLogoUrl());
        enterprise.setUserMax(info.getAuthCorpInfo().getCorpUserMax());
        enterprise.setFullName(info.getAuthCorpInfo().getCorpFullName());
        enterprise.setSubjectType(info.getAuthCorpInfo().getSubjectType());
        enterprise.setVerifiedEndTime(new Date(info.getAuthCorpInfo().getVerifiedEndTime()));
        enterprise.setWxqrCode(info.getAuthCorpInfo().getCorpWxqrcode());
        enterprise.setEmail(info.getAuthUserInfo().getEmail());
        enterprise.setMobile(info.getAuthUserInfo().getMobile());
        enterprise.setUserId(info.getAuthUserInfo().getUserid());
        enterprise.setPermanentCode(info.getPermanentCode());

        return enterprise;
    }

    /**
     * 将部门查询结果转化为Domain对象。查询结果中没有enterpriseId, 需要调用函数自己赋值
     * @param wxDept 部门查询结果
     * @return WxDepartment
     */
    public static com.huawei.sharedrive.uam.organization.domain.Department toDepartment(Department wxDept) {
        com.huawei.sharedrive.uam.organization.domain.Department dept = new com.huawei.sharedrive.uam.organization.domain.Department();

        dept.setDepartmentId(Long.valueOf(wxDept.getId()));
        dept.setName(wxDept.getName());
        if(wxDept.getParentid() != null) {
            dept.setParentId(Long.valueOf(wxDept.getParentid()));
        }
        if(wxDept.getOrder() != null) {
            dept.setOrder(wxDept.getOrder());
        }

        return dept;
    }

    /**
     * 将SuiteCreatePartyEvent转化为Domain对象。
     * @param e SuiteCreatePartyEvent
     * @return WxDepartment
     */
    public static com.huawei.sharedrive.uam.organization.domain.Department toDepartment(SuiteCreatePartyEvent e) {
        com.huawei.sharedrive.uam.organization.domain.Department dept = new com.huawei.sharedrive.uam.organization.domain.Department();
        dept.setDepartmentId(new Long(e.getId()));
        dept.setParentId(Long.parseLong(e.getParentId()));
        dept.setName(e.getName());
        dept.setOrder(e.getOrder());

        return dept;
    }

    /**
     * 将SuiteUpdatePartyEvent转化为Domain对象。
     * @param e SuiteUpdatePartyEvent
     * @return WxDepartment
     */
    public static com.huawei.sharedrive.uam.organization.domain.Department toDepartment(SuiteUpdatePartyEvent e) {
        com.huawei.sharedrive.uam.organization.domain.Department dept = new com.huawei.sharedrive.uam.organization.domain.Department();

        dept.setDepartmentId(new Long(e.getId()));
        if(e.getName() != null) {
            dept.setName(e.getName());
        }
        if(e.getParentId() != null) {
            dept.setParentId(Long.parseLong(e.getParentId()));
        }

        return dept;
    }

    /**
     * 将员工查询结果转化为Domain对象。查询结果中没有enterpriseId, 需要调用函数自己赋值
     * @param wxUser 员工查询结果
     * @return WxDepartment
     */
    public static EnterpriseUser toEnterpriseUser(User wxUser) {
        EnterpriseUser user = new EnterpriseUser();
        user.setName(wxUser.getUserid());
        user.setAlias(wxUser.getName());
        user.setObjectSid(wxUser.getUserid());
        user.setMobile(wxUser.getMobile());
        user.setType(EnterpriseUser.TYPE_MEMBER);
        if(user.getMobile() == null && wxUser.getTelephone() != null) {
            user.setMobile(wxUser.getTelephone());
        }
        user.setEmail(wxUser.getEmail());
        user.setStatus(EnterpriseUser.STATUS_ENABLE);
        user.setDescription(wxUser.getPosition());

        return user;
    }

    /**
     * 将SuiteCreateUserEvent果转化为Domain对象。
     * @param e SuiteCreateUserEvent
     * @return WxDepartment
     */
    public static EnterpriseUser toEnterpriseUser(SuiteCreateUserEvent e) {
        EnterpriseUser user = new EnterpriseUser();
        user.setName(e.getUserID());
        user.setAlias(e.getName());
        user.setObjectSid(e.getUserID());
        user.setMobile(e.getMobile());
        user.setType(EnterpriseUser.TYPE_MEMBER);
        user.setEmail(e.getEmail());
        user.setStatus(EnterpriseUser.STATUS_ENABLE);
        user.setDescription(e.getPosition());

        return user;
    }

    /**
     * 将SuiteUpdateUserEvent果转化为Domain对象。
     * @param e SuiteUpdateUserEvent
     * @return WxDepartment
     */
    public static WxEnterpriseUser toWxEnterpriseUser(SuiteUpdateUserEvent e) {
        WxEnterpriseUser wx = new WxEnterpriseUser();
        wx.setCorpId(e.getAuthCorpId());
        String[] split = e.getDepartment().split(",");
        wx.setDepartmentId(Integer.parseInt(split[split.length - 1]));
        wx.setUserId(e.getUserID());
        wx.setName(e.getName());
//        wx.setOrder(e.getOrder().get(e.getOrder().size() - 1));
        wx.setPosition(e.getPosition());
        wx.setMobile(e.getMobile());
        wx.setGender(e.getGender());
        wx.setEmail(e.getEmail());
        wx.setIsLeader(e.getIsLeader());
//        wx.setAvatar(e.getAvatar());
        wx.setTelephone(e.getTelephone());
        wx.setEnglishName(e.getEnglishName());
        wx.setStatus(e.getStatus());

        return wx;
    }

    /**
     * 将AppCreatePartyEvent转化为Domain对象。
     * @param e SuiteCreatePartyEvent
     * @return WxDepartment
     */
    public static WxDepartment toWxDepartment(AppCreatePartyEvent e) {
        WxDepartment wx = new WxDepartment();
        wx.setId(e.getId());
        wx.setParentId(Integer.parseInt(e.getParentId()));
        wx.setName(e.getName());
        wx.setOrder(e.getOrder());

        return wx;
    }

    /**
     * 将AppUpdatePartyEvent转化为Domain对象。
     * @param e SuiteUpdatePartyEvent
     * @return WxDepartment
     */
    public static WxDepartment toWxDepartment(AppUpdatePartyEvent e) {
        WxDepartment wx = new WxDepartment();
        wx.setId(e.getId());

        if(e.getParentId() != null) {
            wx.setParentId(Integer.parseInt(e.getParentId()));
        }

        wx.setName(e.getName());

        return wx;
    }

    /**
     * 将AppCreateUserEvent果转化为Domain对象。
     * @param e SuiteCreateUserEvent
     * @return WxDepartment
     */
    public static WxEnterpriseUser toWxEnterpriseUser(AppCreateUserEvent e) {
        WxEnterpriseUser wx = new WxEnterpriseUser();
        wx.setCorpId(e.getToUserName());
        String[] split = e.getDepartment().split(",");
        wx.setDepartmentId(Integer.parseInt(split[split.length - 1]));
        wx.setUserId(e.getUserID());
        wx.setName(e.getName());
//        wx.setOrder(e.getOrder().get(e.getOrder().size() - 1));
        wx.setPosition(e.getPosition());
        wx.setMobile(e.getMobile());
        wx.setGender(e.getGender());
        wx.setEmail(e.getEmail());
        wx.setIsLeader(e.getIsLeader());
        wx.setAvatar(e.getAvatar());
        wx.setTelephone(e.getTelephone());
        wx.setEnglishName(e.getEnglishName());
        wx.setStatus(e.getStatus());

        return wx;
    }

    /**
     * 将AppUpdateUserEvent果转化为Domain对象。
     * @param e SuiteUpdateUserEvent
     * @return WxDepartment
     */
    public static WxEnterpriseUser toWxEnterpriseUser(AppUpdateUserEvent e) {
        WxEnterpriseUser wx = new WxEnterpriseUser();
        wx.setCorpId(e.getToUserName());
        String[] split = e.getDepartment().split(",");
        wx.setDepartmentId(Integer.parseInt(split[split.length - 1]));
        wx.setUserId(e.getUserID());
        wx.setName(e.getName());
//        wx.setOrder(e.getOrder().get(e.getOrder().size() - 1));
        wx.setPosition(e.getPosition());
        wx.setMobile(e.getMobile());
        wx.setGender(e.getGender());
        wx.setEmail(e.getEmail());
        wx.setIsLeader(e.getIsLeader());
//        wx.setAvatar(e.getAvatar());
        wx.setTelephone(e.getTelephone());
        wx.setEnglishName(e.getEnglishName());
        wx.setStatus(e.getStatus());

        return wx;
    }
}
