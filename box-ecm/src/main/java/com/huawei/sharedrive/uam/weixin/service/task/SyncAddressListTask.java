/* 
 * 版权声明(Copyright Notice)：
 * Copyright(C) 2017-2017 聚数科技成都有限公司。保留所有权利。
 * Copyright(C) 2017-2017 www.cdmi.pw Inc. All rights reserved.
 * 警告：本内容仅限于聚数科技成都有限公司内部传阅，禁止外泄以及用于其他的商业项目
 */
package com.huawei.sharedrive.uam.weixin.service.task;

import com.huawei.sharedrive.uam.authapp.service.AuthAppService;
import com.huawei.sharedrive.uam.enterpriseuser.domain.EnterpriseUser;
import com.huawei.sharedrive.uam.enterpriseuser.service.EnterpriseUserService;
import com.huawei.sharedrive.uam.organization.domain.Department;
import com.huawei.sharedrive.uam.organization.service.DepartmentService;
import com.huawei.sharedrive.uam.system.service.SystemConfigService;
import com.huawei.sharedrive.uam.user.domain.Admin;
import com.huawei.sharedrive.uam.user.domain.AdminRole;
import com.huawei.sharedrive.uam.user.domain.MessageTemplate;
import com.huawei.sharedrive.uam.user.service.AdminService;
import com.huawei.sharedrive.uam.user.service.MessageTemplateService;
import com.huawei.sharedrive.uam.util.Constants;
import com.huawei.sharedrive.uam.util.PasswordGenerateUtil;
import com.huawei.sharedrive.uam.weixin.domain.WxEnterprise;
import com.huawei.sharedrive.uam.weixin.rest.*;
import com.huawei.sharedrive.uam.weixin.rest.proxy.WxWorkOauth2SuiteProxy;
import com.huawei.sharedrive.uam.weixin.service.WxWorkDepartmentManager;
import com.huawei.sharedrive.uam.weixin.service.WxWorkUserManager;
import com.huawei.sharedrive.uam.weixin.service.impl.WxDomainUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import pw.cdmi.common.domain.SystemConfig;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/************************************************************
 * @author Rox
 * @version 3.0.1
 * @Description:
 * 同步企业微信用户信息与内部的系统账号信息。
 * @Project Alpha CDMI Service Platform, box-uam-web Component. 2017/7/31
 ************************************************************/
@Component
@Scope("prototype")
public class SyncAddressListTask implements Runnable {
    private static Logger logger = LoggerFactory.getLogger(SyncAddressListTask.class);

    @Autowired
    private WxWorkOauth2SuiteProxy wxWorkOauth2SuiteProxy;

    @Autowired
    private DepartmentService departmentService;

    @Autowired
    private EnterpriseUserService enterpriseUserService;

    @Autowired
    private WxWorkDepartmentManager wxWorkDepartmentManager;

    @Autowired
    private WxWorkUserManager wxWorkUserManager;

    @Autowired
    private AdminService adminService;

    @Autowired
    private SystemConfigService systemConfigService;

    @Autowired
    private AuthAppService authAppService;

    @Autowired
    private MessageTemplateService messageTemplateService;

    private WxEnterprise wxEnterprise;

    AuthInfo authInfo;

    public SyncAddressListTask() {
    }

    public WxEnterprise getWxEnterprise() {
        return wxEnterprise;
    }

    public void setWxEnterprise(WxEnterprise wxEnterprise) {
        this.wxEnterprise = wxEnterprise;
    }

    public AuthInfo getAuthInfo() {
        return authInfo;
    }

    public void setAuthInfo(AuthInfo authInfo) {
        this.authInfo = authInfo;
    }

