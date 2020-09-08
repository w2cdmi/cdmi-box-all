/* 
 * 版权声明(Copyright Notice)：
 * Copyright(C) 2017-2017 聚数科技成都有限公司。保留所有权利。
 * Copyright(C) 2017-2017 www.cdmi.pw Inc. All rights reserved.
 * 警告：本内容仅限于聚数科技成都有限公司内部传阅，禁止外泄以及用于其他的商业项目
 */
package com.huawei.sharedrive.uam.weixin.service.impl;

import com.huawei.sharedrive.uam.authapp.service.AuthAppService;
import com.huawei.sharedrive.uam.enterprise.service.EnterpriseAccountService;
import com.huawei.sharedrive.uam.enterprise.service.EnterpriseService;
import com.huawei.sharedrive.uam.enterpriseuser.domain.EnterpriseUser;
import com.huawei.sharedrive.uam.enterpriseuser.manager.EnterpriseUserManager;
import com.huawei.sharedrive.uam.enterpriseuser.service.EnterpriseUserService;
import com.huawei.sharedrive.uam.organization.domain.EnterpriseUserDept;
import com.huawei.sharedrive.uam.organization.service.EnterpriseUserDeptService;
import com.huawei.sharedrive.uam.system.service.SystemConfigService;
import com.huawei.sharedrive.uam.teamspace.domain.RestTeamMemberInfo;
import com.huawei.sharedrive.uam.user.domain.MessageTemplate;
import com.huawei.sharedrive.uam.user.service.MessageTemplateService;
import com.huawei.sharedrive.uam.user.service.UserImageService;
import com.huawei.sharedrive.uam.weixin.domain.WxWorkCorpApp;
import com.huawei.sharedrive.uam.weixin.event.SuiteCreateUserEvent;
import com.huawei.sharedrive.uam.weixin.rest.User;
import com.huawei.sharedrive.uam.weixin.rest.proxy.WxWorkOauth2SuiteProxy;
import com.huawei.sharedrive.uam.weixin.service.WxWorkCorpAppService;
import com.huawei.sharedrive.uam.weixin.service.WxWorkUserManager;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pw.cdmi.common.cache.CacheClient;
import pw.cdmi.common.domain.SystemConfig;
import pw.cdmi.common.domain.enterprise.Enterprise;
import pw.cdmi.common.domain.enterprise.EnterpriseAccount;

import java.util.List;

/************************************************************
 * @Description:
 * <pre>企业信息用户账户管理</pre>
 * @author Rox
 * @version 3.0.1
 * @Project Alpha CDMI Service Platform, box-uam-web Component. 2017/11/25
 ************************************************************/
@Service
public class WxWorkUserManagerImpl implements WxWorkUserManager {
    private static Logger logger = LoggerFactory.getLogger(WxWorkUserManagerImpl.class);

    @Autowired
    private AuthAppService authAppService;

    @Autowired
    private EnterpriseService enterpriseService;

    @Autowired
    private EnterpriseAccountService enterpriseAccountService;

    @Autowired
    private EnterpriseUserManager enterpriseUserManager;

    @Autowired
    private EnterpriseUserService enterpriseUserService;

    @Autowired
    private EnterpriseUserDeptService enterpriseUserDeptService;

    @Autowired
    private UserImageService userImageService;

    @Autowired
    WxWorkOauth2SuiteProxy wxOauth2SuiteProxy;

    @Autowired
    private WxWorkCorpAppService wxWorkCorpAppService;

    @Autowired
    private SystemConfigService systemConfigService;

    @Autowired
    MessageTemplateService messageTemplateService;

    @Autowired
    CacheClient cacheClient;

    String suiteId = "ww2e8f675c0308dbb3";

    public String getSuiteId() {
        return suiteId;
    }

    public void setSuiteId(String suiteId) {
        this.suiteId = suiteId;
    }

