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
import com.huawei.sharedrive.uam.weixin.event.SuiteDeletePartyEvent;
import com.huawei.sharedrive.uam.weixin.service.WxDepartmentService;
import com.huawei.sharedrive.uam.weixin.service.WxEnterpriseService;
import com.huawei.sharedrive.uam.weixin.service.WxWorkDepartmentManager;
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
public class SuiteDeletePartyTask extends SuiteTask {
    private static Logger logger = LoggerFactory.getLogger(SuiteDeletePartyTask.class);

    @Autowired
    private WxWorkDepartmentManager wxWorkDepartmentManager;

    @Autowired
    WxEnterpriseService wxEnterpriseService;

    @Autowired
    WxDepartmentService wxDepartmentService;

    @Autowired
    DepartmentService departmentService;

    public SuiteDeletePartyTask() {
    }

    @Override
    public void run() {
        SuiteDeletePartyEvent deleteEvent = (SuiteDeletePartyEvent)event;
        String suiteId = deleteEvent.getSuiteId();
        String corpId = deleteEvent.getAuthCorpId();

        try {
            WxEnterprise wxEnterprise = wxEnterpriseService.get(corpId);
            if(wxEnterprise == null) {
                logger.error("Failed to create party, no WxEnterprise found in database. corpId={}", corpId);
                return;
            }

            if(wxEnterprise.getBoxEnterpriseId() == null) {
                logger.warn("No Enterprise account found in database, just open the enterprise account. corpId={}", corpId);

                //生成企业账号，走一遍同步流程。同步后用户信息已经是最新的，不再需要更新。
                SuiteChangeAuthTask task = SpringContextUtil.getBean(SuiteChangeAuthTask.class);
                task.setEvent(new SuiteChangeAuthEvent(suiteId, corpId));
                task.run();

                return;
            }

            long enterpriseId = wxEnterprise.getBoxEnterpriseId();
            Department sysDept = departmentService.getByEnterpriseIdAndDepartmentId(enterpriseId, deleteEvent.getId());
            if(sysDept != null) {
                wxWorkDepartmentManager.closeAccount(sysDept);
            } else {
                logger.info("No department found in system. ignore this event. enterpriseId={}, departmentId={}", enterpriseId, deleteEvent.getId());
            }
        } catch (Exception e) {
            logger.error("Failed to delete party: corpId={}, deptId={}, error={}", corpId, deleteEvent.getId(), e.getMessage());
            logger.error("Failed to delete party:", e);
        }
    }
}