    @Override
    public void run() {
        if(wxEnterprise == null) {
            logger.error("Failed to synchronize address list, no corporation found: null");
            return;
        }

        String corpId = wxEnterprise.getId();

        List<Agent> agentList = authInfo.getAgent();
        if(agentList == null || agentList.isEmpty()) {
            logger.warn("No Agent found in auth_info: corpId={}", corpId);
            return;
        }

        logger.debug("There are {} agents to synchronize: corpId={}", agentList.size(), corpId);

        Set<Integer> deptList = new HashSet<>();
        Set<String> userList = new HashSet<>();
        for(Agent agent : agentList) {
            Privilege privilege = agent.getPrivilege();

            if(privilege.getAllowParty() != null && !privilege.getAllowParty().isEmpty()) {
                deptList.addAll(privilege.getAllowParty());
            } else {
                logger.info("No allow_party found in auth_info.");
            }

            if(privilege.getAllowUser() != null && !privilege.getAllowUser().isEmpty()) {
                userList.addAll(privilege.getAllowUser());
            } else {
                logger.info("No allow_user found in auth_info.");
            }
        }

        logger.debug("There are {} departments to synchronize.", deptList.size());
        Long enterpriseId = wxEnterprise.getBoxEnterpriseId();
        if(enterpriseId == null) {
            logger.error("Failed to synchronize address list, no enterprise account created for corporation: corpId={}", corpId);
            return;
        }

        //1. 同步前将所有的部门标记为-1
        departmentService.changeState(enterpriseId, Department.STATE_NOT_SYNC);

        //2. 同步部门，并将查询出来的所有部门都放入缓存。
        Set<Integer> tmpDeptList = new HashSet<>(deptList);//复制数据，避免ConcurrentModificationException
        for(Integer deptId : tmpDeptList) {
            syncDepartment(deptList, corpId, enterpriseId, deptId);
        }

        //3.不删除部门标识为 STATE_NOT_SYNC 的部门
        closeDepartmentAccount(enterpriseId);

        //4.更正部门中根节点不存在的情况。
        departmentService.fixParentId(enterpriseId);

        //1. 同步前将所有的用户标记为-1
        enterpriseUserService.changeStatus(enterpriseId, EnterpriseUser.STATUS_NOT_SYNC);

        //2.1 遍历查询缓存的部门列表下的用户，并同步
        for(Integer deptId : deptList) {
            syncUser(corpId, enterpriseId, deptId);
        }

        //2.2 查询“可见范围内用户”
        logger.debug("There are {} users of white list to synchronize.", deptList.size());
        for(String userId : userList) {
            syncUser(corpId, enterpriseId, userId);
        }

        //3.不删除标记为-1的用户
        closeEnterpriseUserAccount(enterpriseId);

        //同步企业管理员信息
        int agentId = agentList.get(0).getAgentid();
        AdminListInfo adminList = wxWorkOauth2SuiteProxy.getCorpAdminList(corpId, agentId);
        if(adminList != null) {
            if(!adminList.hasError()) {
                //给用户发送消息
                SystemConfig systemConfig = systemConfigService.getSystemConfig("sent.user.account.message", authAppService.getDefaultWebApp().getAuthAppId());
                boolean notify = systemConfig != null && (systemConfig.getValue().equals("1") || systemConfig.getValue().equalsIgnoreCase("enable"));

                for(AdminInfo admin : adminList.getAdmin()) {
                    syncEnterpriseAdmin(corpId, enterpriseId, admin, notify, agentId);
                }
            } else {
                logger.error("Failed to create enterprise admin: error occurred while getting AdminListInfo. code={}, error={}", adminList.getErrcode(), adminList.getErrmsg());
            }
        } else {
            logger.error("Failed to create enterprise admin: AdminListInfo is null.");
        }
    }

    protected void syncDepartment(Set<Integer> deptList, String corpId, long enterpriseId, Integer deptId) {
        try {
            QueryDepartmentResponse deptResponse = wxWorkOauth2SuiteProxy.getDepartmentList(corpId, String.valueOf(deptId));
            if(deptResponse == null) {
                logger.error("Failed to query department list from corporate={}, enterpriseId={}: query result is null.", corpId, enterpriseId);
                return;
            }

            //返回错误
            if(deptResponse.hasError()) {
                logger.error("Failed to query department list from corporate={}, code={}, msg={}", corpId, deptResponse.getErrcode(), deptResponse.getErrmsg());
                return;
            }

            if(deptResponse.getDepartment() == null) {
                logger.error("Stop to sync department, the department list in response is null, corpId={}, ", corpId);
                return;
            }

            for(com.huawei.sharedrive.uam.weixin.rest.Department dept : deptResponse.getDepartment()) {
                //将查询出来的部门都加入到set中
                deptList.add(dept.getId());

                //同步部门数据
                syncDepartment(enterpriseId, dept);
            }
        } catch (Exception e) {
            logger.error("Failed to synchronize department list: corpId={}, deptId={}, error={}", corpId, deptId, e.getMessage());
            logger.error("Failed to synchronize department list:", e);
        }
    }