    @Override
    public EnterpriseUser openAccount(long enterpriseId, User wxUser) {
        try {
            EnterpriseUser user = WxDomainUtils.toEnterpriseUser(wxUser);
            user.setEnterpriseId(enterpriseId);

            long enterpriseUserId = enterpriseUserManager.createWeixin(user, true);
            String appId = authAppService.getDefaultWebApp().getAuthAppId();
            EnterpriseAccount enterpriseAccount = enterpriseAccountService.getByEnterpriseApp(user.getEnterpriseId(), appId);

            //todo: 如果是leader，自动设置为审批人

            //更新用户头像
            if(StringUtils.isNotBlank(wxUser.getAvatar())) {
                userImageService.updateUserImage(enterpriseUserId, enterpriseAccount.getAccountId(), wxUser.getAvatar());
            }

            //创建用户-部门关系表
            createEnterpriseUserDept(enterpriseId, enterpriseUserId, wxUser);

            //给用户发送消息
            SystemConfig systemConfig = systemConfigService.getSystemConfig("sent.user.account.message", appId);
            if(systemConfig != null && systemConfig.getValue().equals("1")) {
                Enterprise enterprise = enterpriseService.getById(enterpriseId);
                sendUserMessage(wxUser.getCorpId(), enterprise.getName(), wxUser.getUserid(), user);
            }

            return user;
        } catch (Exception e) {
            logger.error("failed to create account for WxEnterpriseUser: corpId={}, userId={}, error={}", wxUser.getCorpId(), wxUser.getUserid(), e.getMessage());
            logger.error("failed to create account for WxEnterpriseUser: ", e);
//            e.printStackTrace();
        }

        return null;
    }

    private void createEnterpriseUserDept(long enterpriseId, long userId, User wxUser) {
        List<Long> deptList = wxUser.getDepartment();
        if(wxUser.getDepartment() != null) {
            for(int i = 0; i < deptList.size(); i++) {
                try {
                    EnterpriseUserDept userDept = new EnterpriseUserDept();
                    userDept.setEnterpriseId(enterpriseId);
                    userDept.setDepartmentId(deptList.get(i));
                    userDept.setEnterpriseUserId(userId);
                    userDept.setOrder(wxUser.getOrder().get(i));
                    enterpriseUserDeptService.create(userDept);
                } catch (Exception e) {
                    logger.error("Failed to create EnterpriseUserDept, enterpriseId={}, deptId={}, userId={}, error={}", enterpriseId, deptList.get(i), userId, e.getMessage());
                }
            }
        }

        //修复因为部门不可见带来的问题
        enterpriseUserDeptService.fixDepartmentId(enterpriseId, userId);
    }

    @Override
    public EnterpriseUser openAccount(long enterpriseId, SuiteCreateUserEvent event) {
        try {
            EnterpriseUser user = WxDomainUtils.toEnterpriseUser(event);
            user.setEnterpriseId(enterpriseId);

            long enterpriseUserId = enterpriseUserManager.createWeixin(user, true);
            String appId = authAppService.getDefaultWebApp().getAuthAppId();
            EnterpriseAccount enterpriseAccount = enterpriseAccountService.getByEnterpriseApp(user.getEnterpriseId(), appId);

            //todo: 如果是leader，自动设置为审批人

            //更新用户头像
            if(StringUtils.isNotBlank(event.getAvatar())) {
                userImageService.updateUserImage(enterpriseUserId, enterpriseAccount.getAccountId(), event.getAvatar());
            }

            //创建用户-部门关系表
            createEnterpriseUserDept(enterpriseId, enterpriseUserId, event.getDepartment());

            //给用户发送消息
            SystemConfig systemConfig = systemConfigService.getSystemConfig("sent.user.account.message", appId);
            if(systemConfig != null && systemConfig.getValue().equals("1")) {
                Enterprise enterprise = enterpriseService.getById(enterpriseId);
                sendUserMessage(event.getAuthCorpId(), enterprise.getName(), event.getUserID(), user);
            }

            return user;
        } catch (Exception e) {
            logger.error("failed to create account for WxEnterpriseUser: corpId={}, userId={}, error={}", event.getAuthCorpId(), event.getUserID(), e.getMessage());
            logger.error("failed to create account for WxEnterpriseUser: ", e);
//            e.printStackTrace();
        }

        return null;
    }

    private void createEnterpriseUserDept(long enterpriseId, long userId, String department) {
        if(StringUtils.isNotBlank(department)) {
            String[] array = department.split(",");
            for(String deptId : array) {
                try {
                    EnterpriseUserDept userDept = new EnterpriseUserDept(userId, enterpriseId, Long.parseLong(deptId));
                    userDept.setOrder(0);

                    enterpriseUserDeptService.create(userDept);
                } catch (Exception e) {
                    logger.error("failed to add EnterpriseUserDept for WxEnterpriseUser: enterpriseId={}, userId={}, deptId={}, error={}", enterpriseId, userId, deptId, e.getMessage());
                    logger.error("failed to add EnterpriseUserDept for WxEnterpriseUser: ", e);
                }
            }
        } else {
            logger.warn("No department in event: enterpriseId={}, userId={}, department=null.", enterpriseId, userId);
        }

        //修复因为部门不可见带来的问题
        enterpriseUserDeptService.fixDepartmentId(enterpriseId, userId);
    }

