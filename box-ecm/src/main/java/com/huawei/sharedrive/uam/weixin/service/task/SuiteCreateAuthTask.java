/* 
 * 版权声明(Copyright Notice)：
 * Copyright(C) 2017-2017 聚数科技成都有限公司。保留所有权利。
 * Copyright(C) 2017-2017 www.cdmi.pw Inc. All rights reserved.
 * 警告：本内容仅限于聚数科技成都有限公司内部传阅，禁止外泄以及用于其他的商业项目
 */
package com.huawei.sharedrive.uam.weixin.service.task;

import com.huawei.sharedrive.uam.weixin.domain.WxEnterprise;
import com.huawei.sharedrive.uam.weixin.domain.WxWorkCorpApp;
import com.huawei.sharedrive.uam.weixin.event.SuiteCreateAuthEvent;
import com.huawei.sharedrive.uam.weixin.rest.Agent;
import com.huawei.sharedrive.uam.weixin.rest.PermanentCodeInfo;
import com.huawei.sharedrive.uam.weixin.rest.proxy.WxWorkOauth2SuiteProxy;
import com.huawei.sharedrive.uam.weixin.service.WxEnterpriseService;
import com.huawei.sharedrive.uam.weixin.service.WxWorkCorpAppService;
import com.huawei.sharedrive.uam.weixin.service.impl.WxDomainUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import pw.cdmi.core.utils.SpringContextUtil;

import java.util.List;

/************************************************************
 * @author Rox
 * @version 3.0.1
 * @Description: <pre>企业管理员授权后，为企业开户，并初始化部门和用户数据</pre>
 * @Project Alpha CDMI Service Platform, box-uam-web Component. 2017/7/31
 ************************************************************/
@Component
@Scope("prototype")
public class SuiteCreateAuthTask extends SuiteTask {
    private static Logger logger = LoggerFactory.getLogger(SuiteCreateAuthTask.class);

    @Autowired
    private WxEnterpriseService wxEnterpriseService;

    @Autowired
    WxWorkOauth2SuiteProxy wxOauth2SuiteProxy;

    @Autowired
    WxWorkCorpAppService wxWorkCorpAppService;

    public SuiteCreateAuthTask() {
    }

    @Override
    public void run() {
        SuiteCreateAuthEvent authEvent = (SuiteCreateAuthEvent)event;
        String authCode = authEvent.getAuthCode();
        String suiteId = authEvent.getSuiteId();

        String suiteToken = wxOauth2SuiteProxy.getSuiteToken();
        if(suiteToken == null) {
            logger.error("Can't get suite access token, auth code: " + authEvent.getAuthCode());
            return;
        }

        //获取永久授权码
        PermanentCodeInfo codeInfo = wxOauth2SuiteProxy.getPermanentCode(suiteToken, authCode);
        if(codeInfo.getErrcode() != null && codeInfo.getErrcode() != 0) {
            logger.error("Can't get permanent code of corporation, auth code={}, errcode={}, errmsg={}", authCode, codeInfo.getErrcode(), codeInfo.getErrmsg());
            return;
        }

        //永久授权码放入缓存, 同时更新access token
        String corpId = codeInfo.getAuthCorpInfo().getCorpid();
        wxOauth2SuiteProxy.setCorpPermanentCode(corpId, codeInfo.getPermanentCode());
        wxOauth2SuiteProxy.setCorpToken(corpId, codeInfo.getAccessToken(), codeInfo.getExpiresIn());

        //首先将企业信息保存到数据库，再做其他操作。
        WxEnterprise wxEnterprise = saveWxEnterprise(codeInfo);

        //保存企业安装的应用信息
        saveWxWorkCorpApp(codeInfo);

        try {
            //企业账号全同步
            FullSyncSysAccountTask syncTask = SpringContextUtil.getBean(FullSyncSysAccountTask.class);
            syncTask.setSuiteId(suiteId);
            syncTask.setWxEnterprise(wxEnterprise);
            syncTask.setAuthInfo(codeInfo.getAuthInfo());

            syncTask.run();
        } catch (Exception e) {
            logger.error("Failed to create WxEnterprise: corpId={}, error={}", corpId, e.getMessage());
            logger.error("Failed to create WxEnterprise: ", e);
        }
    }

    private WxEnterprise saveWxEnterprise(PermanentCodeInfo codeInfo) {
        WxEnterprise wxEnterprise = WxDomainUtils.toWxEnterprise(codeInfo);
        try {
            WxEnterprise dbEnterprise = wxEnterpriseService.get(wxEnterprise.getId());
            if(dbEnterprise == null) {
                wxEnterprise.setState(WxEnterprise.STATE_INITIAL); //设置为初始状态
                wxEnterpriseService.create(wxEnterprise);
            } else {
                //企业已经存在：删除应用后又重新安装。
                wxEnterprise.setState(WxEnterprise.STATE_INITIAL); //设置为初始状态
                wxEnterpriseService.update(wxEnterprise);
            }

            //重新查询，使用更新后的值
            wxEnterprise = wxEnterpriseService.get(wxEnterprise.getId());
        } catch (Exception e) {
            logger.error("Failed to create WxEnterprise: corpId={}, error={}", wxEnterprise.getId(), e.getMessage());
            logger.error("Failed to create WxEnterprise: ", e);
        }

        return wxEnterprise;
    }

    private void saveWxWorkCorpApp(PermanentCodeInfo codeInfo) {
        String suiteId = event.getSuiteId();
        String corpId = codeInfo.getAuthCorpInfo().getCorpid();
        try {
            List<Agent> agentList = codeInfo.getAuthInfo().getAgent();
            for(Agent agent : agentList) {
                WxWorkCorpApp corpApp = new WxWorkCorpApp();
                corpApp.setCorpId(corpId);
                corpApp.setSuiteId(suiteId);
                corpApp.setAppId(agent.getAppid());
                corpApp.setAgentId(agent.getAgentid());
                corpApp.setName(agent.getName());
                corpApp.setRoundLogoUrl(agent.getRoundLogoUrl());
                corpApp.setSquareLogoUrl(agent.getSquareLogoUrl());

                WxWorkCorpApp dbOne = wxWorkCorpAppService.getByCorpIdAndAgentId(corpId, agent.getAgentid());
                if(dbOne == null) {
                    wxWorkCorpAppService.create(corpApp);
                }
            }
        } catch (Exception e) {
            logger.error("Failed to save WxWorkCorpApp: corpId={}, suiteId={}, error={}", corpId, suiteId, e.getMessage());
            logger.error("Failed to save WxWorkCorpApp: ", e);
        }
    }
}