    private void syncDepartment(long enterpriseId, com.huawei.sharedrive.uam.weixin.rest.Department dept) {
        try {
            //企业微信中，公司会作为第一级的部门同步过来，它的parentId=0, ID一般为1。我们的系统中不需要公司级的部分，所以此处不同步
            if(dept.getParentid() == 0) {
                //删除已经存在的公司级部门
                logger.info("Delete the corporation department, enterpriseId={}, deptId={}, parentId={}", enterpriseId, dept.getId(), dept.getParentid());
                departmentService.delete(enterpriseId, dept.getId());
                return;
            }

            Department newDept = WxDomainUtils.toDepartment(dept);
            newDept.setEnterpriseId(enterpriseId);
            newDept.setState(Department.STATE_ENABLE);

            Department dbDept = departmentService.getByEnterpriseIdAndDepartmentId(enterpriseId, dept.getId());
            if(dbDept == null) {
                wxWorkDepartmentManager.openAccount(newDept);
            } else {
                wxWorkDepartmentManager.updateAccount(newDept);
            }
        } catch (Exception e) {
            logger.error("Failed to synchronize department: enterpriseId={}, deptId={}, error={}", enterpriseId, dept.getId(), e.getMessage());
            logger.error("Failed to synchronize department:", e);
        }
    }

    protected void syncUser(String corpId, long enterpriseId, Integer deptId) {
        try {
            QueryUserResponse userResponse = wxWorkOauth2SuiteProxy.getUserListOfDept(corpId, String.valueOf(deptId));
            if(userResponse == null) {
                logger.error("Failed to query user list of dept: corporate={}, deptId={}, error=response is null.", corpId, deptId);
                return;
            }
            if(!userResponse.hasError()) {
                for(User user : userResponse.getUserlist()) {
                    synUser(corpId, enterpriseId, user);
                }
            } else {
                logger.error("Failed to query user list of department from corpId={}, department={}, code={}, msg={}", corpId, deptId, userResponse.getErrcode(), userResponse.getErrmsg());
            }
        } catch (Exception e) {
            logger.error("Failed to synchronize WxEnterpriseUser: corpId={}, deptId={}, error={}", corpId, deptId, e.getMessage());
            logger.error("Failed to synchronize WxEnterpriseUser:", e);
        }
    }

    protected void syncUser(String corpId, long enterpriseId, String userId) {
        try {
            User user = wxWorkOauth2SuiteProxy.getUserInfo(corpId, userId);
            if(user == null) {
                logger.error("Failed to query user info: corporate={}, userId={}, error=response is null.", corpId, userId);
                return;
            }
            if(!user.hasError()) {
                synUser(corpId, enterpriseId, user);
            } else {
                logger.error("Failed to query user list from corpId={}, userId={}, code={}, msg={}", corpId, userId, user.getErrcode(), user.getErrmsg());
            }
        } catch (Exception e) {
            logger.error("Failed to synchronize WxEnterpriseUser: corpId={}, userId={}, error={}", corpId, userId, e.getMessage());
            logger.error("Failed to synchronize WxEnterpriseUser:", e);
        }
    }

    private void synUser(String corpId, long enterpriseId, User user) {
        try {
            user.setCorpId(corpId);
            EnterpriseUser dbUser = enterpriseUserService.getByEnterpriseIdAndName(enterpriseId, user.getUserid());
            if(dbUser == null) {
                wxWorkUserManager.openAccount(enterpriseId, user);
            } else {
                wxWorkUserManager.updateAccount(enterpriseId, user);
            }
        } catch (Exception e) {
            logger.error("Failed to synchronize WxEnterpriseUser with db: corpId={}, userId={}, error={}", corpId, user.getUserid(), e.getMessage());
            logger.error("Failed to synchronize WxEnterpriseUser with db:", e);
        }
    }