    @Override
    public void updateAccount(long enterpriseId, User wxUser) {
        EnterpriseUser sysUser = enterpriseUserService.getByEnterpriseIdAndName(enterpriseId, wxUser.getUserid());
        //更新显示名称
        if(StringUtils.isNotBlank(wxUser.getName())) {
            sysUser.setAlias(wxUser.getName());
        }

        //将职位信息更新到description字段
        if(StringUtils.isNotBlank(wxUser.getPosition())) {
            sysUser.setDescription(wxUser.getPosition());
        }

        //
        if(StringUtils.isNotBlank(wxUser.getMobile())) {
            sysUser.setMobile(wxUser.getMobile());
        }

        if(StringUtils.isNotBlank(wxUser.getEmail())) {
            sysUser.setEmail(wxUser.getEmail());
        }

        //用户可以修改userId一次，所以要在此更新。
        if(StringUtils.isNotBlank(wxUser.getUserid())) {
            sysUser.setName(wxUser.getUserid());
        }

        sysUser.setStatus(EnterpriseUser.STATUS_ENABLE);

        enterpriseUserService.update(sysUser);

        //更新用户头像
        if(StringUtils.isNotBlank(wxUser.getAvatar())) {
            String appId = authAppService.getDefaultWebApp().getAuthAppId();
            EnterpriseAccount enterpriseAccount = enterpriseAccountService.getByEnterpriseApp(enterpriseId, appId);

            userImageService.updateUserImage(sysUser.getId(), enterpriseAccount.getAccountId(), wxUser.getAvatar());
        }

        //先删除所有的部门关系
        enterpriseUserDeptService.deleteByEnterpriseUserId(enterpriseId, sysUser.getId());

        //创建用户-部门关系表
        createEnterpriseUserDept(enterpriseId, sysUser.getId(), wxUser);

        //

        //TODO：检查账户状态，如果账户已经关闭，重新打开
        //todo: 更新用户状态，如果账户已经被关闭，重新打开。
    }

    protected Long take(List<RestTeamMemberInfo> accountList, Long id) {
        for(int i = 0; i < accountList.size(); i++) {
            RestTeamMemberInfo item = accountList.get(i);
            if(item.getTeamId().equals(id)) {
                accountList.remove(i);
                return item.getTeamId();
            }
        }

        return null;
    }

    @Override
    public void closeAccount(EnterpriseUser sysUser) {
        //不删除系统账号，只是将账号置为禁用。因为需要手工迁移数据后才能删除账号信息。
        sysUser.setStatus(EnterpriseUser.STATUS_DISMISS);
        enterpriseUserService.update(sysUser);
//        enterpriseUserManager.delete(sysUser.getId(), sysUser.getEnterpriseId());
    }

    private void sendUserMessage(String corpId, String enterpriseName, String userId, EnterpriseUser user) {
        WxWorkCorpApp app = wxWorkCorpAppService.getByCorpIdAndSuiteId(corpId, suiteId);
        if(app != null) {
            //通过消息模板来构造消息
            String template = getMessageTemplate("createUser");
            if(StringUtils.isNotBlank(template)) {
                String message = template.replaceAll("(\\$\\{username})", user.getName());
                message = message.replaceAll("(\\$\\{password})", user.getPassword());
                message = message.replaceAll("(\\$\\{enterpriseName})", enterpriseName);

                wxOauth2SuiteProxy.sendTextMessage(app.getCorpId(), app.getAgentId(), userId, message);
            }
        } else {
            logger.error("Failed to send WxWork user message, No App installed: corpId={}, suiteId={}, userId={}", corpId, suiteId, userId);
        }
    }

    private String getMessageTemplate(String id) {
        String key = "MessageTemplate." + id;
        String content = (String)cacheClient.getCache(key);
        if(content == null) {
            MessageTemplate template = messageTemplateService.getById(id);
            if(template != null) {
                content = template.getContent();
                cacheClient.setCache(key, content, 60000);
            } else {
                content = "您好，您的文件宝账号已经开通：<br>企业名称：${enterpriseName}<br>用户名：${username}<br> 密码: ${password}<br>" +
                        "您可以在微信小程序中搜索“企业云盘”，进入小程序进行绑定。绑定后你可以访问<a href=\"http://www.jmapi.cn\">www.jmapi.cn</a>。通过微信扫描登陆管理您的知识文档，与同事进行工作协同。" +
                        "您也可以登陆<a href=\"http://www.jmapi.cn\">www.jmapi.cn</a>。首次登陆通过企业微信扫描登陆，然后在设置->用户信息中绑定您的微信号码。";
                logger.warn("No message template found in db: id={}", id);
            }
        }

        return content;
    }
}
