/* 
 * 版权声明(Copyright Notice)：
 * Copyright(C) 2017-2017 聚数科技成都有限公司。保留所有权利。
 * Copyright(C) 2017-2017 www.cdmi.pw Inc. All rights reserved.
 * 警告：本内容仅限于聚数科技成都有限公司内部传阅，禁止外泄以及用于其他的商业项目
 */
package com.huawei.sharedrive.uam.weixin.service.task;

import com.huawei.sharedrive.uam.organization.domain.Department;
import com.huawei.sharedrive.uam.organization.service.DepartmentService;
import com.huawei.sharedrive.uam.weixin.domain.WxEnterprise;
import com.huawei.sharedrive.uam.weixin.event.SuiteChangeAuthEvent;
import com.huawei.sharedrive.uam.weixin.event.SuiteUpdatePartyEvent;
import com.huawei.sharedrive.uam.weixin.rest.QueryDepartmentResponse;
import com.huawei.sharedrive.uam.weixin.rest.proxy.WxWorkOauth2SuiteProxy;
import com.huawei.sharedrive.uam.weixin.service.WxDepartmentService;
import com.huawei.sharedrive.uam.weixin.service.WxEnterpriseService;
import com.huawei.sharedrive.uam.weixin.service.WxWorkDepartmentManager;
import com.huawei.sharedrive.uam.weixin.service.impl.WxDomainUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import pw.cdmi.core.utils.SpringContextUtil;

/************************************************************
 * @author Rox
 * @version 3.0.1
 * @Description: <pre>企业管理员授权后，为企业开户，并初始化部门和用户数据</pre>
 * @Project Alpha CDMI Service Platform, box-uam-web Component. 2017/7/31
 ************************************************************/
@Component
@Scope("prototype")
public class SuiteUpdatePartyTask extends SuiteTask {
    private static Logger logger = LoggerFactory.getLogger(SuiteUpdatePartyTask.class);

    @Autowired
    private WxWorkDepartmentManager wxWorkDepartmentManager;

    @Autowired
    WxEnterpriseService wxEnterpriseService;

    @Autowired
    WxDepartmentService wxDepartmentService;

    @Autowired
    DepartmentService departmentService;

    @Autowired
    WxWorkOauth2SuiteProxy wxWorkOauth2SuiteProxy;

    public SuiteUpdatePartyTask() {
    }

    @Override
    public void run() {
        SuiteUpdatePartyEvent updateEvent = (SuiteUpdatePartyEvent)event;
        String suiteId = updateEvent.getSuiteId();
        String corpId = updateEvent.getAuthCorpId();

        try {
            WxEnterprise wxEnterprise = wxEnterpriseService.get(corpId);
            if(wxEnterprise == null) {
                logger.error("Failed to update party, no WxEnterprise found in database. corpId={}", corpId);
                return;
            }

            if(wxEnterprise.getBoxEnterpriseId() == null) {
                logger.warn("No Enterprise found in database, just open the enterprise account. corpId={}", corpId);

                //生成企业账号，走一遍同步流程。同步后用户信息已经是最新的，不再需要更新。
                SuiteChangeAuthTask task = SpringContextUtil.getBean(SuiteChangeAuthTask.class);
                task.setEvent(new SuiteChangeAuthEvent(suiteId, corpId));
                task.run();

                return;
            }

            long enterpriseId = wxEnterprise.getBoxEnterpriseId();
            Department newDept = WxDomainUtils.toDepartment(updateEvent);
            newDept.setEnterpriseId(enterpriseId);

            Department dbDept = departmentService.getByEnterpriseIdAndDepartmentId(enterpriseId, updateEvent.getId());
            if(dbDept != null) {
                //部门层级发生了改变: 此处只更新部门信息，部门范围内的员工会收到SuiteUpdateUserEvent，然后更新
                wxWorkDepartmentManager.updateAccount(newDept);
            } else {
                //部门不存在，正常情况下不应该发生
                createDepartmentAccount(enterpriseId, updateEvent);
            }
        } catch (Exception e) {
            logger.error("Failed to update party: corpId={}, deptId={}, error={}", corpId, updateEvent.getId(), e.getMessage());
            logger.error("Failed to update party:", e);
        }
    }

    protected void createDepartmentAccount(long enterpriseId, SuiteUpdatePartyEvent event) {
        String corpId = event.getAuthCorpId();
        String deptId = String.valueOf(event.getId());

        try {
            QueryDepartmentResponse deptResponse = wxWorkOauth2SuiteProxy.getDepartmentList(corpId, deptId);
            if(deptResponse == null) {
                logger.error("Failed to query department list from corporate={}, deptId={}: query result is null.", corpId, event.getId());
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
                try {
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
        } catch (Exception e) {
            logger.error("Failed to synchronize department list: corpId={}, deptId={}, error={}", corpId, deptId, e.getMessage());
            logger.error("Failed to synchronize department list:", e);
        }
    }
}