    private void closeDepartmentAccount(long enterpriseId) {
/*
        List<Department> deptList = departmentService.getByEnterpriseIdAndState(enterpriseId, Department.STATE_NOT_SYNC);

        for(Department dept : deptList) {
        }
*/
    }

    private void closeEnterpriseUserAccount(long enterpriseId) {
/*
        List<EnterpriseUser> userList = enterpriseUserService.getByEnterpriseIdAndStatus(enterpriseId, EnterpriseUser.STATUS_NOT_SYNC);

        for(EnterpriseUser user : userList) {
            //OD
        }
*/
    }

    protected void syncEnterpriseAdmin(String corpId, long enterpriseId, AdminInfo adminInfo, boolean notify, int agentId) {
        // loginName + enterpriseId唯一，不会有重名
        Admin oldAdmin = adminService.getByLoginNameAndEnterpriseId(adminInfo.getUserid(), enterpriseId);
        if (oldAdmin == null) {
            Admin admin = new Admin();
            HashSet<AdminRole> roles = new HashSet<>(1);
            roles.add(AdminRole.ENTERPRISE_MANAGER);

            admin.setEnterpriseId(enterpriseId);
            admin.setLoginName(adminInfo.getUserid());
            admin.setRoles(roles);
            admin.setDomainType(Constants.DOMAIN_TYPE_LOCAL);

            EnterpriseUser enterpriseUser = enterpriseUserService.getByEnterpriseIdAndName(enterpriseId, adminInfo.getUserid());
            if(enterpriseUser != null) {
                //管理员已经开户
                admin.setName(enterpriseUser.getAlias());
                admin.setEmail(enterpriseUser.getEmail());
            } else {
                logger.info("Can't get administrator info from enterprise_user, enterpriseId={}, userId={}", enterpriseId, adminInfo.getUserid());
                //管理员没有开户，可能是不在授权范围。单独从微信服务器中查询用户信息
                User user = wxWorkOauth2SuiteProxy.getUserInfo(corpId, adminInfo.getUserid());
                if(user != null && !user.hasError()) {
                    admin.setName(user.getName());
                    admin.setEmail(user.getEmail());
                } else {
                    if(user == null) {
                        logger.error("Failed to query user info from WxWork Server: corpId={}, userId={}, response is null.", corpId, adminInfo.getUserid());
                    } else {
                        logger.error("Failed to query user info from WxWork Server: corpId={}, userId={}, code={}, msg={}", corpId, adminInfo.getUserid(), user.getErrcode(), user.getErrmsg());
                    }
                }
            }

            //设置随机密码
            String randomPassword = PasswordGenerateUtil.getRandomPassword(6);
            admin.setPassword(randomPassword);
            admin.setType(Constants.ROLE_ENTERPRISE_ADMIN);

            adminService.create(admin);

            //发送消息通知用户
            if(notify) {
                sendAdminMessage(corpId, agentId, adminInfo.getUserid(), admin);
            }
        }
    }

    private void sendAdminMessage(String corpId, Integer agentId, String userId, Admin admin) {
        MessageTemplate template = messageTemplateService.getById("createAdmin");

        if(template != null) {
            //在企业微信中发送消息。
            String message = template.getContent().replaceAll("(\\$\\{username})", admin.getLoginName());
            message = message.replaceAll("(\\$\\{password})", admin.getPassword());

            SendMessageResult result = wxWorkOauth2SuiteProxy.sendTextMessage(corpId, agentId, userId, message);
            if(result.hasError()) {
                logger.error("Failed to send message: corpId={}, agentId={}, user={}, code={}, error={}", corpId, agentId, userId, result.getErrcode(), result.getErrmsg());
            }
        } else {
            logger.warn("No Message Template found: id = createAdmin");
        }
    }
}
