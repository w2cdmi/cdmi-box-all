
/*
 * 版权声明(Copyright Notice)：
 * Copyright(C) 2017-2018 聚数科技成都有限公司。保留所有权利。
 * Copyright(C) 2017-2018 www.cdmi.pw Inc. All rights reserved.
 * 警告：本内容仅限于聚数科技成都有限公司内部传阅，禁止外泄以及用于其他的商业项目
 */
package com.huawei.sharedrive.uam.weixin.service.impl;

import com.huawei.sharedrive.uam.authapp.service.AuthAppService;
import com.huawei.sharedrive.uam.enterprise.service.EnterpriseAccountService;
import com.huawei.sharedrive.uam.enterpriseuser.domain.EnterpriseUser;
import com.huawei.sharedrive.uam.enterpriseuser.service.EnterpriseUserService;
import com.huawei.sharedrive.uam.organization.domain.Department;
import com.huawei.sharedrive.uam.organization.domain.DepartmentAccount;
import com.huawei.sharedrive.uam.organization.manager.DepartmentManager;
import com.huawei.sharedrive.uam.organization.service.DepartmentAccountService;
import com.huawei.sharedrive.uam.organization.service.DepartmentService;
import com.huawei.sharedrive.uam.organization.service.EnterpriseUserDeptService;
import com.huawei.sharedrive.uam.teamspace.domain.RestTeamSpaceModifyRequest;
import com.huawei.sharedrive.uam.teamspace.service.TeamSpaceService;
import com.huawei.sharedrive.uam.weixin.service.WxWorkDepartmentManager;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pw.cdmi.common.domain.enterprise.EnterpriseAccount;
import pw.cdmi.uam.domain.AuthApp;

import java.util.List;

/************************************************************
 * @Description:
 * <pre>企业微信部门账号管理</pre>
 * @author Rox
 * @version 3.0.1
 * @Project Alpha CDMI Service Platform, box-uam-web Component. 2018/1/27
 ************************************************************/
@Service
public class WxWorkDepartmentManagerImpl implements WxWorkDepartmentManager {
    private static Logger logger = LoggerFactory.getLogger(WxWorkDepartmentManagerImpl.class);

    @Autowired
    private EnterpriseUserService enterpriseUserService;

    @Autowired
    private EnterpriseAccountService enterpriseAccountService;

    @Autowired
    private DepartmentService departmentService;

    @Autowired
    private DepartmentManager departmentManager;

    @Autowired
    private DepartmentAccountService departmentAccountService;

    @Autowired
    private EnterpriseUserDeptService enterpriseUserDeptService;

    @Autowired
    private AuthAppService authAppService;

    @Autowired
    private TeamSpaceService teamSpaceService;

    @Override
    public Department openAccount(Department dept) {
        try {
            Department parentDept = departmentService.getByEnterpriseIdAndDepartmentId(dept.getEnterpriseId(), dept.getParentId());
            if(parentDept != null) {
                dept.setParentId(parentDept.getDepartmentId());
            } else {
                //父部门不可见，将其作为根部门显示
                dept.setParentId(0L);
            }

            departmentManager.create(dept);

            return dept;
        } catch (Exception e) {
            logger.error("Failed to create department: enterpriseId={}, deptId={}, deptName={}, error={}", dept.getEnterpriseId(), dept.getDepartmentId(), dept.getName(), e.getMessage());
            logger.error("Failed to create department: ", e);
        }

        return null;
    }

    @Override
    public void updateAccount(Department dept) {
        try {
            if(dept.getParentId() != null) {
                Department parentDept = departmentService.getByEnterpriseIdAndDepartmentId(dept.getEnterpriseId(), dept.getParentId());
                if(parentDept != null) {
                    dept.setParentId(parentDept.getDepartmentId());
                } else {
                    //父部门不可见，将其作为根部门显示
                    dept.setParentId(0L);
                }
            }
            departmentService.update(dept);

            //修改了部门名称（企业微信的机制，只有修改后的字段才会出现在消息中）
            if(StringUtils.isNotBlank(dept.getName())) {
                AuthApp app = authAppService.getDefaultWebApp();
                EnterpriseAccount account = enterpriseAccountService.getByEnterpriseApp(dept.getEnterpriseId(), app.getAuthAppId());
                DepartmentAccount deptAccount = departmentAccountService.getByDeptIdAndAccountId(dept.getDepartmentId(), account.getAccountId());

                RestTeamSpaceModifyRequest modifyRequest = new RestTeamSpaceModifyRequest();
                modifyRequest.setName(dept.getName());
                teamSpaceService.modifyTeamSpace(dept.getEnterpriseId(), deptAccount.getCloudUserId(), app.getAuthAppId(), modifyRequest);
            }
        } catch (Exception e) {
            logger.error("Failed to update department account: enterpriseId={}, dept={}, error={}", dept.getEnterpriseId(), dept.getName(), e.getMessage());
            logger.error("Failed to update department account: ", e);
        }
    }

    public void closeAccount(Department dept) {
        try {
            byte state = dept.getState();
            //部门正在使用中, 标记为已解散，删除员工关系。未授权也按此流程走
            if(state == Department.STATE_ENABLE || state == Department.STATE_NOT_SYNC) {
                dept.setState(Department.STATE_DISMISS);
                departmentService.update(dept);

                //将该部门下的员工移除。
                removeUserFromDepartment(dept);
            }

            if(state == Department.STATE_DISMISS) {
                logger.warn("The department is dismissed, but not hand over, just waiting administrator hand over it. corpId={}, deptId={}, name={}", dept.getEnterpriseId(), dept.getDepartmentId(), dept.getName());
            }

            //部门已经移交完成，直接删除（移交需要手工操作，只有用户提前移交，才会进入此流程）
            if(state == Department.STATE_HAND_OVER) {
                logger.info("delete the department which is hand over. enterpriseId={}, deptId={}, name={}", dept.getEnterpriseId(), dept.getDepartmentId(), dept.getName());
                deleteDepartment(dept);
            }
        } catch (Exception e) {
            logger.error("Failed to delete department: enterpriseId={}, deptId={}, error={}.", dept.getEnterpriseId(), dept.getDepartmentId(), e.getMessage());
            logger.error("Failed to delete department: ", e);
        }
    }

    private void removeUserFromDepartment(Department dept) {
        List<EnterpriseUser>  userList = enterpriseUserService.getByDeptId(dept.getEnterpriseId(), dept.getDepartmentId());
        for(EnterpriseUser user : userList) {
            int count = departmentService.countByEnterpriseUserId(user.getEnterpriseId(), user.getId());

            if(count > 1) {
                //该员工在其他部门也存在,从该部门删除。
                enterpriseUserDeptService.deleteByDepartmentIdAndEnterpriseUserId(user.getEnterpriseId(), dept.getDepartmentId(), user.getId());
            } else {
                user.setStatus(EnterpriseUser.STATUS_DISMISS);
                enterpriseUserService.update(user);
            }
        }
    }

    private void deleteDepartment(Department dept) {
        AuthApp app = authAppService.getDefaultWebApp();
        EnterpriseAccount account = enterpriseAccountService.getByEnterpriseApp(dept.getEnterpriseId(), app.getAuthAppId());
        DepartmentAccount deptAccount = departmentAccountService.getByDeptIdAndAccountId(dept.getDepartmentId(), account.getAccountId());
        if(deptAccount != null) {
            //删除部门空间. cloudId就是teamId
            teamSpaceService.deleteTeamSpace(dept.getEnterpriseId(), app.getAuthAppId(), deptAccount.getCloudUserId());

            //删除部门对应的账户
            departmentAccountService.delete(deptAccount.getId());

            //删除部门信息
            departmentService.delete(dept.getEnterpriseId(), dept.getDepartmentId());
        }
    }
}
